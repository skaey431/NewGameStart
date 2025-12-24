package NewGameStart.NewGame.entities;

import NewGameStart.NewGame.tools.Constants;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;

public class InstantKillBox {

    private final Rectangle bounds;
    private Body body; // 물리 바디 추가

    public InstantKillBox(World world, float x, float y, float width, float height) {
        this.bounds = new Rectangle(x, y, width, height);

        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set((x + width / 2f) / Constants.PPM, (y + height / 2f) / Constants.PPM);
        this.body = world.createBody(bdef);
        this.body.setUserData(this);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2f / Constants.PPM, height / 2f / Constants.PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.isSensor = true; // 즉사 구역은 통과 가능해야 함
        fdef.filter.categoryBits = Constants.BIT_HAZARD;
        fdef.filter.maskBits = Constants.CATEGORY_PLAYER;

        body.createFixture(fdef).setUserData("kill");
        shape.dispose();
    }

    public Body getBody() { return body; }
    public Rectangle getBounds() { return bounds; }
}
