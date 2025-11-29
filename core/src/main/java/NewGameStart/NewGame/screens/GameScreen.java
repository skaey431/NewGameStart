package NewGameStart.NewGame.screens;

import NewGameStart.NewGame.Main;

import NewGameStart.NewGame.entities.Player;
import NewGameStart.NewGame.world.WorldManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class GameScreen implements Screen {

    private final Main game;

    private WorldManager worldManager;
    private Player player;

    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;

    private ShapeRenderer shapeRenderer;

    public GameScreen(Main game) {
        this.game = game;

        worldManager = new WorldManager();
        debugRenderer = new Box2DDebugRenderer();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 16, 9);

        shapeRenderer = new ShapeRenderer();

        createPlayer();
        createGround();
        createTestWall();
        createCeiling();
    }

    private void createPlayer() {
        player = new Player(worldManager.getWorld(), 2, 2);
    }

    private void createGround() {
        World world = worldManager.getWorld();

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        def.position.set(0, 0);

        Body body = world.createBody(def);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(20f, 0.5f);

        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.friction = 1f;

        body.createFixture(fd);
        shape.dispose();
    }

    private void createTestWall() {
        World world = worldManager.getWorld();

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        def.position.set(5, 0);

        Body body = world.createBody(def);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 2f);

        FixtureDef fd = new FixtureDef();
        fd.shape = shape;

        body.createFixture(fd);
        shape.dispose();
    }

    private void createCeiling() {
        World world = worldManager.getWorld();

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        def.position.set(0, 9);

        Body body = world.createBody(def);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(20f, 0.3f); // 천장

        FixtureDef fd = new FixtureDef();
        fd.shape = shape;

        body.createFixture(fd);
        shape.dispose();
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
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
