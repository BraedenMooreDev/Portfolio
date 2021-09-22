package dev.moore.fightTerrors.playState.textures;

import java.nio.ByteBuffer;

public class TextureData {

	private int width, height;
	private ByteBuffer buffer;

	public TextureData(ByteBuffer buffer, int width, int height) {

		this.buffer = buffer;
		this.width = width;
		this.height = height;
	}

	// GETTERS

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public ByteBuffer getBuffer() {
		return buffer;
	}
}
