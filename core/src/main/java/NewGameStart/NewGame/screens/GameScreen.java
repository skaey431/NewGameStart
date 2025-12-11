package NewGameStart.NewGame.screens;

import NewGameStart.NewGame.Main;
import NewGameStart.NewGame.entities.DamageBox;
import NewGameStart.NewGame.entities.Checkpoint;
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

    // --- Checkpoint 관련 필드 ---
    private Array<Checkpoint> checkpoints;
    private final float CHECKPOINT_INTERACT_DISTANCE = 1.5f;
    private Label interactPromptLabel;
    private Checkpoint currentInteractableCheckpoint; // 현재 상호작용 가능한 체크포인트 (근처에 있는 것)

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
        createCheckpoints();

        loadSkin();
        setupUI();

        gameStateManager = new GameStateManager(game, player, gameOverLabel);
        // 초기 스폰 위치 설정
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

    private void checkDamage(float delta) {
        if (!player.isAlive()) return;

        damageTimer += delta;

        if (damageTimer >= DAMAGE_CHECK_RATE) {
            com.badlogic.gdx.math.Rectangle playerBounds = player.getBounds();

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

    /**
     * 체크포인트 상호작용을 처리합니다. 이제 활성화된 체크포인트도 다시 설정 가능합니다.
     */
    private void checkCheckpointInteraction() {
        if (gameStateManager.isGameOver() || !player.isAlive()) {
            interactPromptLabel.setVisible(false);
            currentInteractableCheckpoint = null;
            return;
        }

        currentInteractableCheckpoint = null;
        Body playerBody = player.getBody();

        // 1. 플레이어 근처의 체크포인트를 찾습니다. (활성화 여부 무관)
        for (Checkpoint cp : checkpoints) {
            if (cp.isPlayerNear(
                playerBody.getPosition().x,
                playerBody.getPosition().y,
                CHECKPOINT_INTERACT_DISTANCE)) {

                currentInteractableCheckpoint = cp;
                break;
            }
        }

        // 2. 상호작용 프롬프트 표시 및 입력 처리
        if (currentInteractableCheckpoint != null) {
            interactPromptLabel.setVisible(true);

            // 현재 체크포인트가 이미 활성화된 상태인지 확인하여 텍스트를 조정합니다.
            if (currentInteractableCheckpoint.isActivated()) {
                interactPromptLabel.setText("Press C to Re-set Checkpoint");
            } else {
                interactPromptLabel.setText("Press C to Set Checkpoint");
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {

                // 3. 모든 체크포인트 비활성화
                for (Checkpoint cp : checkpoints) {
                    cp.deactivate();
                }

                // 4. 현재 체크포인트 활성화 및 GameStateManager 업데이트
                currentInteractableCheckpoint.activate();
                gameStateManager.setCheckpoint(
                    currentInteractableCheckpoint.getSpawnPosition().x,
                    currentInteractableCheckpoint.getSpawnPosition().y
                );

                // 설정 완료 메시지 출력
                interactPromptLabel.setText("Checkpoint Saved!");
            }
        } else {
            interactPromptLabel.setVisible(false);
            interactPromptLabel.setText("Press C to Set Checkpoint"); // 기본 텍스트로 리셋
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
            checkDamage(delta);
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

        // 데미지 박스 시각화
        shapeRenderer.setColor(1f, 0f, 0f, 0.5f);
        for (DamageBox box : damageBoxes) {
            com.badlogic.gdx.math.Rectangle rect = box.getBounds();
            shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
        }

        // 체크포인트 시각화
        for (Checkpoint cp : checkpoints) {
            com.badlogic.gdx.math.Rectangle rect = cp.getBounds();
            // 활성화 상태에 따라 색상 변경
            if (cp.isActivated()) {
                shapeRenderer.setColor(0.1f, 0.8f, 0.1f, 0.9f); // 녹색 (현재 활성화됨)
            } else {
                shapeRenderer.setColor(0.1f, 0.1f, 0.8f, 0.7f); // 파란색 (비활성화)
            }
            shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
        }

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // UI 렌더링
        stage.act(delta);
        stage.draw();

        // UI 위치 업데이트
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
