package org.bitbucket.sunrise.maneuver.game;

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
    private List<SpawnListener> spawnListeners = new ArrayList<SpawnListener>();
    private Vector2 rocketForce;

    public interface SpawnListener {
        void spawned(GameWorld.GameBody rocket);
    }


    public RocketSpawner(
            GameWorld.GameBody plane,
            float frequency,
            float distance,
            Vector2 rocketForce,
            float width,
            float height
        ) {
        this.plane = plane;
        this.frequency = frequency;
        this.distance = distance;
        this.rocketForce = rocketForce;
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
        Vector2 distVector = new Vector2(distance, 0).setToRandomDirection();
        GameWorld.GameBody rocket = plane.getWorld().addRectangularBody(
                plane.getPosition().add(distVector),
                width,
                height
        );

        for (SpawnListener l : spawnListeners) {
            l.spawned(rocket);
        }
    }
}
