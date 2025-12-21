package NewGameStart.NewGame.screens.managers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class WorldManager {
    private World world;

    public WorldManager() {
        // 중력 설정 및 월드 생성
        world = new World(new Vector2(0, -9.8f), true);

        // 기존의 createGround()는 MapManager가 수행하므로 삭제되었습니다.
    }

    public void update(float delta) {
        // 물리 연산 스텝 수행
        world.step(1/60f, 6, 2);
    }

    public World getWorld() { return world; }

    public void dispose() {
        if (world != null) world.dispose();
    }
}
