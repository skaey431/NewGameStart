package NewGameStart.NewGame.entities;


import com.badlogic.gdx.physics.box2d.*;

public class Ground extends BaseEntity {

    float w, h;

    public Ground(World world, float x, float y, float w, float h) {
        super(world);

        this.w = w;
        this.h = h;
        System.out.println("Grounded");
        body.setTransform(x, y, 0);
    }

    @Override
    protected void createBody() {
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.StaticBody;
        body = world.createBody(bd);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(w / 2f, h / 2f);

        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.friction = 1f;

        body.createFixture(fd);
        shape.dispose();
    }

    @Override
    public void update() {}
}
