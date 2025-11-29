package NewGameStart.NewGame.world;

import NewGameStart.NewGame.entities.Player;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class WorldManager {

    private World world;

    public WorldManager() {
        world = new World(new Vector2(0, -9.8f), true);

        createWalls();

        world.setContactListener(new ContactListener() {

            @Override
            public void beginContact(Contact contact) {
                Fixture a = contact.getFixtureA();
                Fixture b = contact.getFixtureB();

                handleContact(a, b, true);
                handleContact(b, a, true);
            }

            @Override
            public void endContact(Contact contact) {
                Fixture a = contact.getFixtureA();
                Fixture b = contact.getFixtureB();

                handleContact(a, b, false);
                handleContact(b, a, false);
            }

            private void handleContact(Fixture fx, Fixture other, boolean begin) {

                if (other.getBody().getType() != BodyDef.BodyType.StaticBody)
                    return;

                Object data = fx.getUserData();
                if (data == null) return;

                Player player = (Player) fx.getBody().getUserData();
                if (player == null) return;

                switch (data.toString()) {
                    case "foot":
                        player.isOnGround = begin;
                        break;
                    case "head":
                        player.isTouchingCeiling = begin;
                        break;
                    case "player":
                        player.isTouchingWall = begin;
                        break;
                }
            }

            @Override public void preSolve(Contact contact, Manifold oldManifold) {}
            @Override public void postSolve(Contact contact, ContactImpulse impulse) {}
        });
    }


    private void createWalls() {
        createStaticBox(-5f, 3f, 1f, 6f);   // 왼벽
        createStaticBox(5f, 3f, 1f, 6f);    // 오른벽
        createStaticBox(0f, 0f, 10f, 1f);   // 바닥
        createStaticBox(0f, 10f, 10f, 1f);  // 천장
    }

    private void createStaticBox(float x, float y, float hw, float hh) {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        def.position.set(x, y);

        Body body = world.createBody(def);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(hw, hh);

        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.friction = 0.5f;

        body.createFixture(fd);
        shape.dispose();
    }

    public World getWorld() { return world; }

    public void update() {
        world.step(1 / 60f, 6, 2);
    }

    public void dispose() {
        world.dispose();
    }
}
