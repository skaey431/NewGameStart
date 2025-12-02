package NewGameStart.NewGame.screens;

import NewGameStart.NewGame.Main;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class TitleScreen extends BaseScreen {

    public TitleScreen(Main game) {
        super(game);

        Table table = new Table(skin);
        table.setFillParent(true);
        stage.addActor(table);

        // 새 게임 버튼
        TextButton newGameButton = new TextButton("새 게임", skin);
        newGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game));
                dispose();
            }
        });

        // 이어하기 버튼
        TextButton continueButton = new TextButton("이어하기", skin);
        continueButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // 게임 로드 로직
            }
        });

        // 옵션 버튼
        TextButton optionButton = new TextButton("옵션", skin);
        optionButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new OptionScreen(game));
                dispose();
            }
        });

        table.add(newGameButton).width(200).height(50).pad(10).row();
        table.add(continueButton).width(200).height(50).pad(10).row();
        table.add(optionButton).width(200).height(50).pad(10).row();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        stage.act(delta);
        stage.draw();
    }
}
