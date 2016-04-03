package org.bitbucket.sunrise.maneuver;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.bitbucket.sunrise.maneuver.asset.ResourceManager;
import org.bitbucket.sunrise.maneuver.game.ScoreManager;
import org.bitbucket.sunrise.maneuver.screens.MenuScreen;

public class ManeuverGame extends Game {
	private static final float DEFAULT_ROCKET_FORCE = 300f;
	private static final float DEFAULT_ROCKET_RESOURCE = 5f;
	private static final float DEFAULT_PLANE_SPEED = 200f;

	public static final int WIDTH = 1184 * 2;
	public static final int HEIGHT = 720 * 2;
	public static final double RATIO = (double) WIDTH / HEIGHT;
	Preferences preferences;
	ResourceManager resourceManager = new ResourceManager();
	ScoreManager scoreManager;
	SpriteBatch batch;
	public ResourceManager getResourceManager() {
		return resourceManager;
	}

	public ScoreManager getScoreManager() {
		return scoreManager;
	}

	public Preferences getPreferences() {
		return preferences;
	}

	public void initPreferences() {
		if (!preferences.contains("rocketSpeed"))
			preferences.putFloat("rocketSpeed", DEFAULT_ROCKET_FORCE);
		if (!preferences.contains("rocketResource"))
			preferences.putFloat("rocketResource", DEFAULT_ROCKET_RESOURCE);
		if (!preferences.contains("planeSpeed"))
			preferences.putFloat("planeSpeed", DEFAULT_PLANE_SPEED);
	}

	@Override
	public void create () {
		batch = new SpriteBatch();
		//setScreen(new GameScreen(this, batch));
		setScreen(new MenuScreen(this));
		preferences = Gdx.app.getPreferences("ManeuverGame");
		scoreManager = new ScoreManager(preferences);
		initPreferences();
	}

	@Override
	public void render () {
		super.render();
	}
}
