package NewGameStart.NewGame.tools;

public class Constants {
    public static final float PPM = 32f; // Tiled와 Box2D 비율 통일

    // 충돌 카테고리 (비트 플래그)
    public static final short CATEGORY_PLAYER = 1;
    public static final short BIT_GROUND = 2;
    public static final short BIT_WALL = 4;
    public static final short BIT_MONSTER = 8;
    public static final short BIT_HAZARD = 16;      // 데미지 박스, 즉사 박스
    public static final short BIT_CHECKPOINT = 32;  // 체크포인트

    // 플레이어 마스크: 플레이어가 충돌을 감지해야 할 모든 대상
    public static final short MASK_PLAYER = CATEGORY_PLAYER | BIT_GROUND | BIT_WALL | BIT_MONSTER | BIT_HAZARD | BIT_CHECKPOINT;
}
