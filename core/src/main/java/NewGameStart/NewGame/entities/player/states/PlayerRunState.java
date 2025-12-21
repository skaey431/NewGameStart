package NewGameStart.NewGame.entities.player.states;

import NewGameStart.NewGame.entities.player.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

public class PlayerRunState implements PlayerState {

    @Override
    public void enter(Player player) {
        // 지상 상태 진입 시 점프 횟수 초기화
        player.jumpsPerformed = 0;
    }

    @Override
    public void update(Player player, float delta) {
        boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean space = Gdx.input.isKeyJustPressed(Input.Keys.SPACE);
        Vector2 vel = player.getBody().getLinearVelocity();

        // 1. 점프 입력 확인 (최우선)
        if (space) {
            player.jumpsPerformed = 1; // 첫 번째 점프 카운트
            player.changeState(new PlayerJumpState());
            player.getBody().applyLinearImpulse(new Vector2(0, player.JUMP_FORCE), player.getBody().getWorldCenter(), true);
            return;
        }

        // 2. 입력에 따른 수평 이동 처리
        if (left) {
            player.getBody().setLinearVelocity(-player.MOVE_SPEED, vel.y);
        } else if (right) {
            player.getBody().setLinearVelocity(player.MOVE_SPEED, vel.y);
        } else {
            // 키를 떼면 Idle 상태로 전환
            player.changeState(new PlayerIdleState());
            return;
        }

        // 3. 지면 이탈 확인 (낙하 시작)
        if (!player.isOnGround()) {
            player.changeState(new PlayerJumpState());
            return;
        }
    }

    @Override
    public void exit(Player player) {
        // Run 상태를 빠져나갈 때 특별히 처리할 것은 없음
    }

    @Override
    public String getName() {
        return "Run";
    }
}
