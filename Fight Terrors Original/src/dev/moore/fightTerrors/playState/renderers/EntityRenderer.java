package dev.moore.fightTerrors.playState.renderers;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import dev.moore.fightTerrors.general.Handler;
import dev.moore.fightTerrors.playState.collision.NarrowCollisionDetection;
import dev.moore.fightTerrors.playState.entities.Entity;
import dev.moore.fightTerrors.playState.entities.mobs.Player;
import dev.moore.fightTerrors.playState.models.base.ModelTexture;
import dev.moore.fightTerrors.playState.models.base.RawModel;
import dev.moore.fightTerrors.playState.models.base.TexturedModel;
import dev.moore.fightTerrors.playState.shaders.StaticShader;

public class EntityRenderer {

	public StaticShader shader;
	private Player player;

	public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix) {

		this.shader = shader;

		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.connectTextureUnits();
		shader.stop();
	}

	public void render(Map<TexturedModel, List<Entity>> entities, Matrix4f toShadowSpace) {

		shader.loadToShadowMapSpace(toShadowSpace);

		for (TexturedModel model : entities.keySet()) {

			prepareTexturedModel(model);
			List<Entity> batch = entities.get(model);

			for (Entity entity : batch) {

				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}

			unbindTexturedModel();
		}
	}

	private void prepareTexturedModel(TexturedModel model) {

		RawModel rawModel = model.getRawModel();

		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);

		ModelTexture texture = model.getTexture();
		shader.loadAtlasSize(texture.getAtlasSize());

		if (texture.hasTransparency()) {

			PlayStateRenderer.disableCulling();
		}

		shader.loadFakeLightingVariable(texture.usingFakeLighting());
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
	}

	private void unbindTexturedModel() {

		PlayStateRenderer.enableCulling();
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}
	
	private void prepareInstance(Entity entity) {

		Matrix4f transformationMatrix = Handler.createTransformationMatrix(entity.getPosition(), entity.getRotation().x,
				entity.getRotation().y, entity.getRotation().z, entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
		shader.loadOffset(entity.getTextureXOffset(), entity.getTextureYOffset());
				
//		float[] vertices = entity.getModel().getRawModel().getModelData().getVertices();		
//		int[] indices = entity.getModel().getRawModel().getModelData().getIndices();
//
//		NarrowCollisionDetection[] colliders = new NarrowCollisionDetection[indices.length / 3];
//
//		int j = 0;
//
//		for (int i = 0; i < colliders.length * 3; i += 3) {
//
//			Vector3f P1 = new Vector3f(vertices[indices[i] * 3], vertices[indices[i] * 3 + 1],
//					vertices[indices[i] * 3 + 2]);
//			Vector3f P2 = new Vector3f(vertices[indices[i + 1] * 3], vertices[indices[i + 1] * 3 + 1],
//					vertices[indices[i + 1] * 3 + 2]);
//			Vector3f P3 = new Vector3f(vertices[indices[i + 2] * 3], vertices[indices[i + 2] * 3 + 1],
//					vertices[indices[i + 2] * 3 + 2]);
//
//			colliders[j] = new NarrowCollisionDetection(player.getPosition(), player.getVelocity(), P1, P2, P3, 1, 1,
//					1);
//
//			j++;
//		}
//
//		entity.getModel().getRawModel().setColliders(colliders);
	}
	
	// SETTERS

	public void setPlayer(Player player) { this.player = player; }
	
	// GETTERS
	
	public Player getPlayer() { return player; }
}