package NewGameStart.NewGame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Player {

    private Body body;

    public boolean isOnGround = false;
    public boolean isTouchingLeft = false;
    public boolean isTouchingRight = false;
    public boolean isTouchingCeiling = false;

    public boolean isClinging = false;

    private final float MOVE_SPEED = 5f;
    private final float CLIMB_SPEED = 4f;
    private final float JUMP_FORCE = 3f;

    public Player(World world, float x, float y) {

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(x, y);
        body = world.createBody(def);
        body.setUserData(this);
        body.setFixedRotation(true);

        // 몸체
        PolygonShape main = new PolygonShape();
        main.setAsBox(0.3f, 0.5f);

        FixtureDef fd = new FixtureDef();
        fd.shape = main;
        fd.density = 1f;
        fd.friction = 0.3f;
        Fixture fx = body.createFixture(fd);
        fx.setUserData("player");
        main.dispose();

        // FOOT SENSOR
        PolygonShape foot = new PolygonShape();
        foot.setAsBox(0.25f, 0.05f, new Vector2(0, -0.55f), 0);
        FixtureDef footFd = new FixtureDef();
        footFd.shape = foot;
        footFd.isSensor = true;
        body.createFixture(footFd).setUserData("foot");
        foot.dispose();

        // HEAD SENSOR
        PolygonShape head = new PolygonShape();
        head.setAsBox(0.25f, 0.05f, new Vector2(0, 0.55f), 0);
        FixtureDef headFd = new FixtureDef();
        headFd.shape = head;
        headFd.isSensor = true;
        body.createFixture(headFd).setUserData("head");
        head.dispose();

        // LEFT SENSOR
        PolygonShape left = new PolygonShape();
        left.setAsBox(0.05f, 0.4f, new Vector2(-0.35f, 0), 0);
        FixtureDef leftFd = new FixtureDef();
        leftFd.shape = left;
        leftFd.isSensor = true;
        body.createFixture(leftFd).setUserData("left");
        left.dispose();

        // RIGHT SENSOR
        PolygonShape right = new PolygonShape();
        right.setAsBox(0.05f, 0.4f, new Vector2(0.35f, 0), 0);
        FixtureDef rightFd = new FixtureDef();
        rightFd.shape = right;
        rightFd.isSensor = true;
        body.createFixture(rightFd).setUserData("right");
        right.dispose();
    }


    public void update(float delta) {

        boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean up = Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean down = Gdx.input.isKeyPressed(Input.Keys.DOWN);
        Vector2 vel = body.getLinearVelocity();

        // --- 클링 시작 조건 ---
        if ((isTouchingLeft || isTouchingRight || isTouchingCeiling) &&
            (left || right || up || down)) {
            isClinging = true;
            body.setGravityScale(0);
        }

        // --- 클링 종료 ---
        if (!isTouchingLeft && !isTouchingRight && !isTouchingCeiling) {
            isClinging = false;
            body.setGravityScale(1);
        }

        // --- 클링 이동 ---
        if (isClinging) {
            float vx = 0;
            float vy = 0;

            if (left)  vx = -CLIMB_SPEED;
            if (right) vx =  CLIMB_SPEED;
            if (up)    vy =  CLIMB_SPEED;
            if (down)  vy = -CLIMB_SPEED;

            body.setLinearVelocity(vx, vy);
            return;
        }

        // 일반 이동
        if (left) body.setLinearVelocity(-MOVE_SPEED, vel.y);
        else if (right) body.setLinearVelocity(MOVE_SPEED, vel.y);
        else body.setLinearVelocity(0, vel.y);

        // 점프 = Space
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && isOnGround) {
            body.applyLinearImpulse(new Vector2(0, JUMP_FORCE), body.getWorldCenter(), true);
        }
    }

    public Body getBody() { return body; }
}
