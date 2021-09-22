package dev.moore.fightTerrors.playState.shaders;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import dev.moore.fightTerrors.general.Handler;
import dev.moore.fightTerrors.general.ShaderProgram;
import dev.moore.fightTerrors.playState.entities.Camera;
import dev.moore.fightTerrors.playState.entities.Light;
import dev.moore.fightTerrors.playState.renderers.AnimatedModelRenderer;

public class ItemShader extends ShaderProgram {

	private static final int MAX_LIGHTS = 4;

	private static final String VERTEX_FILE = "playState/shaders/glslFiles/itemVertexShader.glsl";
	private static final String FRAGMENT_FILE = "playState/shaders/glslFiles/itemFragmentShader.glsl";

	private int location_playerTransformationMatrix, location_posMatrix, location_projectionMatrix, location_viewMatrix, location_lightPosition[],
			location_lightColor[], location_attenuation[], location_shineDamper, location_reflectivity,
			location_useFakeLighting, location_skyColor, location_atlasSize, location_offset, location_plane,
			location_toShadowMapSpace, location_shadowMap, location_shadowDistance, location_shadowMapSize,
			location_pcfCount, location_jointTransforms[], location_jointIDs, location_jointWeight;

	public ItemShader() {

		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	protected void bindAttributes() {

		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
	}

	protected void getAllUniformLocations() {
		
		location_playerTransformationMatrix = super.getUniformLocation("playerTransformationMatrix");
		location_posMatrix = super.getUniformLocation("posMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_useFakeLighting = super.getUniformLocation("useFakeLighting");
		location_skyColor = super.getUniformLocation("skyColor");
		location_atlasSize = super.getUniformLocation("atlasSize");
		location_offset = super.getUniformLocation("offset");
		location_plane = super.getUniformLocation("plane");
		location_toShadowMapSpace = super.getUniformLocation("toShadowMapSpace");
		location_shadowMap = super.getUniformLocation("shadowMap");
		location_shadowDistance = super.getUniformLocation("shadowDistance");
		location_shadowMapSize = super.getUniformLocation("shadowMapSize");
		location_pcfCount = super.getUniformLocation("pcfCount");
		location_jointIDs = super.getUniformLocation("jointIndices");
		location_jointWeight = super.getUniformLocation("jointWeight");
		
		location_lightPosition = new int[MAX_LIGHTS];
		location_lightColor = new int[MAX_LIGHTS];
		location_attenuation = new int[MAX_LIGHTS];

		for (int i = 0; i < MAX_LIGHTS; i++) {

			location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
			location_lightColor[i] = super.getUniformLocation("lightColor[" + i + "]");
			location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
		}
		
		location_jointTransforms = new int[AnimatedModelRenderer.MAX_WEIGHTS];
		
		for(int i = 0; i < AnimatedModelRenderer.MAX_WEIGHTS; i++) {
			
			location_jointTransforms[i] = super.getUniformLocation("jointTransforms[" + i + "]");
		}
	}

	public void loadShadowMap(int shadowMapID) {

		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, shadowMapID);
	}

	public void connectTextureUnits() {

		super.loadInt(location_shadowMap, 1);
	}

	public void loadShadowEdgeFilter(int pcfCount) {

		super.loadInt(location_pcfCount, pcfCount);
	}

	public void loadShadowMapSize(float size) {

		super.loadFloat(location_shadowMapSize, size);
	}

	public void loadShadowDistance(float distance) {

		super.loadFloat(location_shadowDistance, distance);
	}

	public void loadToShadowMapSpace(Matrix4f matrix) {

		super.loadMatrix(location_toShadowMapSpace, matrix);
	}

	public void loadClipPlane(Vector4f plane) {

		super.load4DVector(location_plane, plane);
	}

	public void loadAtlasSize(int atlasSize) {

		super.loadFloat(location_atlasSize, atlasSize);
	}

	public void loadOffset(float x, float z) {

		super.load2DVector(location_offset, new Vector2f(x, z));
	}

	public void loadSkyColor(float r, float g, float b) {

		super.load3DVector(location_skyColor, new Vector3f(r, g, b));
	}

	public void loadFakeLightingVariable(boolean useFakeLighting) {

		super.loadBoolean(location_useFakeLighting, useFakeLighting);
	}

	public void loadShineVariables(float damper, float reflectivity) {

		super.loadFloat(location_shineDamper, damper);
		super.loadFloat(location_reflectivity, reflectivity);
	}
	
	public void loadPlayerTransformationMatrix(Matrix4f matrix) {
		
		super.loadMatrix(location_playerTransformationMatrix, matrix);
	}
	
	public void loadPosMatrix(Matrix4f matrix) {
		
		super.loadMatrix(location_posMatrix, matrix);
	}

	public void loadLights(List<Light> lights) {

		for (int i = 0; i < MAX_LIGHTS; i++) {

			if (i < lights.size()) {

				super.load3DVector(location_lightPosition[i], lights.get(i).getPosition());
				super.load3DVector(location_lightColor[i], lights.get(i).getColor());
				super.load3DVector(location_attenuation[i], lights.get(i).getAttenuation());
			} else {

				super.load3DVector(location_lightPosition[i], new Vector3f(0, 0, 0));
				super.load3DVector(location_lightColor[i], new Vector3f(0, 0, 0));
				super.load3DVector(location_attenuation[i], new Vector3f(1, 0, 0));

			}
		}
	}

	public void loadViewMatrix(Camera camera) {

		Matrix4f viewMatrix = Handler.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}

	public void loadProjectionMatrix(Matrix4f projection) {

		super.loadMatrix(location_projectionMatrix, projection);
	}
	
	public void loadJoint(Matrix4f[] jTransforms, Vector3f jID, Vector3f jWeight) {
		
		super.load3DVector(location_jointIDs, jID);
		super.load3DVector(location_jointWeight, jWeight);
		
		for(int i = 0; i < AnimatedModelRenderer.MAX_WEIGHTS; i++) {
			
			super.loadMatrix(location_jointTransforms[i], jTransforms[i]);
		}
	}
}