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
        // ⭐ 사용자님의 원래 코드를 유지: 이 설정이 충돌 인식이 되도록 보장합니다.
        fdef.filter.maskBits = Constants.CATEGORY_PLAYER;

        body.createFixture(fdef).setUserData(userData);
        shape.dispose();

        return body;
    }

    public void createDefaultStage() {
        createStaticBox(world, 8f, 0.5f, 16f, 1f, "ground");
        createStaticBox(world, 0.5f, 5f, 1f, 9f, "normalWall");
        createStaticBox(world, 15f, 5f, 1f, 9f, "specialWall");
        createStaticBox(world, 25f, 5f, 1f, 9f, "specialWall");
        createStaticBox(world, 35f, 5f, 1f, 9f, "specialWall");
    }

    public void dispose() {
        world.dispose();
    }

    // ------------------------------------------
    // ContactListener 초기화 (최종 버그 수정)
    // ------------------------------------------
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

                // 1. Player Body를 가진 Fixture와 Other Fixture를 명확히 분리하여 찾습니다.
                if (fA.getBody().getUserData() instanceof Player) {
                    p = (Player) fA.getBody().getUserData();
                    playerSensor = fA;
                    otherFixture = fB;
                } else if (fB.getBody().getUserData() instanceof Player) {
                    p = (Player) fB.getBody().getUserData();
                    playerSensor = fB;
                    otherFixture = fA;
                }

                if (p == null) return;

                String sensorType = (String) playerSensor.getUserData();
                Object otherUserData = otherFixture.getUserData(); // 상대방 Fixture UserData

                // 2. 센서가 유효한 센서인지 확인
                if ("foot".equals(sensorType) || "left".equals(sensorType) ||
                    "right".equals(sensorType) || "head".equals(sensorType)) {

                    // ⭐ 무한 점프 버그 수정: 발 센서 필터링을 재도입합니다.
                    if ("foot".equals(sensorType)) {
                        // 다른 물체의 UserData가 "ground"가 아니면 footContact 카운트를 막습니다.
                        if (!"ground".equals(otherUserData)) {
                            return;
                        }
                    }

                    // 3. 접촉 카운트 업데이트
                    if (isBegin) {
                        p.incrementContact(sensorType);
                    } else {
                        p.decrementContact(sensorType);
                    }
                }
            }

            @Override public void preSolve(Contact c, Manifold m) {
                // ... (SpecialWall 통과/충돌 결정 로직은 동일) ...
                Fixture fA = c.getFixtureA();
                Fixture fB = c.getFixtureB();

                boolean isPlayerA = fA.getBody().getUserData() instanceof Player;
                boolean isPlayerB = fB.getBody().getUserData() instanceof Player;

                if (isPlayerA && "specialWall".equals(fB.getUserData())) {
                    handleSpecialWallCollision(c, (Player) fA.getBody().getUserData());
                } else if (isPlayerB && "specialWall".equals(fA.getUserData())) {
                    handleSpecialWallCollision(c, (Player) fB.getBody().getUserData());
                }
            }

            private void handleSpecialWallCollision(Contact c, Player p) {
                // ... (로직은 동일) ...
                Object uA = c.getFixtureA().getUserData();
                Object uB = c.getFixtureB().getUserData();

                boolean isPlayerMainBody = "player".equals(uA) || "player".equals(uB);

                if (isPlayerMainBody) {
                    if (!p.isClinging) {
                        c.setEnabled(false);
                    } else {
                        c.setEnabled(true);
                    }
                }
            }

            @Override public void postSolve(Contact c, ContactImpulse i) {}
        });
    }
}
