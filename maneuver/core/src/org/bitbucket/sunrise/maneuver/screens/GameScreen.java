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
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import org.bitbucket.sunrise.maneuver.ManeuverGame;
import org.bitbucket.sunrise.maneuver.asset.ResourceManager;
import org.bitbucket.sunrise.maneuver.game.GameWorld;
import org.bitbucket.sunrise.maneuver.game.RocketSpawner;
import org.bitbucket.sunrise.maneuver.render.InfiniteBackground;
import org.bitbucket.sunrise.maneuver.render.PhysicsActor;

import java.util.ArrayList;
import java.util.List;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

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
    private ResourceManager resourceManager;
    private List<GameWorld.GameBody> rockets = new ArrayList<GameWorld.GameBody>();
    private Stage stage = new Stage();
    private Stage hud = new Stage();
    private OrthographicCamera cam;
    private Queue<GameWorld.GameBody> bodiesToBeRemoved = new ArrayDeque<GameWorld.GameBody>();

    private PhysicsActor planeActor;
    private SpriteBatch batch;
    private Animation rocketAnimation = new Animation(
            0.5f,
            new TextureRegion(new Texture(Gdx.files.internal("graphics/missile/1.png"))),
            new TextureRegion(new Texture(Gdx.files.internal("graphics/missile/2.png"))),
            new TextureRegion(new Texture(Gdx.files.internal("graphics/missile/3.png")))
    );
    private Animation rocketDepletedAnimation;

    private Texture rightTurnTexture = new Texture(Gdx.files.internal("graphics/jet/right_turn.png"));
    private Texture leftTurnTexture = new Texture(Gdx.files.internal("graphics/jet/left_turn.png"));
    private Texture background = new Texture(Gdx.files.internal("background.png"));


    private Sound rocketSound = Gdx.audio.newSound(Gdx.files.internal("sounds/boom/rocket.mp3"));
    private Sound boomSound = Gdx.audio.newSound(Gdx.files.internal("sounds/boom/short explosion.mp3"));
    private Music planeSound = Gdx.audio.newMusic(Gdx.files.internal("sounds/airplane/uniform_noise.mp3"));
    private Music rotationSound = Gdx.audio.newMusic(Gdx.files.internal("sounds/airplane/rotation noise.mp3"));
    private final BitmapFont font = new BitmapFont();
    private static volatile float time = 0;
    private static Thread timeThread = null;

    public GameScreen(final ManeuverGame maneuverGame, final SpriteBatch batch) {
        resourceManager = maneuverGame.getResourceManager();
        initResources();
        font.setColor(1,0,0,1);
        time = 0;
        planeSound.setVolume(0.3f);
        rotationSound.setVolume(0.5f);
        this.batch = batch;
        world = new GameWorld(new Vector2(0, 0), 0.01f);
        debugRenderer = world.getDebugRenderer();
        TextureRegion planeTexture = resourceManager.getRegion("graphics/jet/plane.png");
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
                rocketAnimation.getKeyFrame(1).getRegionWidth(),
                rocketAnimation.getKeyFrame(1).getRegionHeight(),
                2
        );
        rocketSpawner.addSpawnListener(new RocketSpawner.SpawnListener() {
            PhysicsActor rocketActor;

            @Override
            public void spawned(final GameWorld.GameBody rocket) {
                rocketSound.play();
                for (GameWorld.GameBody body : rockets) {
                    final GameWorld.GameBody rocket2 = body;
                    world.addContactHandler(rocket, rocket2, new GameWorld.ContactListener() {
                        @Override
                        public void beginContact() {
                            boomSound.play();
                            bodiesToBeRemoved.offer(rocket);
                            bodiesToBeRemoved.offer(rocket2);
                        }

                        @Override
                        public void endContact() {

                        }
                    });
                }
                rockets.add(rocket);
                rocketActor = new PhysicsActor(rocket, rocketAnimation);
                rocketActor.addAnimation("depleted", rocketDepletedAnimation);
                stage.addActor(rocketActor);
                rocket.setDestroyListener(new GameWorld.DestroyListener() {
                    @Override
                    public void destroyed() {
                        rocketActor.remove();
                        rockets.remove(rocket);
                        boomSound.play();
                    }
                });
                world.addContactHandler(plane, rocket, new GameWorld.ContactListener() {
                    @Override
                    public void beginContact() {
                        System.out.println("KABOOM!");

                        bodiesToBeRemoved.offer(plane);
                        bodiesToBeRemoved.offer(rocket);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    TimeUnit.SECONDS.sleep(3);
                                    Gdx.app.postRunnable(new Runnable() {
                                        @Override
                                        public void run() {
                                            maneuverGame.setScreen(new GameScreen(maneuverGame, batch));
                                        }
                                    });
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            }
                        }).start();

                    }

                    @Override
                    public void endContact() {
                    }
                });
            }

            @Override
            public void depleted(GameWorld.GameBody rocket) {
                rocketActor.setCurrentAnimation("depleted");
            }
        });
        planeActor = new PhysicsActor(plane, planeTexture);
        planeActor.addTexture("right", resourceManager.getTexture("graphics/jet/right_turn.png"));
        planeActor.addTexture("left", resourceManager.getTexture("graphics/jet/left_turn.png"));
        stage.addActor(planeActor);
        plane.setDestroyListener(new GameWorld.DestroyListener() {
            @Override
            public void destroyed() {
                planeActor.remove();
            }
        });
        cam = (OrthographicCamera) stage.getViewport().getCamera();
        Actor backgroundActor = new InfiniteBackground(
                    cam,
                    new TextureRegion(background),
                    new Vector2(0, 0)
        );
        stage.addActor(backgroundActor);
        backgroundActor.toBack();
        initHud();

    }

    private void initHud() {
        Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        Table table = new Table();
        table.setFillParent(true);
        table.add(new Label("Time: ", skin) {

            @Override
            public void act(float delta) {
                setText("Score: " + (int)(time * 100));
            }
        }).expandX().align(Align.right).pad(20);
        table.row();
        table.add().expandY();
        table.row();
        hud.addActor(table);
    }

    public void initResources() {
        rocketDepletedAnimation = new Animation(
                0.2f,
                new Array<TextureRegion>(
                        new TextureRegion[]{
                                resourceManager.getRegion("graphics/depleted_missile/Missile00.png"),
                                resourceManager.getRegion("graphics/depleted_missile/Missile01.png"),
                                resourceManager.getRegion("graphics/depleted_missile/Missile02.png"),
                                resourceManager.getRegion("graphics/depleted_missile/Missile03.png"),
                                resourceManager.getRegion("graphics/depleted_missile/Missile04.png"),
                                resourceManager.getRegion("graphics/depleted_missile/Missile05.png"),
                                resourceManager.getRegion("graphics/depleted_missile/Missile06.png"),
                                resourceManager.getRegion("graphics/depleted_missile/Missile07.png"),
                                resourceManager.getRegion("graphics/depleted_missile/Missile08.png"),
                                resourceManager.getRegion("graphics/depleted_missile/Missile09.png"),
                                resourceManager.getRegion("graphics/depleted_missile/Missile010.png")
                        }
                ),
                Animation.PlayMode.NORMAL
        );
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
            planeActor.setCurrentAnimation("left");
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            plane.rotateVelocity(-1f);
            System.out.println(getCameraAngle(cam));
            planeActor.setCurrentAnimation("right");
        } else {
            planeActor.setCurrentAnimation("default");
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
        time += delta;
        handleInput();
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();
        hud.act();
        hud.draw();
        // Matrix4 debugMatrix = cam.combined.cpy().scale(1 / world.getScale(), 1 / world.getScale(), 0);
        //debugRenderer.render(debugMatrix);
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
