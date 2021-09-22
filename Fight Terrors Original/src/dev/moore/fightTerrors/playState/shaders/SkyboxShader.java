package dev.moore.fightTerrors.playState.shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import dev.moore.fightTerrors.general.DisplayMaster;
import dev.moore.fightTerrors.general.Handler;
import dev.moore.fightTerrors.general.ShaderProgram;
import dev.moore.fightTerrors.playState.entities.Camera;

public class SkyboxShader extends ShaderProgram {

	private static final String VERTEX_FILE = "playState/shaders/glslFiles/skyboxVertexShader.glsl";
	private static final String FRAGMENT_FILE = "playState/shaders/glslFiles/skyboxFragmentShader.glsl";

	private static final float ROTATE_SPEED = 0.25f;

	private int location_projectionMatrix, location_viewMatrix, location_fogColor, location_cubeMapDay,
			location_cubeMapNight, location_blendFactor;

	private float rotation = 0f;

	public SkyboxShader() {

		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	protected void getAllUniformLocations() {

		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_fogColor = super.getUniformLocation("fogColor");
		location_cubeMapDay = super.getUniformLocation("cubeMapDay");
		location_cubeMapNight = super.getUniformLocation("cubeMapNight");
		location_blendFactor = super.getUniformLocation("blendFactor");
	}

	protected void bindAttributes() {

		super.bindAttribute(0, "position");
	}

	public void connectTextureUnits() {

		super.loadInt(location_cubeMapDay, 0);
		super.loadInt(location_cubeMapNight, 1);
	}

	public void loadBlendFactor(float blend) {

		super.loadFloat(location_blendFactor, blend);
	}

	public void loadFogColor(Vector3f fogColor) {

		super.load3DVector(location_fogColor, fogColor);
	}

	public void loadProjectionMatrix(Matrix4f matrix) {

		super.loadMatrix(location_projectionMatrix, matrix);
	}

	public void loadViewMatrix(Camera camera) {

		Matrix4f matrix = Handler.createViewMatrix(camera);
		matrix.m30 = 0;
		matrix.m31 = 0;
		matrix.m32 = 0;
		rotation += ROTATE_SPEED * DisplayMaster.getFrameTime();
		Matrix4f.rotate((float) Math.toRadians(rotation), new Vector3f(0, 1, 0), matrix, matrix);
		super.loadMatrix(location_viewMatrix, matrix);
	}
}