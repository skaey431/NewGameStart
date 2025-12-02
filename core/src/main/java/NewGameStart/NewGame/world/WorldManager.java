package NewGameStart.NewGame.world;

import NewGameStart.NewGame.entities.Player;
import NewGameStart.NewGame.tools.Constants; // Constants import 추가
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class WorldManager {

    private final World world;

    public WorldManager() {
        world = new World(new Vector2(0, -9.8f), true);
        initContactListener();
    }

    public World getWorld() { return world; }

    // ⭐ 수정: 충돌 필터링을 명확히 설정합니다.
    public static Body createStaticBox(World world, float x, float y, float w, float h, String userData) {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(x, y);
        Body body = world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(w / 2f, h / 2f);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 1.0f;

        // ⭐ 수정: 환경 객체 카테고리 및 마스크 설정
        short category = Constants.CATEGORY_GROUND;
        if ("normalWall".equals(userData)) {
            category = Constants.CATEGORY_WALL;
        } else if ("specialWall".equals(userData)) {
            category = Constants.CATEGORY_SPECIAL_WALL;
        }

        // 이 물체의 카테고리 (자신이 누구인지)
        fdef.filter.categoryBits = category;
        // 자신이 충돌할 대상 (플레이어 카테고리와 충돌하도록 설정)
        fdef.filter.maskBits = Constants.CATEGORY_PLAYER; // 환경은 오직 플레이어와 충돌하면 됨

        body.createFixture(fdef).setUserData(userData);
        shape.dispose();

        return body;
    }

    public void createDefaultStage() {
        createStaticBox(world, 8f, 0.5f, 16f, 1f, "ground");
        createStaticBox(world, 0.5f, 5f, 1f, 9f, "normalWall");
        createStaticBox(world, 15.5f, 5f, 1f, 9f, "specialWall");
    }

    public void dispose() {
        world.dispose();
    }

    // ------------------------------------------
    // ContactListener 초기화 (로직은 이미 안정화됨)
    // ------------------------------------------
    private void initContactListener() {
        world.setContactListener(new ContactListener() {

            @Override public void beginContact(Contact c) { handle(c, true); }
            @Override public void endContact(Contact c) { handle(c, false); }

            private void handle(Contact c, boolean isBegin) {
                Fixture a = c.getFixtureA();
                Fixture b = c.getFixtureB();
                if (a == null || b == null) return;

                Player p = null;
                if (a.getBody().getUserData() instanceof Player) p = (Player) a.getBody().getUserData();
                if (b.getBody().getUserData() instanceof Player) p = (Player) b.getBody().getUserData();
                if (p == null) return;

                Object ua = a.getUserData();
                Object ub = b.getUserData();

                String sensorType = null;

                // 플레이어 센서 피스쳐의 UserData를 확인합니다.
                if ("foot".equals(ua) || "left".equals(ua) || "right".equals(ua) || "head".equals(ua)) {
                    sensorType = (String) ua;
                } else if ("foot".equals(ub) || "left".equals(ub) || "right".equals(ub) || "head".equals(ub)) {
                    sensorType = (String) ub;
                }

                if (sensorType != null) {
                    if (isBegin) {
                        p.incrementContact(sensorType);
                    } else {
                        p.decrementContact(sensorType);
                    }
                }
            }

            @Override public void preSolve(Contact c, Manifold m) {}
            @Override public void postSolve(Contact c, ContactImpulse i) {}
        });
    }
}
