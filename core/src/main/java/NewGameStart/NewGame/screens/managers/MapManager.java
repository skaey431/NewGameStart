package NewGameStart.NewGame.screens.managers;

import NewGameStart.NewGame.tools.Constants;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;

public class MapManager {
    private TiledMap tiledMap;
    private World world;
    private ShapeRenderer shapeRenderer;

    public MapManager(World world) {
        this.world = world;
        this.shapeRenderer = new ShapeRenderer();
    }

    /**
     * .tmx 파일을 로드하고 물리 객체를 생성합니다.
     */
    public void loadMap(String fileName) {
        this.tiledMap = new TmxMapLoader().load(fileName);

        // "collision" 이라는 이름의 오브젝트 레이어를 분석합니다.
        if (tiledMap.getLayers().get("collision") != null) {
            for (MapObject object : tiledMap.getLayers().get("collision").getObjects()) {
                if (object instanceof RectangleMapObject) {
                    createPhysicsObject((RectangleMapObject) object);
                }
            }
        }
    }

    /**
     * Tiled의 사각형 데이터를 Box2D StaticBody로 변환합니다.
     */
    private void createPhysicsObject(RectangleMapObject rectObject) {
        Rectangle rect = rectObject.getRectangle();

        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;

        // Tiled 좌표를 Box2D 미터 단위로 변환 (중심점 기준)
        float worldX = (rect.x + rect.width / 2) / Constants.PPM;
        float worldY = (rect.y + rect.height / 2) / Constants.PPM;
        bdef.position.set(worldX, worldY);

        Body body = world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(rect.width / 2 / Constants.PPM, rect.height / 2 / Constants.PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.friction = 0.5f;
        // Constants에 정의된 비트 사용
        fdef.filter.categoryBits = Constants.BIT_GROUND;

        body.createFixture(fdef).setUserData("ground");
        shape.dispose();
    }

    /**
     * 이미지가 없으므로 맵의 물리적 경계를 화면에 선으로 그립니다.
     */
    public void renderDebug(com.badlogic.gdx.graphics.OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0, 1, 0, 1); // 녹색 선으로 표시

        if (tiledMap != null && tiledMap.getLayers().get("collision") != null) {
            for (MapObject object : tiledMap.getLayers().get("collision").getObjects()) {
                if (object instanceof RectangleMapObject) {
                    Rectangle r = ((RectangleMapObject) object).getRectangle();
                    // 렌더링 시에는 PPM을 나누지 않고 픽셀 단위로 그림 (또는 카메라 설정에 맞춤)
                    shapeRenderer.rect(r.x / Constants.PPM, r.y / Constants.PPM,
                        r.width / Constants.PPM, r.height / Constants.PPM);
                }
            }
        }
        shapeRenderer.end();
    }

    public void dispose() {
        if (tiledMap != null) tiledMap.dispose();
        shapeRenderer.dispose();
    }
}
