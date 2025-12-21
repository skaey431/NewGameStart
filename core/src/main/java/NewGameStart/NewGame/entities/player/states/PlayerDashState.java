package NewGameStart.NewGame.entities.player.states;

import NewGameStart.NewGame.entities.player.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class PlayerDashState implements PlayerState {

    private final float DASH_DURATION = 0.15f;
    private final float DASH_SPEED = 15f;
    private float timer = 0f;
    private float dashDirection = 0f;

    @Override
    public void enter(Player player) {
        timer = DASH_DURATION;
        player.isDashing = true;
        player.dashCooldownTimer = player.DASH_COOLDOWN;

        // ⭐ 대쉬 횟수 증가 로직 추가
        player.dashesPerformed++;

        // 대쉬 방향 설정
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            dashDirection = -1;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            dashDirection = 1;
        }

        // 벽에 매달릴 때 공중 능력 초기화 (대쉬 및 점프 횟수 0으로 리셋)
        player.jumpsPerformed = 0;

        // 중력 무시 및 대쉬 속도 적용
        player.getBody().setGravityScale(0);
        player.getBody().setLinearVelocity(dashDirection * DASH_SPEED, 0);
    }

    @Override
    public void update(Player player, float delta) {
        timer -= delta;

        if (timer <= 0) {
            // 대쉬가 끝나면 다음 상태로 전환
            if (player.isOnGround()) {
                player.changeState(new PlayerIdleState());
            } else if (player.isTouchingLeft() || player.isTouchingRight()) {
                player.changeState(new PlayerClingState());
            } else {
                player.changeState(new PlayerAirState());
            }
        }
    }

    @Override
    public void exit(Player player) {
        player.isDashing = false;
        // 중력 스케일 복원
        player.getBody().setGravityScale(1);
        // 대쉬 종료 후 즉시 속도 초기화 (관성 제거)
        player.getBody().setLinearVelocity(0, player.getBody().getLinearVelocity().y);
    }

    @Override
    public String getName() {
        return "Dash";
    }
}
