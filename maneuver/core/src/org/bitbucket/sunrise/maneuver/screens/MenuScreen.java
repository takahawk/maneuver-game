package org.bitbucket.sunrise.maneuver.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import org.bitbucket.sunrise.maneuver.ManeuverGame;

/**
 * Created by snuff on 16.03.2016.
 */
public class MenuScreen implements Screen {
    Skin skin;
    Stage stage;
    BitmapFont font;
    final ManeuverGame mGame;
    public MenuScreen(ManeuverGame g){
        createBasicSkin();
        this.mGame=g;

        stage = new Stage() {

            @Override
            public boolean keyDown(int keyCode) {
                if (keyCode == Input.Keys.BACK) {
                    Gdx.app.exit();
                }
                return false;
            }
        };
        Gdx.input.setInputProcessor(stage);// Make the stage consume events

        createBasicSkin();
        Table table = new Table();
        table.setFillParent(true);
        table.add().expandY(); table.add().expandY(); table.add().expandY();
        table.row();
        final TextButton newGameButton = new TextButton("  new game  ", skin); // Use the initialized skin
        final TextButton scoresButton = new TextButton("high scores", skin);
        final TextButton optionsButton = new TextButton("   options   ", skin);
        final TextButton exitGameButton = new TextButton("   exit   ", skin);
//        newGameButton.setPosition(Gdx.graphics.getWidth()/2 - Gdx.graphics.getWidth()/8 , Gdx.graphics.getHeight()/2);
//        optionsButton.setPosition(Gdx.graphics.getWidth()/2 - Gdx.graphics.getWidth()/8 , newGameButton.getY() - newGameButton.getHeight() - 5.f );
//        scoresButton.setPosition(Gdx.graphics.getWidth()/2 - Gdx.graphics.getWidth()/8 , optionsButton.getY() - optionsButton.getHeight() - 5.f );
//        exitGameButton.setPosition(Gdx.graphics.getWidth()/2 - Gdx.graphics.getWidth()/8 , scoresButton.getY() - scoresButton.getHeight() - 5.f );
        table.add().expandX(); table.add(newGameButton).pad(20).fill(); table.add().expandX(); table.row();
        table.add().expandX(); table.add(optionsButton).pad(20).fill(); table.add().expandX();  table.row();
        table.add().expandX(); table.add(scoresButton).pad(20).fill(); table.add().expandX();  table.row();
        table.add().expandX(); table.add(exitGameButton).pad(20).fill(); table.add().expandX();  table.row();
//        stage.addActor(newGameButton);
//        stage.addActor(optionsButton);
//        stage.addActor(scoresButton);
//        stage.addActor(exitGameButton);
        table.add().expand();
        table.row();
        stage.addActor(table);
        newGameButton.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                mGame.setScreen( new GameScreen(mGame, new SpriteBatch()));
            }
        });

        optionsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                mGame.setScreen(new OptionsScreen(mGame));
            }
        });

        scoresButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                mGame.setScreen(new ScoreScreen(mGame));
            }
        });
        exitGameButton.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
    }

    private void createBasicSkin(){
        //Create a font
        font = new BitmapFont(Gdx.files.internal("fonts/white.fnt"));
        skin = new Skin();
        skin.add("default", font);

        //Create a texture
        Pixmap pixmap = new Pixmap(
                Gdx.graphics.getWidth() / 4,
                Gdx.graphics.getHeight() / 10,
                Pixmap.Format.RGB888
        );
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("background",new Texture(pixmap));

        //Create a button style
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("background", Color.GRAY);
        textButtonStyle.down = skin.newDrawable("background", Color.DARK_GRAY);
        textButtonStyle.checked = skin.newDrawable("background", Color.DARK_GRAY);
        textButtonStyle.over = skin.newDrawable("background", Color.LIGHT_GRAY);
        textButtonStyle.font = skin.getFont("default");
        skin.add("default", textButtonStyle);

    }

    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    public void resize (int width, int height) {

    }

    @Override
    public void dispose () {
        font.dispose();
        stage.dispose();
        skin.dispose();
    }

    @Override
    public void show() {
        // TODO Auto-generated method stub

    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub

    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }
}