package NewGameStart.NewGame.entities;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Wall extends BaseEntity {

    float width, height;

    public Wall(World world, float x, float y, float width, float height) {

        super(world);
        this.width = width;
        this.height = height;
        body.setTransform(x, y, 0);
    }

    @Override
    protected void createBody() {
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.StaticBody;
        body = world.createBody(bd);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/2, height/2);

        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.friction = 1f;
        body.createFixture(fd);
        shape.dispose();
    }

    @Override
    public void update() {}
}

