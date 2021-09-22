package dev.moore.fightTerrors.playState.water;

public class WaterTile {

	public float tileSize;
	private float height;
	private float x, z;

	public WaterTile(float x, float z, float height, float tileSize) {

		this.x = x + tileSize;
		this.z = z + tileSize;
		this.height = height;
		this.tileSize = tileSize;
	}

	// GETTERS

	public float getHeight() {
		return height;
	}

	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}
}
