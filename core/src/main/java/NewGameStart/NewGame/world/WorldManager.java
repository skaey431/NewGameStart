package NewGameStart.NewGame.world;

import NewGameStart.NewGame.entities.Player;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class WorldManager {

    private World world;

    public WorldManager() {
        world = new World(new Vector2(0, -10), true);
        initContactListener();
        initContactFilter();
        createDefaultStage();
    }

    public World getWorld() { return world; }

    public void update() {
        world.step(1/60f, 6, 2);
    }

    public void dispose() { world.dispose(); }

    private void createDefaultStage() {
        // 바닥
        createStatic(0, -1, 20, 1);

        // 양쪽 벽
        createStatic(-8, 4, 1, 10);
        createStatic(8, 4, 1, 10);

        // 천장
        createStatic(0, 9, 20, 1);
    }

    private void createStatic(float x, float y, float w, float h) {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        def.position.set(x, y);

        Body body = world.createBody(def);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(w / 2f, h / 2f);

        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.friction = 1f;

        body.createFixture(fd).setUserData("wall");

        shape.dispose();
    }

    // ------------------------------------------
    // ContactListener
    // ------------------------------------------
    private void initContactListener() {
        world.setContactListener(new ContactListener() {

            @Override
            public void beginContact(Contact c) { handle(c, true); }
            @Override
            public void endContact(Contact c) { handle(c, false); }

            private void handle(Contact c, boolean begin) {
                Fixture a = c.getFixtureA();
                Fixture b = c.getFixtureB();
                if (a == null || b == null) return;

                Player p = null;

                if (a.getBody().getUserData() instanceof Player)
                    p = (Player) a.getBody().getUserData();
                if (b.getBody().getUserData() instanceof Player)
                    p = (Player) b.getBody().getUserData();

                if (p == null) return;

                Object ua = a.getUserData();
                Object ub = b.getUserData();

                if ("foot".equals(ua) || "foot".equals(ub))
                    p.isOnGround = begin;

                if ("left".equals(ua) || "left".equals(ub))
                    p.isTouchingLeft = begin;

                if ("right".equals(ua) || "right".equals(ub))
                    p.isTouchingRight = begin;

                if ("head".equals(ua) || "head".equals(ub))
                    p.isTouchingCeiling = begin;
            }

            @Override public void preSolve(Contact c, Manifold m) {}
            @Override public void postSolve(Contact c, ContactImpulse i) {}
        });
    }

    // ------------------------------------------
    // SpecialWall 충돌 필터
    // ------------------------------------------
    private void initContactFilter() {
        world.setContactFilter((a, b) -> {

            Object ua = a.getUserData();
            Object ub = b.getUserData();

            boolean aSpecial = "specialWall".equals(ua);
            boolean bSpecial = "specialWall".equals(ub);

            if (aSpecial || bSpecial) {

                Body pb = aSpecial ? b.getBody() : a.getBody();

                if (pb.getUserData() instanceof Player) {
                    Player p = (Player) pb.getUserData();
                    return p.isClinging;
                }
            }
            return true;
        });
    }
}
