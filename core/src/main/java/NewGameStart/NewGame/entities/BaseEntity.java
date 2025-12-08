package NewGameStart.NewGame.entities;

import com.badlogic.gdx.Gdx;

/**
 * 게임 내 모든 엔티티가 상속받는 기본 클래스입니다.
 * 체력 및 생존 상태 관리 로직을 포함합니다.
 */
public abstract class BaseEntity {

    // 최대 체력 값 (상수)
    protected final float MAX_HEALTH = 100f;
    protected float currentHealth;
    protected boolean isAlive;

    public BaseEntity() {
        this.currentHealth = MAX_HEALTH;
        this.isAlive = true;
    }

    /**
     * 엔티티의 상태를 업데이트합니다. 상속받는 클래스에서 구현해야 합니다.
     */
    public abstract void update(float delta);

    /**
     * HealthBar에서 사용하기 위한 최대 체력 getter.
     * @return 최대 체력
     */
    public float getMaxHealth() {
        return MAX_HEALTH;
    }

    public float getCurrentHealth() {
        return currentHealth;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void takeDamage(float amount) {
        if (isAlive) {
            currentHealth -= amount;
            if (currentHealth <= 0) {
                currentHealth = 0;
                die();
            }
        }
    }

    public void heal(float amount) {
        if (isAlive) {
            currentHealth += amount;
            if (currentHealth > MAX_HEALTH) {
                currentHealth = MAX_HEALTH;
            }
        }

    }

    protected void die() {
        isAlive = false;
        Gdx.app.log("BaseEntity", "Entity has died.");
    }
}
