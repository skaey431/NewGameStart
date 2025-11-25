package NewGameStart.NewGame.entities;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.Vector2;

public class Player extends BaseEntity {

    public boolean isOnGround = false;

    public Player(World world, float x, float y) {
        super(world);
        createBody();
        body.setTransform(x, y, 0);

        // ★ ContactListener에서 Player 찾기 위함
        body.setUserData(this);
    }

    @Override
    protected void createBody() {
        // 메인 바디
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

        // ★ FOOT SENSOR (바닥 체크 용)
        PolygonShape footShape = new PolygonShape();
        footShape.setAsBox(
            0.45f,
            0.1f,
            new Vector2(0, -1f), // 발 아래 위치
            0
        );

        FixtureDef footFD = new FixtureDef();
        footFD.shape = footShape;
        footFD.isSensor = true;

        // Fixture를 만든 다음에 userData 설정 (중요)
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

    @Override
    public void update() {}
}
