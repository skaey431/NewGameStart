package NewGameStart.NewGame.entities.player.states;

import NewGameStart.NewGame.entities.player.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

public class PlayerIdleState implements PlayerState {

    @Override
    public void enter(Player player) {
        // 지상 상태 진입 시 점프 횟수 초기화
        player.jumpsPerformed = 0;

        // Idle 상태 진입 시 수평 속도를 0으로 설정하여 정지
        player.getBody().setLinearVelocity(0, player.getBody().getLinearVelocity().y);
    }

    @Override
    public void update(Player player, float delta) {
        boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean space = Gdx.input.isKeyJustPressed(Input.Keys.SPACE);

        // 1. 점프 입력 확인
        if (space) {
            player.jumpsPerformed = 1; // 첫 번째 점프 카운트
            player.changeState(new PlayerJumpState());
            player.getBody().applyLinearImpulse(new Vector2(0, player.JUMP_FORCE), player.getBody().getWorldCenter(), true);
            return;
        }

        // 2. 좌우 이동 입력 확인
        if (left || right) {
            player.changeState(new PlayerRunState());
            return;
        }

        // 3. 지면 이탈 확인 (낙하 시작)
        if (!player.isOnGround()) {
            player.changeState(new PlayerJumpState());
            return;
        }

        // Idle 상태에서는 속도 0 유지 (enter에서 이미 처리됨)
    }

    @Override
    public void exit(Player player) {
        // Idle 상태를 빠져나갈 때 특별히 처리할 것은 없음
    }

    @Override
    public String getName() {
        return "Idle";
    }
}
