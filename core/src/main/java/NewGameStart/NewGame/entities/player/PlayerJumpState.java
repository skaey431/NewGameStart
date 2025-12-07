package NewGameStart.NewGame.entities.player;

import NewGameStart.NewGame.entities.player.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

public class PlayerJumpState implements PlayerState {

    @Override
    public void enter(Player player) {
        // Jump/Fall 상태 진입 시 특별히 초기화할 것은 없음
    }

    @Override
    public void update(Player player, float delta) {
        boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean spaceJustPressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE);
        Vector2 vel = player.getBody().getLinearVelocity();

        // 1. 지면 착지 확인
        if (player.isOnGround()) {
            player.changeState(new PlayerIdleState());
            return;
        }

        // 2. 더블 점프 로직
        if (spaceJustPressed && player.jumpsPerformed < player.MAX_JUMPS) {
            player.jumpsPerformed++; // 점프 횟수 증가 (2가 됨)

            // 기존 수직 속도를 0으로 초기화하고 새로운 점프 힘을 적용
            player.getBody().setLinearVelocity(vel.x, 0);
            player.getBody().applyLinearImpulse(new Vector2(0, player.JUMP_FORCE), player.getBody().getWorldCenter(), true);
            return;
        }

        // 3. 벽 클링 조건 확인
        boolean canCling = player.wallJumpTimer <= 0;
        if (canCling && (player.isTouchingLeft() && left || player.isTouchingRight() && right)) {
            player.changeState(new PlayerClingState());
            return;
        }

        // 4. 공중 이동 처리 (벽 점프 쿨다운 중 입력 무시)
        float targetVelocityX = 0;

        if (player.wallJumpTimer > 0) {
            targetVelocityX = vel.x;
        } else {
            // 벽에 닿았을 때는 반대 방향으로 이동하려고 하지 않는 이상 속도 유지 (벽에 붙지 않음)
            if (left) {
                // 왼쪽 이동: 왼쪽 벽에 닿았으면 속도 0, 아니면 이동 속도
                targetVelocityX = player.isTouchingLeft() ? 0 : -player.MOVE_SPEED;
            } else if (right) {
                // 오른쪽 이동: 오른쪽 벽에 닿았으면 속도 0, 아니면 이동 속도
                targetVelocityX = player.isTouchingRight() ? 0 : player.MOVE_SPEED;
            } else {
                targetVelocityX = vel.x;
            }
        }

        player.getBody().setLinearVelocity(targetVelocityX, vel.y);
    }

    @Override
    public void exit(Player player) {
        // Jump 상태를 빠져나갈 때 특별히 처리할 것은 없음
    }

    @Override
    public String getName() {
        return "Jump/Fall";
    }
}
