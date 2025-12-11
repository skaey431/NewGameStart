package NewGameStart.NewGame.world;

import NewGameStart.NewGame.entities.player.Player;
import NewGameStart.NewGame.tools.Constants;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class WorldManager {

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
        fdef.filter.maskBits = Constants.CATEGORY_PLAYER; // 플레이어와만 충돌

        body.createFixture(fdef).setUserData(userData);
        shape.dispose();

        return body;
    }

    public void createDefaultStage() {
        // 바닥
        createStaticBox(world, 8f, 0.5f, 16f, 1f, "ground");
        // 일반 벽 (왼쪽 끝)
        createStaticBox(world, 0.5f, 5f, 1f, 9f, "normalWall");
        // 특수 벽
        createStaticBox(world, 15f, 5f, 1f, 9f, "specialWall");
        createStaticBox(world, 25f, 5f, 1f, 9f, "specialWall");
        createStaticBox(world, 35f, 5f, 1f, 9f, "specialWall");
    }

    public void dispose() {
        world.dispose();
    }

    // ContactListener 초기화 (Special Wall 충돌 처리)
    private void initContactListener() {
        world.setContactListener(new ContactListener() {

            @Override public void beginContact(Contact c) { handle(c, true); }
            @Override public void endContact(Contact c) { handle(c, false); }

            private void handle(Contact c, boolean isBegin) {
                Fixture fA = c.getFixtureA();
                Fixture fB = c.getFixtureB();
                if (fA == null || fB == null) return;

                Fixture playerSensor = null;
                Fixture otherFixture = null;
                Player p = null;

                // 1. Player Body를 가진 Fixture와 Other Fixture를 명확히 분리하여 찾습니다. (센서만 처리)
                if (fA.getBody().getUserData() instanceof Player && fA.isSensor()) {
                    p = (Player) fA.getBody().getUserData();
                    playerSensor = fA;
                    otherFixture = fB;
                } else if (fB.getBody().getUserData() instanceof Player && fB.isSensor()) {
                    p = (Player) fB.getBody().getUserData();
                    playerSensor = fB;
                    otherFixture = fA;
                }

                if (p == null) return;

                String sensorType = (String) playerSensor.getUserData();
                Object otherUserData = otherFixture.getUserData(); // 상대방 Fixture UserData

                // 2. 센서 카운트 업데이트
                if ("foot".equals(sensorType) || "left".equals(sensorType) ||
                    "right".equals(sensorType) || "head".equals(sensorType)) {

                    // foot 센서 필터링 (ground가 아니면 footContact 카운트 막기)
                    if ("foot".equals(sensorType) && !"ground".equals(otherUserData)) {
                        return;
                    }

                    // 접촉 카운트 업데이트
                    if (isBegin) {
                        p.incrementContact(sensorType);
                    } else {
                        p.decrementContact(sensorType);
                    }
                }
            }

            @Override public void preSolve(Contact c, Manifold m) {
                // Special Wall 충돌 처리
                Fixture fA = c.getFixtureA();
                Fixture fB = c.getFixtureB();

                boolean isPlayerA = fA.getBody().getUserData() instanceof Player;
                boolean isPlayerB = fB.getBody().getUserData() instanceof Player;

                if (isPlayerA && "specialWall".equals(fB.getUserData())) {
                    handleSpecialWallCollision(c, (Player) fA.getBody().getUserData(), fA);
                } else if (isPlayerB && "specialWall".equals(fA.getUserData())) {
                    handleSpecialWallCollision(c, (Player) fB.getBody().getUserData(), fB);
                }
            }

            private void handleSpecialWallCollision(Contact c, Player p, Fixture playerFixture) {
                // 플레이어의 메인 바디 Fixture가 접촉했는지 확인 ("player_main" UserData를 가진 Fixture)
                boolean isPlayerMainBody = "player_main".equals(playerFixture.getUserData());

                if (isPlayerMainBody) {
                    // 플레이어가 매달리는 상태가 아니면 충돌 비활성화 (통과)
                    if (!p.isClinging) {
                        c.setEnabled(false);
                    } else {
                        // 플레이어가 매달리는 상태이면 충돌 활성화 (벽에 붙음)
                        c.setEnabled(true);
                    }
                }
            }


            @Override public void postSolve(Contact c, ContactImpulse i) {}
        });
    }
}
