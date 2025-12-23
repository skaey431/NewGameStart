package NewGameStart.NewGame.screens.managers;

import NewGameStart.NewGame.tools.Constants;
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

    /**
     * 새 맵을 로드합니다. 호출 시 기존의 모든 StaticBody(지형)를 월드에서 제거합니다.
     */
    public void loadMap(String fileName) {
        // 1. 기존 리소스 해제 및 물리 바디 제거
        clearCurrentMap();

        // 2. 새 TMX 파일 로드
        this.tiledMap = new TmxMapLoader().load(fileName);

        // 3. 'collision' 레이어 분석 및 물리 객체 생성
        if (tiledMap.getLayers().get("collision") != null) {
            for (MapObject object : tiledMap.getLayers().get("collision").getObjects()) {
                if (object instanceof RectangleMapObject) {
                    createPhysicsObject((RectangleMapObject) object);
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

        // Tiled 속성에서 "type"을 읽어 ground와 wall 구분
        String type = rectObject.getProperties().get("type", "ground", String.class);

        if ("wall".equals(type)) {
            fdef.filter.categoryBits = Constants.BIT_WALL;
            fdef.friction = 0.0f; // 벽은 매끄럽게
        } else {
            fdef.filter.categoryBits = Constants.BIT_GROUND;
            fdef.friction = 0.5f; // 바닥은 마찰력 부여
        }

        fdef.filter.maskBits = -1; // 모든 객체와 충돌 가능

        body.createFixture(fdef).setUserData(type);
        shape.dispose();
    }

    /**
     * 현재 월드에 생성된 모든 지형 바디를 삭제하고 맵 리소스를 비웁니다.
     */
    public void clearCurrentMap() {
        if (tiledMap != null) {
            Array<Body> bodies = new Array<>();
            world.getBodies(bodies);
            for (Body body : bodies) {
                // StaticBody만 골라서 삭제 (플레이어나 몬스터는 Dynamic이므로 유지됨)
                if (body.getType() == BodyDef.BodyType.StaticBody) {
                    world.destroyBody(body);
                }
            }
            tiledMap.dispose();
            tiledMap = null;
        }
    }

    public void dispose() {
        clearCurrentMap();
    }

    // MapManager.java 클래스 맨 아래에 추가
    public TiledMap getTiledMap() {
        return tiledMap;
    }
}
