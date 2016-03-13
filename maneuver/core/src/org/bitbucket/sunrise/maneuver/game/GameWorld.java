package org.bitbucket.sunrise.maneuver.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by takahawk on 12.03.16.
 */
public class GameWorld {
    private World world;
    private float scale = 1f;

    public class GameBody {
        Body body;

        public GameBody(Body body) {
            this.body = body;
        }

        public void applyForce(float forceX, float forceY, boolean wake) {
            body.applyForceToCenter(forceX * scale, forceY * scale, wake);
        }
        public void applyForce(float forceX, float forceY, float pointX, float pointY, boolean wake) {
            body.applyForce(forceX * scale, forceY * scale, pointX * scale, pointY * scale, wake);
        }

        public Vector2 getPosition() {
            return new Vector2(body.getPosition().x / scale, body.getPosition().y / scale);
        }

    }

    public GameWorld(Vector2 gravity) {
        world = new World(gravity, true);
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void update() {
        world.step(1 / 60f, 6, 3);
    }

    public GameBody addRectangularBody(Vector2 position, float width, float height) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position.x * scale, position.y * scale);
        Body body = world.createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width * scale, height * scale);
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);
        return new GameBody(body);
    }

}