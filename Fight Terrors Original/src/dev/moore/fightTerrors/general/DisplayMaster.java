package dev.moore.fightTerrors.general;

import java.awt.Dimension;
import java.awt.Toolkit;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.PixelFormat;

public class DisplayMaster {

	public static int WIDTH = 1024, HEIGHT = 576, FPS_CAP = 120;

	public static final float ASPECT_RATIO = 16f / 9f;

	private static long lastFrameTime;
	private static float delta;

	public static final String TITLE = "Fight Terrors (Inf-Dev)";

	public static void createDisplay() {

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		ContextAttribs attribs = new ContextAttribs(3, 3).withForwardCompatible(true).withProfileCore(true);

		try {

			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.setLocation((int) (screenSize.getWidth() - WIDTH) / 2, 15);
			Display.create(new PixelFormat().withSamples(8).withDepthBits(24), attribs);
			Display.setTitle(TITLE);
			GL11.glEnable(GL13.GL_MULTISAMPLE);

		} catch (LWJGLException e) {

			e.printStackTrace();
		}

		lastFrameTime = getCurrentTime();
	}

	public static void showLoadingDisplay() {

		Display.setTitle(TITLE + " - Loading");
	}

	public static void unshowLoadingDisplay() {

		Display.setTitle(TITLE);
	}

	static boolean once = false;
	public static boolean printFPS = false;

	static int updates = 0;
	static float startTime = getCurrentTime();

	public static void updateDisplay() {

		Display.sync(FPS_CAP);
		Display.update();
		long currFrameTime = getCurrentTime();
		delta = (currFrameTime - lastFrameTime) / 1000f;
		lastFrameTime = currFrameTime;

		updates++;

		if (getCurrentTime() - startTime >= 1000) {

			if (printFPS)
				System.out.println("UPS: " + updates);

			updates = 0;
			startTime = getCurrentTime();
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_F11)) {

			if (!once) {

				try {

					if (Display.isFullscreen()) {

						Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
					} else {

						Display.setDisplayModeAndFullscreen(Display.getDesktopDisplayMode());
					}

				} catch (LWJGLException e) {

					e.printStackTrace();
				}

				once = true;
			}
		} else
			once = false;

		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
			System.exit(0);
	}

	public static float getFrameTime() {
		
		return delta;
	}

	public static void clearDisplay() {

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(0.15f, 0f, 0.2f, 1f);
	}

	public static void closeDisplay() {

		Display.destroy();
		Mouse.destroy();
		Keyboard.destroy();
	}

	private static long getCurrentTime() {

		return Sys.getTime() * 1000 / Sys.getTimerResolution();
	}
}