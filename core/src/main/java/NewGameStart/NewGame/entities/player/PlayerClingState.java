package NewGameStart.NewGame.entities.player;

import NewGameStart.NewGame.entities.player.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

public class PlayerClingState implements PlayerState {

    @Override
    public void enter(Player player) {
        player.isClinging = true;
        player.getBody().setGravityScale(0);
        player.getBody().setLinearVelocity(0, 0);
    }

    @Override
    public void update(Player player, float delta) {
        // 벽 점프 처리
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            player.changeState(new PlayerJumpState());

            // 벽 점프는 점프 횟수를 1로 초기화 (더블 점프 기회 1회 남김)
            player.jumpsPerformed = 1;

            // 벽 점프 동작 실행
            float jumpX = 0;
            float jumpY = player.WALL_JUMP_VERTICAL;

            if (player.isTouchingLeft()) {
                jumpX = player.WALL_JUMP_HORIZONTAL;
            } else if (player.isTouchingRight()) {
                jumpX = -player.WALL_JUMP_HORIZONTAL;
            }

            player.getBody().setLinearVelocity(jumpX, jumpY);
            player.wallJumpTimer = player.WALL_JUMP_COOLDOWN;
            return;
        }

        // 클링 해제 조건
        boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);

        if (player.isOnGround()) {
            player.changeState(new PlayerRunState());
            return;
        }

        // 벽에서 벗어나려는 키를 누르거나, 벽 센서가 끊겼을 때
        if ((player.isTouchingLeft() && right) || (player.isTouchingRight() && left) || (!player.isTouchingLeft() && !player.isTouchingRight())) {
            player.changeState(new PlayerJumpState());
            return;
        }
    }

    @Override
    public void exit(Player player) {
        player.isClinging = false;
        player.getBody().setGravityScale(1);
    }

    @Override
    public String getName() {
        return "Cling";
    }
}
