package swen30006.driving.headless;

// import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
// import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;

import swen30006.driving.Simulation;

public class ServerLauncher {
	public static void main (String[] arg) {
		HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();		
		// config.backgroundFPS = 0;
		// config.foregroundFPS = 0;
		new HeadlessApplication(new Simulation(arg), config);
	}
}
