package NewGameStart.NewGame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import NewGameStart.NewGame.Main;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.graphics.GL20;

public class GameScreen implements Screen {
    private final Main game;
    private ShapeRenderer shapeRenderer;

    private World world; // Box2D world
    private Body playerBody; // 플레이어 바디
    private Body groundBody; // 바닥 Body
    private Body leftWallBody; // 왼쪽 벽 Body
    private Body rightWallBody; // 오른쪽 벽 Body
    private Body topWallBody; // 위쪽 벽 Body

    public GameScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        shapeRenderer = new ShapeRenderer();

        // Box2D 월드 설정
        world = new World(new Vector2(0, -9.8f), true); // 중력 설정 (y축 방향으로 -9.8m/s^2)

        // 플레이어, 바닥, 벽 생성
        createPlayer();
        createGround();
        createWalls();
    }

    private void createPlayer() {
        // 플레이어 바디 정의
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody; // 동적 바디
        bodyDef.position.set(100, 100); // 초기 위치

        // 바디 생성
        playerBody = world.createBody(bodyDef);

        // 플레이어 모양 정의 (사각형)
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.3f;

        // 사각형 모양 설정
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(32, 32); // 크기 설정 (32x32)

        fixtureDef.shape = shape;

        // Fixture 추가
        playerBody.createFixture(fixtureDef);
    }

    private void createGround() {
        // 바닥 바디 정의
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody; // StaticBody
        bodyDef.position.set(0, 0); // 바닥 위치

        groundBody = world.createBody(bodyDef);

        // 바닥의 형태
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 0;
        fixtureDef.friction = 0.5f;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(400, 20); // 크기 (400x20)

        fixtureDef.shape = shape;
        groundBody.createFixture(fixtureDef);
    }

    private void createWalls() {
        // 왼쪽 벽
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(-400, 0);

        leftWallBody = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 0;
        fixtureDef.friction = 0.5f;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(20, 300); // 벽 크기 (20x300)

        fixtureDef.shape = shape;
        leftWallBody.createFixture(fixtureDef);

        // 오른쪽 벽
        bodyDef.position.set(400, 0);
        rightWallBody = world.createBody(bodyDef);
        rightWallBody.createFixture(fixtureDef);

        // 위쪽 벽
        bodyDef.position.set(0, 300);
        topWallBody = world.createBody(bodyDef);
        shape.setAsBox(400, 20); // 위쪽 벽 크기 (400x20)
        topWallBody.createFixture(fixtureDef);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 물리 엔진 업데이트 (delta값을 사용하여 프레임에 맞게 업데이트)
        world.step(delta, 6, 2);

        // 플레이어 입력 처리
        handleInput();

        // ShapeRenderer로 그리기
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // 플레이어 그리기 (사각형)
        shapeRenderer.setColor(Color.RED); // 플레이어 색상
        shapeRenderer.rect(playerBody.getPosition().x - 32, playerBody.getPosition().y - 32, 64, 64);

        // 바닥 그리기
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(groundBody.getPosition().x - 400, groundBody.getPosition().y - 10, 800, 20);

        // 왼쪽 벽 그리기
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rect(leftWallBody.getPosition().x - 10, leftWallBody.getPosition().y - 150, 20, 300);

        // 오른쪽 벽 그리기
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rect(rightWallBody.getPosition().x - 10, rightWallBody.getPosition().y - 150, 20, 300);

        // 위쪽 벽 그리기
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rect(topWallBody.getPosition().x - 400, topWallBody.getPosition().y - 10, 800, 20);

        shapeRenderer.end(); // ShapeRenderer 끝
    }

    private void handleInput() {
        float speed = 500f; // 속도

        // 방향키 입력 처리
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            // 왼쪽 방향으로 속도 설정
            playerBody.setLinearVelocity(-speed, playerBody.getLinearVelocity().y);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            // 오른쪽 방향으로 속도 설정
            playerBody.setLinearVelocity(speed, playerBody.getLinearVelocity().y);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            // 위쪽 방향으로 속도 설정
            playerBody.setLinearVelocity(playerBody.getLinearVelocity().x, speed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            // 아래쪽 방향으로 속도 설정
            playerBody.setLinearVelocity(playerBody.getLinearVelocity().x, -speed);
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        shapeRenderer.dispose(); // ShapeRenderer 리소스 해제
        world.dispose(); // 월드 리소스 해제
    }
}
