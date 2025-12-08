package NewGameStart.NewGame.screens;

import NewGameStart.NewGame.Main;
import NewGameStart.NewGame.entities.player.Player;
import NewGameStart.NewGame.ui.HealthBar;
import NewGameStart.NewGame.world.WorldManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameScreen implements Screen {

    private final Main game;
    private WorldManager worldManager;
    private Player player;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;

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

        // UI 초기화
        loadSkin();
        setupUI();
    }

    private void createPlayer() {
        player = new Player(worldManager.getWorld(), 3f, 5f);
    }

    // Skin 로드 로직: 여러 경로를 시도하고 실패 시 로그를 남김
    private void loadSkin() {
        // LibGDX 프로젝트에서 스킨 파일이 있을 수 있는 일반적인 경로
        String[] potentialPaths = {"skin/uiskin.json", "data/uiskin.json", "uiskin.json"};
        skin = null;

        for (String path : potentialPaths) {
            FileHandle skinFile = Gdx.files.internal(path);
            if (skinFile.exists()) {
                try {
                    skin = new Skin(skinFile);
                    Gdx.app.log("GameScreen", "UISkin loaded successfully from: " + path);
                    return; // 성공적으로 로드했으면 종료
                } catch (Exception e) {
                    Gdx.app.error("GameScreen", "Error loading skin from " + path + ". Check for JSON errors or missing assets: " + e.getMessage());
                }
            }
        }

        // 모든 경로 시도 실패
        Gdx.app.error("GameScreen", "FATAL: UISkin file not found or failed to load. Check file path in assets folder.");
    }


    // HealthBar를 생성하고 Stage에 추가
    private void setupUI() {
        // Skin이 로드된 경우에만 HealthBar 생성 시도
        if (skin != null) {
            try {
                // HealthBar는 내부적으로 스타일이 없으면 RuntimeException을 던집니다.
                healthBar = new HealthBar(skin, player);

                // HealthBar를 화면 좌측 상단에 배치
                healthBar.setPosition(20, Gdx.graphics.getHeight() - healthBar.getHeight() - 20);

                stage.addActor(healthBar);
            } catch (RuntimeException e) {
                // HealthBar 생성 중 스타일 누락 예외가 발생하면 여기서 잡고 로그를 남깁니다.
                Gdx.app.error("GameScreen", "Failed to setup HealthBar UI (ProgressBarStyle missing in skin JSON): " + e.getMessage());
                healthBar = null;
            }
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

        // 카메라 업데이트
        camera.position.x = player.getBody().getPosition().x;
        camera.position.y = player.getBody().getPosition().y;
        camera.update();

        // 화면 클리어
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 월드 렌더링
        debugRenderer.render(worldManager.getWorld(), camera.combined);

        // 플레이어 시각화
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        Body pb = player.getBody();
        shapeRenderer.rect(
            pb.getPosition().x - 0.3f,
            pb.getPosition().y - 0.5f,
            0.6f, 1.0f
        );

        shapeRenderer.end();

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
