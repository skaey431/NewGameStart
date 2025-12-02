package NewGameStart.NewGame.screens;

import NewGameStart.NewGame.Main;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import NewGameStart.NewGame.tools.FontInit;

public abstract class BaseScreen implements Screen {
    protected Stage stage;
    protected Skin skin;

    public BaseScreen(Main game) {
        stage = new Stage(new ScreenViewport());
        skin = FontInit.loadKoreanSkin();
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); }
    @Override public void show() {}
}
