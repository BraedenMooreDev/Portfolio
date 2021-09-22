package dev.moore.fightTerrors.playState.models.base;

public class TexturedModel {

	private RawModel rawModel;
	private ModelTexture texture;

	public TexturedModel(RawModel model, ModelTexture texture) {

		this.rawModel = model;
		this.texture = texture;
	}

	// GETTERS

	public RawModel getRawModel() {
		return rawModel;
	}

	public ModelTexture getTexture() {
		return texture;
	}
}