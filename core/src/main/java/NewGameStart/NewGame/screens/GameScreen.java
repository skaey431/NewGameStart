package NewGameStart.NewGame.screens;

import NewGameStart.NewGame.Main;
import NewGameStart.NewGame.entities.Player;
import NewGameStart.NewGame.entities.SpecialWall;
import NewGameStart.NewGame.entities.NormalWall;
import NewGameStart.NewGame.world.WorldManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.*;

public class GameScreen implements Screen {

    private final Main game;

    private WorldManager worldManager;
    private Player player;

    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;

    private ShapeRenderer shapeRenderer;

    private SpecialWall specialWall;
    private NormalWall wall;

    public GameScreen(Main game) {
        this.game = game;

        worldManager = new WorldManager();
        debugRenderer = new Box2DDebugRenderer();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 16, 9);

        shapeRenderer = new ShapeRenderer();

        createPlayer();
        createSpecialWall();
    }

    private void createPlayer() {
        player = new Player(worldManager.getWorld(), 0, 2);
    }

    private void createSpecialWall() {
        specialWall = new SpecialWall(worldManager.getWorld(), 8, 2, 10f, 1f);
        wall = new NormalWall(worldManager.getWorld(),0, 0, 30f, 1f);
        wall = new NormalWall(worldManager.getWorld(),15, 0, 1f, 30f);
    }

    @Override
    public void render(float delta) {

        worldManager.update();
        player.update(delta);

        camera.update();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        debugRenderer.render(worldManager.getWorld(), camera.combined);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        Body pb = player.getBody();
        shapeRenderer.rect(
            pb.getPosition().x - 0.3f,
            pb.getPosition().y - 0.5f,
            0.6f, 1.0f
        );

        shapeRenderer.end();
    }

    @Override public void dispose() {
        worldManager.dispose();
        debugRenderer.dispose();
        shapeRenderer.dispose();
    }

    @Override public void show() {}
    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
