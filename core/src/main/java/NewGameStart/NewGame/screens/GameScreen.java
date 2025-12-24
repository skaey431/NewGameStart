package NewGameStart.NewGame.screens;

import NewGameStart.NewGame.Main;
import NewGameStart.NewGame.tools.Constants;
import NewGameStart.NewGame.entities.*;
import NewGameStart.NewGame.entities.monster.StaticMonster;
import NewGameStart.NewGame.entities.player.Player;
import NewGameStart.NewGame.screens.UI.HealthBar;
import NewGameStart.NewGame.screens.managers.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
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

    private OrthogonalTiledMapRenderer mapRenderer;

    private Stage stage;
    private Skin skin;
    private HealthBar playerHealthBar;
    private Label gameOverLabel;
    private Label interactPromptLabel;

    private static final float WORLD_WIDTH = 16f;
    private static final float WORLD_HEIGHT = 9f;

    private int currentStage = 1;

    public GameScreen(Main game) {
        this.game = game;

        this.worldManager = new WorldManager();
        this.debugRenderer = new Box2DDebugRenderer();
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        this.shapeRenderer = new ShapeRenderer();
        this.stage = new Stage(new ScreenViewport());

        createPlayer();

        // 매니저들 초기화 (entityManager를 먼저 생성)
        this.gameStateManager = new GameStateManager(game, player, gameOverLabel);
        this.entityManager = new EntityManager(gameStateManager, player);
        this.mapManager = new MapManager(worldManager.getWorld());

        loadStage(currentStage);

        loadSkin();
        setupUI();
        this.gameStateManager.setCheckpoint(3f, 5f);
    }

    private void loadStage(int stageNum) {
        String mapPath = "maps/stage" + stageNum + ".tmx";

        // 1. 엔티티 리스트 비우기
        entityManager.clearEntities();

        // 2. [연동] 맵 로드 시 entityManager를 같이 넘겨 데미지 필드 자동 생성
        mapManager.loadMap(mapPath, entityManager);

        // 3. 렌더러 교체
        if (mapRenderer != null) mapRenderer.dispose();
        mapRenderer = new OrthogonalTiledMapRenderer(mapManager.getTiledMap(), 1 / Constants.PPM);
    }

    private void createPlayer() {
        player = new Player(worldManager.getWorld(), 3f, 5f);
    }

    // ... loadSkin, setupUI (원본 유지) ...

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

        if (mapRenderer != null) {
            mapRenderer.setView(camera);
            mapRenderer.render();
        }

        debugRenderer.render(worldManager.getWorld(), camera.combined);

        shapeRenderer.setProjectionMatrix(camera.combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // --- 플레이어 및 엔티티 렌더링 ---
        Body pb = player.getBody();
        shapeRenderer.setColor(isGameOver ? new Color(1, 1, 1, 0.5f) : Color.WHITE);
        shapeRenderer.rect(pb.getPosition().x - 0.3f, pb.getPosition().y - 0.5f, 0.6f, 1.0f);

        // 맵 매니저가 자동으로 채워준 리스트를 순회하며 그림
        for (StaticMonster monster : entityManager.getMonsters()) {
            if (monster.isAlive()) {
                shapeRenderer.setColor(0.5f, 0f, 0.5f, 0.9f);
                shapeRenderer.rect(monster.getBounds().x / Constants.PPM, monster.getBounds().y / Constants.PPM,
                    monster.getBounds().width / Constants.PPM, monster.getBounds().height / Constants.PPM);
            }
        }

        for (DamageBox box : entityManager.getDamageBoxes()) {
            shapeRenderer.setColor(1f, 0.5f, 0f, 0.5f);
            shapeRenderer.rect(box.getBounds().x / Constants.PPM, box.getBounds().y / Constants.PPM,
                box.getBounds().width / Constants.PPM, box.getBounds().height / Constants.PPM);
        }

        // ... (InstantKillBox, Checkpoint 드로잉 로직 생략) ...

        shapeRenderer.end();

        stage.act(delta);
        stage.draw();
    }

    public void nextStage() {
        currentStage++;
        loadStage(currentStage);
        player.getBody().setTransform(3f, 5f, 0);
        player.resetAbilities();
        System.out.println("Stage " + currentStage + " Loaded!");
    }

    @Override public void dispose() {
        worldManager.dispose();
        if (mapManager != null) mapManager.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
        debugRenderer.dispose();
        shapeRenderer.dispose();
        if (skin != null) skin.dispose();
        if (stage != null) stage.dispose();
    }

    // ... 나머지 resize, show 등 원본 유지 ...
    private void loadSkin() { /* 원본과 동일 */ }
    private void setupUI() { /* 원본과 동일 */ }
    @Override public void resize(int width, int height) { /* 원본과 동일 */ }
    @Override public void show() { Gdx.input.setInputProcessor(stage); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
