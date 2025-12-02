package NewGameStart.NewGame.tools;

public class Constants {
    // 픽셀 대 미터 비율 (Box2D 월드 단위)
    public static final float PPM = 32.0f;

    // 비트 마스크 정의 (충돌 필터링을 위해 필요)
    public static final short CATEGORY_PLAYER = 0x0001;
    public static final short CATEGORY_GROUND = 0x0002;
    public static final short CATEGORY_WALL = 0x0004;
    public static final short CATEGORY_SPECIAL_WALL = 0x0008;

    // 플레이어가 충돌할 수 있는 대상: 땅, 일반 벽, 특수 벽
    public static final short MASK_PLAYER = CATEGORY_GROUND | CATEGORY_WALL | CATEGORY_SPECIAL_WALL;

    // 나머지 객체에 대한 마스크는 필요 시 추가
}
