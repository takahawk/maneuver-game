package org.bitbucket.sunrise.maneuver.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by takahawk on 15.03.16.
 */
public class RocketSpawner {
    private GameWorld.GameBody plane;
    private float frequency;
    private float distance;
    private float width;
    private float height;
    private float elapsedTime = 0;
    private float resourceTime;
    private List<SpawnListener> spawnListeners = new ArrayList<SpawnListener>();
    private float rocketForce;

    public interface SpawnListener {
        void spawned(GameWorld.GameBody rocket);
    }


    public RocketSpawner(
            GameWorld.GameBody plane,
            float frequency,
            float distance,
            float rocketForce,
            float width,
            float height,
            float resourceTime
        ) {
        this.plane = plane;
        this.frequency = frequency;
        this.distance = distance;
        this.rocketForce = rocketForce;
        this.width = width;
        this.height = height;
        this.resourceTime = resourceTime;
    }

    public void addSpawnListener(SpawnListener listener) {
        spawnListeners.add(listener);
    }

    public void update(float delta) {
        elapsedTime += delta;
        if (elapsedTime > frequency) {
            elapsedTime -= frequency;
            spawn();
        }
    }
    public void spawn() {
        Vector2 distVector = new Vector2(distance, 0);
        distVector.rotate(MathUtils.random(360));
        final GameWorld.GameBody rocket = plane.getWorld().addRectangularBody(
                new Vector2(plane.getPosition()).add(distVector),
                width,
                height
        );
        rocket.setUpdateListener(new GameWorld.UpdateListener() {
            @Override
            public void update(float delta) {
                rocket.applyForce(
                        plane.getPosition().sub(rocket.getPosition()).setLength(rocketForce),
                        true
                );
            }
        });
        for (SpawnListener l : spawnListeners) {
            l.spawned(rocket);
        }
    }
}
