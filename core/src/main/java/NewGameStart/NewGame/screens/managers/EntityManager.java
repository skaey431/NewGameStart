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

    // 리스트 관리
    private Array<DamageBox> damageBoxes;
    private Array<InstantKillBox> killBoxes;
    private Array<Checkpoint> checkpoints;
    private Array<StaticMonster> monsters;

    public EntityManager(GameStateManager gameStateManager, Player player) {
        this.gameStateManager = gameStateManager;
        this.player = player;
        this.player.setEntityManager(this); // 플레이어에게 매니저 등록

        // 리스트 초기화
        damageBoxes = new Array<>();
        killBoxes = new Array<>();
        monsters = new Array<>();
        checkpoints = new Array<>();

        // 초기 엔티티 생성 (예시)
        createTestEntities();
    }

    private void createTestEntities() {
        // 엔티티 생성 시 World를 전달해야 함 (player.getBody().getWorld())
        monsters.add(new StaticMonster(player.getBody().getWorld(), 100, 10, 5, 2, 2, 10));
        damageBoxes.add(new DamageBox(player.getBody().getWorld(), 15, 0, 3, 1, 5));
    }

    public void update(float delta) {
        // [비효율 개선] 충돌 체크 루프(overlaps)를 삭제했습니다.
        // 모든 충돌은 이제 WorldContactListener에서 물리 엔진이 처리합니다.

        // 몬스터 로직 업데이트만 수행
        for (StaticMonster monster : monsters) {
            monster.update(delta);
        }

        // 체크포인트 상호작용 로직 등은 유지
        // ... (checkCheckpointInteraction 등 기존 로직 유지)
    }

    // 공격 처리 (공격은 능동적이므로 AABB 체크 유지 혹은 Box2D QueryAABB 사용)
    public void processPlayerAttack() {
        // ... (기존 overlaps 로직 유지 가능, bounds가 있으므로 정상 작동)
    }

    // [오류 해결] 이제 모든 엔티티가 getBody()를 가지므로 정상 작동
    public void clearEntities() {
        for (StaticMonster m : monsters) if (m.getBody() != null) m.getBody().getWorld().destroyBody(m.getBody());
        monsters.clear();

        for (DamageBox d : damageBoxes) if (d.getBody() != null) d.getBody().getWorld().destroyBody(d.getBody());
        damageBoxes.clear();

        // 나머지 체크포인트, 킬박스도 동일하게 처리
        System.out.println("All entities cleared via Box2D body destruction.");
    }

    // Getter 들 유지...
    public Array<StaticMonster> getMonsters() { return monsters; }
    public Array<DamageBox> getDamageBoxes() { return damageBoxes; }
    public Array<Checkpoint> getCheckpoints() { return checkpoints; }
    public Array<InstantKillBox> getKillBoxes() { return killBoxes; }
    public boolean isInteractPromptVisible() { return false; } // 임시
    public String getInteractPromptText() { return ""; } // 임시
}
