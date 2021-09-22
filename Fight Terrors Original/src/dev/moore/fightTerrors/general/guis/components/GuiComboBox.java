package dev.moore.fightTerrors.general.guis.components;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;

import dev.moore.fightTerrors.general.Loader;
import dev.moore.fightTerrors.general.fontMeshCreator.FontText;
import dev.moore.fightTerrors.general.fontRendering.TextMaster;
import dev.moore.fightTerrors.general.guis.GuiTexture;

public class GuiComboBox extends GuiTexture {

	private float boxX, boxY, boxW, boxH, choiceBoxXOffset, choiceBoxW, choiceBoxH;
	private float prevW, prevH, prevIndex;

	private int clickTexture, hoverTexture, normTexture;

	private boolean open = false, once = false;
	private TextMaster textMaster;
	private String[] choices;
	private int currIndex;
	private FontText arrow = null;
	private FontText[] labels = null;

	public GuiComboBox(String[] choices, int initialIndex, TextMaster textMaster, Loader loader, String texFileName,
			Vector2f texPosition, Vector2f texScale) {
		super(loader, texFileName, texPosition, texScale);

		boxW = texScale.x * Display.getWidth();
		boxH = texScale.y * Display.getHeight();
		boxX = ((texPosition.x + 1) / 2) * Display.getWidth() - boxW / 2;
		boxY = ((texPosition.y + 1) / 2) * Display.getHeight() - boxH / 2;

		this.textMaster = textMaster;
		this.choices = choices;
		this.labels = new FontText[choices.length];
		this.currIndex = initialIndex;
		this.prevIndex = initialIndex;
		this.arrow = new FontText("v", 1.15f, loader.getFont(),
				new Vector2f(texPosition.x + texScale.x * 0.79f, texPosition.y + texScale.y * 0.6f), 1f, true);
		textMaster.loadText(this.arrow);

		choiceBoxXOffset = boxW * (41f / 2048f);
		choiceBoxW = boxW * (1583f / 2048f);
		choiceBoxH = boxH * (430f / 512f);

		int j = 0;

		for (int i = 0; i < choices.length; i++) {

			if (i != currIndex) {

				j++;
				labels[i] = new FontText(choices[i], 1.5f, loader.getFont(),
						new Vector2f((boxX + choiceBoxXOffset + choiceBoxW / 2) / Display.getWidth() * 2 + -1,
								((boxY - choiceBoxH * j) / Display.getHeight() * 2) - 1 + (super.getScale().y * 1.76f)),
						1f, true);
			} else {

				labels[i] = new FontText(choices[i], 1.5f, loader.getFont(),
						new Vector2f((boxX + choiceBoxXOffset + choiceBoxW / 2) / Display.getWidth() * 2 + -1,
								super.getPosition().y + (super.getScale().y * 0.75f)),
						1f, true);
				textMaster.loadText(labels[i]);
			}
		}

		clickTexture = loader.loadTexture(texFileName + "Down");
		hoverTexture = loader.loadTexture(texFileName + "Hover");
		normTexture = loader.loadTexture(texFileName);
	}

	public void update() {

		int x = Mouse.getX(), y = Mouse.getY();

		if (Display.getWidth() != prevW || Display.getHeight() != prevH || currIndex != prevIndex) {

			refresh();
		}

		if (x > boxX && x < boxX + boxW && y > (open ? boxY - choiceBoxH * (choices.length - 1) : boxY)
				&& y < boxY + boxH) { // Mouse is within Bounding Box of Button

			if (Mouse.isButtonDown(0) || Mouse.isButtonDown(1) || Mouse.isButtonDown(2)) { // Left Mouse Button was
																							// pressed

				/* Left Click Action */
				super.setTextureID(clickTexture);

				if (open && y < boxY) {

					int index = (int) ((boxY - y) / choiceBoxH);
					int j = 0;

					for (int i = 0; i < labels.length; i++) {

						if (i != currIndex) {

							if (j == index) {

								currIndex = i;
								refresh();
								break;
							}

							j++;
						}
					}

					clickAction(choices[currIndex]);
				}

				if (!once) {

					if (open)
						close();
					else
						open();

					once = true;
				}

			} else { // No Mouse Buttons are being pressed

				/* Hover Action */
				super.setTextureID(hoverTexture);
				once = false;
				hoverAction();
			}
		} else {

			super.setTextureID(normTexture);

			if (open) {

				close();
			}
		}
	}

	public void clickAction(String selection) {

		System.err.println(
				"Button with texture: (" + super.getTextureFileName() + ") has not been assigned any click action.");
	}

	public void hoverAction() {

	}

	private void close() {

		open = false;

		textMaster.removeText(arrow);
		arrow = new FontText("v", 1.15f, loader.getFont(),
				new Vector2f(super.getPosition().x + super.getScale().x * 0.79f,
						super.getPosition().y + super.getScale().y * 0.6f),
				1f, true);
		textMaster.loadText(arrow);

		for (int i = 0; i < labels.length; i++) {

			if (i != currIndex)
				textMaster.removeText(labels[i]);
		}
	}

	private void open() {

		open = true;

		textMaster.removeText(arrow);
		arrow = new FontText("^", 1.15f, loader.getFont(),
				new Vector2f(super.getPosition().x + super.getScale().x * 0.79f,
						super.getPosition().y + super.getScale().y * 0.4f),
				1f, true);
		textMaster.loadText(arrow);

		for (int i = 0; i < labels.length; i++) {

			if (i != currIndex)
				textMaster.loadText(labels[i]);
		}
	}

	private void refresh() {

		prevW = Display.getWidth();
		prevH = Display.getHeight();
		prevIndex = currIndex;

		boxW = super.getScale().x * Display.getWidth();
		boxH = super.getScale().y * Display.getHeight();
		boxX = ((super.getPosition().x + 1) / 2) * Display.getWidth() - boxW / 2;
		boxY = ((super.getPosition().y + 1) / 2) * Display.getHeight() - boxH / 2;

		choiceBoxXOffset = boxW * (41f / 2048f);
		choiceBoxW = boxW * (1583f / 2048f);
		choiceBoxH = boxH * (430f / 512f);

		int j = 0;

		for (int i = 0; i < choices.length; i++) {

			if (labels[i] != null)
				textMaster.removeText(labels[i]);

			if (i != currIndex) {

				j++;
				labels[i] = new FontText(choices[i], 1.5f, loader.getFont(),
						new Vector2f((boxX + choiceBoxXOffset + choiceBoxW / 2) / Display.getWidth() * 2 + -1,
								(boxY - choiceBoxH * j) / Display.getHeight() * 2 - 1 + (super.getScale().y * 1.75f)),
						1f, true);
				labels[i].setColour(0f, 1f, 0f);
				textMaster.removeText(labels[i]);
			} else {

				labels[i] = new FontText(choices[i], 1.5f, loader.getFont(),
						new Vector2f((boxX + choiceBoxXOffset + choiceBoxW / 2) / Display.getWidth() * 2 + -1,
								super.getPosition().y + (super.getScale().y * 0.75f)),
						1f, true);
				textMaster.loadText(labels[i]);
			}
		}
	}
}