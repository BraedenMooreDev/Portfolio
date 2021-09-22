package dev.moore.fightTerrors.playState.models.base;

public class ModelTexture {

	private int textureID;

	private float shineDamper = 1;
	private float reflectivity = 0;

	private boolean hasTransparency = false, useFakeLighting = false;

	private int atlasSize = 1;

	public ModelTexture(int texture) {

		this.textureID = texture;
	}

	// GETTERS

	public int getID() {
		return textureID;
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public boolean hasTransparency() {
		return hasTransparency;
	}

	public boolean usingFakeLighting() {
		return useFakeLighting;
	}

	public int getAtlasSize() {
		return atlasSize;
	}

	// SETTERS

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}

	public void setTransparency(boolean transparency) {
		this.hasTransparency = transparency;
	}

	public void useFakeLighting(boolean useFakeLighting) {
		this.useFakeLighting = useFakeLighting;
	}

	public void setAtlasSize(int atlasSize) {
		this.atlasSize = atlasSize;
	}
}