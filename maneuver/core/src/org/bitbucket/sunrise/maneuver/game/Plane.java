package org.bitbucket.sunrise.maneuver.game;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by takahawk on 07.03.16.
 */
public class Plane {
    private static final float WIDTH = 200;
    private static final float HEIGHT = 100;
    private Vector2 position;
    private Rectangle bounds;

    public Plane(Vector2 position) {
        this.position = position;
        bounds = new Rectangle();
    }

    public Plane(float x, float y) {
        this(new Vector2(x, y));
    }

    public Rectangle getBounds() {
        bounds.set(getX(), getY(), WIDTH, HEIGHT);
        return bounds;
    }

    public float getOriginX() {
        return position.x;
    }

    public float getOriginY() {
        return position.y;
    }

    public float getX() {
        return position.x - WIDTH / 2;
    }

    public float getY() {
        return position.y - HEIGHT / 2;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void moveLeft(float amount) {
        position.x -= amount;
    }

    public void moveRight(float amount) {
        position.x += amount;
    }
}
