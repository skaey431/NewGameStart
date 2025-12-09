package NewGameStart.NewGame.screens.UI;

import NewGameStart.NewGame.entities.BaseEntity;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * 엔티티의 현재 체력을 시각적으로 표시하는 ProgressBar입니다.
 */
public class HealthBar extends ProgressBar {

    private BaseEntity targetEntity;

    private static ProgressBarStyle getProgressBarStyle(Skin skin) {

        ProgressBarStyle resultStyle = null;

        // 1. JSON에 정의된 이름을 직접 요청합니다 (정상적인 방법).
        Gdx.app.log("HealthBar", "Attempting direct retrieval of style 'default-horizontal'.");
        try {
            // JSON 오류가 없다면, 여기서 스타일을 성공적으로 로드하고 반환합니다.
            resultStyle = skin.get("default-horizontal", ProgressBarStyle.class);
            Gdx.app.log("HealthBar", "Successfully retrieved style 'default-horizontal'.");
            return resultStyle;

        } catch (GdxRuntimeException e) {
            // Style 클래스가 JSON에 등록되지 않았음을 확인 (구문 분석 오류).
            Gdx.app.error("HealthBar", "Failed to retrieve 'default-horizontal'. Proceeding to robust fallback.");
        }

        // 2. ⭐ 최후의 비상용 대체 스타일 생성 (앱 실행 보장)
        Gdx.app.error("HealthBar", "CRITICAL: ProgressBarStyle not found. Creating emergency style using dynamic 'white' drawable.");

        try {
            // 'white' Drawable만 사용하여 배경과 체력 바 색상을 동적으로 생성합니다.

            // 배경: 어두운 회색 (테두리 효과)
            Drawable background = skin.newDrawable("white", 0.1f, 0.1f, 0.1f, 1f);
            // 체력 바 (KnobBefore): 붉은색
            Drawable knobBefore = skin.newDrawable("white", 1f, 0.1f, 0.1f, 1f);

            resultStyle = new ProgressBarStyle();
            resultStyle.background = background;
            resultStyle.knobBefore = knobBefore;

            Gdx.app.log("HealthBar", "Successfully created EMERGENCY FALLBACK ProgressBarStyle (Red bar on Dark Gray background).");
            return resultStyle; // 성공적으로 생성된 스타일 객체를 반환합니다.

        } catch (Exception e) {
            // 'white' Drawable조차 없다면 심각한 문제이므로 최종적으로 에러를 던집니다.
            Gdx.app.error("HealthBar", "CRITICAL: Cannot create fallback style. 'skin.newDrawable(\"white\",...)' failed.", e);
            throw new RuntimeException("Failed to initialize HealthBar: Skin is missing the essential 'white' drawable.", e);
        }
    }

    public HealthBar(Skin skin, BaseEntity entity) {

        // super() 호출에 getProgressBarStyle()이 반환한 유효한 ProgressBarStyle 객체가 전달됩니다.
        super(0f, entity.getMaxHealth(), 1f, false, getProgressBarStyle(skin));

        this.targetEntity = entity;

        // Progress Bar의 크기 설정
        setWidth(100f);
        setHeight(10f);

        // 초기 값 설정
        setValue(targetEntity.getCurrentHealth());
    }

    /**
     * 프레임마다 호출되어 엔티티의 체력에 따라 바를 업데이트합니다.
     * @param delta 이전 프레임 이후 경과 시간
     */
    @Override
    public void act(float delta) {
        super.act(delta);
        // 엔티티의 현재 체력으로 ProgressBar 값을 설정합니다.
        setValue(targetEntity.getCurrentHealth());
    }
}
