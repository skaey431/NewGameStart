package NewGameStart.NewGame.entities;

import com.badlogic.gdx.physics.box2d.Body;

public abstract class BaseEntity {
    protected Body body;

    public BaseEntity(Body body) {
        this.body = body;
    }

    public Body getBody() {
        return body;
    }
}
