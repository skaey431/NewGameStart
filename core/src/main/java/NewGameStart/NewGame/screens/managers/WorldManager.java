package NewGameStart.NewGame.screens.managers;

import NewGameStart.NewGame.tools.WorldContactListener; // 추가
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class WorldManager {
    private World world;

    public WorldManager() {
        // 중력 설정 (사용자님 원본 유지)
        world = new World(new Vector2(0, -9.8f), true);

        // [중요] 프로젝트에 없던 ContactListener를 여기서 등록합니다.
        world.setContactListener(new WorldContactListener());
    }

    public void update(float delta) {
        world.step(delta, 6, 2);
    }

    public World getWorld() { return world; }

    public void dispose() {
        if (world != null) world.dispose();
    }
}
