package NewGameStart.NewGame.entities;

import com.badlogic.gdx.math.Rectangle;

/**
 * 플레이어에게 데미지를 입히는 고정된 위험 구역(예: 용암, 독성 가스).
 */
public class DamageBox {

    // 구역의 위치와 크기를 나타내는 충돌 경계
    private Rectangle bounds;
    // 이 구역이 입히는 데미지 양
    private final float damageAmount;
    // 데미지를 입힐 간격 (초)
    private final float damageInterval;

    public DamageBox(float x, float y, float width, float height, float damageAmount, float damageInterval) {
        this.bounds = new Rectangle(x, y, width, height);
        this.damageAmount = damageAmount;
        this.damageInterval = damageInterval;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public float getDamageAmount() {
        return damageAmount;
    }

    public float getDamageInterval() {
        return damageInterval;
    }

    // 필요에 따라 렌더링을 위한 메서드를 추가할 수 있습니다.
}
