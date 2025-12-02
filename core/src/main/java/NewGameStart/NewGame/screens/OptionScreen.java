package NewGameStart.NewGame.screens;

import NewGameStart.NewGame.Main;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class OptionScreen extends BaseScreen {
    Main game;

    public OptionScreen(Main game) {
        super(game);

        Table table = new Table(skin);
        table.setFillParent(true);
        stage.addActor(table);

        Label title = new Label("옵션 화면 (ESC 키로 돌아가기)", skin);
        table.add(title).pad(20).row();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        stage.act(delta);
        stage.draw();

        // ESC 키 입력 처리
        if (com.badlogic.gdx.Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new TitleScreen(game));
            dispose();
        }
    }
}
