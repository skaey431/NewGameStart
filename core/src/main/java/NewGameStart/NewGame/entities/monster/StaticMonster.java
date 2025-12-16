package NewGameStart.NewGame.entities.monster;

import NewGameStart.NewGame.entities.BaseEntity; // BaseEntity import
import com.badlogic.gdx.math.Rectangle;

/**
 * AI 로직 없이 특정 위치에 고정되어 플레이어에게 데미지를 입히는 몬스터 엔티티입니다.
 */
public class StaticMonster extends BaseEntity { // BaseEntity 상속

    private final Rectangle bounds; // BaseEntity에는 bounds가 없으므로 여기서 관리
    private final float attackDamage;

    /**
     * 정적 몬스터 객체를 생성합니다.
     * @param initialHealth 몬스터의 초기 체력 (BaseEntity의 MAX_HEALTH를 사용하지 않고 개별 설정 가능하게 함)
     * @param x 몬스터의 X 위치 (월드 좌표)
     * @param y 몬스터의 Y 위치 (월드 좌표)
     * @param width 충돌 영역 너비
     * @param height 충돌 영역 높이
     * @param attackDamage 플레이어에게 입힐 데미지 양
     */
    public StaticMonster(float initialHealth, float x, float y, float width, float height, float attackDamage) {
        super(); // BaseEntity의 생성자 호출 (MAX_HEALTH=100 설정)
        // BaseEntity의 currentHealth를 오버라이드하여 몬스터 체력을 설정할 수 있습니다.
        // 하지만 BaseEntity가 MAX_HEALTH를 final로 100으로 고정했으므로,
        // 몬스터 체력도 100으로 고정됩니다. (이 부분은 BaseEntity 설계에 따름)

        // BaseEntity에 update 메서드가 abstract로 선언되었으므로, 빈 구현을 추가합니다.
        // 몬스터는 현재 AI가 없으므로 update는 비워둡니다.

        this.bounds = new Rectangle(x, y, width, height);
        this.attackDamage = attackDamage;
    }

    // BaseEntity의 abstract update 메서드 구현
    @Override
    public void update(float delta) {
        // AI가 없으므로 비워둡니다.
    }

    // BaseEntity에 bounds가 없으므로 Getter를 추가합니다.
    public Rectangle getBounds() {
        return bounds;
    }

    public float getAttackDamage() {
        return attackDamage;
    }
}
