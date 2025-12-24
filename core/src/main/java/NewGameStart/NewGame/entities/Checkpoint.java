package NewGameStart.NewGame.entities;

import NewGameStart.NewGame.tools.Constants;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Checkpoint {

    private final Rectangle bounds;
    private final Vector2 spawnPosition;
    private boolean isActivated;
    private Body body; // 물리 바디 추가

    /**
     * @param world 물리 월드
     * @param x Tiled 좌표 X
     * @param y Tiled 좌표 Y
     * @param width 너비
     * @param height 높이
     */
    public Checkpoint(World world, float x, float y, float width, float height) {
        // Tiled 픽셀 좌표를 저장
        this.bounds = new Rectangle(x, y, width, height);
        // 부활 지점은 박스의 중앙으로 설정 (PPM 변환 적용)
        this.spawnPosition = new Vector2((x + width / 2f) / Constants.PPM, (y + height / 2f) / Constants.PPM);
        this.isActivated = false;

        // Box2D 바디 생성
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(spawnPosition.x, spawnPosition.y);
        this.body = world.createBody(bdef);
        this.body.setUserData(this);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2f / Constants.PPM, height / 2f / Constants.PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.isSensor = true; // 플레이어가 겹칠 수 있게 센서 설정
        fdef.filter.categoryBits = Constants.BIT_CHECKPOINT; // Constants에 비트 정의 필요
        fdef.filter.maskBits = Constants.CATEGORY_PLAYER;

        body.createFixture(fdef).setUserData("checkpoint");
        shape.dispose();
    }

    public Body getBody() { return body; }
    public Rectangle getBounds() { return bounds; }
    public Vector2 getSpawnPosition() { return spawnPosition; }
    public boolean isActivated() { return isActivated; }

    public void activate() { this.isActivated = true; }
    public void deactivate() { this.isActivated = false; }

    // 물리 기반으로 바뀌었으므로 거리를 수동으로 계산할 필요 없이
    // ContactListener에서 닿았을 때 감지하면 됩니다.
}
