package NewGameStart.NewGame.entities;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public abstract class BaseEntity {
    protected Body body;
    protected World world;

    public BaseEntity(World world) {
        this.world = world;
        createBody();
    }

    protected abstract void createBody();
    public abstract void update();
    public Body getBody() { return body; }
}
