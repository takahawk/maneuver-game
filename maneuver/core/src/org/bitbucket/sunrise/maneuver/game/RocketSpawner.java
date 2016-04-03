package org.bitbucket.sunrise.maneuver.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import static org.bitbucket.sunrise.maneuver.Constants.*;

/**
 * Created by takahawk on 15.03.16.
 */
public class RocketSpawner {
    public static final float DEPLETED_MISSILE_LIFETIME = 2;
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
        void depleted(GameWorld.GameBody rocket);
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
        distVector.rotate(MathUtils.random(
                plane.getVelocityAngle() - 90 + 180,
                plane.getVelocityAngle() - 90 + 330
        ));
        final GameWorld.GameBody rocket = plane.getWorld().addRectangularBody(
                new Vector2(plane.getPosition()).add(distVector),
                width,
                height
        );
        rocket.setUpdateListener(new GameWorld.UpdateListener() {
            float resource = resourceTime;
            @Override
            public void update(float delta) {
                if (!rocket.isActive())
                    return;
                resource -= delta;
                if (resource > 0) {
                    Vector2 velocity = rocket.getLinearVelocity();
                    rocket.applyForce(
                            new Vector2(rocketForce * AIR_RESISTANCE_MODIFIER, 0).setAngle(velocity.angle() - 180),
                            true
                    );
                    rocket.applyForce(
                            plane.getPosition().sub(rocket.getPosition()).setLength(rocketForce / AIR_RESISTANCE_MODIFIER),
                            true
                    );
                    rocket.setVelocityAngle(
                            rocket.getVelocityAngle()
                    );
                } else if (resource < -DEPLETED_MISSILE_LIFETIME) {
                    GameWorld world = rocket.getWorld();
                    if (rocket.isActive())
                        world.destroyBody(rocket);
                } else {
                    for (SpawnListener listener : spawnListeners) {
                        listener.depleted(rocket);
                    }
                }
            }
        });
        for (SpawnListener l : spawnListeners) {
            l.spawned(rocket);
        }
    }
}
