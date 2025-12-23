package NewGameStart.NewGame.tools;

import NewGameStart.NewGame.entities.DamageBox;
import NewGameStart.NewGame.entities.monster.StaticMonster;
import NewGameStart.NewGame.entities.player.Player;
import com.badlogic.gdx.physics.box2d.*;

public class WorldContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        // 1. 플레이어와 지형(점프/벽타기) 처리
        processSensorContact(fixA, fixB, true);

        // 2. 플레이어와 몬스터/함정 충돌 처리 (비효율적인 반복문 제거)
        processHazardContact(fixA, fixB);
    }

    @Override
    public void endContact(Contact contact) {
        processSensorContact(contact.getFixtureA(), contact.getFixtureB(), false);
    }

    private void processSensorContact(Fixture a, Fixture b, boolean begin) {
        if (isPlayerSensor(a) && isTerrain(b)) {
            updatePlayerSensor(a, (String) b.getUserData(), begin);
        } else if (isPlayerSensor(b) && isTerrain(a)) {
            updatePlayerSensor(b, (String) a.getUserData(), begin);
        }
    }

    // 위험 요소 충돌 처리 로직 추가
    private void processHazardContact(Fixture a, Fixture b) {
        Fixture playerFix = null;
        Fixture hazardFix = null;

        if (isPlayerMain(a)) { playerFix = a; hazardFix = b; }
        else if (isPlayerMain(b)) { playerFix = b; hazardFix = a; }

        if (playerFix == null) return;

        Player player = (Player) playerFix.getBody().getUserData();
        Object hazardData = hazardFix.getUserData();
        Object hazardEntity = hazardFix.getBody().getUserData(); // 엔티티 객체

        if ("monster".equals(hazardData) && hazardEntity instanceof StaticMonster) {
            player.takeDamage(((StaticMonster) hazardEntity).getAttackDamage());
            System.out.println("Player hit by Monster via Box2D!");
        }
        else if ("damage".equals(hazardData) && hazardEntity instanceof DamageBox) {
            player.takeDamage(((DamageBox) hazardEntity).getDamageAmount());
            System.out.println("Player hit by DamageBox via Box2D!");
        }
    }

    private boolean isPlayerSensor(Fixture f) {
        Object data = f.getUserData();
        return data instanceof String && (((String)data).equals("foot") || ((String)data).equals("left") || ((String)data).equals("right"));
    }

    private boolean isPlayerMain(Fixture f) {
        return "player_main".equals(f.getUserData());
    }

    private boolean isTerrain(Fixture f) {
        Object data = f.getUserData();
        return "ground".equals(data) || "wall".equals(data);
    }

    private void updatePlayerSensor(Fixture sensor, String type, boolean begin) {
        Player player = (Player) sensor.getBody().getUserData();
        String name = (String) sensor.getUserData();
        if (begin) player.incrementContact(name, type);
        else player.decrementContact(name, type);
    }

    @Override public void preSolve(Contact contact, Manifold oldManifold) {}
    @Override public void postSolve(Contact contact, ContactImpulse impulse) {}
}
