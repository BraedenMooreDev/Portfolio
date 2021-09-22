package dev.moore.fightTerrors.playState.shaders;

import org.lwjgl.util.vector.Matrix4f;

import dev.moore.fightTerrors.general.Handler;
import dev.moore.fightTerrors.general.ShaderProgram;
import dev.moore.fightTerrors.playState.entities.Camera;
import dev.moore.fightTerrors.playState.entities.Light;

public class WaterShader extends ShaderProgram {

	private final static String VERTEX_FILE = "playState/shaders/glslFiles/waterVertexShader.glsl";
	private final static String FRAGMENT_FILE = "playState/shaders/glslFiles/waterFragmentShader.glsl";

	private int location_modelMatrix, location_viewMatrix, location_projectionMatrix, location_reflectionTexture,
			location_refractionTexture, location_dudvMap, location_normalMap, location_moveFactor,
			location_cameraPosition, location_lightPosition, location_lightColor, location_depthMap;

	public WaterShader() {

		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	protected void bindAttributes() {

		bindAttribute(0, "position");
	}

	protected void getAllUniformLocations() {

		location_projectionMatrix = getUniformLocation("projectionMatrix");
		location_viewMatrix = getUniformLocation("viewMatrix");
		location_modelMatrix = getUniformLocation("modelMatrix");
		location_reflectionTexture = getUniformLocation("reflectionTexture");
		location_refractionTexture = getUniformLocation("refractionTexture");
		location_dudvMap = getUniformLocation("dudvMap");
		location_normalMap = getUniformLocation("normalMap");
		location_moveFactor = getUniformLocation("moveFactor");
		location_cameraPosition = getUniformLocation("cameraPosition");
		location_lightPosition = getUniformLocation("lightPosition");
		location_lightColor = getUniformLocation("lightColor");
		location_depthMap = getUniformLocation("depthMap");
	}

	public void connectTextureUnits() {

		super.loadInt(location_reflectionTexture, 0);
		super.loadInt(location_refractionTexture, 1);
		super.loadInt(location_dudvMap, 2);
		super.loadInt(location_normalMap, 3);
		super.loadInt(location_depthMap, 4);
	}

	public void loadLight(Light sun) {

		super.load3DVector(location_lightPosition, sun.getPosition());
		super.load3DVector(location_lightColor, sun.getColor());
	}

	public void loadMoveFactor(float factor) {

		super.loadFloat(location_moveFactor, factor);
	}

	public void loadProjectionMatrix(Matrix4f projection) {

		loadMatrix(location_projectionMatrix, projection);
	}

	public void loadViewMatrix(Camera camera) {

		Matrix4f viewMatrix = Handler.createViewMatrix(camera);
		loadMatrix(location_viewMatrix, viewMatrix);
		super.load3DVector(location_cameraPosition, camera.getPosition());
	}

	public void loadModelMatrix(Matrix4f modelMatrix) {

		loadMatrix(location_modelMatrix, modelMatrix);
	}

}
