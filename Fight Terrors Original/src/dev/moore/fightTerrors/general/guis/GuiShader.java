package dev.moore.fightTerrors.general.guis;

import org.lwjgl.util.vector.Matrix4f;

import dev.moore.fightTerrors.general.ShaderProgram;

public class GuiShader extends ShaderProgram {

	private static final String VERTEX_FILE = "general/guis/guiVertexShader.txt";
	private static final String FRAGMENT_FILE = "general/guis/guiFragmentShader.txt";

	private int location_transformationMatrix;

	public GuiShader() {

		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	public void loadTransformation(Matrix4f matrix) {

		super.loadMatrix(location_transformationMatrix, matrix);
	}

	protected void getAllUniformLocations() {

		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
	}

	protected void bindAttributes() {

		super.bindAttribute(0, "position");
	}
}
