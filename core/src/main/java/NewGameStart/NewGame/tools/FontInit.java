package NewGameStart.NewGame.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.kotcrab.vis.ui.VisUI;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class FontInit {
    public static Skin loadKoreanSkin() {
        if (!VisUI.isLoaded()) {
            VisUI.load(); // VisUI 기본 스킨 로드
        }

        // 한글 폰트 생성
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/NotoSansKR-Light.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 24;
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS +
            "가나다라마바사아자차카타파하" +
            "ㄱㄴㄷㄹㅁㅂㅅㅇㅈㅊㅋㅌㅍㅎ" +
            "ㄲㄸㅃㅆㅉ" +
            "ㆍㅠㅜㅑㅕㅓㅏㅣㅔㅐㅖㅒ" +
            "?!,.:-()[]{}" +
            " ";
        BitmapFont koreanFont = generator.generateFont(parameter);
        generator.dispose();

        // VisUI 스킨에 폰트 적용
        Skin skin = VisUI.getSkin();
        skin.add("default-font", koreanFont, BitmapFont.class);
        skin.getFont("default-font").getData().markupEnabled = true;

        return skin;
    }
}
