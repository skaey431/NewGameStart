package NewGameStart.NewGame.screens;

import NewGameStart.NewGame.Main;
import NewGameStart.NewGame.entities.DamageBox;
import NewGameStart.NewGame.entities.Checkpoint;
import NewGameStart.NewGame.entities.InstantKillBox;
import NewGameStart.NewGame.entities.player.Player;
import NewGameStart.NewGame.screens.UI.HealthBar;
import NewGameStart.NewGame.screens.managers.GameStateManager;
import NewGameStart.NewGame.screens.managers.EntityManager; // EntityManager import
import NewGameStart.NewGame.world.WorldManager;
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
    private final WorldManager worldManager;
    private Player player;
    private final Box2DDebugRenderer debugRenderer;
    private final OrthographicCamera camera;
    private final ShapeRenderer shapeRenderer;

    // 핵심 관리자 클래스들
    private final GameStateManager gameStateManager;
    private final EntityManager entityManager;

    private final Stage stage;
    private Skin skin;
    private HealthBar healthBar;
    private Label gameOverLabel;
    private Label interactPromptLabel;

    private static final float WORLD_WIDTH = 16f;
    private static final float WORLD_HEIGHT = 9f;

    public GameScreen(Main game) {
        this.game = game;

        worldManager = new WorldManager();
        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        shapeRenderer = new ShapeRenderer();

        stage = new Stage(new ScreenViewport());

        createPlayer();
        worldManager.createDefaultStage();

        loadSkin();
        setupUI();

        // GameStateManager는 Player 및 UI 관리
        gameStateManager = new GameStateManager(game, player, gameOverLabel);
        gameStateManager.setCheckpoint(3f, 5f);

        // EntityManager는 GameStateManager와 Player를 기반으로 엔티티 로직 관리
        entityManager = new EntityManager(gameStateManager, player);
    }

    private void createPlayer() {
        player = new Player(worldManager.getWorld(), 3f, 5f);
    }

    // 엔티티 생성/관리 메서드 제거됨

    private void loadSkin() {
        String[] potentialPaths = {"skin/uiskin.json", "data/uiskin.json", "uiskin.json"};
        skin = null;
        for (String path : potentialPaths) {
            FileHandle skinFile = Gdx.files.internal(path);
            if (skinFile.exists()) {
                try {
                    skin = new Skin(skinFile);
                    Gdx.app.log("GameScreen", "UISkin loaded successfully from: " + path);
                    return;
                } catch (Exception e) {
                    Gdx.app.error("GameScreen", "Error loading skin from " + path + ". Check for JSON errors or missing assets: " + e.getMessage());
                }
            }
        }
        Gdx.app.error("GameScreen", "FATAL: UISkin file not found or failed to load. Check file path in assets folder.");
    }

    private void setupUI() {
        if (skin != null) {
            try {
                healthBar = new HealthBar(skin, player);
                healthBar.setPosition(20, Gdx.graphics.getHeight() - healthBar.getHeight() - 20);
                stage.addActor(healthBar);

                gameOverLabel = new Label("GAME OVER\n(Respawning...)", skin);
                gameOverLabel.setFontScale(2.0f);
                gameOverLabel.setVisible(false);
                stage.addActor(gameOverLabel);

                interactPromptLabel = new Label("Press C to Set Checkpoint", skin);
                interactPromptLabel.setFontScale(1.0f);
                interactPromptLabel.setVisible(false);
                stage.addActor(interactPromptLabel);

            } catch (RuntimeException e) {
                Gdx.app.error("GameScreen", "Failed to setup UI (Skin element missing): " + e.getMessage());
                healthBar = null;
                gameOverLabel = null;
                interactPromptLabel = null;
            }
        }
    }

    // checkHazards() 및 checkCheckpointInteraction() 메서드 제거됨

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {

        gameStateManager.update(delta);
        boolean isGameOver = gameStateManager.isGameOver();

        if (!isGameOver) {
            worldManager.getWorld().step(delta, 6, 2);
            player.update(delta);
            entityManager.update(delta); // 엔티티 로직 및 충돌 처리 위임
        }

        // 카메라 및 화면 클리어 로직 (유지)
        camera.position.x = player.getBody().getPosition().x;
        camera.position.y = player.getBody().getPosition().y;
        camera.update();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        debugRenderer.render(worldManager.getWorld(), camera.combined);

        shapeRenderer.setProjectionMatrix(camera.combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // --- 렌더링 로직 (EntityManager의 엔티티들을 가져와서 그림) ---

        // 1. 플레이어 시각화
        Body pb = player.getBody();
        if (isGameOver) {
            float alpha = (float)(0.5 + 0.5 * Math.sin(Gdx.graphics.getFrameId() * 0.2));
            shapeRenderer.setColor(1f, 1f, 1f, alpha);
        } else {
            shapeRenderer.setColor(Color.WHITE);
        }

        shapeRenderer.rect(
            pb.getPosition().x - 0.3f,
            pb.getPosition().y - 0.5f,
            0.6f, 1.0f
        );

        // 2. 즉사 박스 시각화
        shapeRenderer.setColor(1f, 0f, 0f, 0.8f);
        for (InstantKillBox killBox : entityManager.getKillBoxes()) {
            com.badlogic.gdx.math.Rectangle rect = killBox.getBounds();
            shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
        }

        // 3. 데미지 박스 시각화
        shapeRenderer.setColor(1f, 0.5f, 0f, 0.5f);
        for (DamageBox box : entityManager.getDamageBoxes()) {
            com.badlogic.gdx.math.Rectangle rect = box.getBounds();
            shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
        }

        // 4. 체크포인트 시각화
        for (Checkpoint cp : entityManager.getCheckpoints()) {
            com.badlogic.gdx.math.Rectangle rect = cp.getBounds();
            if (cp.isActivated()) {
                shapeRenderer.setColor(0.1f, 0.8f, 0.1f, 0.9f);
            } else {
                shapeRenderer.setColor(0.1f, 0.1f, 0.8f, 0.7f);
            }
            shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
        }

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // --- UI 업데이트 및 렌더링 ---

        // 1. 상호작용 프롬프트 UI 업데이트
        interactPromptLabel.setVisible(entityManager.isInteractPromptVisible());
        if (entityManager.isInteractPromptVisible()) {
            interactPromptLabel.setText(entityManager.getInteractPromptText());
            interactPromptLabel.pack(); // 텍스트 변경 시 레이블 크기 재조정
        }

        stage.act(delta);
        stage.draw();

        if (healthBar != null) {
            healthBar.setPosition(20, Gdx.graphics.getHeight() - healthBar.getHeight() - 20);
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = WORLD_WIDTH * ((float)width / height);
        camera.viewportHeight = WORLD_HEIGHT * ((float)height / height);
        camera.update();
        stage.getViewport().update(width, height, true);

        if (gameOverLabel != null) {
            gameOverLabel.pack();
            gameOverLabel.setPosition(
                (stage.getWidth() - gameOverLabel.getWidth()) / 2,
                (stage.getHeight() - gameOverLabel.getHeight()) / 2
            );
        }

        if (interactPromptLabel != null) {
            interactPromptLabel.pack();
            interactPromptLabel.setPosition(
                (stage.getWidth() - interactPromptLabel.getWidth()) / 2,
                50
            );
        }
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        worldManager.dispose();
        debugRenderer.dispose();
        shapeRenderer.dispose();

        if (skin != null) skin.dispose();
        if (stage != null) stage.dispose();
    }

    public Main getGame() {
        return game;
    }
}
