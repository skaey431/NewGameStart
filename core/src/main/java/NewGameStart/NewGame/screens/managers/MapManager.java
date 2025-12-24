package NewGameStart.NewGame.screens.managers;

import NewGameStart.NewGame.entities.Checkpoint;
import NewGameStart.NewGame.entities.DamageBox;
import NewGameStart.NewGame.entities.InstantKillBox;
import NewGameStart.NewGame.entities.monster.StaticMonster;
import NewGameStart.NewGame.tools.Constants;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

public class MapManager {
    private TiledMap tiledMap;
    private final World world;

    public MapManager(World world) {
        this.world = world;
    }

    // [수정] EntityManager를 파라미터로 추가하여 엔티티 자동 스폰 연동
    public void loadMap(String fileName, EntityManager entityManager) {
        clearCurrentMap();
        this.tiledMap = new TmxMapLoader().load(fileName);

        // 1. 지형 레이어 생성 (ground, wall)
        String[] layers = {"ground", "wall"};
        for (String layerName : layers) {
            MapLayer layer = tiledMap.getLayers().get(layerName);
            if (layer != null) {
                for (MapObject object : layer.getObjects()) {
                    if (object instanceof RectangleMapObject) {
                        createPhysicsObject((RectangleMapObject) object);
                    }
                }
            }
        }

        // 2. [연동] objects 레이어를 분석하여 데미지 필드 및 몬스터 생성
        MapLayer objectLayer = tiledMap.getLayers().get("objects");
        if (objectLayer != null && entityManager != null) {
            for (MapObject object : objectLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    String name = object.getName(); // Tiled에서 설정한 Name 속성
                    Gdx.app.log("object",name);

                    // MapManager.java의 loadMap 루프 내부 switch/if 문 수정 예시
                    if ("damage-box".equals(name)) {
                        entityManager.getDamageBoxes().add(new DamageBox(world, rect.x, rect.y, rect.width, rect.height, 10f));
                    } else if ("monster".equals(name)) {
                        entityManager.getMonsters().add(new StaticMonster(world, 100, rect.x, rect.y, rect.width, rect.height, 20f));
                    } else if ("kill-box".equals(name)) {
                        // 이제 world를 인자로 넘깁니다.
                        entityManager.getKillBoxes().add(new InstantKillBox(world, rect.x, rect.y, rect.width, rect.height));
                    } else if ("checkpoint".equals(name)) {
                        // 체크포인트도 자동 생성 리스트에 추가
                        entityManager.getCheckpoints().add(new Checkpoint(world, rect.x, rect.y, rect.width, rect.height));
                    }
                }
            }
        }
    }

    private void createPhysicsObject(RectangleMapObject rectObject) {
        Rectangle rect = rectObject.getRectangle();
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set((rect.x + rect.width / 2) / Constants.PPM, (rect.y + rect.height / 2) / Constants.PPM);

        Body body = world.createBody(bdef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(rect.width / 2 / Constants.PPM, rect.height / 2 / Constants.PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;

        String type = rectObject.getProperties().get("type", "ground", String.class);
        if ("wall".equals(type)) {
            fdef.filter.categoryBits = Constants.BIT_WALL;
            fdef.friction = 0.0f;
        } else {
            fdef.filter.categoryBits = Constants.BIT_GROUND;
            fdef.friction = 0.5f;
        }
        fdef.filter.maskBits = -1;

        body.createFixture(fdef).setUserData(type);
        shape.dispose();
    }

    public void clearCurrentMap() {
        if (tiledMap != null) {
            Array<Body> bodies = new Array<>();
            world.getBodies(bodies);
            for (Body body : bodies) {
                // StaticBody만 골라서 삭제 (지형 + Hazard + Monster 센서가 모두 Static이므로 깔끔하게 삭제됨)
                if (body.getType() == BodyDef.BodyType.StaticBody) {
                    world.destroyBody(body);
                }
            }
            tiledMap.dispose();
            tiledMap = null;
        }
    }

    public void dispose() { clearCurrentMap(); }
    public TiledMap getTiledMap() { return tiledMap; }
}
