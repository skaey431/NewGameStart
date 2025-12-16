package NewGameStart.NewGame.screens;

import NewGameStart.NewGame.Main;
import NewGameStart.NewGame.entities.DamageBox;
import NewGameStart.NewGame.entities.Checkpoint;
import NewGameStart.NewGame.entities.InstantKillBox; // InstantKillBox import
import NewGameStart.NewGame.entities.player.Player;
import NewGameStart.NewGame.screens.UI.HealthBar;
import NewGameStart.NewGame.screens.managers.GameStateManager;
import NewGameStart.NewGame.world.WorldManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameScreen implements Screen {

    private final Main game;
    private WorldManager worldManager;
    private Player player;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;

    private GameStateManager gameStateManager;

    private Array<DamageBox> damageBoxes;
    private float damageTimer = 0f;
    private final float DAMAGE_CHECK_RATE = 0.5f;

    // --- 즉사 박스 관련 필드 추가 ---
    private Array<InstantKillBox> killBoxes;

    // --- Checkpoint 관련 필드 ---
    private Array<Checkpoint> checkpoints;
    private final float CHECKPOINT_INTERACT_DISTANCE = 1.5f;
    private Label interactPromptLabel;
    private Checkpoint currentInteractableCheckpoint;

    private Stage stage;
    private Skin skin;
    private HealthBar healthBar;
    private Label gameOverLabel;

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

        createDamageBoxes();
        createInstantKillBoxes(); // 즉사 박스 생성 호출
        createCheckpoints();

        loadSkin();
        setupUI();

        gameStateManager = new GameStateManager(game, player, gameOverLabel);
        gameStateManager.setCheckpoint(3f, 5f);
    }

    private void createPlayer() {
        player = new Player(worldManager.getWorld(), 3f, 5f);
    }

    private void createDamageBoxes() {
        damageBoxes = new Array<>();
        damageBoxes.add(new DamageBox(10f, 1f, 3f, 0.5f, 5f, DAMAGE_CHECK_RATE));
        damageBoxes.add(new DamageBox(20f, 5f, 2f, 2f, 10f, DAMAGE_CHECK_RATE));
    }

    // --- 즉사 박스 생성 메서드 추가 ---
    private void createInstantKillBoxes() {
        killBoxes = new Array<>();
        // 예시 즉사 박스: 맵의 가장 아래 (낙사 지점)
        killBoxes.add(new InstantKillBox(-10f, -5f, 50f, 4f));
        // 예시 즉사 박스 2: 특정 지점 (25, 0.5)
        killBoxes.add(new InstantKillBox(25f, 0.5f, 3f, 0.5f));
    }

    private void createCheckpoints() {
        checkpoints = new Array<>();
        checkpoints.add(new Checkpoint(8f, 1.5f, 1f, 2f, 8f, 2.5f));
        checkpoints.add(new Checkpoint(30f, 7.5f, 1f, 2f, 30f, 8.5f));
    }

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

    /**
     * 이름 변경: checkDamage() -> checkHazards()
     * 데미지 박스와 즉사 박스 충돌을 모두 검사합니다.
     */
    private void checkHazards(float delta) {
        if (!player.isAlive()) return;

        com.badlogic.gdx.math.Rectangle playerBounds = player.getBounds();
        boolean killed = false;

        // 1. 즉사 박스 충돌 검사
        for (InstantKillBox killBox : killBoxes) {
            if (playerBounds.overlaps(killBox.getBounds())) {
                player.takeDamage(player.getMaxHealth()); // 체력과 무관하게 즉사 처리
                killed = true;
                break;
            }
        }

        if (killed) {
            gameStateManager.handleGameOver();
            return;
        }

        // 2. 일반 데미지 박스 충돌 검사
        damageTimer += delta;
        if (damageTimer >= DAMAGE_CHECK_RATE) {

            for (DamageBox box : damageBoxes) {
                if (playerBounds.overlaps(box.getBounds())) {
                    player.takeDamage(box.getDamageAmount());

                    if (!player.isAlive()) {
                        gameStateManager.handleGameOver();
                        break;
                    }
                }
            }
            damageTimer -= DAMAGE_CHECK_RATE;
        }
    }

    private void checkCheckpointInteraction() {
        if (gameStateManager.isGameOver() || !player.isAlive()) {
            interactPromptLabel.setVisible(false);
            currentInteractableCheckpoint = null;
            return;
        }

        currentInteractableCheckpoint = null;
        Body playerBody = player.getBody();

        for (Checkpoint cp : checkpoints) {
            if (cp.isPlayerNear(
                playerBody.getPosition().x,
                playerBody.getPosition().y,
                CHECKPOINT_INTERACT_DISTANCE)) {

                currentInteractableCheckpoint = cp;
                break;
            }
        }

        if (currentInteractableCheckpoint != null) {
            interactPromptLabel.setVisible(true);

            if (currentInteractableCheckpoint.isActivated()) {
                interactPromptLabel.setText("Press C to Re-set Checkpoint");
            } else {
                interactPromptLabel.setText("Press C to Set Checkpoint");
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {

                for (Checkpoint cp : checkpoints) {
                    cp.deactivate();
                }

                currentInteractableCheckpoint.activate();
                gameStateManager.setCheckpoint(
                    currentInteractableCheckpoint.getSpawnPosition().x,
                    currentInteractableCheckpoint.getSpawnPosition().y
                );

                interactPromptLabel.setText("Checkpoint Saved!");
            }
        } else {
            interactPromptLabel.setVisible(false);
            interactPromptLabel.setText("Press C to Set Checkpoint");
        }
    }


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
            checkHazards(delta); // checkHazards() 호출
            checkCheckpointInteraction();
        }

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

        // 플레이어 시각화
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

        // --- 즉사 박스 시각화 (빨간색, 짙은 투명도) ---
        shapeRenderer.setColor(1f, 0f, 0f, 0.8f);
        for (InstantKillBox killBox : killBoxes) {
            com.badlogic.gdx.math.Rectangle rect = killBox.getBounds();
            shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
        }

        // 데미지 박스 시각화 (주황색, 보통 투명도)
        shapeRenderer.setColor(1f, 0.5f, 0f, 0.5f); // 색상 변경
        for (DamageBox box : damageBoxes) {
            com.badlogic.gdx.math.Rectangle rect = box.getBounds();
            shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
        }

        // 체크포인트 시각화
        for (Checkpoint cp : checkpoints) {
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
}
