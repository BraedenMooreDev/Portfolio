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
import dev.moore.fightTerrors.playState.entities.Entity;
import dev.moore.fightTerrors.playState.entities.mobs.Player;
import dev.moore.fightTerrors.playState.models.base.RawModel;
import dev.moore.fightTerrors.playState.models.base.TexturedModel;
import dev.moore.fightTerrors.playState.shaders.ShadowShader;

public class ShadowMapEntityRenderer {

	private Matrix4f projectionViewMatrix;
	private ShadowShader shader;

	/**
	 * @param shader               - the simple shader program being used for the
	 *                             shadow render pass.
	 * @param projectionViewMatrix - the orthographic projection matrix multiplied
	 *                             by the light's "view" matrix.
	 */
	protected ShadowMapEntityRenderer(ShadowShader shader, Matrix4f projectionViewMatrix) {
		this.shader = shader;
		this.projectionViewMatrix = projectionViewMatrix;
	}

	/**
	 * Renders entieis to the shadow map. Each model is first bound and then all of
	 * the entities using that model are rendered to the shadow map.
	 * 
	 * @param entities - the entities to be rendered to the shadow map.
	 */
	protected void renderEntities(Map<TexturedModel, List<Entity>> entities) {

		for (TexturedModel model : entities.keySet()) {

			RawModel rawModel = model.getRawModel();
			bindModel(rawModel);
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());

			if (model.getTexture().hasTransparency()) {

				PlayStateRenderer.disableCulling();
			}

			for (Entity entity : entities.get(model)) {
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}

			if (model.getTexture().hasTransparency()) {

				PlayStateRenderer.enableCulling();
			}
		}

		unbindModel();
	}
		
	protected void renderAnimEntities(Map<TexturedModel, List<AnimatedEntity>> entities) {

		for (TexturedModel model : entities.keySet()) {

			RawModel rawModel = model.getRawModel();
			bindAnimModel(rawModel);
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());

			if (model.getTexture().hasTransparency()) {

				PlayStateRenderer.disableCulling();
			}

			for (AnimatedEntity entity : entities.get(model)) {
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}

			if (model.getTexture().hasTransparency()) {

				PlayStateRenderer.enableCulling();
			}
		}

		unbindAnimModel();
	}

	/**
	 * Binds a raw model before rendering. Only the attribute 0 is enabled here
	 * because that is where the positions are stored in the VAO, and only the
	 * positions are required in the vertex shader.
	 * 
	 * @param rawModel - the model to be bound.
	 */
	private void bindModel(RawModel rawModel) {
		
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
	}
	
	private void bindAnimModel(RawModel rawModel) {
		
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL20.glEnableVertexAttribArray(3);
	}
	
	private void unbindModel() {
		
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
	}
	
	private void unbindAnimModel() {
		
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(3);
		GL30.glBindVertexArray(0);
	}

	/**
	 * Prepares an entity to be rendered. The model matrix is created in the usual
	 * way and then multiplied with the projection and view matrix (often in the
	 * past we've done this in the vertex shader) to create the mvp-matrix. This is
	 * then loaded to the vertex shader as a uniform.
	 * 
	 * @param entity - the entity to be prepared for rendering.
	 */
	private void prepareInstance(Entity entity) {
		
		Matrix4f modelMatrix = Handler.createTransformationMatrix(entity.getPosition(), entity.getRotation().x,
				entity.getRotation().y, entity.getRotation().z, entity.getScale());
		Matrix4f mvpMatrix = Matrix4f.mul(projectionViewMatrix, modelMatrix, null);
		shader.loadMvpMatrix(mvpMatrix);
		
		shader.loadIsAnimEntity(false);
	}
	
	private void prepareInstance(AnimatedEntity animEntity) {
				
		Matrix4f modelMatrix = Handler.createTransformationMatrix(animEntity.getPosition(), animEntity.getRotation().x,
				animEntity.getRotation().y, animEntity.getRotation().z, animEntity.getScale());
		Matrix4f mvpMatrix = Matrix4f.mul(projectionViewMatrix, modelMatrix, null);
		shader.loadMvpMatrix(mvpMatrix);
		
		shader.loadIsAnimEntity(true);
		
		shader.loadJointTransforms(animEntity.getAnimModel().getJointTransforms(), animEntity.getAnimModel().getJointCount());
	}
}
