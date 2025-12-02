package NewGameStart.NewGame.entities;

import NewGameStart.NewGame.world.WorldManager;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public class Ground {
    public static Body create(World world, float x, float y, float w, float h) {
        // WorldManager에 구현된 정적 Body 생성 메서드를 사용
        return WorldManager.createStaticBox(world, x, y, w, h, "ground");
    }
}
