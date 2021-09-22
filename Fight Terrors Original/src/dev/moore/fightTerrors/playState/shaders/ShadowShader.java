package dev.moore.fightTerrors.playState.shaders;

import org.lwjgl.util.vector.Matrix4f;

import dev.moore.fightTerrors.general.ShaderProgram;
import dev.moore.fightTerrors.playState.renderers.AnimatedModelRenderer;

public class ShadowShader extends ShaderProgram {

	private static final int MAX_JOINTS = 50, // max number of joints in a skeleton
			MAX_LIGHTS = 4;
	
	private static final String VERTEX_FILE = "playState/shaders/glslFiles/shadowVertexShader.glsl";
	private static final String FRAGMENT_FILE = "playState/shaders/glslFiles/shadowFragmentShader.glsl";

	private int location_mvpMatrix, location_isAnimEntity, location_jointTransforms[];

	public ShadowShader() {
		
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		
		location_mvpMatrix = super.getUniformLocation("mvpMatrix");
		location_isAnimEntity = super.getUniformLocation("isAnimEntity");
		
		location_jointTransforms = new int[MAX_JOINTS];

		for (int i = 0; i < MAX_JOINTS; i++) {

			location_jointTransforms[i] = super.getUniformLocation("jointTransforms[" + i + "]");
		}
	}

	public void loadMvpMatrix(Matrix4f mvpMatrix) {
		
		super.loadMatrix(location_mvpMatrix, mvpMatrix);
	}
	
	public void loadIsAnimEntity(boolean isAnimEntity) {
		
		super.loadBoolean(location_isAnimEntity, isAnimEntity);
	}

	public void loadJointTransforms(Matrix4f[] jointTransforms, int jointCount) {

		for (int i = 0; i < MAX_JOINTS; i++) {

			if (i < jointCount && jointTransforms[i] != null) {

				super.loadMatrix(location_jointTransforms[i], jointTransforms[i]);
			} else {

				super.loadMatrix(location_jointTransforms[i], new Matrix4f());
			}
		}
	}
	
	@Override
	protected void bindAttributes() {
		
		super.bindAttribute(0, "in_position");
		super.bindAttribute(1, "in_textureCoords");
		super.bindAttribute(2, "in_jointIndices");
		super.bindAttribute(3, "in_weights");
	}

}
