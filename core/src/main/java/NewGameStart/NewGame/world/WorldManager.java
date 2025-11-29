package NewGameStart.NewGame.world;

import NewGameStart.NewGame.entities.Player;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class WorldManager {

    private World world;

    public WorldManager() {

        world = new World(new Vector2(0, -9.8f), true);

        createDefaultWalls();
        setupContactListener();
    }

    private void setupContactListener() {

        world.setContactListener(new ContactListener() {

            @Override
            public void beginContact(Contact c) {
                Fixture a = c.getFixtureA();
                Fixture b = c.getFixtureB();
                process(a, b, true);
                process(b, a, true);
            }

            @Override
            public void endContact(Contact c) {
                Fixture a = c.getFixtureA();
                Fixture b = c.getFixtureB();
                process(a, b, false);
                process(b, a, false);
            }

            private void process(Fixture fx, Fixture other, boolean begin) {
                if (fx == null || fx.getUserData() == null) return;

                if (!(fx.getBody().getUserData() instanceof Player)) return;

                Player p = (Player) fx.getBody().getUserData();

                switch (fx.getUserData().toString()) {

                    case "foot":
                        p.isOnGround = begin;
                        break;

                    case "head":
                        p.isTouchingCeiling = begin;
                        break;

                    case "player":
                        if (!"specialWall".equals(other.getUserData()))
                            if (other.getBody().getType() == BodyDef.BodyType.StaticBody)
                                p.isTouchingWall = begin;
                        break;
                }
            }

            @Override
            public void preSolve(Contact c, Manifold m) {

                Fixture A = c.getFixtureA();
                Fixture B = c.getFixtureB();

                if (A.getUserData() == null || B.getUserData() == null) return;

                boolean special =
                    A.getUserData().equals("specialWall") ||
                        B.getUserData().equals("specialWall");

                boolean player =
                    A.getUserData().equals("player") ||
                        B.getUserData().equals("player");

                if (special && player) {

                    Player p = null;

                    if (A.getBody().getUserData() instanceof Player)
                        p = (Player) A.getBody().getUserData();

                    if (B.getBody().getUserData() instanceof Player)
                        p = (Player) B.getBody().getUserData();

                    if (p == null) return;

                    // ⭐ 클링 상태일 때만 충돌 허용
                    c.setEnabled(p.isClinging);
                }
            }

            @Override public void postSolve(Contact c, ContactImpulse i) {}
        });
    }

    private void createDefaultWalls() {
        createStaticBox(-5, 3, 1, 6);
        createStaticBox(5, 3, 1, 6);
        createStaticBox(0, 0, 10, 1);
        createStaticBox(0, 10, 10, 1);
    }

    private void createStaticBox(float x, float y, float hw, float hh) {
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.StaticBody;
        bd.position.set(x, y);

        Body b = world.createBody(bd);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(hw, hh);

        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.friction = 0.5f;

        b.createFixture(fd);

        shape.dispose();
    }

    public World getWorld() { return world; }

    public void update() {
        world.step(1/60f, 6, 2);
    }

    public void dispose() {
        world.dispose();
    }
}
