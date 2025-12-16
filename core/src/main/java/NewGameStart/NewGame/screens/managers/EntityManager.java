package NewGameStart.NewGame.screens.managers;

import NewGameStart.NewGame.entities.DamageBox;
import NewGameStart.NewGame.entities.InstantKillBox;
import NewGameStart.NewGame.entities.Checkpoint;
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

    private float damageTimer = 0f;
    private final float DAMAGE_CHECK_RATE = 0.5f;
    private final float CHECKPOINT_INTERACT_DISTANCE = 1.5f;

    // UI 레이블을 직접 참조하지 않고, 상호작용 프롬프트 텍스트를 GameScreen에 전달하기 위한 필드
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

        // 데미지 박스 예시 (기존 GameScreen에서 이동)
        damageBoxes.add(new DamageBox(10f, 1f, 3f, 0.5f, 5f, DAMAGE_CHECK_RATE));
        damageBoxes.add(new DamageBox(20f, 5f, 2f, 2f, 10f, DAMAGE_CHECK_RATE));

        // 즉사 박스 예시 (기존 GameScreen에서 이동)
        killBoxes.add(new InstantKillBox(-10f, -5f, 50f, 4f));
        killBoxes.add(new InstantKillBox(25f, 0.5f, 3f, 0.5f));
    }

    private void createCheckpoints() {
        checkpoints = new Array<>();

        // 체크포인트 예시 (기존 GameScreen에서 이동)
        checkpoints.add(new Checkpoint(8f, 1.5f, 1f, 2f, 8f, 2.5f));
        checkpoints.add(new Checkpoint(30f, 7.5f, 1f, 2f, 30f, 8.5f));
    }

    /**
     * 모든 엔티티의 로직을 업데이트하고 플레이어와의 충돌을 처리합니다.
     */
    public void update(float delta) {
        if (!player.isAlive()) return;

        // 1. 위험 요소 충돌 처리
        checkHazards(delta);

        // 2. 체크포인트 상호작용 처리
        checkCheckpointInteraction();
    }

    private void checkHazards(float delta) {
        if (!player.isAlive()) return;

        Rectangle playerBounds = player.getBounds();
        boolean killed = false;

        // 1. 즉사 박스 충돌 검사
        for (InstantKillBox killBox : killBoxes) {
            if (playerBounds.overlaps(killBox.getBounds())) {
                player.takeDamage(player.getMaxHealth());
                killed = true;
                break;
            }
        }

        if (killed) {
            gameStateManager.handleGameOver();
            return;
        }

        // 2. 일반 데미지 박스 충돌 검사
        damageTimer += delta;
        if (damageTimer >= DAMAGE_CHECK_RATE) {

            for (DamageBox box : damageBoxes) {
                if (playerBounds.overlaps(box.getBounds())) {
                    player.takeDamage(box.getDamageAmount());

                    if (!player.isAlive()) {
                        gameStateManager.handleGameOver();
                        break;
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

        // 1. 플레이어 근처의 체크포인트를 찾습니다.
        for (Checkpoint cp : checkpoints) {
            if (cp.isPlayerNear(
                player.getBody().getPosition().x,
                player.getBody().getPosition().y,
                CHECKPOINT_INTERACT_DISTANCE)) {

                currentInteractableCheckpoint = cp;
                break;
            }
        }

        // 2. 상호작용 프롬프트 텍스트 설정
        if (currentInteractableCheckpoint != null) {
            isInteractPromptVisible = true;

            if (currentInteractableCheckpoint.isActivated()) {
                interactPromptText = "Press C to Re-set Checkpoint";
            } else {
                interactPromptText = "Press C to Set Checkpoint";
            }

            // 3. 키 입력 처리
            if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {

                // 모든 체크포인트 비활성화
                for (Checkpoint cp : checkpoints) {
                    cp.deactivate();
                }

                // 현재 체크포인트 활성화 및 GameStateManager 업데이트
                currentInteractableCheckpoint.activate();
                gameStateManager.setCheckpoint(
                    currentInteractableCheckpoint.getSpawnPosition().x,
                    currentInteractableCheckpoint.getSpawnPosition().y
                );

                interactPromptText = "Checkpoint Saved!";
            }
        } else {
            isInteractPromptVisible = false;
            interactPromptText = "Press C to Set Checkpoint"; // 기본 텍스트 리셋
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

    public String getInteractPromptText() {
        return interactPromptText;
    }

    public boolean isInteractPromptVisible() {
        return isInteractPromptVisible;
    }
}
