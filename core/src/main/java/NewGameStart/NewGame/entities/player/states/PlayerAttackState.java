package NewGameStart.NewGame.entities.player.states;

import NewGameStart.NewGame.entities.player.*;

public class PlayerAttackState implements PlayerState {
    private float timer = 0f;
    private final float DURATION = 0.25f;
    private boolean damageApplied = false;

    @Override
    public void enter(Player player) {
        timer = 0f;
        damageApplied = false;
    }

    @Override
    public void update(Player player, float delta) {
        timer += delta;

        if (!damageApplied && timer >= 0.1f) {
            if (player.getEntityManager() != null) {
                player.getEntityManager().processPlayerAttack();
            }
            damageApplied = true;
        }

        if (timer >= DURATION) {
            // 원본 상태 전환 로직 준수
            if (player.isOnGround()) player.changeState(new PlayerIdleState());
            else if (player.isTouchingLeft() || player.isTouchingRight()) player.changeState(new PlayerClingState());
            else player.changeState(new PlayerAirState());
        }
    }

    @Override
    public void exit(Player player) {}

    @Override
    public String getName() { return "Attack"; }
}
