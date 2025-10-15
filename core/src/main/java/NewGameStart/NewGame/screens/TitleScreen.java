package NewGameStart.NewGame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import NewGameStart.NewGame.Main;

public class TitleScreen extends BaseScreen {
    private final Main game;

    public TitleScreen(Main game) {
        super();
        this.game = game;

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // 버튼 생성
        TextButton newGameButton = new TextButton("새 게임", skin);
        TextButton continueButton = new TextButton("이어하기", skin);
        TextButton optionsButton = new TextButton("옵션", skin);

        // 버튼 이벤트
        newGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("새 게임 시작");
                // TODO: 새 게임 화면으로 전환
            }
        });

        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("이어하기 클릭");
                // TODO: 저장된 게임 불러오기
            }
        });

        optionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("옵션 클릭");
                // TODO: 옵션 화면으로 전환
            }
        });

        // 테이블에 버튼 배치
        table.add(newGameButton).width(200).height(50).pad(10).row();
        table.add(continueButton).width(200).height(50).pad(10).row();
        table.add(optionsButton).width(200).height(50).pad(10);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        super.render(delta); // stage.act() & stage.draw() 호출
    }
}
