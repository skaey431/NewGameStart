package NewGameStart.NewGame.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * 플레이어가 상호작용하여 리스폰 지점을 저장할 수 있는 객체입니다.
 */
public class Checkpoint {

    private final Rectangle bounds;
    private final Vector2 spawnPosition;
    // 이 체크포인트가 현재 게임에서 마지막으로 활성화된 체크포인트인지를 나타냅니다.
    private boolean isActivated;

    /**
     * 체크포인트 객체를 생성합니다.
     * @param x 체크포인트 박스의 X 위치 (월드 좌표)
     * @param y 체크포인트 박스의 Y 위치 (월드 좌표)
     * @param width 충돌 영역 너비
     * @param height 충돌 영역 높이
     * @param spawnX 리스폰 지점 X
     * @param spawnY 리스폰 지점 Y
     */
    public Checkpoint(float x, float y, float width, float height, float spawnX, float spawnY) {
        this.bounds = new Rectangle(x, y, width, height);
        this.spawnPosition = new Vector2(spawnX, spawnY);
        this.isActivated = false;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public Vector2 getSpawnPosition() {
        return spawnPosition;
    }

    public boolean isActivated() {
        return isActivated;
    }

    /**
     * 이 체크포인트를 활성화(현재 리스폰 지점으로 설정)하고, 상태를 true로 설정합니다.
     * GameScreen에서 다른 체크포인트의 상태를 false로 재설정해야 합니다.
     */
    public void activate() {
        this.isActivated = true;
    }

    /**
     * 이 체크포인트의 활성화 상태를 비활성화(false)로 설정합니다.
     */
    public void deactivate() {
        this.isActivated = false;
    }

    /**
     * 플레이어가 상호작용할 수 있는 거리를 체크합니다.
     */
    public boolean isPlayerNear(float playerX, float playerY, float interactDistance) {
        float centerX = bounds.x + bounds.width / 2f;
        float centerY = bounds.y + bounds.height / 2f;

        float dx = playerX - centerX;
        float dy = playerY - centerY;

        return (dx * dx + dy * dy) <= (interactDistance * interactDistance);
    }
}
