package dev.moore.fightTerrors.playState.shaders;

import org.lwjgl.util.vector.Matrix4f;

import dev.moore.fightTerrors.general.ShaderProgram;

public class ParticleShader extends ShaderProgram {

	private static final String VERTEX_FILE = "playState/shaders/glslFiles/particleVertexShader.glsl";
	private static final String FRAGMENT_FILE = "playState/shaders/glslFiles/particleFragmentShader.glsl";

	private int location_projectionMatrix, location_numOfRows;

	public ParticleShader() {

		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	protected void getAllUniformLocations() {

		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_numOfRows = super.getUniformLocation("numOfRows");
	}

	protected void bindAttributes() {

		super.bindAttribute(0, "position");
		super.bindAttribute(1, "modelViewMatrix");
		super.bindAttribute(5, "texOffsets");
		super.bindAttribute(6, "blend");
	}

	public void loadProjectionMatrix(Matrix4f projectionMatrix) {

		super.loadMatrix(location_projectionMatrix, projectionMatrix);
	}

	public void loadNumOfRows(float numOfRows) {

		super.loadFloat(location_numOfRows, numOfRows);
	}
}
