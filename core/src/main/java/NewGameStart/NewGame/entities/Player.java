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

    // ⭐ 쿨다운 시간 0.3초로 증가
    private float wallJumpTimer = 0f;
    private final float WALL_JUMP_COOLDOWN = 0.3f;

    private final float MOVE_SPEED = 5f;
    private final float JUMP_FORCE = 3.5f;
    private final float WALL_JUMP_HORIZONTAL = 4.0f;
    private final float WALL_JUMP_VERTICAL = 5.5f;

    public Player(World world, float x, float y) {

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(x, y);
        body = world.createBody(def);
        body.setUserData(this);
        body.setFixedRotation(true);

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
        boolean space = Gdx.input.isKeyJustPressed(Input.Keys.SPACE);

        Vector2 vel = body.getLinearVelocity();

        boolean isOnGround = footContacts > 0;
        boolean isTouchingLeft = leftContacts > 0;
        boolean isTouchingRight = rightContacts > 0;
        boolean isTouchingCeiling = headContacts > 0;

        // 1. 타이머 업데이트
        if (wallJumpTimer > 0) {
            wallJumpTimer -= delta;
        }

        // --- 클링 시작/종료 로직 (⭐ isTouchingCeiling 제거) ---
        boolean canCling = wallJumpTimer <= 0;

        // 땅이 아니고, 쿨다운 중이 아니며, (좌측 벽에 닿고 좌측 키를 누르거나 || 우측 벽에 닿고 우측 키를 누를 때)
        if (!isOnGround && canCling && (isTouchingLeft && left || isTouchingRight && right)) {
            isClinging = true;
            body.setGravityScale(0);
            body.setLinearVelocity(0, 0);
        } else {
            isClinging = false;
            body.setGravityScale(1);
        }

        // --- 클링 이동 (벽 점프) ---
        if (isClinging) {

            if (space) {
                isClinging = false;
                body.setGravityScale(1);

                float jumpX = 0;
                float jumpY = WALL_JUMP_VERTICAL;

                if (isTouchingLeft) {
                    jumpX = WALL_JUMP_HORIZONTAL;
                } else if (isTouchingRight) {
                    jumpX = -WALL_JUMP_HORIZONTAL;
                }

                body.setLinearVelocity(jumpX, jumpY);

                // 2. 벽 점프 직후 타이머 시작
                wallJumpTimer = WALL_JUMP_COOLDOWN;
            }
            return;
        }

        // --- 일반 이동 (벽 점프 쿨다운 중 입력 무시) ---
        float targetVelocityX = 0;

        if (wallJumpTimer > 0) {
            // 쿨다운 중: 현재 속도(vel.x)를 유지하여 관성만 적용
            targetVelocityX = vel.x;
        } else {
            // 쿨다운 해제: 정상적인 이동 로직
            if (left) {
                // 왼쪽 이동: 왼쪽 벽에 닿았다면 속도 0, 아니면 MOVE_SPEED
                targetVelocityX = isTouchingLeft ? 0 : -MOVE_SPEED;
            } else if (right) {
                // 오른쪽 이동: 오른쪽 벽에 닿았다면 속도 0, 아니면 MOVE_SPEED
                targetVelocityX = isTouchingRight ? 0 : MOVE_SPEED;
            } else {
                // 입력 없음: 지상이라면 멈추고, 공중이라면 관성 유지
                targetVelocityX = isOnGround ? 0 : vel.x;
            }
        }

        body.setLinearVelocity(targetVelocityX, vel.y);

        // 점프 = Space (일반 지상 점프)
        if (space && isOnGround) {
            body.applyLinearImpulse(new Vector2(0, JUMP_FORCE), body.getWorldCenter(), true);
        }
    }

    // --- Contact Methods (이전과 동일) ---
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
