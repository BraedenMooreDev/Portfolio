package dev.moore.fightTerrors.general.guis;

import org.lwjgl.util.vector.Vector2f;

import dev.moore.fightTerrors.general.Loader;

public abstract class GuiTexture {

	protected Loader loader;
	private String textureFileName;
	private int textureID;
	private Vector2f position, scale;
	private boolean hasTexture = false;

	public GuiTexture(Loader loader, String textureFileName, Vector2f position, Vector2f scale) {

		this.loader = loader;
		this.textureFileName = textureFileName;
		this.position = position;
		this.scale = scale;
	}

	public abstract void update();

	// GETTERS

	public boolean hasTexture() {
		return hasTexture;
	}

	public String getTextureFileName() {
		return textureFileName;
	}

	public int getTextureID() {
		return textureID;
	}

	public Vector2f getPosition() {
		return position;
	}

	public Vector2f getScale() {
		return scale;
	}

	// SETTERS

	public void setTextureID(int textureID) {
		this.textureID = textureID;
		hasTexture = true;
	}
}
