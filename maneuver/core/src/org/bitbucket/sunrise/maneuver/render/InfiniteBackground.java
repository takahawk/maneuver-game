package org.bitbucket.sunrise.maneuver.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by takahawk on 15.03.16.
 */
public class InfiniteBackground extends Actor {
    private Camera camera;
    private TextureRegion texture;


    public InfiniteBackground(Camera camera, TextureRegion texture, Vector2 originPosition) {
        this.camera = camera;
        this.texture = texture;
        setPosition(originPosition.x, originPosition.y);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float leftX = getX();
        float rightX = getX();
        float topY = getY();
        float bottomY = getY();
        while (leftX > camera.position.x - camera.viewportWidth)
            leftX -= texture.getRegionWidth();
        while (rightX < camera.position.x + camera.viewportWidth)
            rightX += texture.getRegionWidth();
        while (bottomY > camera.position.y - camera.viewportHeight)
            bottomY -= texture.getRegionHeight();
        while (topY < camera.position.y + camera.viewportHeight)
            topY += texture.getRegionHeight();
        for (float x = leftX; x < rightX + 1; x += texture.getRegionWidth()) {
            for (float y = bottomY; y < topY + 1; y += texture.getRegionHeight()) {
                batch.draw(texture, x, y, texture.getRegionWidth(), texture.getRegionHeight());
            }
        }
        super.draw(batch, parentAlpha);
    }

}
