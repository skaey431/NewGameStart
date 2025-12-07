package NewGameStart.NewGame.entities.player;

import NewGameStart.NewGame.entities.player.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

/**
 * 플레이어가 공중에 떠 있는 상태 (점프, 낙하, 관성 이동 등)를 관리합니다.
 */
public class PlayerAirState implements PlayerState {

    @Override
    public void enter(Player player) {
        // 공중 상태 진입 시 특별한 초기화 작업은 없습니다.
    }

    @Override
    public void update(Player player, float delta) {

        // 1. 좌우 이동 처리 (공중 조작)
        Vector2 velocity = player.getBody().getLinearVelocity();
        float targetSpeed = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            targetSpeed = -player.MOVE_SPEED;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            targetSpeed = player.MOVE_SPEED;
        }

        // 공중에서는 지상보다 가속도를 낮게 적용하여 조작감을 만듭니다.
        float currentSpeed = velocity.x;
        float speedDifference = targetSpeed - currentSpeed;
        float newVelocityX = currentSpeed + speedDifference * 0.1f; // 0.1f는 공중 가속도 계수

        player.getBody().setLinearVelocity(newVelocityX, velocity.y);

        // 2. 상태 전환 체크

        // 벽 점프 쿨다운 중이 아닐 때만 벽에 붙을 수 있습니다.
        if (player.wallJumpTimer <= 0) {

            // 왼쪽 벽 매달리기 감지: (왼쪽 벽에 닿아 있고) AND (왼쪽 키를 누르고 있을 때)
            if (player.isTouchingLeft() && Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                player.isClinging = true;
                player.changeState(new PlayerClingState());
                return;
            }

            // 오른쪽 벽 매달리기 감지: (오른쪽 벽에 닿아 있고) AND (오른쪽 키를 누르고 있을 때)
            if (player.isTouchingRight() && Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                player.isClinging = true;
                player.changeState(new PlayerClingState());
                return;
            }
        }

        // 지면 착지 감지
        if (player.isOnGround()) {
            player.changeState(new PlayerIdleState());
            return;
        }
    }

    @Override
    public void exit(Player player) {
        // 공중 상태 이탈 시 정리 작업은 없습니다.
    }

    @Override
    public String getName() {
        return "Air";
    }
}
