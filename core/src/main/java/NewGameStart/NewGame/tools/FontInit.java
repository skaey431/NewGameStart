package NewGameStart.NewGame.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.kotcrab.vis.ui.VisUI;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class FontInit {
    private static Skin koreanSkin;
    private static BitmapFont koreanFont;

    // 한글 폰트가 이미 로드되어 있으면 반환, 아니면 새로 로드
    public static Skin loadKoreanSkin() {
        if (koreanSkin != null) return koreanSkin;

        if (!VisUI.isLoaded()) {
            VisUI.load();
        }

        if (koreanFont == null) {
            // 폰트가 없다면 생성
            generateKoreanFont();
        }

        Skin skin = VisUI.getSkin();
        skin.add("default-font", koreanFont, BitmapFont.class);
        skin.getFont("default-font").getData().markupEnabled = true;

        // 주요 스타일에 한글 폰트 적용
        applyFontToStyles(skin);

        koreanSkin = skin;
        return koreanSkin;
    }

    // 폰트 생성 메서드
    private static void generateKoreanFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/NotoSansKR-Light.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 24;
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS +
            "가나다라마바사아자차카타파하" +
            "ㄱㄴㄷㄹㅁㅂㅅㅇㅈㅊㅋㅌㅍㅎ" +
            "ㄲㄸㅃㅆㅉ" +
            "ㆍㅠㅜㅑㅕㅓㅏㅣㅔㅐㅖㅒ" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz" +
            "0123456789" +
            "?!,.:-()[]{}" +
            " " +
            "한글 테스트 문장입니다" +
            "새게임이어하기옵션"
        ;

        koreanFont = generator.generateFont(parameter);
        generator.dispose(); // 더 이상 사용하지 않으면 바로 메모리에서 제거
    }

    // 주요 스타일에 한글 폰트 적용
    private static void applyFontToStyles(Skin skin) {
        // Label 스타일에 폰트 적용
        for (Label.LabelStyle style : skin.getAll(Label.LabelStyle.class).values()) {
            style.font = koreanFont;
        }
        // TextButton 스타일에 폰트 적용
        for (TextButton.TextButtonStyle style : skin.getAll(TextButton.TextButtonStyle.class).values()) {
            style.font = koreanFont;
        }
    }

    // 폰트 크기 업데이트 (동적으로 폰트 크기 변경)
    public static void updateFontSize(int newSize) {
        if (koreanFont != null) {
            koreanFont.getData().setScale(newSize / 24.0f); // 24px 기준으로 비율 계산
        }
    }

    // 메모리 해제 (폰트 리소스를 해제하는 메서드)
    public static void disposeFont() {
        if (koreanFont != null) {
            koreanFont.dispose();
            koreanFont = null;
        }
    }
}
