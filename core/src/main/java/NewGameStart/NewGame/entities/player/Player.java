package NewGameStart.NewGame.entities.player;

import NewGameStart.NewGame.entities.BaseEntity;
import NewGameStart.NewGame.tools.Constants;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Player extends BaseEntity {

    private Body body;
    private int footContacts = 0;
    private int leftContacts = 0;
    private int rightContacts = 0;
    private int headContacts = 0;

    private PlayerState currentState; // PlayerState 인터페이스와 상태 클래스가 존재한다고 가정합니다.

    public boolean isClinging = false;
    public boolean isDashing = false;

    public float wallJumpTimer = 0f;
    public final float WALL_JUMP_COOLDOWN = 0.4f;

    public float dashCooldownTimer = 0f;
    public final float DASH_COOLDOWN = 0.5f;

    public final int MAX_DASHES_AIR = 1;
    public int dashesPerformed = 0;

    public final int MAX_JUMPS = 1;
    public int jumpsPerformed = 0;

    public final float MOVE_SPEED = 5f;
    public final float JUMP_FORCE = 3.5f;
    public final float WALL_JUMP_HORIZONTAL = 3f;
    public final float WALL_JUMP_VERTICAL = 8f;

    private final com.badlogic.gdx.math.Rectangle tempBounds = new com.badlogic.gdx.math.Rectangle();

    public Player(World world, float x, float y) {
        super();

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
        body.createFixture(fd).setUserData("player_main");
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

        this.currentState = new PlayerRunState(); // PlayerRunState가 존재한다고 가정합니다.
        this.currentState.enter(this);
    }

    public void changeState(PlayerState newState) {
        if (currentState != null) {
            currentState.exit(this);
        }
        this.currentState = newState;
        newState.enter(this);
    }

    @Override
    public void update(float delta) {
        if (!isAlive()) {
            return;
        }

        if (wallJumpTimer > 0) wallJumpTimer -= delta;
        if (dashCooldownTimer > 0) dashCooldownTimer -= delta;

        if (isOnGround()) {
            dashesPerformed = 0;
            jumpsPerformed = 0;
        }

        boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean dashInput = Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_RIGHT);

        if (dashInput && dashCooldownTimer <= 0 && (left || right) && !(currentState instanceof PlayerDashState)) { // PlayerDashState 존재 가정
            if (!isOnGround() && dashesPerformed >= MAX_DASHES_AIR) {
                return;
            }

            changeState(new PlayerDashState());
            return;
        }

        currentState.update(this, delta);

        if (!isDashing) {
            if (!isOnGround() && !(currentState instanceof PlayerJumpState) && !(currentState instanceof PlayerClingState)) { // PlayerJumpState, PlayerClingState 존재 가정
                changeState(new PlayerJumpState());
                return;
            }
        }
    }

    /**
     * 리스폰 시 호출되어 플레이어의 모든 능력 상태를 초기화합니다.
     */
    public void resetAbilities() {
        this.dashesPerformed = 0;
        this.jumpsPerformed = 0;
        this.wallJumpTimer = 0f;
        this.dashCooldownTimer = 0f;
        this.isClinging = false;
        this.isDashing = false;
        this.changeState(new PlayerIdleState()); // PlayerIdleState 존재 가정
    }

    /**
     * 플레이어의 체력을 최대치로 회복시킵니다.
     */
    public void resetHealth() {
        // BaseEntity의 heal() 또는 직접 필드에 접근하여 초기화
        this.currentHealth = getMaxHealth();
        this.isAlive = true; // BaseEntity의 die()로 인해 false가 되었을 수 있으므로 복구
    }

    // BaseEntity의 takeDamage(float damage) 메서드를 사용합니다.

    public com.badlogic.gdx.math.Rectangle getBounds() {
        Vector2 position = body.getPosition();
        float width = 0.6f;
        float height = 1.0f;

        tempBounds.set(
            position.x - width / 2f,
            position.y - height / 2f,
            width,
            height
        );
        return tempBounds;
    }

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
