package NewGameStart.NewGame;

import NewGameStart.NewGame.tools.FontInit;
import com.badlogic.gdx.Game;
import NewGameStart.NewGame.screens.TitleScreen;

public class Main extends Game {
    @Override
    public void create() {
        setScreen(new TitleScreen(this));
    }

}
