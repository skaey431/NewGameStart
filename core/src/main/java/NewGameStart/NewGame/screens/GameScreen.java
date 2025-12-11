package NewGameStart.NewGame.screens;

import NewGameStart.NewGame.Main;
import NewGameStart.NewGame.entities.DamageBox; // 데미지 박스 임포트
import NewGameStart.NewGame.entities.player.Player;
import NewGameStart.NewGame.screens.UI.HealthBar;
import NewGameStart.NewGame.world.WorldManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color; // 색상 임포트
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array; // Array 임포트
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameScreen implements Screen {

    private final Main game;
    private WorldManager worldManager;
    private Player player;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;

    // --- 데미지 박스 관련 필드 (복구) ---
    private Array<DamageBox> damageBoxes;
    private float damageTimer = 0f;
    private final float DAMAGE_CHECK_RATE = 0.5f; // 0.5초마다 데미지 확인
    // ----------------------------

    // UI 관련 필드
    private Stage stage;
    private Skin skin;
    private HealthBar healthBar;

    private static final float WORLD_WIDTH = 16f;
    private static final float WORLD_HEIGHT = 9f;

    public GameScreen(Main game) {
        this.game = game;

        // 초기화
        worldManager = new WorldManager();
        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        shapeRenderer = new ShapeRenderer();

        // UI Stage 초기화
        stage = new Stage(new ScreenViewport());

        createPlayer();
        worldManager.createDefaultStage();
        createDamageBoxes(); // 데미지 박스 초기화 (복구)

        // UI 초기화
        loadSkin();
        setupUI();
    }

    private void createPlayer() {
        // 플레이어를 (3f, 5f)에 생성
        player = new Player(worldManager.getWorld(), 3f, 5f);
    }

    /**
     * 데미지 박스 인스턴스를 생성하고 배열에 추가합니다. (복구)
     */
    private void createDamageBoxes() {
        damageBoxes = new Array<>();
        // 예시 데미지 박스 1: 용암 구역 (맵 좌표 x=10, y=1, 폭 3, 높이 0.5f, 0.5초마다 5 데미지)
        damageBoxes.add(new DamageBox(10f, 1f, 3f, 0.5f, 5f, DAMAGE_CHECK_RATE));
        // 예시 데미지 박스 2: 독성 가스 (맵 좌표 x=20, y=5, 폭 2, 높이 2, 0.5초마다 10 데미지)
        damageBoxes.add(new DamageBox(20f, 5f, 2f, 2f, 10f, DAMAGE_CHECK_RATE));
    }

    // Skin 로드 로직
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


    // HealthBar를 생성하고 Stage에 추가
    private void setupUI() {
        if (skin != null) {
            try {
                healthBar = new HealthBar(skin, player);
                healthBar.setPosition(20, Gdx.graphics.getHeight() - healthBar.getHeight() - 20);
                stage.addActor(healthBar);
            } catch (RuntimeException e) {
                Gdx.app.error("GameScreen", "Failed to setup HealthBar UI (ProgressBarStyle missing in skin JSON): " + e.getMessage());
                healthBar = null;
            }
        }
    }

    /**
     * 플레이어와 데미지 박스 간의 충돌을 확인하고 데미지를 적용합니다. (복구)
     */
    private void checkDamage(float delta) {
        if (!player.isAlive()) return;

        damageTimer += delta;

        // 지정된 데미지 간격이 되었을 때만 충돌을 확인하고 데미지를 적용합니다.
        if (damageTimer >= DAMAGE_CHECK_RATE) {

            // Box2D Body 기반의 플레이어 충돌 경계 (Player.getBounds() 사용)
            com.badlogic.gdx.math.Rectangle playerBounds = player.getBounds();

            for (DamageBox box : damageBoxes) {
                // 플레이어와 데미지 박스 경계가 겹치는지 확인
                if (playerBounds.overlaps(box.getBounds())) {

                    // 겹치는 경우, 플레이어에게 데미지 적용
                    player.takeDamage(box.getDamageAmount());
                    Gdx.app.log("GameScreen", "Player took " + box.getDamageAmount() +
                        " damage from DamageBox. Current Health: " + player.getCurrentHealth());

                    if (!player.isAlive()) {
                        Gdx.app.log("GameScreen", "Player has died!");
                        // TODO: 게임 오버 화면으로 전환 로직을 여기에 구현합니다.
                        break;
                    }
                }
            }

            // 데미지를 적용했으므로 타이머를 재설정합니다.
            // DAMAGE_CHECK_RATE를 넘긴 시간만큼만 남기고 리셋하여 정확도를 높입니다.
            damageTimer -= DAMAGE_CHECK_RATE;
        }
    }


    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        // 물리 업데이트
        worldManager.getWorld().step(delta, 6, 2);
        player.update(delta);

        // --- 데미지 체크 (복구) ---
        checkDamage(delta);
        // ------------------

        // 카메라 업데이트
        camera.position.x = player.getBody().getPosition().x;
        camera.position.y = player.getBody().getPosition().y;
        camera.update();

        // 화면 클리어
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 월드 렌더링 (디버그 렌더러)
        debugRenderer.render(worldManager.getWorld(), camera.combined);

        // --- 게임 오브젝트 렌더링 (ShapeRenderer) ---
        shapeRenderer.setProjectionMatrix(camera.combined);
        Gdx.gl.glEnable(GL20.GL_BLEND); // 반투명 렌더링 활성화
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // 1. 플레이어 시각화 (흰색)
        Body pb = player.getBody();
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(
            pb.getPosition().x - 0.3f,
            pb.getPosition().y - 0.5f,
            0.6f, 1.0f
        );

        // 2. 데미지 박스 시각화 (반투명 빨간색) (복구)
        shapeRenderer.setColor(1f, 0f, 0f, 0.5f); // 붉은색 반투명
        for (DamageBox box : damageBoxes) {
            com.badlogic.gdx.math.Rectangle rect = box.getBounds();
            shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
        }

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND); // 블렌딩 비활성화
        // --- END GAME OBJECT RENDERING ---

        // UI 렌더링
        stage.act(delta);
        stage.draw();

        // HealthBar 위치 업데이트
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
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    // 중요: 자원 해제
    @Override
    public void dispose() {
        worldManager.dispose();
        debugRenderer.dispose();
        shapeRenderer.dispose();

        if (skin != null) skin.dispose();
        if (stage != null) stage.dispose();
    }
}
