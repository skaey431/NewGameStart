package NewGameStart.NewGame.tools;

import NewGameStart.NewGame.entities.player.Player;
import com.badlogic.gdx.physics.box2d.*;

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        // 발 센서와 땅(ground)의 접촉 판정
        if ("foot".equals(fixA.getUserData()) && "ground".equals(fixB.getUserData())) {
            ((Player) fixA.getBody().getUserData()).incrementContact("foot");
        } else if ("foot".equals(fixB.getUserData()) && "ground".equals(fixA.getUserData())) {
            ((Player) fixB.getBody().getUserData()).incrementContact("foot");
        }

        // 왼쪽/오른쪽 벽 센서 판정도 같은 방식으로 추가 가능
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        if ("foot".equals(fixA.getUserData()) && "ground".equals(fixB.getUserData())) {
            ((Player) fixA.getBody().getUserData()).decrementContact("foot");
        } else if ("foot".equals(fixB.getUserData()) && "ground".equals(fixA.getUserData())) {
            ((Player) fixB.getBody().getUserData()).decrementContact("foot");
        }
        if ("right".equals(fixA.getUserData()) && "wall".equals(fixB.getUserData())) {
            ((Player) fixA.getBody().getUserData()).decrementContact("right");
        } else if ("right".equals(fixB.getUserData()) && "wall".equals(fixA.getUserData())) {
            ((Player) fixB.getBody().getUserData()).decrementContact("wall");
        }
        if ("left".equals(fixA.getUserData()) && "wall".equals(fixB.getUserData())) {
            ((Player) fixA.getBody().getUserData()).decrementContact("left");
        } else if ("left".equals(fixB.getUserData()) && "wall".equals(fixA.getUserData())) {
            ((Player) fixB.getBody().getUserData()).decrementContact("wall");
        }
    }

    @Override public void preSolve(Contact contact, Manifold oldManifold) {}
    @Override public void postSolve(Contact contact, ContactImpulse impulse) {}
}
