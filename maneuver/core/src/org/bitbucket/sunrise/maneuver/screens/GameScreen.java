package org.bitbucket.sunrise.maneuver.screens;


import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.bitbucket.sunrise.maneuver.ManeuverGame;
import org.bitbucket.sunrise.maneuver.asset.ResourceManager;
import org.bitbucket.sunrise.maneuver.game.GameWorld;
import org.bitbucket.sunrise.maneuver.game.RocketSpawner;
import org.bitbucket.sunrise.maneuver.render.InfiniteBackground;
import org.bitbucket.sunrise.maneuver.render.PhysicsActor;
import static org.bitbucket.sunrise.maneuver.Constants.*;

import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by takahawk on 07.03.16.
 */
public class GameScreen implements Screen {
    private static final int BOOM_TIME = 3;
    private static final int SWITCH_TO_GAME_OVER_TIME = 3;
    private static final float ROCKET_SPAWN_FREQ = 5f;
    private static final float ROCKET_DISTANCE = 500f;
    private float gameOverTimer = SWITCH_TO_GAME_OVER_TIME;
    private GameWorld world;
    private GameWorld.DebugRenderer debugRenderer;
    private GameWorld.GameBody plane;
    private RocketSpawner rocketSpawner;
    private ManeuverGame maneuverGame;
    private ResourceManager resourceManager;
    private List<GameWorld.GameBody> rockets = new ArrayList<GameWorld.GameBody>();

    private Viewport stageViewport = new FitViewport(
            ManeuverGame.WIDTH,
            (int) (ManeuverGame.WIDTH / ManeuverGame.RATIO)
        );
    private Stage gameStage = new Stage(stageViewport);
    private Stage pauseStage = new Stage() {

        @Override
        public boolean keyDown(int keyCode) {
            if (keyCode == Input.Keys.BACK)
                GameScreen.this.maneuverGame.setScreen(
                        new MenuScreen(GameScreen.this.maneuverGame)
                );
            return false;
        }
    };
    private Stage hud = new Stage() {

        @Override
        public boolean keyDown(int keyCode) {
            if (keyCode == Input.Keys.BACK) {
                paused = true;
                Gdx.input.setInputProcessor(pauseStage);
            }
            return false;
        }
    };
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
    private Animation rocketExplosionAnimation;
    private Animation planeExplosionAnimation;

    private Texture rightTurnTexture = new Texture(Gdx.files.internal("graphics/jet/right_turn.png"));
    private Texture leftTurnTexture = new Texture(Gdx.files.internal("graphics/jet/left_turn.png"));
    private Texture background;


    private Sound rocketSound = Gdx.audio.newSound(Gdx.files.internal("sounds/boom/rocket.mp3"));
    private Sound boomSound = Gdx.audio.newSound(Gdx.files.internal("sounds/boom/short explosion.mp3"));
    private Music planeSound = Gdx.audio.newMusic(Gdx.files.internal("sounds/airplane/uniform_noise.mp3"));
    private Music rotationSound = Gdx.audio.newMusic(Gdx.files.internal("sounds/airplane/rotation noise.mp3"));
    private final BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/white.fnt"));
    private static volatile float time = 0;
    private boolean isGameOver = false;
    private boolean paused = false;

    public GameScreen(final ManeuverGame maneuverGame, final SpriteBatch batch) {
        this.maneuverGame = maneuverGame;
        Preferences preferences = maneuverGame.getPreferences();
        resourceManager = maneuverGame.getResourceManager();
        initResources();
        font.setColor(1,0,0,1);
        time = 0;
        planeSound.setVolume(0.05f);
        rotationSound.setVolume(0.05f);
        this.batch = batch;
        world = new GameWorld(new Vector2(0, 0), 0.01f);
        debugRenderer = world.getDebugRenderer();
        TextureRegion planeTexture = resourceManager.getRegion("graphics/jet/plane.png");
        plane = world.addRectangularBody(
                new Vector2(400, 300),
                planeTexture.getRegionWidth(),
                planeTexture.getRegionHeight()
        );
        float rocketSpeed = preferences.getFloat("rocketSpeed");
        plane.setLinearVelocity(0, rocketSpeed * PLANE_SPEED_TO_ROCKET_SPEED_RATIO);
        rocketSpawner = new RocketSpawner(
                plane,
                ROCKET_SPAWN_FREQ,
                ROCKET_DISTANCE,
                rocketSpeed,
                rocketAnimation.getKeyFrame(1).getRegionWidth(),
                rocketAnimation.getKeyFrame(1).getRegionHeight(),
                preferences.getFloat("rocketResource")
        );
        rocketSpawner.addSpawnListener(new RocketSpawner.SpawnListener() {
            Map<GameWorld.GameBody, PhysicsActor> rocketActors = new HashMap<GameWorld.GameBody, PhysicsActor>();

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
                final PhysicsActor rocketActor = new PhysicsActor(rocket, rocketAnimation);
                rocketActors.put(rocket, rocketActor);
                rocketActor.addAnimation("depleted", rocketDepletedAnimation);
                rocketActor.addAnimation("explosion", rocketExplosionAnimation);
                rocketSound.play(0.05f);
                gameStage.addActor(rocketActor);
                rocket.setDestroyListener(new GameWorld.DestroyListener() {
                    @Override
                    public void destroyed() {
                        rocketActor.setCurrentAnimation("explosion", true);
                        rocketActor.addAction(new Action() {
                            float ttl = BOOM_TIME;
                            @Override
                            public boolean act(float delta) {
                                ttl -= delta;
                                if (ttl < 0) {
                                    rocketActor.remove();
                                    return true;
                                }
                                return false;
                            }
                        });
                        rockets.remove(rocket);
                        rocketActors.remove(rocket);
                        boomSound.play();
                    }
                });
                world.addContactHandler(plane, rocket, new GameWorld.ContactListener() {
                    @Override
                    public void beginContact() {
                        System.out.println("KABOOM!");
                        maneuverGame.getScoreManager().addScore("Player", (int) (time * 100));
                        bodiesToBeRemoved.offer(plane);
                        bodiesToBeRemoved.offer(rocket);
                        isGameOver = true;
                    }

                    @Override
                    public void endContact() {
                    }
                });
            }

            @Override
            public void depleted(GameWorld.GameBody rocket) {
                rocketActors.get(rocket).setCurrentAnimation("depleted", true);
            }
        });
        planeActor = new PhysicsActor(plane, planeTexture);
        planeActor.addTexture("right", resourceManager.getTexture("graphics/jet/right_turn.png"));
        planeActor.addTexture("left", resourceManager.getTexture("graphics/jet/left_turn.png"));
        planeActor.addAnimation("explosion", planeExplosionAnimation);
        gameStage.addActor(planeActor);
        plane.setDestroyListener(new GameWorld.DestroyListener() {
            @Override
            public void destroyed() {
                planeActor.setCurrentAnimation("explosion", true);
                planeActor.addAction(new Action() {
                    float ttl = BOOM_TIME;
                    @Override
                    public boolean act(float delta) {
                        ttl -= delta;
                        if (ttl < 0) {
                            planeActor.remove();
                            return true;
                        }
                        return false;
                    }
                });
            }
        });
        cam = (OrthographicCamera) gameStage.getViewport().getCamera();
        Actor backgroundActor = new InfiniteBackground(
                    cam,
                    new TextureRegion(background),
                    new Vector2(0, 0)
        );
        gameStage.addActor(backgroundActor);
        backgroundActor.toBack();
        initHud();
        setInputProcessor();
//        Gdx.input.setInputProcessor(hud);

        initHud();
        initPauseStage();
    }

    private void setInputProcessor() {
        Gdx.input.setInputProcessor(new InputMultiplexer(
                hud,
                gameStage
        ));
    }
    private void initPauseStage() {
        Table table = new Table();
        table.setFillParent(true);
        Texture texture = new Texture(Gdx.files.internal("play.png"));
        TextureRegionDrawable pauseImage = new TextureRegionDrawable(new TextureRegion(texture));
        ImageButton playButton = new ImageButton(pauseImage);
        table.add(playButton).center();
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                paused = false;
                Gdx.input.setInputProcessor(hud);
            }
        });

        Skin skin = new Skin();
        skin.add("white", new Texture("white4px.png"));
        Drawable backgroubd = skin.newDrawable("white", new Color(0, 0, 0, 0.8f));
        table.setBackground(backgroubd);
        pauseStage.addActor(table);
    }

    private void initHud() {
        Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        new Label("", skin).getStyle().font = font;
        Table table = new Table();
        table.setFillParent(true);
        table.add(new Label("", skin) {

            @Override
            public void act(float delta) {
                int score = (int) (time * 100);
                setText("HIGHSCORE: " + Math.max(score, maneuverGame.getScoreManager().highResult()));
            }
        }).align(Align.left);
        table.add(new Label("Time: ", skin) {

            @Override
            public void act(float delta) {
                setText("Score: " + (int)(time * 100));
            }
        }).expandX().align(Align.right).pad(20);
        table.row();
        table.add().expandY();
        table.row();
        Texture texture = new Texture(Gdx.files.internal("pause-button.png"));
        TextureRegionDrawable pauseImage = new TextureRegionDrawable(new TextureRegion(texture));
        ImageButton pauseButton = new ImageButton(pauseImage);
        table.add().expandX();
        table.add(pauseButton).expandX().right().pad(20);

        pauseButton.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                paused = true;
                Gdx.input.setInputProcessor(pauseStage);
                return true;
            }
        });

        hud.addActor(table);
    }

    public void initResources() {
        rocketDepletedAnimation = new Animation(
                0.2f,
                new Array<TextureRegion>(
                        new TextureRegion[] {
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
        rocketExplosionAnimation = new Animation(
                0.15f,
                new Array<TextureRegion>(
                        new TextureRegion[] {
                                resourceManager.getRegion("graphics/exploding_missile/00.png"),
                                resourceManager.getRegion("graphics/exploding_missile/01.png"),
                                resourceManager.getRegion("graphics/exploding_missile/02.png"),
                                resourceManager.getRegion("graphics/exploding_missile/03.png"),
                                resourceManager.getRegion("graphics/exploding_missile/04.png"),
                                resourceManager.getRegion("graphics/exploding_missile/05.png"),
                                resourceManager.getRegion("graphics/exploding_missile/06.png"),
                                resourceManager.getRegion("graphics/exploding_missile/07.png")
                        }
                )
        );
        planeExplosionAnimation = new Animation(
                0.1f,
                new Array<TextureRegion>(
                        new TextureRegion[] {
                                resourceManager.getRegion("graphics/exploding_jet/00.png"),
                                resourceManager.getRegion("graphics/exploding_jet/01.png"),
                                resourceManager.getRegion("graphics/exploding_jet/02.png"),
                                resourceManager.getRegion("graphics/exploding_jet/03.png"),
                                resourceManager.getRegion("graphics/exploding_jet/04.png"),
                                resourceManager.getRegion("graphics/exploding_jet/05.png"),
                                resourceManager.getRegion("graphics/exploding_jet/06.png"),
                                resourceManager.getRegion("graphics/exploding_jet/08.png"),
                                resourceManager.getRegion("graphics/exploding_jet/11.png"),
                                resourceManager.getRegion("graphics/exploding_jet/12.png"),
                                resourceManager.getRegion("graphics/exploding_jet/13.png"),
                                resourceManager.getRegion("graphics/exploding_jet/14.png"),
                                resourceManager.getRegion("graphics/exploding_jet/15.png")
                        }
                )
        );
        background = new Texture(Gdx.files.internal("graphics/background.jpg"));
    }

    public void playSounds() {
        if (plane.isActive() && !paused) {
            if (!planeSound.isPlaying())
                planeSound.play();
        } else if (planeSound.isPlaying()) {
            planeSound.stop();
        }
        if (isRotateControlPressed() && !paused) {
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
            plane.rotateVelocity(1.5f);
            System.out.println(getCameraAngle(cam));
            planeActor.setCurrentAnimation("left", false);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            plane.rotateVelocity(-1.5f);
            System.out.println(getCameraAngle(cam));
            planeActor.setCurrentAnimation("right", false);
        } else {
            planeActor.setCurrentAnimation("default", false);
        }
    }

    private float getCameraAngle(Camera cam) {
        return (float) Math.atan2(cam.up.x, cam.up.y) * MathUtils.radiansToDegrees;
    }

    private void updateCam() {
        cam.position.x = plane.getPosition().x ;
        cam.position.y = plane.getPosition().y ;
        cam.rotate(90f - getCameraAngle(cam) - plane.getVelocityAngle());
        cam.update();
    }

    public void update(float delta) {
        if(!paused) {
            rocketSpawner.update(delta);
            world.update(delta);
            while (!bodiesToBeRemoved.isEmpty()) {
                world.destroyBody(bodiesToBeRemoved.poll());
            }
            updateCam();
        }
        playSounds();
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        time += delta;
        if(!paused && !isGameOver) {
            handleInput();
        }
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!isGameOver && !paused) {
            hud.act();
        }
        gameStage.act(delta);
        gameStage.draw();
        hud.draw();
        if(paused) {
            pauseStage.act();
            pauseStage.draw();
        }
        if (isGameOver) {
            gameOverTimer -= delta;
            if (gameOverTimer < 0) {
                maneuverGame.setScreen(new GameOverScreen(maneuverGame));
            }
        }
//        Matrix4 debugMatrix = cam.combined.cpy().scale(1 / world.getScale(), 1 / world.getScale(), 0);
//        debugRenderer.render(debugMatrix);
    }

    @Override
    public void resize(int width, int height) {
        stageViewport.update(width, height);
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
