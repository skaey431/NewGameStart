package NewGameStart.NewGame.entities;

import NewGameStart.NewGame.world.WorldManager;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public class SpecialWall {
    public static Body create(World world, float x, float y, float w, float h) {
        // 특수 충돌 필터링을 위해 UserData를 "specialWall"로 설정
        return WorldManager.createStaticBox(world, x, y, w, h, "specialWall");
    }
}
