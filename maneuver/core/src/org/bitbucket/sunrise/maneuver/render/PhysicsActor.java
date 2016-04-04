package org.bitbucket.sunrise.maneuver.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import org.bitbucket.sunrise.maneuver.game.GameWorld;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by takahawk on 12.03.16.
 */
public class PhysicsActor extends Actor {
    private GameWorld.GameBody gameBody;
    private Map<String, Animation> animations = new HashMap<String, Animation>();
    private Animation currentAnimation;
    private float elapsedTime = 0;

    public PhysicsActor(GameWorld.GameBody gameBody, Animation animation) {
        this.gameBody = gameBody;
        animations.put("default", animation);
        currentAnimation = animation;
    }

    public PhysicsActor(GameWorld.GameBody gameBody, TextureRegion texture) {
        this(gameBody, new Animation(1f, texture));
    }

    public PhysicsActor(GameWorld.GameBody gameBody, Texture texture) {
        this(gameBody, new TextureRegion(texture));
    }

    public void addAnimation(String name, Animation animation) {
        animations.put(name, animation);
    }

    public void addTexture(String name, TextureRegion texture) {
        addAnimation(name, new Animation(1f, texture));
    }

    public void addTexture(String name, Texture texture) {
        addTexture(name, new TextureRegion(texture));
    }

    public void setCurrentAnimation(String name, boolean updateElapsedTime) {
        currentAnimation = animations.get(name);
        if (updateElapsedTime)
            elapsedTime = 0;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion texture = currentAnimation.getKeyFrame(elapsedTime, currentAnimation.getPlayMode() != Animation.PlayMode.NORMAL);
        batch.draw(
                texture,
                gameBody.getPosition().x - texture.getRegionWidth() / 2,
                gameBody.getPosition().y - texture.getRegionHeight() / 2,
                texture.getRegionWidth() / 2,
                texture.getRegionHeight() / 2,
                texture.getRegionWidth(),
                texture.getRegionHeight(),
                1,
                1,
                gameBody.getVelocityAngle() - 90f
        );
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        elapsedTime += delta;
    }
}
