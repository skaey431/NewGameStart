package NewGameStart.NewGame.entities;

import com.badlogic.gdx.physics.box2d.*;

public class NormalWall {

    private Body body;

    public NormalWall(World world, float x, float y, float w, float h) {

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        def.position.set(x, y);

        body = world.createBody(def);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(w / 2f, h / 2f);

        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.friction = 1f;

        body.createFixture(fd).setUserData("wall");

        shape.dispose();
    }

    public Body getBody() { return body; }
}
