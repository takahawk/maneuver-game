package org.bitbucket.sunrise.maneuver.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by takahawk on 12.03.16.
 */
public class GameWorld {
    private World world;
    private float scale = 1f;
    private Map<Body, GameBody> gameBodies = new HashMap<Body, GameBody>();
    private Map<ContactPair, ContactListener> contactListeners = new HashMap<ContactPair, ContactListener>();

    public class DebugRenderer {
        Box2DDebugRenderer renderer = new Box2DDebugRenderer();

        public void render(Matrix4 projMatrix) {
            renderer.render(world, projMatrix);
        }
    }

    public class ContactPair {
        public GameBody first;
        public GameBody second;

        public ContactPair(GameBody first, GameBody second) {
            this.first = first;
            this.second = second;
        }
        @Override
        public int hashCode() {
            return first.hashCode() + second.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof ContactPair))
                return false;
            ContactPair contactPair = (ContactPair) obj;
            return first == contactPair.first && second == contactPair.second;
        }
    }

    public interface ContactListener {
        void beginContact();
        void endContact();
    }

    public interface UpdateListener {
        void update(float delta);
    }

    public interface DestroyListener {
        void destroyed();
    }

    public class GameBody {
        private UpdateListener updateListener;
        private DestroyListener destroyListener;
        private Body body;
        private boolean alive = true;

        public GameBody(Body body) {
            this.body = body;
        }

        public void applyForce(float forceX, float forceY, boolean wake) {
            body.applyForceToCenter(forceX * scale, forceY * scale, wake);
        }

        public void applyForce(Vector2 force, boolean wake) {
            applyForce(force.x, force.y, wake);
        }

        public void applyForce(float forceX, float forceY, float pointX, float pointY, boolean wake) {
            body.applyForce(forceX * scale, forceY * scale, pointX * scale, pointY * scale, wake);
        }

        public Vector2 getLinearVelocity() {
            return body.getLinearVelocity().cpy().scl(1 / scale, 1 / scale);
        }

        public void setLinearVelocity(float x, float y) {
            body.setLinearVelocity(x * scale, y * scale);
        }

        public void rotateVelocity(float angle) {
            float oldAngle = body.getLinearVelocity().angle();
            setVelocityAngle(angle + oldAngle);
        }

        public GameWorld getWorld() {
            return GameWorld.this;
        }

        public void setVelocityAngle(float angle) {
            float length = body.getLinearVelocity().len();
            body.setLinearVelocity(
                    new Vector2(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle)).setLength(length)
            );
            body.setTransform(body.getPosition(), (angle - 90f) * MathUtils.degreesToRadians);
        }

        public void setUpdateListener(UpdateListener updateListener) {
            this.updateListener = updateListener;
        }

        public void setDestroyListener(DestroyListener destroyListener) {
            this.destroyListener = destroyListener;
        }

        public float getVelocityAngle() {
            return body.getLinearVelocity().angle();
        }

        public Vector2 getPosition() {
            return new Vector2(body.getPosition().x / scale, body.getPosition().y / scale);
        }

        public Body getBody() {return this.body; }

        public boolean isActive() {
            return alive;
        }

    }

    public GameWorld(Vector2 gravity) {
        world = new World(gravity, true);
    }
    public GameWorld(Vector2 gravity, float scale) {
        this(gravity);
        this.scale = scale;
        world.setContactListener(new com.badlogic.gdx.physics.box2d.ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                ContactPair contactPair = new ContactPair(
                        gameBodies.get(contact.getFixtureA().getBody()),
                        gameBodies.get(contact.getFixtureB().getBody())
                );
                if (contactListeners.containsKey(contactPair)) {
                    contactListeners.get(contactPair).beginContact();
                }
            }

            @Override
            public void endContact(Contact contact) {
                ContactPair contactPair = new ContactPair(
                        gameBodies.get(contact.getFixtureA().getBody()),
                        gameBodies.get(contact.getFixtureB().getBody())
                );
                if (contactListeners.containsKey(contactPair)) {
                    contactListeners.get(contactPair).endContact();
                }
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });
    }

    public float getScale() {
        return scale;
    }
    public void update(float delta) {
        world.step(1 / 60f, 6, 3);
        for (GameBody body : gameBodies.values()) {
            if (body.updateListener != null)
                body.updateListener.update(delta);
        }
    }

    public GameBody addRectangularBody(Vector2 position, float width, float height) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position.x * scale, position.y * scale);
        Body body = world.createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width * scale / 2, height * scale / 2);
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);
        GameBody gameBody = new GameBody(body);
        gameBodies.put(gameBody.body, gameBody);
        return gameBody;
    }

    public DebugRenderer getDebugRenderer() {
        return new DebugRenderer();
    }

    public void addContactHandler(GameBody first, GameBody second, ContactListener listener) {
        contactListeners.put(new ContactPair(first, second), listener);
    }

    public void destroyBody(GameBody body) {
        if (body.alive) {
            world.destroyBody(body.getBody());
            if (body.destroyListener != null)
                body.destroyListener.destroyed();
            body.alive = false;
        }
    }
}