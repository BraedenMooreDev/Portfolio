package dev.moore.fightTerrors.general.fontRendering;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import dev.moore.fightTerrors.general.ShaderProgram;

public class FontShader extends ShaderProgram {

	private static final String VERTEX_FILE = "general/fontRendering/fontVertex.txt";
	private static final String FRAGMENT_FILE = "general/fontRendering/fontFragment.txt";

	private int location_color, location_translation;

	public FontShader() {

		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	protected void getAllUniformLocations() {

		location_color = super.getUniformLocation("color");
		location_translation = super.getUniformLocation("translation");
	}

	protected void loadTranslation(Vector2f translation) {

		super.load2DVector(location_translation, translation);
	}

	protected void loadColor(Vector3f color) {

		super.load3DVector(location_color, color);
	}

	protected void bindAttributes() {

		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}

}
