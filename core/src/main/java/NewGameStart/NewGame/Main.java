package NewGameStart.NewGame;

import NewGameStart.NewGame.screens.TitleScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.VisUI;

public class Main extends Game {

    public static final int V_WIDTH = 800;
    public static final int V_HEIGHT = 480;

    public OrthographicCamera camera;
    public Viewport viewport;

    @Override
    public void create() {
        // UI 스킨 로드
        if (!VisUI.isLoaded()) {
            VisUI.load();
        }

        camera = new OrthographicCamera();
        viewport = new FitViewport(V_WIDTH, V_HEIGHT, camera);
        camera.position.set(V_WIDTH / 2f, V_HEIGHT / 2f, 0);

        // 첫 화면을 TitleScreen으로 설정
        setScreen(new TitleScreen(this));
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        super.resize(width, height);
    }

    @Override
    public void dispose() {
        super.dispose();
        VisUI.dispose(); // VisUI 자원 해제
        // FontInit.disposeFont(); // 폰트 자원 해제 (필요시)
    }
}
