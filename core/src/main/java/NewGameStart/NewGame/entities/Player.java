package NewGameStart.NewGame.entities;

import NewGameStart.NewGame.tools.Constants;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Player {

    private Body body;
    private int footContacts = 0;
    private int leftContacts = 0;
    private int rightContacts = 0;
    private int headContacts = 0;

    public boolean isClinging = false;

    private final float MOVE_SPEED = 5f;
    private final float CLIMB_SPEED = 4f;
    private final float JUMP_FORCE = 3f;

    public Player(World world, float x, float y) {
        // ... (생성자 및 FixtureDef 코드는 이전 답변과 동일) ...

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(x, y);
        body = world.createBody(def);
        body.setUserData(this);
        body.setFixedRotation(true);

        // --- Fixtures (몸체 및 센서) 생성 및 충돌 필터링 설정 ---

        // 1. 몸체 Fixture
        PolygonShape main = new PolygonShape();
        main.setAsBox(0.3f, 0.5f);
        FixtureDef fd = new FixtureDef();
        fd.shape = main;
        fd.density = 1f;
        fd.friction = 0.3f;
        fd.filter.categoryBits = Constants.CATEGORY_PLAYER;
        fd.filter.maskBits = Constants.MASK_PLAYER;
        body.createFixture(fd).setUserData("player");
        main.dispose();

        // 2. 센서 Fixture (foot, head, left, right)
        FixtureDef sensorFd = new FixtureDef();
        sensorFd.isSensor = true;
        sensorFd.filter.categoryBits = Constants.CATEGORY_PLAYER;
        sensorFd.filter.maskBits = Constants.MASK_PLAYER;

        // FOOT SENSOR
        PolygonShape foot = new PolygonShape();
        foot.setAsBox(0.25f, 0.05f, new Vector2(0, -0.55f), 0);
        sensorFd.shape = foot;
        body.createFixture(sensorFd).setUserData("foot");
        foot.dispose();

        // HEAD SENSOR
        PolygonShape head = new PolygonShape();
        head.setAsBox(0.25f, 0.05f, new Vector2(0, 0.55f), 0);
        sensorFd.shape = head;
        body.createFixture(sensorFd).setUserData("head");
        head.dispose();

        // LEFT SENSOR
        PolygonShape leftSensor = new PolygonShape();
        leftSensor.setAsBox(0.05f, 0.4f, new Vector2(-0.35f, 0), 0);
        sensorFd.shape = leftSensor;
        body.createFixture(sensorFd).setUserData("left");
        leftSensor.dispose();

        // RIGHT SENSOR
        PolygonShape rightSensor = new PolygonShape();
        rightSensor.setAsBox(0.05f, 0.4f, new Vector2(0.35f, 0), 0);
        sensorFd.shape = rightSensor;
        body.createFixture(sensorFd).setUserData("right");
        rightSensor.dispose();
    }

    public void update(float delta) {

        boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean up = Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean down = Gdx.input.isKeyPressed(Input.Keys.DOWN);
        Vector2 vel = body.getLinearVelocity();

        boolean isOnGround = footContacts > 0;
        boolean isTouchingLeft = leftContacts > 0;
        boolean isTouchingRight = rightContacts > 0;
        boolean isTouchingCeiling = headContacts > 0;

        // --- 클링 시작/종료 로직 (수정된 부분) ---
        // ⭐ 핵심 수정: !isOnGround 조건을 추가하여 땅에 있을 때 클링 방지
        if (!isOnGround && (isTouchingLeft && left || isTouchingRight && right || isTouchingCeiling)) {
            isClinging = true;
            body.setGravityScale(0);
        } else {
            isClinging = false;
            body.setGravityScale(1);
        }

        // --- 클링 이동 ---
        if (isClinging) {
            float vx = 0;
            float vy = 0;

            if (left) vx = -CLIMB_SPEED;
            if (right) vx = CLIMB_SPEED;
            if (up) vy = CLIMB_SPEED;
            if (down) vy = -CLIMB_SPEED;

            body.setLinearVelocity(vx, vy);
            return; // 클링 중에는 아래 일반 이동 코드를 실행하지 않음
        }

        // --- 일반 이동 (클링 상태가 아닐 때만 실행됨) ---
        // 이 코드가 이제 클링 상태가 아닐 때 정상적으로 실행되어야 합니다.
        if (left) body.setLinearVelocity(-MOVE_SPEED, vel.y);
        else if (right) body.setLinearVelocity(MOVE_SPEED, vel.y);
        else body.setLinearVelocity(0, vel.y); // 키를 떼면 수평 속도를 0으로 만듦 (일반적인 플랫폼 게임 동작)

        // 점프 = Space
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && isOnGround) {
            body.applyLinearImpulse(new Vector2(0, JUMP_FORCE), body.getWorldCenter(), true);
        }
    }

    // ... (incrementContact, decrementContact 등 나머지 메서드 원본과 동일) ...
    public Body getBody() { return body; }
    public void incrementContact(String sensor) {
        switch (sensor) {
            case "foot": footContacts++; break;
            case "left": leftContacts++; break;
            case "right": rightContacts++; break;
            case "head": headContacts++; break;
        }
    }
    public void decrementContact(String sensor) {
        switch (sensor) {
            case "foot": footContacts = Math.max(0, footContacts - 1); break;
            case "left": leftContacts = Math.max(0, leftContacts - 1); break;
            case "right": rightContacts = Math.max(0, rightContacts - 1); break;
            case "head": headContacts = Math.max(0, headContacts - 1); break;
        }
    }
}
