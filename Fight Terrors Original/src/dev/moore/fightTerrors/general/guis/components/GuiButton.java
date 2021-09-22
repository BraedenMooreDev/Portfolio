package dev.moore.fightTerrors.general.guis.components;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;

import dev.moore.fightTerrors.general.Loader;
import dev.moore.fightTerrors.general.fontMeshCreator.FontText;
import dev.moore.fightTerrors.general.fontRendering.TextMaster;
import dev.moore.fightTerrors.general.guis.GuiTexture;

public class GuiButton extends GuiTexture {

	private float boxX, boxY, boxW, boxH;

	private int clickTexture, hoverTexture, normTexture;

	private TextMaster textMaster;
	private FontText label = null;

	public GuiButton(String label, TextMaster textMaster, Loader loader, String texFileName, Vector2f texPosition,
			Vector2f texScale) {
		super(loader, texFileName, texPosition, texScale);

		boxW = texScale.x * Display.getWidth();
		boxH = texScale.y * Display.getHeight();
		boxX = ((texPosition.x + 1) / 2) * Display.getWidth() - boxW / 2;
		boxY = ((texPosition.y + 1) / 2) * Display.getHeight() - boxH / 2;

		this.textMaster = textMaster;
		this.label = new FontText(label, 2f, loader.getFont(),
				new Vector2f(texPosition.x, texPosition.y + texScale.y / 3f), 1f, true);
		textMaster.loadText(this.label);

		clickTexture = loader.loadTexture(texFileName + "Down");
		hoverTexture = loader.loadTexture(texFileName + "Hover");
		normTexture = loader.loadTexture(texFileName);
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
				super.setTextureID(clickTexture);
				clickAction();

			} else { // No Mouse Buttons are being pressed

				/* Hover Action */
				super.setTextureID(hoverTexture);
				hoverAction();
			}
		} else {

			super.setTextureID(normTexture);
		}
	}

	public void clickAction() {

		System.err.println(
				"Button with texture: (" + super.getTextureFileName() + ") has not been assigned any click action.");
	}

	public void hoverAction() {

	}

	public void setTextColor(float r, float g, float b) {

		textMaster.removeText(label);
		label.setColour(r, g, b);
		textMaster.loadText(label);
	}
}