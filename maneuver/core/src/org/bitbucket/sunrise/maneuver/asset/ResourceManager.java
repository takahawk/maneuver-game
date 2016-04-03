package org.bitbucket.sunrise.maneuver.asset;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by sp213-08 on 21.03.2016.
 */
public final class ResourceManager {
    public AssetManager assetManager = new AssetManager();


    public Texture getTexture(String name) {
        return get(name, Texture.class);
    }

    public TextureRegion getRegion(String name) {
        return new TextureRegion(getTexture(name));
    }

    public <T> T get(String name, Class<T> type) {
        if (!assetManager.isLoaded(name)) {
            assetManager.load(name, type);
            assetManager.finishLoading();
        }
        return assetManager.get(name, type);
    }

    public void dispose() {
        assetManager.dispose();
    }

    public void reset() {
        assetManager.dispose();
        assetManager = new AssetManager();
    }
}
