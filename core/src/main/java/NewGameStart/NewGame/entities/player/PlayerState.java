package NewGameStart.NewGame.entities.player;


/**
 * 모든 플레이어 상태가 구현해야 하는 기본 인터페이스.
 */
public interface PlayerState {
    void enter(Player player);
    void update(Player player, float delta);
    void exit(Player player);
    String getName();
}
