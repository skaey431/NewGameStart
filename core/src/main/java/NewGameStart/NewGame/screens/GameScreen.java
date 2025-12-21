package NewGameStart.NewGame.screens;

import NewGameStart.NewGame.Main;
import NewGameStart.NewGame.tools.Constants;
import NewGameStart.NewGame.entities.DamageBox;
import NewGameStart.NewGame.entities.Checkpoint;
import NewGameStart.NewGame.entities.InstantKillBox;
import NewGameStart.NewGame.entities.monster.StaticMonster;
import NewGameStart.NewGame.entities.player.Player;
import NewGameStart.NewGame.screens.UI.HealthBar;
import NewGameStart.NewGame.screens.managers.GameStateManager;
import NewGameStart.NewGame.screens.managers.EntityManager;
import NewGameStart.NewGame.screens.managers.MapManager;
import NewGameStart.NewGame.screens.managers.WorldManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameScreen implements Screen {

    private final Main game;
    private WorldManager worldManager;
    private Player player;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;

    private GameStateManager gameStateManager;
    private EntityManager entityManager;
    private MapManager mapManager;

    private Stage stage;
    private Skin skin;
    private HealthBar playerHealthBar;
    private Label gameOverLabel;
    private Label interactPromptLabel;

    private static final float WORLD_WIDTH = 16f;
    private static final float WORLD_HEIGHT = 9f;

    public GameScreen(Main game) {
        this.game = game;

        this.worldManager = new WorldManager();
        this.debugRenderer = new Box2DDebugRenderer();
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        this.shapeRenderer = new ShapeRenderer();

        this.stage = new Stage(new ScreenViewport());

        // 1. 플레이어 생성
        createPlayer();

        // 2. 맵 로드 (기존 createDefaultStage 대체/병행)
        this.mapManager = new MapManager(worldManager.getWorld());
        this.mapManager.loadMap("maps/stage1.tmx");

        // worldManager.createDefaultStage(); // 필요 시 유지

        loadSkin();
        setupUI();

        // 3. 매니저 초기화
        this.gameStateManager = new GameStateManager(game, player, gameOverLabel);
        this.gameStateManager.setCheckpoint(3f, 5f);
        this.entityManager = new EntityManager(gameStateManager, player);
    }

    private void createPlayer() {
        player = new Player(worldManager.getWorld(), 3f, 5f);
    }

    private void loadSkin() {
        String[] potentialPaths = {"skin/uiskin.json", "data/uiskin.json", "uiskin.json"};
        skin = null;
        for (String path : potentialPaths) {
            FileHandle skinFile = Gdx.files.internal(path);
            if (skinFile.exists()) {
                try {
                    skin = new Skin(skinFile);
                    return;
                } catch (Exception e) {
                    Gdx.app.error("GameScreen", "Skin Error: " + e.getMessage());
                }
            }
        }
    }

    private void setupUI() {
        if (skin != null) {
            playerHealthBar = new HealthBar(skin, player);
            stage.addActor(playerHealthBar);

            gameOverLabel = new Label("GAME OVER\n(Respawning...)", skin);
            gameOverLabel.setFontScale(2.0f);
            gameOverLabel.setVisible(false);
            stage.addActor(gameOverLabel);

            interactPromptLabel = new Label("Press C to Set Checkpoint", skin);
            interactPromptLabel.setVisible(false);
            stage.addActor(interactPromptLabel);
        }
    }

    @Override
    public void render(float delta) {
        gameStateManager.update(delta);
        boolean isGameOver = gameStateManager.isGameOver();

        if (!isGameOver) {
            worldManager.getWorld().step(delta, 6, 2);
            player.update(delta);
            entityManager.update(delta);
        }

        camera.position.set(player.getBody().getPosition().x, player.getBody().getPosition().y, 0);
        camera.update();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        debugRenderer.render(worldManager.getWorld(), camera.combined);

        shapeRenderer.setProjectionMatrix(camera.combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // --- 시각화 로직 ---
        Body pb = player.getBody();
        shapeRenderer.setColor(isGameOver ? new Color(1, 1, 1, 0.5f) : Color.WHITE);
        shapeRenderer.rect(pb.getPosition().x - 0.3f, pb.getPosition().y - 0.5f, 0.6f, 1.0f);

        if (player.isAttacking()){
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(player.getAttackHitbox().x, player.getAttackHitbox().y,
                player.getAttackHitbox().width, player.getAttackHitbox().height);
        }

        for (InstantKillBox killBox : entityManager.getKillBoxes()) {
            shapeRenderer.setColor(1f, 0f, 0f, 0.8f);
            shapeRenderer.rect(killBox.getBounds().x, killBox.getBounds().y, killBox.getBounds().width, killBox.getBounds().height);
        }

        for (StaticMonster monster : entityManager.getMonsters()) {
            if (monster.isAlive()) {
                shapeRenderer.setColor(0.5f, 0f, 0.5f, 0.9f);
                shapeRenderer.rect(monster.getBounds().x, monster.getBounds().y, monster.getBounds().width, monster.getBounds().height);
            }
        }

        for (DamageBox box : entityManager.getDamageBoxes()) {
            shapeRenderer.setColor(1f, 0.5f, 0f, 0.5f);
            shapeRenderer.rect(box.getBounds().x, box.getBounds().y, box.getBounds().width, box.getBounds().height);
        }

        for (Checkpoint cp : entityManager.getCheckpoints()) {
            shapeRenderer.setColor(cp.isActivated() ? Color.GREEN : Color.BLUE);
            shapeRenderer.rect(cp.getBounds().x, cp.getBounds().y, cp.getBounds().width, cp.getBounds().height);
        }

        shapeRenderer.end();

        interactPromptLabel.setVisible(entityManager.isInteractPromptVisible());
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = WORLD_WIDTH * ((float)width / height);
        camera.viewportHeight = WORLD_HEIGHT;
        camera.update();
        stage.getViewport().update(width, height, true);
    }

    @Override public void show() { Gdx.input.setInputProcessor(stage); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        worldManager.dispose();
        if (mapManager != null) mapManager.dispose();
        debugRenderer.dispose();
        shapeRenderer.dispose();
        if (skin != null) skin.dispose();
        if (stage != null) stage.dispose();
    }
}
