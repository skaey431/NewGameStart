package NewGameStart.NewGame.tools;

import NewGameStart.NewGame.entities.player.Player;
import com.badlogic.gdx.physics.box2d.*;

public class WorldContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        handleContact(contact, true);
    }

    @Override
    public void endContact(Contact contact) {
        handleContact(contact, false);
    }

    private void handleContact(Contact contact, boolean begin) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        Object dataA = fixA.getUserData();
        Object dataB = fixB.getUserData();

        // A가 플레이어 센서이고 B가 지형(ground/wall)인 경우
        if (isPlayerSensor(dataA) && isTerrain(dataB)) {
            updatePlayer(fixA, (String) dataB, begin);
        }
        // B가 플레이어 센서이고 A가 지형(ground/wall)인 경우
        else if (isPlayerSensor(dataB) && isTerrain(dataA)) {
            updatePlayer(fixB, (String) dataA, begin);
        }
    }

    private boolean isPlayerSensor(Object data) {
        if (!(data instanceof String)) return false;
        String s = (String) data;
        return s.equals("foot") || s.equals("left") || s.equals("right") || s.equals("head");
    }

    private boolean isTerrain(Object data) {
        return "ground".equals(data) || "wall".equals(data);
    }

    private void updatePlayer(Fixture sensorFixture, String terrainType, boolean begin) {
        Player player = (Player) sensorFixture.getBody().getUserData();
        String sensorName = (String) sensorFixture.getUserData();

        if (player != null) {
            if (begin) player.incrementContact(sensorName, terrainType);
            else player.decrementContact(sensorName, terrainType);
        }
    }

    @Override public void preSolve(Contact contact, Manifold oldManifold) {}
    @Override public void postSolve(Contact contact, ContactImpulse impulse) {}
}
