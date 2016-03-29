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
	Texture img;

	public ResourceManager getResourceManager() {
		// wdt1 / hgt1 != wdt2 / hgt2
		// ratio1 != ratio2
		// d = ratio2 / ratio1
		// 1000 / 500 != 300 / 200
		// 2 = 300 / 200
		// 200 = 300 / 2
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
