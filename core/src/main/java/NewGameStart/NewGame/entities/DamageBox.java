package NewGameStart.NewGame.entities;

import NewGameStart.NewGame.tools.Constants;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;

public class DamageBox {
    private Body body;
    private Rectangle bounds;
    private float damageAmount;

    public DamageBox(World world, float x, float y, float w, float h, float damageAmount) {
        this.damageAmount = damageAmount;
        this.bounds = new Rectangle(x, y, w, h);

        BodyDef bdef = new BodyDef();
        bdef.position.set((x + w / 2) / Constants.PPM, (y + h / 2) / Constants.PPM);
        bdef.type = BodyDef.BodyType.StaticBody;
        this.body = world.createBody(bdef);
        this.body.setUserData(this);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(w / 2 / Constants.PPM, h / 2 / Constants.PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.isSensor = true;
        fdef.filter.categoryBits = Constants.BIT_HAZARD;
        fdef.filter.maskBits = Constants.CATEGORY_PLAYER;

        body.createFixture(fdef).setUserData("damage");
        shape.dispose();
    }

    public Body getBody() { return body; }
    public Rectangle getBounds() { return bounds; }
    public float getDamageAmount() { return damageAmount; }
}
