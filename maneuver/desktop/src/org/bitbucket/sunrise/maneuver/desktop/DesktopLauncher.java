package org.bitbucket.sunrise.maneuver.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.bitbucket.sunrise.maneuver.ManeuverGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1184;
		config.height = 720;
		new LwjglApplication(new ManeuverGame(), config);
	}
}
