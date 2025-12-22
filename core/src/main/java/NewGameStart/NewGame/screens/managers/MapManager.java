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
                    createGround((RectangleMapObject) object);
                }
            }
        }
        if (tiledMap.getLayers().get("wall") != null) {
            for (MapObject object : tiledMap.getLayers().get("wall").getObjects()) {
                if (object instanceof RectangleMapObject) {
                    createWall((RectangleMapObject) object);
                }
            }
        }
    }

    private void createGround(RectangleMapObject rectObject) {
        Rectangle rect = rectObject.getRectangle();
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set((rect.x + rect.width / 2) / Constants.PPM, (rect.y + rect.height / 2) / Constants.PPM);

        Body body = world.createBody(bdef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(rect.width / 2 / Constants.PPM, rect.height / 2 / Constants.PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.friction = 0.5f;

        // 중요: 이 객체의 카테고리를 BIT_GROUND로 설정
        fdef.filter.categoryBits = Constants.BIT_GROUND;
        // 모든 것과 부딪히도록 설정 (-1)
        fdef.filter.maskBits = -1;

        body.createFixture(fdef).setUserData("ground");
        shape.dispose();
    }
    private void createWall(RectangleMapObject rectObject) {
        Rectangle rect = rectObject.getRectangle();
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set((rect.x + rect.width / 2) / Constants.PPM, (rect.y + rect.height / 2) / Constants.PPM);

        Body body = world.createBody(bdef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(rect.width / 2 / Constants.PPM, rect.height / 2 / Constants.PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.friction = 0.5f;

        // 중요: 이 객체의 카테고리를 BIT_GROUND로 설정
        fdef.filter.categoryBits = Constants.BIT_WALL;
        // 모든 것과 부딪히도록 설정 (-1)
        fdef.filter.maskBits = -1;

        body.createFixture(fdef).setUserData("Wall");
        shape.dispose();
    }

    public void dispose() {
        if (tiledMap != null) tiledMap.dispose();
    }
}
