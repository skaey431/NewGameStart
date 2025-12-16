package NewGameStart.NewGame.entities;

import com.badlogic.gdx.math.Rectangle;

/**
 * 플레이어가 접촉하는 즉시 사망(Instant Kill)시키는 구역입니다.
 */
public class InstantKillBox {

    // 구역의 위치와 크기를 나타내는 충돌 경계
    private final Rectangle bounds;

    /**
     * 즉사 박스 객체를 생성합니다.
     * @param x 박스 왼쪽 하단 X 좌표 (월드 좌표)
     * @param y 박스 왼쪽 하단 Y 좌표 (월드 좌표)
     * @param width 박스 너비
     * @param height 박스 높이
     */
    public InstantKillBox(float x, float y, float width, float height) {
        this.bounds = new Rectangle(x, y, width, height);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    // 이 박스는 별도의 데미지나 간격을 가지지 않습니다.
}
