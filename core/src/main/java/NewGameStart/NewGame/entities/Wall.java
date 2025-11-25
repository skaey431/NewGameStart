package NewGameStart.NewGame.entities;

import com.badlogic.gdx.physics.box2d.*;

public class Wall extends BaseEntity {

    float w, h;

    public Wall(World world, float x, float y, float w, float h) {
        super(world);

        this.w = w;
        this.h = h;

        createBody();
        body.setTransform(x, y, 0);
    }

    @Override
    protected void createBody() {
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.StaticBody;
        body.setType(BodyDef.BodyType.StaticBody);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(w / 2f, h / 2f);

        FixtureDef fd = new FixtureDef();
        fd.shape = shape;

        body.createFixture(fd);
        shape.dispose();
    }

    @Override
    public void update() {}
}
