package NewGameStart.NewGame.entities;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.Vector2;

public class SpecialWall {

    public Body body;

    public SpecialWall(World world, float x, float y, float hw, float hh) {

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        def.position.set(x, y);

        body = world.createBody(def);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(hw, hh);

        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.isSensor = true;     // 기본: 통과

        Fixture fx = body.createFixture(fd);
        fx.setUserData("specialWall");

        shape.dispose();
    }
}
