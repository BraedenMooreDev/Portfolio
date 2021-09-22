package dev.moore.fightTerrors.playState.terrains;

public class TerrainTexturePack {

	private TerrainTexture backgroundTexure, rTexture, gTexture, bTexture;

	public TerrainTexturePack(TerrainTexture backgroundTexure, TerrainTexture rTexture, TerrainTexture gTexture,
			TerrainTexture bTexture) {

		this.backgroundTexure = backgroundTexure;
		this.rTexture = rTexture;
		this.gTexture = gTexture;
		this.bTexture = bTexture;
	}

	// GETTERS

	public TerrainTexture getBackgroundTexture() {
		return backgroundTexure;
	}

	public TerrainTexture getRTexture() {
		return rTexture;
	}

	public TerrainTexture getGTexture() {
		return gTexture;
	}

	public TerrainTexture getBTexture() {
		return bTexture;
	}
}
