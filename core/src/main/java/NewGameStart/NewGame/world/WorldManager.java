package NewGameStart.NewGame.world;

import NewGameStart.NewGame.entities.player.Player;
import NewGameStart.NewGame.tools.Constants;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class WorldManager {

    // ... (world, getWorld, createStaticBox, createDefaultStage, dispose 메서드는 이전과 동일) ...
    private final World world;

    public WorldManager() {
        world = new World(new Vector2(0, -9.8f), true);
        initContactListener();
    }

    public World getWorld() { return world; }

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

        short category = Constants.CATEGORY_GROUND;
        if ("normalWall".equals(userData)) {
            category = Constants.CATEGORY_WALL;
        } else if ("specialWall".equals(userData)) {
            category = Constants.CATEGORY_SPECIAL_WALL;
        }

        fdef.filter.categoryBits = category;
        fdef.filter.maskBits = Constants.CATEGORY_PLAYER;

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
    // ContactListener 초기화 (SpecialWall 통과 로직 추가)
    // ------------------------------------------
    private void initContactListener() {
        world.setContactListener(new ContactListener() {

            @Override public void beginContact(Contact c) { handle(c, true); }
            @Override public void endContact(Contact c) { handle(c, false); }

            private void handle(Contact c, boolean isBegin) {
                // ... (이전의 센서 카운터 로직과 동일) ...
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

            @Override public void preSolve(Contact c, Manifold m) {
                // ⭐ 핵심 수정: SpecialWall 통과/충돌 결정 로직
                Fixture fA = c.getFixtureA();
                Fixture fB = c.getFixtureB();

                // 플레이어와 특수 벽 충돌인지 확인
                boolean isPlayerA = fA.getBody().getUserData() instanceof Player;
                boolean isPlayerB = fB.getBody().getUserData() instanceof Player;

                // 둘 중 하나가 플레이어, 나머지 하나가 특수 벽인지 확인
                if (isPlayerA && "specialWall".equals(fB.getUserData())) {
                    handleSpecialWallCollision(c, (Player) fA.getBody().getUserData());
                } else if (isPlayerB && "specialWall".equals(fA.getUserData())) {
                    handleSpecialWallCollision(c, (Player) fB.getBody().getUserData());
                }
            }

            private void handleSpecialWallCollision(Contact c, Player p) {
                // 플레이어의 메인 Fixture와 특수 벽의 Fixture가 충돌해야만 처리 (센서는 제외)
                // 센서는 UserData가 "foot", "left" 등으로 설정되어 있으므로, "player"만 확인
                Object uA = c.getFixtureA().getUserData();
                Object uB = c.getFixtureB().getUserData();

                boolean isPlayerMainBody = "player".equals(uA) || "player".equals(uB);

                if (isPlayerMainBody) {
                    // isClinging 상태가 아니면 (벽에 매달리지 않으면) -> 충돌 응답 비활성화(통과)
                    if (!p.isClinging) {
                        c.setEnabled(false);
                    } else {
                        // isClinging 상태이면 (벽에 매달리면) -> 충돌 응답 활성화(벽에 붙음)
                        c.setEnabled(true);
                    }
                }
            }

            @Override public void postSolve(Contact c, ContactImpulse i) {}
        });
    }
}
