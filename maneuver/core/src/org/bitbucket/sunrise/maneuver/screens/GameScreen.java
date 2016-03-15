package org.bitbucket.sunrise.maneuver.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import org.bitbucket.sunrise.maneuver.game.GameWorld;
import org.bitbucket.sunrise.maneuver.game.RocketSpawner;
import org.bitbucket.sunrise.maneuver.render.PhysicsActor;

import java.util.ArrayList;
import java.util.List;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created by takahawk on 07.03.16.
 */
public class GameScreen implements Screen {
    private static final float ROCKET_SPAWN_FREQ = 5f;
    private static final float ROCKET_DISTANCE = 300f;
    private static final float ROCKET_FORCE = 300f;
    private GameWorld world;
    private GameWorld.DebugRenderer debugRenderer;
    private GameWorld.GameBody plane;
    private RocketSpawner rocketSpawner;
    private List<GameWorld.GameBody> rockets = new ArrayList<GameWorld.GameBody>();
    private Stage stage = new Stage();
    private OrthographicCamera cam;
    private Queue<GameWorld.GameBody> bodiesToBeRemoved = new ArrayDeque<GameWorld.GameBody>();

    private SpriteBatch batch;
    private TextureRegion rocketTexture = new TextureRegion(new Texture(Gdx.files.internal("rocket.png")));
    private TextureRegion planeTexture = new TextureRegion(new Texture(Gdx.files.internal("plane.png")));
    private Texture background = new Texture(Gdx.files.internal("background.png"));

    private Sound boomSound = Gdx.audio.newSound(Gdx.files.internal("sounds/boom/short explosion.mp3"));
    private Music planeSound = Gdx.audio.newMusic(Gdx.files.internal("sounds/airplane/uniform_noise.mp3"));
    private Music rotationSound = Gdx.audio.newMusic(Gdx.files.internal("sounds/airplane/rotation noise.mp3"));

    public GameScreen(SpriteBatch batch) {
        planeSound.setVolume(0.5f);
        this.batch = batch;
        world = new GameWorld(new Vector2(0, 0), 0.01f);
        debugRenderer = world.getDebugRenderer();
        plane = world.addRectangularBody(
                new Vector2(400, 300),
                planeTexture.getRegionWidth(),
                planeTexture.getRegionHeight()
        );
        plane.setLinearVelocity(0, 200);
        rocketSpawner = new RocketSpawner(
                plane,
                ROCKET_SPAWN_FREQ,
                ROCKET_DISTANCE,
                ROCKET_FORCE,
                rocketTexture.getRegionWidth(),
                rocketTexture.getRegionHeight()
        );
        rocketSpawner.addSpawnListener(new RocketSpawner.SpawnListener() {
            @Override
            public void spawned(final GameWorld.GameBody rocket) {
                final Actor rocketActor = new PhysicsActor(rocket, rocketTexture);
                stage.addActor(rocketActor);
                rocket.setDestroyListener(new GameWorld.DestroyListener() {
                    @Override
                    public void destroyed() {
                        rocketActor.remove();
                    }
                });
                world.addContactHandler(plane, rocket, new GameWorld.ContactListener() {
                    @Override
                    public void beginContact() {
                        System.out.println("KABOOM!");
                        boomSound.play();
                        bodiesToBeRemoved.offer(plane);
                        bodiesToBeRemoved.offer(rocket);
                    }

                    @Override
                    public void endContact() {

                    }
                });
            }
        });
        final Actor planeActor = new PhysicsActor(plane, planeTexture);
        stage.addActor(planeActor);
        plane.setDestroyListener(new GameWorld.DestroyListener() {
            @Override
            public void destroyed() {
                planeActor.remove();
            }
        });
        Actor backgroundActor = new Actor() {

            @Override
            public void draw(Batch batch, float parentAlpha) {
                batch.draw(background, getX(), getY(), background.getWidth(), background.getHeight());
            }
        };
        stage.addActor(backgroundActor);
        backgroundActor.toBack();
        cam = (OrthographicCamera) stage.getViewport().getCamera();
    }

    public void playSounds() {
        if (plane.isActive()) {
            if (!planeSound.isPlaying())
                planeSound.play();
        } else if (planeSound.isPlaying()) {
            planeSound.stop();
        }
        if (isRotateControlPressed()) {
            if (!rotationSound.isPlaying())
                rotationSound.play();
        } else if (rotationSound.isPlaying()) {
            rotationSound.stop();
        }
    }

    public boolean isRotateControlPressed() {
        return  Gdx.input.isTouched() ||
                Gdx.input.isKeyPressed(Input.Keys.LEFT) ||
                Gdx.input.isKeyPressed(Input.Keys.RIGHT);
    }

    public void handleTouch() {
        if(Gdx.input.getX() < Gdx.graphics.getWidth() / 2) {
            plane.rotateVelocity(1f);
        }
        if(Gdx.input.getX() > Gdx.graphics.getWidth() / 2) {
            plane.rotateVelocity(-1f);
        }
    }

    public void handleInput() {
        if(Gdx.input.isTouched()) {
            handleTouch();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            // sound.play();
            plane.rotateVelocity(1f);
            System.out.println(getCameraAngle(cam));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            plane.rotateVelocity(-1f);
            System.out.println(getCameraAngle(cam));
        }
    }

    private float getCameraAngle(Camera cam) {
        return (float) Math.atan2(cam.up.x, cam.up.y) * MathUtils.radiansToDegrees;
    }

    private void updateCam() {
        cam.position.x = plane.getPosition().x;
        cam.position.y = plane.getPosition().y;
        cam.rotate(180f - plane.getVelocityAngle() - 90f - getCameraAngle(cam));
        cam.update();
    }

    public void update(float delta) {
        rocketSpawner.update(delta);
        world.update(delta);
        while (!bodiesToBeRemoved.isEmpty()) {
            world.destroyBody(bodiesToBeRemoved.poll());
        }
        updateCam();
        playSounds();
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
        stage.draw();
        Matrix4 debugMatrix = cam.combined.cpy().scale(1 / world.getScale(), 1 / world.getScale(), 0);
        debugRenderer.render(debugMatrix);
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
