package NewGameStart.NewGame.entities;

import NewGameStart.NewGame.world.WorldManager;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public class NormalWall {
    public static Body create(World world, float x, float y, float w, float h) {
        return WorldManager.createStaticBox(world, x, y, w, h, "normalWall");
    }
}
