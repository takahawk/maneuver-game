package org.bitbucket.sunrise.maneuver;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.bitbucket.sunrise.maneuver.asset.ResourceManager;
import org.bitbucket.sunrise.maneuver.screens.GameScreen;
import org.bitbucket.sunrise.maneuver.screens.MenuScreen;

public class ManeuverGame extends Game {
	public static final int WIDTH = 1184 * 2;
	public static final int HEIGHT = 720 * 2;
	public static final double RATIO = (double) WIDTH / HEIGHT;
	ResourceManager resourceManager = new ResourceManager();
	SpriteBatch batch;
	public ResourceManager getResourceManager() {
		return resourceManager;
	}

	@Override
	public void create () {
		batch = new SpriteBatch();
		//setScreen(new GameScreen(this, batch));
		setScreen(new MenuScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
}
