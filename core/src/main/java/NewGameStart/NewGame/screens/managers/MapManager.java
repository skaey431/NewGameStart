package NewGameStart.NewGame.screens.managers;

import NewGameStart.NewGame.tools.Constants;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;

public class MapManager {
    private TiledMap tiledMap;
    private final World world;

    public MapManager(World world) {
        this.world = world;
    }

    public void loadMap(String fileName) {
        this.tiledMap = new TmxMapLoader().load(fileName);
        if (tiledMap.getLayers().get("ground") != null) {
            for (MapObject object : tiledMap.getLayers().get("ground").getObjects()) {
                if (object instanceof RectangleMapObject) {
                    createPhysicsObject((RectangleMapObject) object);
                }
            }
        }
        if (tiledMap.getLayers().get("wall") != null) {
            for (MapObject object : tiledMap.getLayers().get("wall").getObjects()) {
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

        // Tiled에서 설정한 "type" 속성을 가져옴 (없으면 기본값 "ground")
        String type = rectObject.getProperties().get("type", "ground", String.class);

        if ("wall".equals(type)) {
            fdef.filter.categoryBits = Constants.BIT_WALL;
            fdef.friction = 0.0f; // 벽은 미끄럽게 설정 (벽타기 로직을 위해)
        } if ("ground".equals(type)){
            fdef.filter.categoryBits = Constants.BIT_GROUND;
            fdef.friction = 0.5f; // 바닥은 마찰력 부여
            System.out.println("ground");
        }

        fdef.filter.maskBits = -1;

        // 이제 userData에 "ground" 또는 "wall"이 들어감
        body.createFixture(fdef).setUserData(type);
        shape.dispose();
    }

    public void dispose() {
        if (tiledMap != null) tiledMap.dispose();
    }
}
