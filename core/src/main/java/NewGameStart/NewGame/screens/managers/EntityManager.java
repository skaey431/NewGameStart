package NewGameStart.NewGame.screens.managers;

import NewGameStart.NewGame.entities.DamageBox;
import NewGameStart.NewGame.entities.InstantKillBox;
import NewGameStart.NewGame.entities.Checkpoint;
import NewGameStart.NewGame.entities.monster.StaticMonster;
import NewGameStart.NewGame.entities.player.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/**
 * 게임 월드 내의 모든 엔티티(위험 요소, 상호작용 요소 등)의 생성, 관리 및 플레이어와의 상호작용 로직을 담당합니다.
 */
public class EntityManager {

    private final GameStateManager gameStateManager;
    private final Player player;

    private Array<DamageBox> damageBoxes;
    private Array<InstantKillBox> killBoxes;
    private Array<Checkpoint> checkpoints;
    private Array<StaticMonster> monsters;

    private float damageTimer = 0f;
    private final float DAMAGE_CHECK_RATE = 0.5f; // 0.5초마다 데미지 체크
    private final float CHECKPOINT_INTERACT_DISTANCE = 1.5f;

    private String interactPromptText = null;
    private boolean isInteractPromptVisible = false;

    public EntityManager(GameStateManager gameStateManager, Player player) {
        this.gameStateManager = gameStateManager;
        this.player = player;

        createHazards();
        createCheckpoints();
    }

    private void createHazards() {
        damageBoxes = new Array<>();
        killBoxes = new Array<>();
        monsters = new Array<>();

        // --- DamageBox 예시 ---
        // (x, y, width, height, damageAmount, damageRate)
        damageBoxes.add(new DamageBox(10f, 1f, 3f, 0.5f, 5f, DAMAGE_CHECK_RATE));
        damageBoxes.add(new DamageBox(20f, 5f, 2f, 2f, 10f, DAMAGE_CHECK_RATE));

        // --- InstantKillBox 예시 ---
        // (x, y, width, height)
        killBoxes.add(new InstantKillBox(-10f, -5f, 50f, 4f));
        killBoxes.add(new InstantKillBox(25f, 0.5f, 3f, 0.5f));

        // --- StaticMonster 예시 (BaseEntity 상속 시 몬스터 체력 100으로 고정) ---
        // (initialHealth, x, y, width, height, attackDamage)
        monsters.add(new StaticMonster(100f, 15f, 2.5f, 1f, 1f, 20f)); // 체력 100, 공격력 20
        monsters.add(new StaticMonster(100f, 40f, 5f, 1f, 2f, 30f)); // 체력 100, 공격력 30
    }

    private void createCheckpoints() {
        checkpoints = new Array<>();

        // (x, y, width, height, spawnX, spawnY)
        checkpoints.add(new Checkpoint(8f, 1.5f, 1f, 2f, 8f, 2.5f));
        checkpoints.add(new Checkpoint(30f, 7.5f, 1f, 2f, 30f, 8.5f));
    }

    public void update(float delta) {
        if (!player.isAlive()) return;

        // 몬스터 상태 업데이트 (BaseEntity의 update() 구현 호출)
        for (StaticMonster monster : monsters) {
            monster.update(delta);
        }

        checkHazards(delta);
        checkCheckpointInteraction();
    }

    private void checkHazards(float delta) {
        if (!player.isAlive()) return;

        Rectangle playerBounds = player.getBounds();

        // 1. 즉사 박스 충돌 검사 (닿는 즉시 사망 - 시간 제한 없음)
        for (InstantKillBox killBox : killBoxes) {
            if (playerBounds.overlaps(killBox.getBounds())) {
                player.takeDamage(player.getMaxHealth());
                gameStateManager.handleGameOver();
                return;
            }
        }

        // 2. 일반 데미지 및 몬스터 충돌 검사 (시간 제한 적용)
        damageTimer += delta;
        if (damageTimer >= DAMAGE_CHECK_RATE) {

            // 2-1. StaticMonster 충돌 검사 (시간 제한 적용)
            for (StaticMonster monster : monsters) {
                // 몬스터가 살아있고 플레이어와 닿았을 때만 피해 적용
                if (monster.isAlive() && playerBounds.overlaps(monster.getBounds())) {
                    // 몬스터가 가진 attackDamage만큼 플레이어에게 피해를 입힙니다.
                    player.takeDamage(monster.getAttackDamage());

                    if (!player.isAlive()) {
                        gameStateManager.handleGameOver();
                        return;
                    }
                }
            }

            // 2-2. DamageBox 충돌 검사 (시간 제한 적용)
            for (DamageBox box : damageBoxes) {
                if (playerBounds.overlaps(box.getBounds())) {
                    player.takeDamage(box.getDamageAmount());

                    if (!player.isAlive()) {
                        gameStateManager.handleGameOver();
                        return;
                    }
                }
            }

            damageTimer -= DAMAGE_CHECK_RATE;
        }
    }

    private void checkCheckpointInteraction() {
        if (gameStateManager.isGameOver() || !player.isAlive()) {
            isInteractPromptVisible = false;
            return;
        }

        Checkpoint currentInteractableCheckpoint = null;

        for (Checkpoint cp : checkpoints) {
            if (cp.isPlayerNear(
                player.getBody().getPosition().x,
                player.getBody().getPosition().y,
                CHECKPOINT_INTERACT_DISTANCE)) {

                currentInteractableCheckpoint = cp;
                break;
            }
        }

        if (currentInteractableCheckpoint != null) {
            isInteractPromptVisible = true;

            if (currentInteractableCheckpoint.isActivated()) {
                interactPromptText = "Press C to Re-set Checkpoint";
            } else {
                interactPromptText = "Press C to Set Checkpoint";
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {

                for (Checkpoint cp : checkpoints) {
                    cp.deactivate();
                }

                currentInteractableCheckpoint.activate();
                gameStateManager.setCheckpoint(
                    currentInteractableCheckpoint.getSpawnPosition().x,
                    currentInteractableCheckpoint.getSpawnPosition().y
                );

                interactPromptText = "Checkpoint Saved!";
            }
        } else {
            isInteractPromptVisible = false;
            interactPromptText = "Press C to Set Checkpoint";
        }
    }

    // --- GameScreen이 렌더링을 위해 사용하는 Getter 메서드 ---

    public Array<DamageBox> getDamageBoxes() {
        return damageBoxes;
    }

    public Array<InstantKillBox> getKillBoxes() {
        return killBoxes;
    }

    public Array<Checkpoint> getCheckpoints() {
        return checkpoints;
    }

    public Array<StaticMonster> getMonsters() {
        return monsters;
    }

    public String getInteractPromptText() {
        return interactPromptText;
    }

    public boolean isInteractPromptVisible() {
        return isInteractPromptVisible;
    }
}
