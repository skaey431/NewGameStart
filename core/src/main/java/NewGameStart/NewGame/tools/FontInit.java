package NewGameStart.NewGame.tools;// package NewGameStart.NewGame.tools;
// File: FontInit.java

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.kotcrab.vis.ui.VisUI;

public class FontInit {
    private static Skin koreanSkin;
    private static BitmapFont koreanFont;

    // ⭐ 수정: 한글 폰트의 로딩 성능을 높이기 위해 자주 사용되는 글자만 포함하도록 서브셋을 간소화했습니다.
    private static final String KOREAN_CHARS_SUBSET =
        "가나다라마바사아자차카타파하거너더러머버서어저처커터퍼허고노도로모보소오조초코토포호구누두루무부수우주추쿠투푸후그느드르므브스으즈츠크트프흐기니디리미비시이지치키티피히" // 자주 쓰이는 음절
            + "ㄱㄴㄷㄹㅁㅂㅅㅇㅈㅊㅋㅌㅍㅎㄲㄸㅃㅆㅉ" // 자모
            + "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz" // 영문
            + "0123456789" // 숫자
            + "?!,.:-()[]{}<> " // 특수문자 및 공백
            + "새게임이어하기옵션"; // UI에 직접 사용되는 단어

    public static Skin loadKoreanSkin() {
        if (koreanSkin != null) return koreanSkin;

        if (!VisUI.isLoaded()) {
            VisUI.load();
        }

        if (koreanFont == null) {
            generateKoreanFont();
        }

        Skin skin = VisUI.getSkin();
        skin.add("default-font", koreanFont, BitmapFont.class);
        skin.getFont("default-font").getData().markupEnabled = true;

        applyFontToStyles(skin);

        koreanSkin = skin;
        return koreanSkin;
    }

    private static void generateKoreanFont() {
        FreeTypeFontGenerator generator = null;
        try {
            generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/NotoSansKR-Light.ttf"));
            FreeTypeFontParameter parameter = new FreeTypeFontParameter();
            parameter.size = 24;
            parameter.characters = KOREAN_CHARS_SUBSET; // ⭐ 수정 적용: 간소화된 서브셋 사용
            parameter.color = Color.WHITE;

            koreanFont = generator.generateFont(parameter);
        } catch (Exception e) {
            Gdx.app.error("FontInit", "Failed to load Korean font", e);
            koreanFont = new BitmapFont(); // 실패 시 기본 폰트 사용
        } finally {
            // 원본 코드와 동일하게 생성 직후 dispose() 호출
            if (generator != null) {
                generator.dispose();
            }
        }
    }

    // 주요 스타일에 한글 폰트 적용
    private static void applyFontToStyles(Skin skin) {
        for (Label.LabelStyle style : skin.getAll(Label.LabelStyle.class).values()) {
            style.font = koreanFont;
        }
        for (TextButton.TextButtonStyle style : skin.getAll(TextButton.TextButtonStyle.class).values()) {
            style.font = koreanFont;
        }
    }

    // 폰트 크기 업데이트 (동적으로 폰트 크기 변경)
    public static void updateFontSize(int newSize) {
        if (koreanFont != null) {
            koreanFont.getData().setScale(newSize / 24.0f);
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
