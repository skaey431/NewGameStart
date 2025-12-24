package NewGameStart.NewGame.screens.managers;

import NewGameStart.NewGame.entities.DamageBox;
import NewGameStart.NewGame.entities.InstantKillBox;
import NewGameStart.NewGame.entities.Checkpoint;
import NewGameStart.NewGame.entities.monster.StaticMonster;
import NewGameStart.NewGame.entities.player.Player;
import com.badlogic.gdx.utils.Array;

public class EntityManager {

    private final GameStateManager gameStateManager;
    private final Player player;

    private Array<DamageBox> damageBoxes;
    private Array<InstantKillBox> killBoxes;
    private Array<Checkpoint> checkpoints;
    private Array<StaticMonster> monsters;

    public EntityManager(GameStateManager gameStateManager, Player player) {
        this.gameStateManager = gameStateManager;
        this.player = player;
        this.player.setEntityManager(this);

        damageBoxes = new Array<>();
        killBoxes = new Array<>();
        monsters = new Array<>();
        checkpoints = new Array<>();
    }

    // [복구] PlayerAttackState에서 호출하는 공격 처리 메서드
    public void processPlayerAttack() {
        for (StaticMonster monster : monsters) {
            if (monster.isAlive() && player.getAttackHitbox().overlaps(monster.getBounds())) {
                // 플레이어의 공격 히트박스와 몬스터의 bounds가 겹치면 데미지 전달
                // 수정 필요
                monster.takeDamage(10);
                System.out.println("Monster Hit! Remaining Health: " + monster.getCurrentHealth());
            }
        }
    }

    public void update(float delta) {
        for (StaticMonster monster : monsters) {
            monster.update(delta);
        }
    }

    public void clearEntities() {
        // 물리 바디는 MapManager에서 지우므로 리스트만 비웁니다.
        damageBoxes.clear();
        killBoxes.clear();
        monsters.clear();
        checkpoints.clear();
        System.out.println("EntityManager: 리스트 초기화 완료.");
    }

    // Getter들
    public Array<StaticMonster> getMonsters() { return monsters; }
    public Array<DamageBox> getDamageBoxes() { return damageBoxes; }
    public Array<Checkpoint> getCheckpoints() { return checkpoints; }
    public Array<InstantKillBox> getKillBoxes() { return killBoxes; }

    public boolean isInteractPromptVisible() { return false; }
}
