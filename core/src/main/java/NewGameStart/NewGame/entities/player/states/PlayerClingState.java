package NewGameStart.NewGame.entities.player.states;

import NewGameStart.NewGame.entities.player.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class PlayerClingState implements PlayerState {

    private final float CLING_GRAVITY = 0.5f; // 낮은 중력으로 천천히 미끄러지게 함
    private final float SLIDE_SPEED = -1.0f; // 미끄러지는 최대 속도

    @Override
    public void enter(Player player) {
        player.isClinging = true;
        // 매달리는 동안 수평 속도 0으로 설정
        player.getBody().setLinearVelocity(0, 0);

        // 벽에 매달릴 때 공중 능력 초기화 (대쉬 및 점프 횟수 0으로 리셋)
        player.dashesPerformed = 0;
        player.jumpsPerformed = 0;
    }

    @Override
    public void update(Player player, float delta) {

        // 1. 상태 전환 조건 확인: 매달릴 벽이 없거나 땅에 착지하면 즉시 이탈
        if (player.isOnGround() || (!player.isTouchingLeft() && !player.isTouchingRight())) {
            player.changeState(new PlayerIdleState());
            return;
        }

        // 2. 키를 떼면 일반 낙하 상태로 복귀
        boolean clingingLeft = player.isTouchingLeft() && Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean clingingRight = player.isTouchingRight() && Gdx.input.isKeyPressed(Input.Keys.RIGHT);

        if (!clingingLeft && !clingingRight) {
            player.changeState(new PlayerJumpState());
            return;
        }

        // 3. 벽 점프 (Wall Jump)
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && player.wallJumpTimer <= 0) {

            // ⭐ 수정: 벽 점프는 공중 점프 기회를 소모하지 않으므로, 카운터를 증가시키지 않습니다.
            // player.jumpsPerformed++; // 이 줄을 제거함

            // 점프 방향 설정
            float jumpForceX = player.isTouchingLeft() ? player.WALL_JUMP_HORIZONTAL : -player.WALL_JUMP_HORIZONTAL;

            player.getBody().setLinearVelocity(jumpForceX, player.WALL_JUMP_VERTICAL);
            player.wallJumpTimer = player.WALL_JUMP_COOLDOWN;
            player.changeState(new PlayerJumpState()); // 점프 후 JumpState로 전환
            return;
        }

        // 4. 벽 매달림 중 미끄러짐 처리
        if (player.isClinging) {
            float currentYVelocity = player.getBody().getLinearVelocity().y;

            // 미끄러지는 속도보다 느리게 떨어지고 있으면 속도를 제한 (부드러운 하강)
            if (currentYVelocity < SLIDE_SPEED) {
                player.getBody().setLinearVelocity(0, SLIDE_SPEED);
                player.getBody().setGravityScale(0); // 속도 제한 중에는 중력 스케일 0
            } else {
                // 일반적인 매달림 하강 (낮은 중력 적용)
                player.getBody().setGravityScale(CLING_GRAVITY);
                player.getBody().setLinearVelocity(0, currentYVelocity); // 수평 속도 0 유지
            }
        }
    }

    @Override
    public void exit(Player player) {
        player.isClinging = false;
        // 중력 스케일 복원
        player.getBody().setGravityScale(1);
    }

    @Override
    public String getName() {
        return "Cling";
    }
}
