package dev.moore.fightTerrors.general.guis.components;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;

import dev.moore.fightTerrors.general.Loader;
import dev.moore.fightTerrors.general.guis.GuiTexture;

public class GuiToggle extends GuiTexture {

	private float boxX, boxY, boxW, boxH;
	private boolean toggled = false, once = false;

	private int clickOnTexture, clickOffTexture, hoverOnTexture, hoverOffTexture, onTexture, offTexture;

	public GuiToggle(Loader loader, String texFileName, Vector2f texPosition, Vector2f texScale, boolean toggled) {
		super(loader, texFileName + (toggled ? "On" : "Off"), texPosition, texScale);

		boxW = texScale.x * Display.getWidth();
		boxH = texScale.y * Display.getHeight();
		boxX = ((texPosition.x + 1) / 2) * Display.getWidth() - boxW / 2;
		boxY = ((texPosition.y + 1) / 2) * Display.getHeight() - boxH / 2;
		this.toggled = toggled;

		clickOnTexture = loader.loadTexture(texFileName + "OnDown");
		clickOffTexture = loader.loadTexture(texFileName + "OffDown");
		hoverOnTexture = loader.loadTexture(texFileName + "OnHover");
		hoverOffTexture = loader.loadTexture(texFileName + "OffHover");
		onTexture = loader.loadTexture(texFileName + "On");
		offTexture = loader.loadTexture(texFileName + "Off");
	}

	public void update() {

		int x = Mouse.getX(), y = Mouse.getY();

		boxW = super.getScale().x * Display.getWidth();
		boxH = super.getScale().y * Display.getHeight();
		boxX = ((super.getPosition().x + 1) / 2) * Display.getWidth() - boxW / 2;
		boxY = ((super.getPosition().y + 1) / 2) * Display.getHeight() - boxH / 2;

		if (x > boxX && x < boxX + boxW && y > boxY && y < boxY + boxH) { // Mouse is within Bounding Box of Button

			if (Mouse.isButtonDown(0) || Mouse.isButtonDown(1) || Mouse.isButtonDown(2)) { // Left Mouse Button was
																							// pressed

				/* Left Click Action */
				super.setTextureID(toggled ? clickOnTexture : clickOffTexture);

				if (!once) {

					toggled = !toggled;
					once = true;
				}

				clickAction();

			} else { // No Mouse Buttons are being pressed

				/* Hover Action */
				super.setTextureID(toggled ? hoverOnTexture : hoverOffTexture);
				hoverAction();

				once = false;
			}
		} else {

			super.setTextureID(toggled ? onTexture : offTexture);
		}
	}

	public void clickAction() {

		System.err.println(
				"Button with texture: (" + super.getTextureFileName() + ") has not been assigned any click action.");
	}

	public void hoverAction() {

	}

	public boolean isToggled() {
		return toggled;
	}

	public void toggle() {
		toggled = !toggled;
	}
}
