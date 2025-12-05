package NewGameStart.NewGame.entities.player;

import NewGameStart.NewGame.entities.player.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class PlayerDashState implements PlayerState {

    private final float DASH_DURATION = 0.15f;
    private final float DASH_SPEED = 15f;
    private float timer = 0f;
    private float dashDirection = 0f; // -1 (Left) or 1 (Right)

    @Override
    public void enter(Player player) {
        timer = DASH_DURATION;
        player.isDashing = true;

        // 진입 시 방향 설정
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            dashDirection = -1;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            dashDirection = 1;
        }

        // 중력 무시 및 대쉬 속도 적용
        player.getBody().setGravityScale(0);
        player.getBody().setLinearVelocity(dashDirection * DASH_SPEED, 0);
    }

    @Override
    public void update(Player player, float delta) {
        timer -= delta;

        // 대쉬 중에는 속도를 강제 유지
        player.getBody().setLinearVelocity(dashDirection * DASH_SPEED, 0);

        if (timer <= 0) {
            // 대쉬 종료 후 방향키 입력 여부에 따라 Run 또는 Idle 상태로 복귀
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                player.changeState(new PlayerRunState());
            } else {
                player.changeState(new PlayerIdleState());
            }
        }
    }

    @Override
    public void exit(Player player) {
        player.isDashing = false;
        player.getBody().setGravityScale(1); // 중력 복원
        player.dashCooldownTimer = player.DASH_COOLDOWN; // 쿨다운 시작
    }

    @Override
    public String getName() {
        return "Dash";
    }
}
