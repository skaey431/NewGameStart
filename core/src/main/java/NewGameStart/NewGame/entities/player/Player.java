package NewGameStart.NewGame.entities.player;

import NewGameStart.NewGame.entities.BaseEntity;
import NewGameStart.NewGame.entities.player.PlayerClingState;
import NewGameStart.NewGame.entities.player.PlayerDashState;
import NewGameStart.NewGame.entities.player.PlayerIdleState;
import NewGameStart.NewGame.entities.player.PlayerJumpState;
import NewGameStart.NewGame.entities.player.PlayerRunState;
import NewGameStart.NewGame.entities.player.PlayerState;
import NewGameStart.NewGame.tools.Constants;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

// BaseEntity를 상속받아 체력 시스템을 사용합니다.
public class Player extends BaseEntity {

    private Body body;
    private int footContacts = 0;
    private int leftContacts = 0;
    private int rightContacts = 0;
    private int headContacts = 0;

    private PlayerState currentState;

    // 대쉬 및 벽 점프 관련 필드
    public boolean isClinging = false;
    public boolean isDashing = false;

    public float wallJumpTimer = 0f;
    public final float WALL_JUMP_COOLDOWN = 0.4f;

    public float dashCooldownTimer = 0f;
    public final float DASH_COOLDOWN = 0.5f;

    // 공중 대쉬 제한을 위한 필드
    public final int MAX_DASHES_AIR = 1;
    public int dashesPerformed = 0;

    // 더블 점프 관련 필드
    public final int MAX_JUMPS = 1;
    public int jumpsPerformed = 0;

    // 상수
    public final float MOVE_SPEED = 5f;
    public final float JUMP_FORCE = 3.5f;
    public final float WALL_JUMP_HORIZONTAL = 3f;
    public final float WALL_JUMP_VERTICAL = 8f;

    // Box2D Body의 바운드를 Rectange 형태로 반환합니다.
    // DamageBox와의 충돌 체크에 사용됩니다.
    private final com.badlogic.gdx.math.Rectangle tempBounds = new com.badlogic.gdx.math.Rectangle();

    public Player(World world, float x, float y) {
        // BaseEntity 생성자를 호출하여 체력을 초기화합니다.
        super();

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(x, y);
        body = world.createBody(def);
        body.setUserData(this);
        body.setFixedRotation(true);

        // 1. 몸체 Fixture (충돌 바운드로 사용됨)
        PolygonShape main = new PolygonShape();
        main.setAsBox(0.3f, 0.5f);
        FixtureDef fd = new FixtureDef();
        fd.shape = main;
        fd.density = 1f;
        fd.friction = 0.3f;
        fd.filter.categoryBits = Constants.CATEGORY_PLAYER;
        fd.filter.maskBits = Constants.MASK_PLAYER;
        // 메인 바디 Fixture에 고유의 UserData를 설정하여 충돌 감지에 사용합니다.
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

        this.currentState = new PlayerRunState();
        this.currentState.enter(this);
    }

    public void changeState(PlayerState newState) {
        if (currentState != null) {
            currentState.exit(this);
        }
        this.currentState = newState;
        newState.enter(this);
    }

    // BaseEntity의 추상 메서드(update)를 오버라이드합니다.
    @Override
    public void update(float delta) {
        // BaseEntity의 isAlive() 체크를 사용하여 사망 시 업데이트를 막습니다.
        if (!isAlive()) {
            return;
        }

        if (wallJumpTimer > 0) wallJumpTimer -= delta;
        if (dashCooldownTimer > 0) dashCooldownTimer -= delta;

        // 지상 착지 시 대쉬 및 점프 횟수 초기화
        if (isOnGround()) {
            dashesPerformed = 0;
            jumpsPerformed = 0;
        }

        boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean dashInput = Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_RIGHT);

        if (dashInput && dashCooldownTimer <= 0 && (left || right) && !(currentState instanceof PlayerDashState)) {
            // 공중 대쉬 횟수 제한 확인
            if (!isOnGround() && dashesPerformed >= MAX_DASHES_AIR) {
                return;
            }

            changeState(new PlayerDashState());
            return;
        }

        currentState.update(this, delta);

        // 공중 상태 전환 로직 (PlayerClingState 참조 포함)
        if (!isDashing) {
            if (!isOnGround() && !(currentState instanceof PlayerJumpState) && !(currentState instanceof PlayerClingState)) {
                changeState(new PlayerJumpState());
                return;
            }
        }
    }

    /**
     * Box2D Body의 위치와 크기를 기반으로 충돌 감지를 위한 Rectangle을 생성하여 반환합니다.
     * DamageBox와 같은 비 Box2D 객체와의 충돌 체크에 사용됩니다.
     */
    public com.badlogic.gdx.math.Rectangle getBounds() {
        // Box2D Body의 위치를 가져옵니다.
        Vector2 position = body.getPosition();

        // Body의 크기는 Fixture 생성 시 setAsBox(0.3f, 0.5f)로 정의되었습니다 (전체 폭: 0.6f, 전체 높이: 1.0f).
        // Rectangle은 좌측 하단 코너(x, y)를 기준으로 하므로, Body의 중앙 위치에서 폭/2, 높이/2를 빼줍니다.
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

    // Box2D Body의 x 좌표를 반환합니다.
    public float getX() { return body.getPosition().x; }
    // Box2D Body의 y 좌표를 반환합니다.
    public float getY() { return body.getPosition().y; }

    // Box2D Body를 직접 설정하는 대신, 위치를 설정합니다.
    public void setX(float x) { body.setTransform(x, body.getPosition().y, body.getAngle()); }
    public void setY(float y) { body.setTransform(body.getPosition().x, y, body.getAngle()); }


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
