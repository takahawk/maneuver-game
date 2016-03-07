package org.bitbucket.sunrise.maneuver.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import org.bitbucket.sunrise.maneuver.game.Plane;
import org.bitbucket.sunrise.maneuver.game.Rocket;

/**
 * Created by takahawk on 07.03.16.
 */
public class GameScreen implements Screen {
    private SpriteBatch batch;
    private Plane plane;
    private Rocket rocket;
    private TextureRegion rocketTexture = new TextureRegion(new Texture(Gdx.files.internal("rocket.png")));
    private TextureRegion planeTexture = new TextureRegion(new Texture(Gdx.files.internal("plane.png")));

    public GameScreen(SpriteBatch batch) {
        this.batch = batch;
        plane = new Plane(400, 300);
        rocket = new Rocket(plane, 100, 200, 200);
    }

    public void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            plane.moveLeft(10);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            plane.moveRight(10);
    }

    public void update(float delta) {
        rocket.update(delta);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        handleInput();
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        Rectangle planeBounds = plane.getBounds();
        batch.draw(
                planeTexture,
                planeBounds.getX(),
                planeBounds.getY(),
                planeBounds.width,
                planeBounds.height
        );
        Rectangle rocketBounds = rocket.getBounds();
        batch.draw(
                rocketTexture,
                rocketBounds.getX(),
                rocketBounds.getY(),
                rocketBounds.width / 2,
                rocketBounds.height / 2,
                rocketBounds.width,
                rocketBounds.height,
                1,
                1,
                rocket.getDirectionAngle()
        );
        batch.end();
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
