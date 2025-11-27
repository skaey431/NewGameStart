package NewGameStart.NewGame.screens;

import NewGameStart.NewGame.Constants;
import NewGameStart.NewGame.entities.Ground;
import NewGameStart.NewGame.entities.Player;
import NewGameStart.NewGame.entities.Wall;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.Game;

import NewGameStart.NewGame.world.WorldManager;

public class GameScreen implements Screen {

    private Game game;
    private WorldManager worldManager;
    private Player player;
    private Wall wall;
    private Ground ground;

    private OrthographicCamera camera;
    private Box2DDebugRenderer debugRenderer;

    public GameScreen(Game game) {
        this.game = game;

        worldManager = new WorldManager();
        debugRenderer = new Box2DDebugRenderer();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280 / Constants.PPM, 720 / Constants.PPM);

        // 바닥
        ground = new Ground(worldManager.getWorld(), 6, 0.5f, 12, 1);

        // 플레이어
        player = new Player(worldManager.getWorld(), 2, 3);

        // 벽
        wall = new Wall(worldManager.getWorld(), 6, 1, 1, 2);
    }

    @Override
    public void render(float delta) {

        handleInput();

        // 자동 기립 처리
        player.update();

        // 물리 업데이트
        worldManager.update();

        camera.update();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        debugRenderer.render(worldManager.getWorld(), camera.combined);
    }

    private void handleInput() {

        // 이동
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.moveLeft();
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.moveRight();
        } else {
            player.getBody().setLinearVelocity(0, player.getBody().getLinearVelocity().y);
        }

        // 점프
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            player.jump();
        }

        // ★ UP 키 한 번 → 자동 기립 시작
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            player.startRecovering();
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void dispose() {
        worldManager.dispose();
        debugRenderer.dispose();
    }
}
