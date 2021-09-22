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
import dev.moore.fightTerrors.playState.entities.Entity;
import dev.moore.fightTerrors.playState.entities.mobs.Player;
import dev.moore.fightTerrors.playState.items.Item;
import dev.moore.fightTerrors.playState.models.animated.MeshData;
import dev.moore.fightTerrors.playState.models.base.ModelTexture;
import dev.moore.fightTerrors.playState.models.base.RawModel;
import dev.moore.fightTerrors.playState.models.base.TexturedModel;
import dev.moore.fightTerrors.playState.shaders.ItemShader;

public class ItemRenderer {

	public ItemShader shader;
	private Player player;

	public ItemRenderer(ItemShader shader, Matrix4f projectionMatrix) {

		this.shader = shader;

		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.connectTextureUnits();
		shader.stop();
	}

	public void render(Map<TexturedModel, List<Item>> items, Matrix4f toShadowSpace, Player player) {

		shader.loadToShadowMapSpace(toShadowSpace);
		
		for (TexturedModel model : items.keySet()) {

			prepareTexturedModel(model);
			List<Item> batch = items.get(model);

			for (Item item : batch) {

				prepareInstance(item, player);
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
	
	private void prepareInstance(Item item, Player player) {

		Entity entity = item.getEntity();
		
		Matrix4f playerTransformationMatrix = Handler.createTransformationMatrix(player.getPosition(), player.getRotation());
		shader.loadPlayerTransformationMatrix(playerTransformationMatrix);
		
		MeshData data = player.getModel().getRawModel().getMeshData();
		
		int vID = item.isSheathed() ? item.getSheathVertexID() : item.getWieldVertexID();
		Vector3f pos = item.isSheathed() ? item.getSheathPos() : item.getWieldPos();
		Vector3f rot = item.isSheathed() ? item.getSheathRot() : item.getWieldRot();
				
		Vector3f jIDs = new Vector3f(data.getJointIds()[vID * 3], data.getJointIds()[vID * 3 + 1], data.getJointIds()[vID * 3 + 2]);
		Vector3f jWeight = new Vector3f(data.getVertexWeights()[vID * 3], data.getVertexWeights()[vID * 3 + 1], data.getVertexWeights()[vID * 3 + 2]);
		
		shader.loadJoint(player.getAnimModel().getJointTransforms(), jIDs, jWeight);
		shader.loadOffset(entity.getTextureXOffset(), entity.getTextureYOffset());

		Matrix4f posMatrix = Handler.createTransformationMatrix(pos, rot, entity.getScale());
		shader.loadPosMatrix(posMatrix);
	}
	
	// SETTERS

	public void setPlayer(Player player) { this.player = player; }
}