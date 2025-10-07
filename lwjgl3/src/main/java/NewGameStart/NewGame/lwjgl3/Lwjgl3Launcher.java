package NewGameStart.NewGame.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import NewGameStart.NewGame.Main;

public class Lwjgl3Launcher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return;
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new Main(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("NewGameStart");

        // 기본 설정
        configuration.useVsync(true);
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);

        // ⬇️ 전체화면으로 시작하려면 이걸 사용
        // configuration.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());

        // ⬇️ 창모드로 시작 (비율 16:9)
        configuration.setWindowedMode(1280, 720);

        // ⬇️ 창 크기 조정 가능 (false면 고정)
        configuration.setResizable(true);

        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");

        // OpenGL 호환 설정
        configuration.setOpenGLEmulation(
            Lwjgl3ApplicationConfiguration.GLEmulation.ANGLE_GLES20, 0, 0);

        return configuration;
    }
}
