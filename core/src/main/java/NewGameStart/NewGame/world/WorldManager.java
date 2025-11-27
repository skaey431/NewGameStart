package NewGameStart.NewGame.world;

import NewGameStart.NewGame.entities.Player;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class WorldManager {

    private World world;

    public WorldManager() {
        world = new World(new Vector2(0, -9.8f), true);

        world.setContactListener(new ContactListener() {

            @Override
            public void beginContact(Contact contact) {
                Fixture a = contact.getFixtureA();
                Fixture b = contact.getFixtureB();

                checkFoot(a, b, true);
                checkFoot(b, a, true);
            }

            @Override
            public void endContact(Contact contact) {
                Fixture a = contact.getFixtureA();
                Fixture b = contact.getFixtureB();

                checkFoot(a, b, false);
                checkFoot(b, a, false);
            }

            private void checkFoot(Fixture foot, Fixture other, boolean isBegin) {
                if (foot.getUserData() != null && foot.getUserData().equals("foot")) {

                    Player player = (Player) foot.getBody().getUserData();
                    if (player == null) return;

                    if (other.getBody().getType() == BodyDef.BodyType.StaticBody) {
                        player.isOnGround = isBegin;
                    }
                }
            }

            @Override public void preSolve(Contact contact, Manifold oldManifold) {}
            @Override public void postSolve(Contact contact, ContactImpulse impulse) {}
        });
    }

    public World getWorld() {
        return world;
    }

    public void update() {
        world.step(1 / 60f, 6, 2);
    }

    public void dispose() {
        world.dispose();
    }
}
