package NewGameStart.NewGame.tools; // 사용자님의 패키지 경로 확인

public class Constants {
    public static final float PPM = 32f;

    // 충돌 카테고리 설정 (2의 거듭제곱)
    public static final short CATEGORY_PLAYER = 1;  // 0001
    public static final short BIT_GROUND = 2;       // 0010 (Tiled에서 생성될 지형)
    public static final short BIT_WALL = 4;         // 0100
    public static final short BIT_MONSTER = 8;      // 1000

    // 플레이어가 충돌을 감지할 대상 (비트 연산으로 합침)
    // 땅, 벽, 몬스터 모두를 인식해야 센서가 작동합니다.
    public static final short MASK_PLAYER = CATEGORY_PLAYER | BIT_GROUND | BIT_WALL | BIT_MONSTER;
}
