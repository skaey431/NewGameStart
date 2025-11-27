package NewGameStart.NewGame.entities;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.Vector2;

public class Player extends BaseEntity {

    public boolean isOnGround = false;
    private boolean isRecovering = false; // ★ 자동 기립 중인지 여부

    public Player(World world, float x, float y) {
        super(world);
        createBody();
        body.setTransform(x, y, 0);

        body.setUserData(this);
    }

    @Override
    protected void createBody() {
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        body.setType(BodyDef.BodyType.DynamicBody);

        PolygonShape mainShape = new PolygonShape();
        mainShape.setAsBox(0.5f, 1f);

        FixtureDef mainFD = new FixtureDef();
        mainFD.shape = mainShape;
        mainFD.density = 1f;
        mainFD.friction = 0.2f;
        body.createFixture(mainFD);
        mainShape.dispose();

        PolygonShape footShape = new PolygonShape();
        footShape.setAsBox(
            0.45f,
            0.1f,
            new Vector2(0, -1f),
            0
        );

        FixtureDef footFD = new FixtureDef();
        footFD.shape = footShape;
        footFD.isSensor = true;

        Fixture footFixture = body.createFixture(footFD);
        footFixture.setUserData("foot");

        footShape.dispose();
    }

    public void moveLeft() {
        body.setLinearVelocity(-3f, body.getLinearVelocity().y);
    }

    public void moveRight() {
        body.setLinearVelocity(3f, body.getLinearVelocity().y);
    }

    public void jump() {
        if (isOnGround) {
            body.setLinearVelocity(body.getLinearVelocity().x, 8f);
        }
    }

    // ★ UP 키 한 번 → 자동 기립 시작
    public void startRecovering() {
        isRecovering = true;
    }

    // 호출될 때마다 회전 진행
    public void update() {
        if (isRecovering) {
            float angle = body.getAngle();
            float target = 0f;
            float diff = target - angle;

            // 충분히 세워졌으면 자동 종료
            if (Math.abs(diff) < 0.05f) {
                body.setTransform(body.getPosition(), 0f);
                body.setAngularVelocity(0);
                isRecovering = false;
                return;
            }

            // 땅에 닿아 회전이 막히므로 약간 들어올림
            body.setTransform(
                body.getPosition().x,
                body.getPosition().y + 0.05f,
                angle
            );

            // 자연스러운 회전(토크 적용)
            body.applyTorque(diff * 15f, true);
        }
    }
}
