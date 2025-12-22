package NewGameStart.NewGame.tools;

import NewGameStart.NewGame.entities.player.Player;
import com.badlogic.gdx.physics.box2d.*;

public class WorldContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        // 플레이어 센서와 지형("ground")의 접촉 판정
        processSensorContact(fixA, fixB, true);
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        processSensorContact(fixA, fixB, false);
    }

    private void processSensorContact(Fixture a, Fixture b, boolean begin) {
        // 한쪽이 플레이어 센서(foot, left, right, head)이고 다른 한쪽이 지형("ground")인 경우
        if (isPlayerSensor(a) && "ground".equals(b.getUserData())) {
            updatePlayerSensor(a, begin);
        } else if (isPlayerSensor(b) && "ground".equals(a.getUserData())) {
            updatePlayerSensor(b, begin);
        }
    }

    private boolean isPlayerSensor(Fixture f) {
        Object data = f.getUserData();
        if (!(data instanceof String)) return false;
        String s = (String) data;
        // 플레이어의 모든 센서 이름을 체크
        return s.equals("foot") || s.equals("left") || s.equals("right") || s.equals("head");
    }

    private void updatePlayerSensor(Fixture sensorFixture, boolean begin) {
        Player player = (Player) sensorFixture.getBody().getUserData();
        String sensorName = (String) sensorFixture.getUserData();

        if (player != null) {
            if (begin) {
                player.incrementContact(sensorName);
            } else {
                player.decrementContact(sensorName);
            }
        }
    }

    @Override public void preSolve(Contact contact, Manifold oldManifold) {}
    @Override public void postSolve(Contact contact, ContactImpulse impulse) {}
}
