package NewGameStart.NewGame.screens;

import NewGameStart.NewGame.Main;
import NewGameStart.NewGame.entities.Player;
import NewGameStart.NewGame.world.WorldManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class GameScreen implements Screen {

    private final Main game;
    private WorldManager worldManager;
    private Player player;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private Stage stage;

    private static final float WORLD_WIDTH = 16f;
    private static final float WORLD_HEIGHT = 9f;

    public GameScreen(Main game) {
        this.game = game;

        // 초기화 (원본과 동일)
        worldManager = new WorldManager();
        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        shapeRenderer = new ShapeRenderer();
        stage = new Stage(); // Stage 초기화 추가

        createPlayer();
        worldManager.createDefaultStage(); // 스테이지 생성
    }

    private void createPlayer() {
        // 플레이어는 WorldManager가 아닌, GameScreen에서 직접 생성하고 WorldManager의 World에 추가
        player = new Player(worldManager.getWorld(), 3f, 5f);
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

        // ⭐ 수정: 카메라가 플레이어를 따라가도록 위치를 조정합니다.
        camera.position.x = player.getBody().getPosition().x;
        camera.position.y = player.getBody().getPosition().y;
        // 카메라가 월드 경계를 넘지 않도록 제한하는 로직은 필요시 추가

        camera.update();

        // 화면 클리어
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 디버그 렌더링
        debugRenderer.render(worldManager.getWorld(), camera.combined);

        // 플레이어 시각화 (원본과 동일)
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        Body pb = player.getBody();
        shapeRenderer.rect(
            pb.getPosition().x - 0.3f,
            pb.getPosition().y - 0.5f,
            0.6f, 1.0f
        );

        shapeRenderer.end();

        stage.act(delta);
        stage.draw();
    }

    // ⭐ 중요: 자원 해제 (원본과 동일하게 올바르게 처리)
    @Override
    public void dispose() {
        worldManager.dispose(); // World 해제
        debugRenderer.dispose();
        shapeRenderer.dispose();
        stage.dispose();
    }

    @Override public void resize(int width, int height) { camera.update(); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
