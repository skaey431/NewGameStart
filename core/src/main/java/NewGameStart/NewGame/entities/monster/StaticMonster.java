package NewGameStart.NewGame.entities.monster;

import NewGameStart.NewGame.entities.BaseEntity;
import NewGameStart.NewGame.tools.Constants;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;

public class StaticMonster extends BaseEntity {
    private Body body;
    private float attackDamage;
    private Rectangle bounds; // 렌더링 및 AABB 쿼리용

    public StaticMonster(World world, float initialHealth, float x, float y, float width, float height, float attackDamage) {
        super();
        this.currentHealth = initialHealth; // BaseEntity 필드 설정
        this.attackDamage = attackDamage;
        this.bounds = new Rectangle(x, y, width, height);

        // Box2D Body 생성 (센서로 설정하여 통과는 하되 충돌은 감지)
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody; // 몬스터는 고정
        bdef.position.set((x + width / 2) / Constants.PPM, (y + height / 2) / Constants.PPM);

        this.body = world.createBody(bdef);
        this.body.setUserData(this); // 리스너에서 참조하기 위해 자신을 저장

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2 / Constants.PPM, height / 2 / Constants.PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.isSensor = true; // 플레이어가 통과할 수 있게 센서 처리 (필요시 false로 변경)
        fdef.filter.categoryBits = Constants.BIT_MONSTER;
        fdef.filter.maskBits = Constants.CATEGORY_PLAYER; // 플레이어와만 충돌

        body.createFixture(fdef).setUserData("monster");
        shape.dispose();
    }

    @Override
    public void update(float delta) {
        // AI 로직이 있다면 여기에 추가
    }

    public Body getBody() { return body; }
    public Rectangle getBounds() { return bounds; }
    public float getAttackDamage() { return attackDamage; }
}
