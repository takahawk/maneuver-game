package org.bitbucket.sunrise.maneuver.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import org.bitbucket.sunrise.maneuver.game.GameWorld;

/**
 * Created by takahawk on 12.03.16.
 */
public class PhysicsActor extends Actor {
    private GameWorld.GameBody gameBody;
    TextureRegion texture;

    public PhysicsActor(GameWorld.GameBody gameBody, TextureRegion texture) {
        this.gameBody = gameBody;
        this.texture = texture;
    }

    public PhysicsActor(GameWorld.GameBody gameBody, Texture texture) {
        this(gameBody, new TextureRegion(texture));
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(
                texture,
                gameBody.getPosition().x,
                gameBody.getPosition().y,
                texture.getRegionWidth(),
                texture.getRegionHeight()
        );
    }
}
