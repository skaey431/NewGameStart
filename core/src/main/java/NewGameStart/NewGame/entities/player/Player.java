package NewGameStart.NewGame.entities.player;

import NewGameStart.NewGame.entities.player.PlayerClingState;
import NewGameStart.NewGame.entities.player.PlayerDashState;
import NewGameStart.NewGame.entities.player.PlayerState;
import NewGameStart.NewGame.entities.player.PlayerRunState;
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

    private PlayerState currentState;

    // ⭐ 대쉬 및 벽 점프 관련 필드
    public boolean isClinging = false;
    public boolean isDashing = false;

    public float wallJumpTimer = 0f;
    public final float WALL_JUMP_COOLDOWN = 0.4f;

    public float dashCooldownTimer = 0f;
    public final float DASH_COOLDOWN = 0.5f;

    // ⭐ 더블 점프 관련 필드 추가
    public final int MAX_JUMPS = 2;
    public int jumpsPerformed = 0;

    // ⭐ 상수
    public final float MOVE_SPEED = 5f;
    public final float JUMP_FORCE = 3.5f;
    public final float WALL_JUMP_HORIZONTAL = 5.5f;
    public final float WALL_JUMP_VERTICAL = 5.5f;

    public Player(World world, float x, float y) {
        // ... (Body 및 Fixture 생성 코드는 이전과 동일) ...
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

        PolygonShape foot = new PolygonShape();
        foot.setAsBox(0.25f, 0.05f, new Vector2(0, -0.55f), 0);
        sensorFd.shape = foot;
        body.createFixture(sensorFd).setUserData("foot");
        foot.dispose();

        PolygonShape head = new PolygonShape();
        head.setAsBox(0.25f, 0.05f, new Vector2(0, 0.55f), 0);
        sensorFd.shape = head;
        body.createFixture(sensorFd).setUserData("head");
        head.dispose();

        PolygonShape leftSensor = new PolygonShape();
        leftSensor.setAsBox(0.05f, 0.4f, new Vector2(-0.35f, 0), 0);
        sensorFd.shape = leftSensor;
        body.createFixture(sensorFd).setUserData("left");
        leftSensor.dispose();

        PolygonShape rightSensor = new PolygonShape();
        rightSensor.setAsBox(0.05f, 0.4f, new Vector2(0.35f, 0), 0);
        sensorFd.shape = rightSensor;
        body.createFixture(sensorFd).setUserData("right");
        rightSensor.dispose();

        this.currentState = new PlayerRunState();
        this.currentState.enter(this);
    }

    // ⭐ 상태 전환 메서드 (핵심)
    public void changeState(PlayerState newState) {
        if (currentState != null) {
            currentState.exit(this);
        }
        this.currentState = newState;
        newState.enter(this);
    }

    public void update(float delta) {

        // ⭐ 타이머 업데이트
        if (wallJumpTimer > 0) wallJumpTimer -= delta;
        if (dashCooldownTimer > 0) dashCooldownTimer -= delta;

        // ⭐ 대쉬 입력 우선 확인 (모든 상태보다 우선)
        boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean dashInput = Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_RIGHT);

        if (dashInput && dashCooldownTimer <= 0 && (left || right) && !(currentState instanceof PlayerDashState)) {
            changeState(new PlayerDashState());
            return;
        }

        currentState.update(this, delta);

        // ⭐ 공중/벽 상태 확인 및 전환 (모든 update 후 실행되어야 함)
        if (!isDashing) {
            if (!isOnGround() && !isClinging && wallJumpTimer <= 0 && (isTouchingLeft() && left || isTouchingRight() && right)) {
                changeState(new PlayerClingState());
                return;
            }
        }
    }

    // --- Contact Methods (이전과 동일) ---
    public Body getBody() { return body; }
    public boolean isOnGround() { return footContacts > 0; }
    public boolean isTouchingLeft() { return leftContacts > 0; }
    public boolean isTouchingRight() { return rightContacts > 0; }
    public boolean isTouchingCeiling() { return headContacts > 0; }

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
