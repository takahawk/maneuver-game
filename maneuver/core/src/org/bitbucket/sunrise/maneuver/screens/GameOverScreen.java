package org.bitbucket.sunrise.maneuver.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import org.bitbucket.sunrise.maneuver.ManeuverGame;

/**
 * Created by Михаил on 29.03.2016.
 */
public class GameOverScreen implements Screen {
    Stage stage;
    Skin skin;
    ManeuverGame mGame;

    public GameOverScreen (ManeuverGame game){
        createBasicSkin();
        this.mGame = game;

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);// Make the stage consume events

        createBasicSkin();
        TextButton newGameButton = new TextButton("Play again", skin);
        TextButton mainMenuButton = new TextButton("Main menu", skin);

        newGameButton.setPosition(Gdx.graphics.getWidth() / 2 - Gdx.graphics.getWidth() / 8,
                                    Gdx.graphics.getHeight() / 2);
        mainMenuButton.setPosition(Gdx.graphics.getWidth() / 2 - Gdx.graphics.getWidth() / 8,
                                    newGameButton.getY()- newGameButton.getHeight() - 5.f );

        Texture texture = new Texture(Gdx.files.internal("gameover.png"));
        TextureRegion region = new TextureRegion(texture);
        region.setRegionX(-Gdx.graphics.getWidth() / 3);
        region.setRegionHeight(Gdx.graphics.getHeight());
        region.setRegionWidth(Gdx.graphics.getWidth());
        Image gameOverImage = new Image(region);

        stage.addActor(gameOverImage);
        stage.addActor(newGameButton);
        stage.addActor(mainMenuButton);

        newGameButton.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                mGame.setScreen(new GameScreen(new ManeuverGame(), new SpriteBatch()));
            }
        });

        mainMenuButton.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                mGame.setScreen(new MenuScreen(mGame));
            }
        });
    }

    private void createBasicSkin(){
        //Create a font
        BitmapFont font = new BitmapFont();
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

    @Override
    public void resize (int width, int height) {

    }

    @Override
    public void dispose () {
        stage.dispose();
        skin.dispose();
    }

    @Override
    public void show() {
        // TODO Auto-generated method stub

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
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
