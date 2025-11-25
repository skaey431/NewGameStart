package NewGameStart.NewGame.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Player extends BaseEntity {

    public Player(World world, float x, float y) {
        super(world);
        body.setTransform(x, y, 0);
    }

    @Override
    protected void createBody() {
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.fixedRotation = true; // 어지럽게 회전 방지
        body = world.createBody(bd);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.4f, 0.9f);

        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.density = 1f;
        fd.friction = 0.2f;

        body.createFixture(fd);
        shape.dispose();
    }

    public void moveLeft() {
        body.setLinearVelocity(-5f, body.getLinearVelocity().y);
    }

    public void moveRight() {
        body.setLinearVelocity(5f, body.getLinearVelocity().y);
    }

    public void jump() {
        float jumpForce = 13f;
        body.applyLinearImpulse(new Vector2(0, jumpForce), body.getWorldCenter(), true);
    }

    @Override
    public void update() {}
}

