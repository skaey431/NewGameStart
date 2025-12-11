package NewGameStart.NewGame.screens.managers;

import NewGameStart.NewGame.entities.player.Player;
import NewGameStart.NewGame.Main; // Main 클래스가 존재한다고 가정
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class GameStateManager {

    private final Main game;
    private final Player player;

    // --- 리스폰 및 사망 관련 필드 ---
    private Vector2 currentCheckpoint = new Vector2(3f, 5f);
    private boolean isGameOver = false;
    private float gameOverTimer = 0f;
    private final float GAME_OVER_DURATION = 3.0f; // 3초 자동 리스폰 타이머

    private final Label gameOverLabel;

    public GameStateManager(Main game, Player player, Label gameOverLabel) {
        this.game = game;
        this.player = player;
        this.gameOverLabel = gameOverLabel;
    }

    /**
     * 현재 리스폰 지점을 업데이트합니다. (체크포인트 기능)
     */
    public void setCheckpoint(float x, float y) {
        currentCheckpoint.set(x, y);
        Gdx.app.log("GameStateManager", "Checkpoint updated to: " + x + ", " + y);
    }

    /**
     * 플레이어가 사망했을 때 호출되어 게임 오버 상태로 전환합니다.
     */
    public void handleGameOver() {
        if (isGameOver) return;

        isGameOver = true;
        gameOverTimer = 0f;

        // 플레이어의 물리 상호작용 비활성화 및 멈춤
        player.getBody().setActive(false);
        player.getBody().setLinearVelocity(0, 0);

        if (gameOverLabel != null) {
            gameOverLabel.setText("GAME OVER\n(Press SPACE/ENTER to skip | Respawning in " + (int)GAME_OVER_DURATION + "s)");
            gameOverLabel.setVisible(true);
            gameOverLabel.pack();
        }
        Gdx.app.log("GameStateManager", "Player has died! Starting game over sequence.");
    }

    /**
     * 게임 오버 상태의 타이머를 업데이트하고 리스폰 키 입력을 처리합니다.
     */
    public void update(float delta) {
        if (!isGameOver) return;

        gameOverTimer += delta;

        // 1. 타이머 기반 자동 리스폰
        if (gameOverTimer >= GAME_OVER_DURATION) {
            respawnPlayer();
            return;
        }

        // 2. 키 입력 기반 즉시 리스폰
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            Gdx.app.log("GameStateManager", "Immediate respawn requested.");
            respawnPlayer();
            return;
        }

        // 3. UI 타이머 업데이트
        if (gameOverLabel != null) {
            int remaining = (int)Math.ceil(GAME_OVER_DURATION - gameOverTimer);
            if (remaining >= 0) {
                gameOverLabel.setText("GAME OVER\n(Press SPACE/ENTER to skip | Respawning in " + remaining + "s)");
            }
        }
    }

    /**
     * 플레이어를 체크포인트 위치로 이동시키고 상태를 복구합니다.
     */
    public void respawnPlayer() {
        isGameOver = false;
        gameOverTimer = 0f;

        // 1. Player 상태 복구
        player.resetHealth();
        player.resetAbilities();
        player.getBody().setActive(true);
        player.getBody().setLinearVelocity(0, 0);

        // 2. 체크포인트 위치로 이동
        player.getBody().setTransform(currentCheckpoint.x, currentCheckpoint.y, 0);

        // 3. UI 업데이트
        if (gameOverLabel != null) {
            gameOverLabel.setVisible(false);
        }

        Gdx.app.log("GameStateManager", "Player Respawned at (" + currentCheckpoint.x + ", " + currentCheckpoint.y + ")");
    }

    public boolean isGameOver() {
        return isGameOver;
    }
}
