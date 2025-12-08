package NewGameStart.NewGame.ui;

import NewGameStart.NewGame.entities.BaseEntity;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable; // Drawable 임포트 추가

/**
 * 엔티티의 현재 체력을 시각적으로 표시하는 ProgressBar입니다.
 */
public class HealthBar extends ProgressBar {

    private BaseEntity targetEntity;

    // 비상용 ProgressBarStyle을 가져오거나 생성합니다.
    private static ProgressBarStyle getProgressBarStyle(Skin skin) {
        // 1. 등록된 스타일 중 첫 번째 스타일을 찾습니다.
        try {
            ObjectMap<String, ProgressBarStyle> styles = skin.getAll(ProgressBarStyle.class);

            if (styles != null && styles.size > 0) {
                String firstStyleName = styles.keys().next();
                ProgressBarStyle style = styles.get(firstStyleName);
                Gdx.app.log("HealthBar", "Using first available ProgressBarStyle: " + firstStyleName);
                return style;
            }

        } catch (Exception e) {
            // getAll() 중 문제가 생겨도 일단 넘어갑니다.
            Gdx.app.error("HealthBar", "Error searching for ProgressBarStyle in skin. Proceeding to fallback.", e);
        }

        // 2. ⭐ 비상용 대체 스타일 생성 (FATAL 오류 해결)
        // 스킨에 등록된 스타일이 없는 경우, 기본 Drawable을 사용하여 수동으로 스타일을 만듭니다.
        Gdx.app.error("HealthBar", "FATAL: No ProgressBarStyle found. Creating emergency fallback style.");

        try {
            // uiskin.json에 일반적으로 'white'라는 이름으로 흰색 Drawable이 등록되어 있습니다.
            Drawable background = skin.getDrawable("white");
            Drawable knob = skin.getDrawable("highlight"); // 하이라이트 색상 사용 (이름은 JSON 참조)

            // 만약 'highlight' TintedDrawable이 없다면 'white'를 다시 사용하여 색상을 직접 지정해야 합니다.
            if (knob == null) {
                knob = skin.newDrawable("white", 0.8f, 0.1f, 0.1f, 1f); // 빨간색 Drawable 수동 생성
            }

            ProgressBarStyle fallbackStyle = new ProgressBarStyle();
            fallbackStyle.background = background;
            fallbackStyle.knobBefore = knob;

            Gdx.app.log("HealthBar", "Successfully created emergency fallback ProgressBarStyle.");
            return fallbackStyle;

        } catch (Exception e) {
            Gdx.app.error("HealthBar", "FATAL: Cannot create fallback style. Check if 'white' drawable exists in skin.", e);
            throw new RuntimeException("Failed to initialize HealthBar: Skin is missing basic Drawables (e.g., 'white') for fallback.", e);
        }
    }

    public HealthBar(Skin skin, BaseEntity entity) {

        // super() 호출을 첫 번째 명령문으로 유지합니다.
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
