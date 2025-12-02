package NewGameStart.NewGame.entities;

import NewGameStart.NewGame.world.WorldManager;
import NewGameStart.NewGame.tools.Constants; // Constants import 추가
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Player {
    // ... (필드 선언 및 메서드 원본과 동일) ...
    private Body body;
    private int footContacts = 0;
    private int leftContacts = 0;
    private int rightContacts = 0;
    private int headContacts = 0;
    public boolean isClinging = false;
    private final float MOVE_SPEED = 5f;
    private final float CLIMB_SPEED = 4f;
    private final float JUMP_FORCE = 3f;

    public Player(World world, float x, float y) {

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(x, y);
        body = world.createBody(def);
        body.setUserData(this);
        body.setFixedRotation(true);

        // --- 1. 몸체 Fixture ---
        PolygonShape main = new PolygonShape();
        main.setAsBox(0.3f, 0.5f);
        FixtureDef fd = new FixtureDef();
        fd.shape = main;
        fd.density = 1f;
        fd.friction = 0.3f;

        // ⭐ 수정: 충돌 필터링 명시
        fd.filter.categoryBits = Constants.CATEGORY_PLAYER;
        fd.filter.maskBits = Constants.MASK_PLAYER;

        body.createFixture(fd).setUserData("player");
        main.dispose();

        // --- 2. FOOT SENSOR ---
        PolygonShape foot = new PolygonShape();
        foot.setAsBox(0.25f, 0.05f, new Vector2(0, -0.55f), 0);
        FixtureDef sensorFd = new FixtureDef();
        sensorFd.shape = foot;
        sensorFd.isSensor = true;

        // ⭐ 수정: 충돌 필터링 명시 (센서도 동일한 필터링 사용)
        sensorFd.filter.categoryBits = Constants.CATEGORY_PLAYER;
        sensorFd.filter.maskBits = Constants.MASK_PLAYER;

        body.createFixture(sensorFd).setUserData("foot");
        foot.dispose();

        // --- 3. HEAD SENSOR ---
        PolygonShape head = new PolygonShape();
        head.setAsBox(0.25f, 0.05f, new Vector2(0, 0.55f), 0);
        sensorFd.shape = head;
        body.createFixture(sensorFd).setUserData("head");
        head.dispose();

        // --- 4. LEFT SENSOR ---
        PolygonShape left = new PolygonShape();
        left.setAsBox(0.05f, 0.4f, new Vector2(-0.35f, 0), 0);
        sensorFd.shape = left;
        body.createFixture(sensorFd).setUserData("left");
        left.dispose();

        // --- 5. RIGHT SENSOR ---
        PolygonShape right = new PolygonShape();
        right.setAsBox(0.05f, 0.4f, new Vector2(0.35f, 0), 0);
        sensorFd.shape = right;
        body.createFixture(sensorFd).setUserData("right");
        right.dispose();

        // **주의**: sensorFd 변수를 재활용했으므로, 매번 shape만 교체하면 filter 설정은 유지됩니다.
    }

    // ... (update, incrementContact, decrementContact 메서드는 이전과 동일하게 유지) ...
    public void update(float delta) {
        // ... (이전 코드와 동일) ...
    }
    public Body getBody() { return body; }
    public void incrementContact(String sensor) {
        switch (sensor) {
            case "foot": footContacts++; break;
            case "left": leftContacts++; break;
            case "right": rightContacts++; break;
            case "head": headContacts++; break;
        }
    }
    public void decrementContact(String sensor) {
        switch (sensor) {
            case "foot": footContacts = Math.max(0, footContacts - 1); break;
            case "left": leftContacts = Math.max(0, leftContacts - 1); break;
            case "right": rightContacts = Math.max(0, rightContacts - 1); break;
            case "head": headContacts = Math.max(0, headContacts - 1); break;
        }
    }
}
