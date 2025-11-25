package NewGameStart.NewGame.entities;

import com.badlogic.gdx.physics.box2d.*;

public abstract class BaseEntity {

    protected World world;
    protected Body body;

    public BaseEntity(World world) {
        this.world = world;
        BodyDef bd = new BodyDef();
        body = world.createBody(bd);
    }

    protected abstract void createBody();

    public Body getBody() {
        return body;
    }

    public abstract void update();
}
