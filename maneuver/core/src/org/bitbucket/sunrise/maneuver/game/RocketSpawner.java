package org.bitbucket.sunrise.maneuver.game;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by takahawk on 15.03.16.
 */
public class RocketSpawner {
    private GameWorld.GameBody plane;
    private float frequency;
    private float distance;
    private Vector2 rocketForce;

    public interface SpawnListener {
        void spawned(GameWorld.GameBody rocket);
    }


    public RocketSpawner(
            GameWorld.GameBody plane,
            float frequency,
            float distance,
            Vector2 rocketForce) {
        this.plane = plane;
        this.frequency = frequency;
        this.distance = distance;
        this.rocketForce = rocketForce;
    }

    public void spawn() {
        Vector2 distVector = new Vector2(distance, 0).setToRandomDirection();
    }
}
