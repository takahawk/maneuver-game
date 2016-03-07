package org.bitbucket.sunrise.maneuver.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by takahawk on 07.03.16.
 */
public class Rocket {
    private static final float BASE_ACCELERATION = 300;
    private static final float WIDTH = 40;
    private static final float HEIGHT = 100;
    private Rectangle bounds;
    private Plane target;
    private Vector2 position;
    private Vector2 velocity;
    private Vector2 acceleration;
    private float remainingTime;

    public Rocket(Plane target, float x, float y, float time) {
        bounds = new Rectangle();
        this.target = target;
        position = new Vector2(x, y);
        remainingTime = time;
        velocity = new Vector2(0, 0);
        acceleration = new Vector2(0, 0);
    }

    public Rectangle getBounds() {
        bounds.set(getX(), getY(), WIDTH, HEIGHT);
        return bounds;
    }

    public float getX() {
        return position.x - WIDTH / 2;
    }

    public float getY() {
        return position.y - HEIGHT / 2;
    }

    public float getOriginX() {
        return position.x;
    }

    public float getOriginY() {
        return position.y;
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getDirectionAngle() {
        //return 90;
        /*System.out.println((float) Math.acos(velocity.x / velocity.len()) * MathUtils.radiansToDegrees);
        return (float) (Math.acos(velocity.x / velocity.len()) * MathUtils.radiansToDegrees);*/
        return 270 + MathUtils.radiansToDegrees * MathUtils.atan2(velocity.y, velocity.x);
    }
    public void update(float delta) {
        // angle
        float distance =
                (float)
                Math.sqrt(Math.pow((target.getX() - position.x), 2) + Math.pow((target.getY() - position.y), 2));
        acceleration.set(
                BASE_ACCELERATION * (target.getPosition().x - position.x) / distance,
                BASE_ACCELERATION * (target.getPosition().y - position.y) / distance
        );

        velocity.add(acceleration.x * delta, acceleration.y * delta);
        position.add(velocity.x * delta, velocity.y * delta);
    }
}
