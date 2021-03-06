package dev.moore.fightTerrors.playState.renderers;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import dev.moore.fightTerrors.general.Handler;
import dev.moore.fightTerrors.playState.entities.AnimatedEntity;
import dev.moore.fightTerrors.playState.models.animated.AnimatedModel;
import dev.moore.fightTerrors.playState.models.base.ModelTexture;
import dev.moore.fightTerrors.playState.models.base.TexturedModel;
import dev.moore.fightTerrors.playState.shaders.AnimatedModelShader;

/**
 * 
 * This class deals with rendering an animated entity. Nothing particularly new
 * here. The only exciting part is that the joint transforms get loaded up to
 * the shader in a uniform array.
 * 
 * @author Karl
 *
 */
public class AnimatedModelRenderer {

	public static final int MAX_WEIGHTS = 3;
	private AnimatedModelShader shader;

	public AnimatedModelRenderer(AnimatedModelShader shader, Matrix4f projectionMatrix) {
		
		this.shader = shader;
		
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.connectTextureUnits();
		shader.stop();
	}

	public void render(Map<TexturedModel, List<AnimatedEntity>> entities, Matrix4f toShadowMapSpace) {
		
		shader.loadToShadowMapSpaceMatrix(toShadowMapSpace);
		
		for(TexturedModel texModel : entities.keySet()) {
			
			prepareTexturedModel(texModel);
			List<AnimatedEntity> batch = entities.get(texModel);
			
			for(AnimatedEntity entity : batch) {
								
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, texModel.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			
			unbindTexturedModel();
		}
	}

	private void prepareTexturedModel(TexturedModel texModel) {

		GL30.glBindVertexArray(texModel.getRawModel().getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL20.glEnableVertexAttribArray(3);
		GL20.glEnableVertexAttribArray(4);

		ModelTexture texture = texModel.getTexture();
		shader.loadAtlasSize(texture.getAtlasSize());
		
		if(texture.hasTransparency()) {
			
			PlayStateRenderer.disableCulling();
		}

		shader.loadFakeLightingVariable(texture.usingFakeLighting());
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getID());
	}
	
	private void unbindTexturedModel() {
		
		PlayStateRenderer.enableCulling();
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(3);
		GL20.glDisableVertexAttribArray(4);
		GL30.glBindVertexArray(0);
	}
	
	private void prepareInstance(AnimatedEntity entity) {
		
		Matrix4f transformationMatrix = Handler.createTransformationMatrix(entity.getPosition(), entity.getRotation().x, entity.getRotation().y, entity.getRotation().z, entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
		shader.loadOffset(entity.getTextureXOffset(), entity.getTextureYOffset());
		
		AnimatedModel animModel = entity.getAnimModel();
		int jointCount = animModel.getJointCount();
		
		shader.loadJointTransforms(animModel.getJointTransforms(), jointCount);
	}
	
	public void cleanUp() {
		
		shader.cleanUp();
	}
}
