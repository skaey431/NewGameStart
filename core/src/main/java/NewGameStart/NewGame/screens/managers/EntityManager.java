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

        // [수정된 부분] Player 클래스에 추가된 setEntityManager를 호출하여 상호 참조를 설정합니다.
        this.player.setEntityManager(this);

        createHazards();
        createCheckpoints();
    }

    private void createHazards() {
        damageBoxes = new Array<>();
        killBoxes = new Array<>();
        monsters = new Array<>();

        // --- DamageBox 예시 ---
        damageBoxes.add(new DamageBox(10f, 1f, 3f, 0.5f, 5f, DAMAGE_CHECK_RATE));
        damageBoxes.add(new DamageBox(20f, 5f, 2f, 2f, 10f, DAMAGE_CHECK_RATE));

        // --- InstantKillBox 예시 ---
        killBoxes.add(new InstantKillBox(-10f, -5f, 50f, 4f));
        killBoxes.add(new InstantKillBox(25f, 0.5f, 3f, 0.5f));

        // --- StaticMonster 예시 ---
        monsters.add(new StaticMonster(100f, 15f, 2.5f, 1f, 1f, 20f));
        monsters.add(new StaticMonster(100f, 40f, 5f, 1f, 2f, 30f));
    }

    private void createCheckpoints() {
        checkpoints = new Array<>();

        // (x, y, width, height, spawnX, spawnY)
        checkpoints.add(new Checkpoint(8f, 1.5f, 1f, 2f, 8f, 2.5f));
        checkpoints.add(new Checkpoint(30f, 7.5f, 1f, 2f, 30f, 8.5f));
    }

    // [신규 추가 메서드] 플레이어의 공격 상태에서 호출됩니다.
    public void processPlayerAttack() {
        Rectangle attackArea = player.getAttackHitbox();
        for (StaticMonster monster : monsters) {
            // 몬스터가 살아있고 플레이어의 공격 범위와 겹칠 경우 데미지 적용
            if (monster.isAlive() && attackArea.overlaps(monster.getBounds())) {
                monster.takeDamage(25f); // 공격 데미지 25 (조정 가능)
                Gdx.app.log("Combat", "Monster hit! Remaining HP: " + monster.getCurrentHealth());
            }
        }
    }

    public void update(float delta) {
        if (!player.isAlive()) return;

        // 몬스터 상태 업데이트
        for (StaticMonster monster : monsters) {
            monster.update(delta);
        }

        checkHazards(delta);
        checkCheckpointInteraction();
    }

    private void checkHazards(float delta) {
        if (!player.isAlive()) return;

        Rectangle playerBounds = player.getBounds();

        // 1. 즉사 박스 충돌 검사
        for (InstantKillBox killBox : killBoxes) {
            if (playerBounds.overlaps(killBox.getBounds())) {
                player.takeDamage(player.getMaxHealth());
                gameStateManager.handleGameOver();
                return;
            }
        }

        // 2. 일반 데미지 및 몬스터 충돌 검사
        damageTimer += delta;
        if (damageTimer >= DAMAGE_CHECK_RATE) {

            // 2-1. StaticMonster 충돌 검사
            for (StaticMonster monster : monsters) {
                if (monster.isAlive() && playerBounds.overlaps(monster.getBounds())) {
                    player.takeDamage(monster.getAttackDamage());

                    if (!player.isAlive()) {
                        gameStateManager.handleGameOver();
                        return;
                    }
                }
            }

            // 2-2. DamageBox 충돌 검사
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
