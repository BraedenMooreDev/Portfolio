package dev.moore.fightTerrors.titleState;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import dev.moore.fightTerrors.MainLoop;
import dev.moore.fightTerrors.MainLoop.State;
import dev.moore.fightTerrors.general.DisplayMaster;
import dev.moore.fightTerrors.general.Loader;
import dev.moore.fightTerrors.general.fontMeshCreator.FontText;
import dev.moore.fightTerrors.general.fontMeshCreator.FontType;
import dev.moore.fightTerrors.general.fontRendering.TextMaster;
import dev.moore.fightTerrors.general.guis.GuiRenderer;
import dev.moore.fightTerrors.general.guis.GuiTexture;
import dev.moore.fightTerrors.general.guis.components.GuiButton;
import dev.moore.fightTerrors.general.guis.components.GuiComboBox;
import dev.moore.fightTerrors.general.guis.components.GuiImage;
import dev.moore.fightTerrors.general.guis.components.GuiToggle;
import dev.moore.fightTerrors.playState.renderers.ShadowMapMasterRenderer;
import dev.moore.fightTerrors.playState.shadows.ShadowBox;
import dev.moore.fightTerrors.playState.water.WaterFrameBuffers;

public class TitleState {

	private static final Vector3f BG_COLOR = new Vector3f(0.15f, 0f, 0.2f);

	private List<GuiTexture> guis = new ArrayList<GuiTexture>();
	private List<GuiTexture> settingsGuis = new ArrayList<GuiTexture>();
	private List<GuiTexture> graphicsGuis = new ArrayList<GuiTexture>();
	private List<GuiTexture> audioGuis = new ArrayList<GuiTexture>();
	private List<GuiTexture> gameplayGuis = new ArrayList<GuiTexture>();

	private GuiRenderer guiRenderer = null;
	private TextMaster textMaster = null, settingsTextMaster = null, graphicsTextMaster = null, audioTextMaster = null,
			gameplayTextMaster = null;

	private FontType font = null;

	private MainLoop mainLoop = null;
	private Loader loader = null;

	public TitleState(MainLoop mainLoop, Loader loader) {

		this.mainLoop = mainLoop;
		this.loader = loader;

		init();
	}

	private void init() {

		DisplayMaster.showLoadingDisplay();

		textMaster = new TextMaster(loader);
		guiRenderer = new GuiRenderer(loader);
		GuiImage titleImg = new GuiImage(loader, "titleImg", new Vector2f(-0.3f, 0.3f), new Vector2f(0.8f, 0.7f));

		GuiButton playBtn = new GuiButton("Play", textMaster, loader, "menuBtn", new Vector2f(0.65f, 0.45f),
				new Vector2f(0.3f, 0.15f)) {

			public void clickAction() {

				mainLoop.changeState(MainLoop.State.PLAY);
			}
		};
		playBtn.setTextColor(0.1f, 0.95f, 0.1f);
		GuiButton settingsBtn = new GuiButton("Settings", textMaster, loader, "menuBtn", new Vector2f(0.65f, 0f),
				new Vector2f(0.3f, 0.15f)) {

			public void clickAction() {

				mainLoop.changeState(MainLoop.State.SETTINGS_TITLE);
			}
		};
		settingsBtn.setTextColor(0.8f, 0.8f, 0.8f);
		GuiButton quitBtn = new GuiButton("Quit", textMaster, loader, "menuBtn", new Vector2f(0.65f, -0.45f),
				new Vector2f(0.3f, 0.15f)) {

			public void clickAction() {

				System.exit(0);
			}
		};
		quitBtn.setTextColor(0.95f, 0.1f, 0.1f);

		guis.add(titleImg);
		guis.add(playBtn);
		guis.add(settingsBtn);
		guis.add(quitBtn);

		initSettings();

		DisplayMaster.unshowLoadingDisplay();
	}

	private enum SettingsTab {
		GRAPHICS, AUDIO, GAMEPLAY
	}

	private SettingsTab currTab = SettingsTab.GRAPHICS;

	private GuiButton graphicsBtn, audioBtn, gameplayBtn;

	private void initSettings() {

		settingsTextMaster = new TextMaster(loader);
		audioTextMaster = new TextMaster(loader);
		gameplayTextMaster = new TextMaster(loader);

		FontText settingsLabel = new FontText("Settings", 4f, loader.getFont(), new Vector2f(0f, 1f), 1f, true);
		settingsLabel.setColour(0.6f, 0.6f, 0.6f);
		settingsTextMaster.loadText(settingsLabel);

		GuiImage box = new GuiImage(loader, "boxBorder", new Vector2f(0f, -0.35f), new Vector2f(0.675f, 1f));

		GuiButton backBtn = new GuiButton("<", settingsTextMaster, loader, "pauseBtn", new Vector2f(-0.9f, -0.825f),
				new Vector2f(0.07f, 0.12f)) {

			public void clickAction() {

				mainLoop.changeState(
						mainLoop.getCurrState() == State.SETTINGS_TITLE ? MainLoop.State.TITLE : MainLoop.State.PLAY);
			}
		};
		backBtn.setTextColor(0.8f, 0f, 0f);

		graphicsBtn = new GuiButton("Graphics", settingsTextMaster, loader, "menuBtn", new Vector2f(-0.425f, 0.65f),
				new Vector2f(0.2f, 0.1f)) {

			public void clickAction() {

				currTab = SettingsTab.GRAPHICS;
			}
		};
		audioBtn = new GuiButton("Audio", settingsTextMaster, loader, "menuBtn", new Vector2f(0f, 0.65f),
				new Vector2f(0.2f, 0.1f)) {

			public void clickAction() {

				currTab = SettingsTab.AUDIO;
			}
		};
		gameplayBtn = new GuiButton("Gameplay", settingsTextMaster, loader, "menuBtn", new Vector2f(0.425f, 0.65f),
				new Vector2f(0.2f, 0.1f)) {

			public void clickAction() {

				currTab = SettingsTab.GAMEPLAY;
			}
		};

		settingsGuis.add(box);
		settingsGuis.add(backBtn);
		settingsGuis.add(graphicsBtn);
		settingsGuis.add(audioBtn);
		settingsGuis.add(gameplayBtn);

		initGraphics();
	}

	private void initGraphics() {

		graphicsTextMaster = new TextMaster(loader);

		// Fullscreen
		FontText fullscreenLbl = new FontText("Fullscreen", 1.35f, loader.getFont(), new Vector2f(0.4f, -0.55f), 1f,
				false);
		fullscreenLbl.setColour(0.4f, 0.4f, 0.4f);
		graphicsTextMaster.loadText(fullscreenLbl);
		GuiToggle fullscreenSwitch = new GuiToggle(loader, "switch", new Vector2f(0f, 0.4f),
				new Vector2f(0.065f, 0.05f), false) {

			public void clickAction() {

				try {

					if (Display.isFullscreen()) {

						Display.setDisplayMode(new DisplayMode(DisplayMaster.WIDTH, DisplayMaster.HEIGHT));
					} else {

						Display.setDisplayModeAndFullscreen(Display.getDesktopDisplayMode());
					}

				} catch (LWJGLException e) {

					e.printStackTrace();
				}

				GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
			}
		};
		graphicsGuis.add(fullscreenSwitch);

		// Window Resolution
		FontText resolutionLbl = new FontText("Window Resolution", 1.35f, loader.getFont(), new Vector2f(0.4f, -0.7f),
				1f, false);
		resolutionLbl.setColour(0.4f, 0.4f, 0.4f);
		graphicsTextMaster.loadText(resolutionLbl);
		GuiComboBox resolutionComboBox = new GuiComboBox(
				new String[] { "426x240", "640x360", "800x450", "960x540", "1024x576", "1280x720", "1366x768",
						"1600x900" },
				4, graphicsTextMaster, loader, "comboBox", new Vector2f(0f, 0.25f), new Vector2f(0.125f, 0.05f)) {

			public void clickAction(String selection) {

				int w = Integer.parseInt(selection.substring(0, selection.indexOf("x")));
				int h = Integer.parseInt(selection.substring(selection.indexOf("x") + 1, selection.length()));

				if (fullscreenSwitch.isToggled())
					fullscreenSwitch.toggle();

				DisplayMaster.WIDTH = w;
				DisplayMaster.HEIGHT = h;

				try {

					Display.setDisplayMode(new DisplayMode(w, h));
				} catch (LWJGLException e) {

					e.printStackTrace();
				}

				GL11.glViewport(0, 0, w, h);
			}
		};
		graphicsGuis.add(resolutionComboBox);

		// Water Quality (Resolution of water maps)
		FontText waterQLbl = new FontText("Water Quality", 1.35f, loader.getFont(), new Vector2f(0.4f, -0.85f), 1f,
				false);
		waterQLbl.setColour(0.4f, 0.4f, 0.4f);
		graphicsTextMaster.loadText(waterQLbl);
		GuiComboBox waterComboBox = new GuiComboBox(new String[] { "Very Low", "Low", "Mid", "High", "Very High" }, 1,
				graphicsTextMaster, loader, "comboBox", new Vector2f(0f, 0.1f), new Vector2f(0.12f, 0.05f)) {

			public void clickAction(String selection) {

				if (selection == "Very Low") {

					WaterFrameBuffers.REFLECTION_WIDTH = 160;
					WaterFrameBuffers.REFLECTION_HEIGHT = 90;
					WaterFrameBuffers.REFRACTION_WIDTH = 160;
					WaterFrameBuffers.REFRACTION_HEIGHT = 90;
				} else if (selection == "Low") {

					WaterFrameBuffers.REFLECTION_WIDTH = 320;
					WaterFrameBuffers.REFLECTION_HEIGHT = 180;
					WaterFrameBuffers.REFRACTION_WIDTH = 320;
					WaterFrameBuffers.REFRACTION_HEIGHT = 180;
				} else if (selection == "Mid") {

					WaterFrameBuffers.REFLECTION_WIDTH = 640;
					WaterFrameBuffers.REFLECTION_HEIGHT = 360;
					WaterFrameBuffers.REFRACTION_WIDTH = 640;
					WaterFrameBuffers.REFRACTION_HEIGHT = 360;
				} else if (selection == "High") {

					WaterFrameBuffers.REFLECTION_WIDTH = 960;
					WaterFrameBuffers.REFLECTION_HEIGHT = 540;
					WaterFrameBuffers.REFRACTION_WIDTH = 960;
					WaterFrameBuffers.REFRACTION_HEIGHT = 540;
				} else if (selection == "Very High") {

					WaterFrameBuffers.REFLECTION_WIDTH = 1280;
					WaterFrameBuffers.REFLECTION_HEIGHT = 720;
					WaterFrameBuffers.REFRACTION_WIDTH = 1280;
					WaterFrameBuffers.REFRACTION_HEIGHT = 720;
				}
			}
		};
		graphicsGuis.add(waterComboBox);

		// Shadow Quality (Resolution of shadow map)
		FontText shadowQualityLbl = new FontText("Shadow Quality", 1.35f, loader.getFont(), new Vector2f(0.4f, -1f), 1f,
				false);
		shadowQualityLbl.setColour(0.4f, 0.4f, 0.4f);
		graphicsTextMaster.loadText(shadowQualityLbl);
		GuiComboBox shadowQualityComboBox = new GuiComboBox(
				new String[] { "Off", "Very Low", "Low", "Mid", "High", "Very High", "Ultra" }, 1, graphicsTextMaster,
				loader, "comboBox", new Vector2f(0f, -0.05f), new Vector2f(0.125f, 0.05f)) {

			public void clickAction(String selection) {

				if (selection == "Off") {

					ShadowMapMasterRenderer.SHADOW_MAP_SIZE = 0;
					ShadowBox.SHADOW_DISTANCE = 0;
				} else if (selection == "Very Low") {

					ShadowMapMasterRenderer.SHADOW_MAP_SIZE = 1024;
				} else if (selection == "Low") {

					ShadowMapMasterRenderer.SHADOW_MAP_SIZE = 1024 * 2;
				} else if (selection == "Mid") {

					ShadowMapMasterRenderer.SHADOW_MAP_SIZE = 1024 * 4;
				} else if (selection == "High") {

					ShadowMapMasterRenderer.SHADOW_MAP_SIZE = 1024 * 6;
				} else if (selection == "Very High") {

					ShadowMapMasterRenderer.SHADOW_MAP_SIZE = 1024 * 8;
				} else if (selection == "Ultra") {

					ShadowMapMasterRenderer.SHADOW_MAP_SIZE = 1024 * 10;
				}
			}
		};
		graphicsGuis.add(shadowQualityComboBox);

		// Shadow Smoothing (Edge smoothing of shadow map)
		FontText shadowSmoothingLbl = new FontText("Shadow Smoothing", 1.35f, loader.getFont(),
				new Vector2f(0.4f, -1.15f), 1f, false);
		shadowSmoothingLbl.setColour(0.4f, 0.4f, 0.4f);
		graphicsTextMaster.loadText(shadowSmoothingLbl);
		GuiComboBox shadowSmoothingComboBox = new GuiComboBox(new String[] { "1x", "2x", "3x", "4x", "5x", "6x" }, 0,
				graphicsTextMaster, loader, "comboBox", new Vector2f(0f, -0.2f), new Vector2f(0.125f, 0.05f)) {

			public void clickAction(String selection) {

				ShadowMapMasterRenderer.SHADOW_EDGE_SMOOTHING = Integer
						.parseInt(selection.substring(0, selection.length() - 1));
			}
		};
		graphicsGuis.add(shadowSmoothingComboBox);

		// Shadow Distance (Distance of shadow map)
		FontText shadowDistanceLbl = new FontText("Shadow Distance", 1.35f, loader.getFont(), new Vector2f(0.4f, -1.3f),
				1f, false);
		shadowDistanceLbl.setColour(0.4f, 0.4f, 0.4f);
		graphicsTextMaster.loadText(shadowDistanceLbl);
		GuiComboBox shadowDistanceComboBox = new GuiComboBox(new String[] { "Off", "Low", "Mid", "High" }, 1,
				graphicsTextMaster, loader, "comboBox", new Vector2f(0f, -0.35f), new Vector2f(0.125f, 0.05f)) {

			public void clickAction(String selection) {

				if (selection == "Off") {

					ShadowMapMasterRenderer.SHADOW_MAP_SIZE = 0;
					ShadowBox.SHADOW_DISTANCE = 0;
				} else if (selection == "Low") {

					ShadowBox.SHADOW_DISTANCE = 100;
				} else if (selection == "Mid") {

					ShadowBox.SHADOW_DISTANCE = 200;
				} else if (selection == "High") {

					ShadowBox.SHADOW_DISTANCE = 300;
				}
			}
		};
		graphicsGuis.add(shadowDistanceComboBox);
	}

	public void update() {

		prepare();

		guiRenderer.render(guis);
		textMaster.render();

		DisplayMaster.updateDisplay();
	}

	private boolean once = false;

	public void settingsUpdate() {

		prepare();

		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {

			if (!once) {

				mainLoop.changeState(
						mainLoop.getCurrState() == State.SETTINGS_TITLE ? MainLoop.State.TITLE : MainLoop.State.PLAY);
				once = true;
			}
		} else
			once = false;

		guiRenderer.render(settingsGuis);
		settingsTextMaster.render();

		switch (currTab) {

		case GRAPHICS:

			graphicsBtn.setTextColor(0.4f, 0f, 0.4f);
			audioBtn.setTextColor(0.1f, 0.1f, 0.1f);
			gameplayBtn.setTextColor(0.1f, 0.1f, 0.1f);
			guiRenderer.render(graphicsGuis);
			graphicsTextMaster.render();
			break;
		case AUDIO:

			graphicsBtn.setTextColor(0.1f, 0.1f, 0.1f);
			audioBtn.setTextColor(0.4f, 0f, 0.4f);
			gameplayBtn.setTextColor(0.1f, 0.1f, 0.1f);
			guiRenderer.render(audioGuis);
			audioTextMaster.render();
			break;
		case GAMEPLAY:

			graphicsBtn.setTextColor(0.1f, 0.1f, 0.1f);
			audioBtn.setTextColor(0.1f, 0.1f, 0.1f);
			gameplayBtn.setTextColor(0.4f, 0f, 0.4f);
			guiRenderer.render(gameplayGuis);
			gameplayTextMaster.render();
			break;
		}

		DisplayMaster.updateDisplay();
	}

	private void prepare() {

		Mouse.setGrabbed(false);

		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glClearColor(BG_COLOR.x, BG_COLOR.y, BG_COLOR.z, 1f);
	}

	public void cleanUp() {

		textMaster.cleanUp();
		settingsTextMaster.cleanUp();
		graphicsTextMaster.cleanUp();
		audioTextMaster.cleanUp();
		gameplayTextMaster.cleanUp();
		guiRenderer.cleapUp();
		DisplayMaster.closeDisplay();
	}
}
