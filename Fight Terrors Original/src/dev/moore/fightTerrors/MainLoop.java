package dev.moore.fightTerrors;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import dev.moore.fightTerrors.general.DisplayMaster;
import dev.moore.fightTerrors.general.Loader;
import dev.moore.fightTerrors.general.fontMeshCreator.FontType;
import dev.moore.fightTerrors.playState.PlayState;
import dev.moore.fightTerrors.titleState.TitleState;

public class MainLoop implements Runnable {

	private boolean running = false;

	public enum State {
		TITLE, SETTINGS_TITLE, SETTINGS_PAUSE, PLAY, PAUSE
	}

	private State currState = State.TITLE;

	public void changeState(State newState) {

		this.currState = newState;

		if (newState == State.PLAY)
			playState.reinitWater();
	}

	public State getCurrState() {
		return currState;
	}

	public static TitleState titleState = null;
	public static PlayState playState = null;

	private Loader loader = null;

	public void run() {

		init();

		while (!Display.isCloseRequested() && running) {

			if (!Display.isCreated()) {

				DisplayMaster.createDisplay();
			}

			if (!Mouse.isCreated()) {

				try {
					Mouse.create();
				} catch (LWJGLException e) {
					e.printStackTrace();
				}
			}

			if (!Keyboard.isCreated()) {

				try {
					Keyboard.create();
				} catch (LWJGLException e) {
					e.printStackTrace();
				}
			}

			if (getCurrState() == State.TITLE)
				titleState.update();
			else if (getCurrState() == State.SETTINGS_TITLE || getCurrState() == State.SETTINGS_PAUSE)
				titleState.settingsUpdate();
			else if (getCurrState() == State.PLAY)
				playState.update();
			else if (getCurrState() == State.PAUSE)
				playState.pauseUpdate();
		}
	}

	private void init() {

		DisplayMaster.showLoadingDisplay();
		DisplayMaster.createDisplay();

		loader = new Loader();
		loader.loadFont(new FontType(loader.loadTexture("candara"), "candara"));

		playState = new PlayState(this, loader);
		titleState = new TitleState(this, loader);

		DisplayMaster.unshowLoadingDisplay();
	}

	synchronized void start() {

		new Thread(this).start();
		running = true;
	}

	synchronized void stop() {

		running = false;
	}

	public static void main(String[] args) {

		new MainLoop().start();
	}
}