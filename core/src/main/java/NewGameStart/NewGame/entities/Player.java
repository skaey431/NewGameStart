package NewGameStart.NewGame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Player {

    private Body body;

    public boolean isOnGround = false;
    public boolean isTouchingWall = false;
    public boolean isTouchingCeiling = false;

    public boolean isClinging = false;

    private final float MOVE_SPEED = 5f;
    private final float JUMP_FORCE = 9f;

    public Player(World world, float x, float y) {

        // 본체 생성
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(x, y);

        body = world.createBody(def);
        body.setUserData(this);

        body.setFixedRotation(true);   // ✔ 회전 금지

        // --- 몸체 ---
        PolygonShape main = new PolygonShape();
        main.setAsBox(0.3f, 0.5f);

        FixtureDef fd = new FixtureDef();
        fd.shape = main;
        fd.density = 1f;
        fd.friction = 0.3f;

        Fixture mainFix = body.createFixture(fd);
        mainFix.setUserData("player");
        main.dispose();

        // --- foot ---
        PolygonShape foot = new PolygonShape();
        foot.setAsBox(0.25f, 0.05f, new Vector2(0, -0.55f), 0);

        FixtureDef footFd = new FixtureDef();
        footFd.shape = foot;
        footFd.isSensor = true;

        Fixture footFx = body.createFixture(footFd);
        footFx.setUserData("foot");
        foot.dispose();

        // --- head ---
        PolygonShape head = new PolygonShape();
        head.setAsBox(0.25f, 0.05f, new Vector2(0, 0.55f), 0);

        FixtureDef headFd = new FixtureDef();
        headFd.shape = head;
        headFd.isSensor = true;

        Fixture headFx = body.createFixture(headFd);
        headFx.setUserData("head");
        head.dispose();
    }

    public void update(float delta) {

        boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean up = Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean down = Gdx.input.isKeyPressed(Input.Keys.DOWN);

        Vector2 vel = body.getLinearVelocity();

        // 클링 시작 조건
        if ((isTouchingWall && (left || right || up || down))
            || (isTouchingCeiling && up)) {

            isClinging = true;
            body.setGravityScale(0);
        }

        // 클링 해제 조건
        if (!isTouchingWall && !isTouchingCeiling) {
            isClinging = false;
            body.setGravityScale(1);
        }

        // 클링 중 이동
        if (isClinging) {
            float vx = 0, vy = 0;

            if (left) vx = -MOVE_SPEED;
            if (right) vx = MOVE_SPEED;
            if (up) vy = MOVE_SPEED;
            if (down) vy = -MOVE_SPEED;

            body.setLinearVelocity(vx, vy);
            return;
        }

        // 일반 이동
        if (left) body.setLinearVelocity(-MOVE_SPEED, vel.y);
        else if (right) body.setLinearVelocity(MOVE_SPEED, vel.y);
        else body.setLinearVelocity(0, vel.y);

        // 점프
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && isOnGround) {
            body.applyLinearImpulse(new Vector2(0, JUMP_FORCE), body.getWorldCenter(), true);
        }
    }

    public Body getBody() { return body; }
}
