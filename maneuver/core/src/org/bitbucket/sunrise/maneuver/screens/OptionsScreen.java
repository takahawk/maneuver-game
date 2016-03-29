package org.bitbucket.sunrise.maneuver.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import org.bitbucket.sunrise.maneuver.ManeuverGame;
import org.bitbucket.sunrise.maneuver.game.ScoreManager;

/**
 * Created by takahawk on 29.03.16.
 */
public class OptionsScreen implements Screen {
    private static final int WIDTH = 1184 / 3;
    private static final int HEIGHT = 720 / 3;
    private static final float RATIO = (float) WIDTH / HEIGHT;

    public ManeuverGame game;
    public Stage stage = new Stage(new FitViewport(
            WIDTH,
            (int) (WIDTH / RATIO)
    ));
    static {
        if (!VisUI.isLoaded())
            VisUI.load();
    }

    public OptionsScreen(ManeuverGame game) {
        this.game = game;
        initUI();
    }

    public void initUI() {
        final Preferences preferences = game.getPreferences();
        VisWindow table = new VisWindow("OPTIONS");
        table.setFillParent(true);
        table.setMovable(false);
        table.add(new VisLabel("Rocket speed: ")).align(Align.left);
        table.add(getFloatPreferenceSlider("rocketSpeed", 100, 1000, 25)).pad(15);
        table.add(getFloatPreferenceLabel("rocketSpeed"));
        table.row();
        table.add(new VisLabel("Rocket resource: ")).align(Align.left);
        table.add(getFloatPreferenceSlider("rocketResource", 2, 10, 0.25f)).pad(15);
        table.add(getFloatPreferenceLabel("rocketResource"));
        table.row();
        table.add(new VisLabel("Plane speed: ")).align(Align.left);
        table.add(getFloatPreferenceSlider("planeSpeed", 100, 1000, 25f)).pad(15);
        table.add(getFloatPreferenceLabel("planeSpeed"));
        table.row();


        table.add().expand();
        table.row();
        VisTextButton toMenu = new VisTextButton("MENU");
        toMenu.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                preferences.flush();
                game.setScreen(new MenuScreen(game));
            }
        });
        table.add(toMenu);
        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
    }

    private VisSlider getFloatPreferenceSlider(final String preference, float min, float max, float step) {
        final VisSlider rocketSpeedSlider = new VisSlider(min, max, step, false);
        rocketSpeedSlider.setValue(game.getPreferences().getFloat(preference));
        rocketSpeedSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.getPreferences().putFloat(preference, rocketSpeedSlider.getValue());
            }
        });
        return rocketSpeedSlider;
    }

    private VisLabel getFloatPreferenceLabel(final String preference) {
        return new VisLabel() {

            @Override
            public void act(float delta) {
                setText(Float.toString(game.getPreferences().getFloat(preference)));
            }
        };
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
