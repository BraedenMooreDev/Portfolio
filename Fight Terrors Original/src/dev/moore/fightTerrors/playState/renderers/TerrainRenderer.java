package dev.moore.fightTerrors.playState.renderers;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import dev.moore.fightTerrors.general.Handler;
import dev.moore.fightTerrors.playState.models.base.RawModel;
import dev.moore.fightTerrors.playState.shaders.TerrainShader;
import dev.moore.fightTerrors.playState.terrains.TerrainTexturePack;
import dev.moore.fightTerrors.playState.terrains.TerrainTile;

public class TerrainRenderer {

	public TerrainShader shader;

	public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix) {

		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.connectTextureUnits();
		shader.stop();
	}

	public void render(List<TerrainTile> terrains, Matrix4f toShadowSpace) {

		shader.loadToShadowMapSpace(toShadowSpace);

		for (TerrainTile terrain : terrains) {

			prepareTerrain(terrain);
			loadModelMatrix(terrain);

			GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

			unbindTerrain();
		}
	}

	private void prepareTerrain(TerrainTile terrain) {

		RawModel rawModel = terrain.getModel();

		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);

		bindTextures(terrain);
		shader.loadShineVariables(1, 0);
	}

	private void bindTextures(TerrainTile terrain) {

		TerrainTexturePack texturePack = terrain.getTexturePack();

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getBackgroundTexture().getID());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getRTexture().getID());
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getGTexture().getID());
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getBTexture().getID());
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMap().getID());
	}

	private void unbindTerrain() {

		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	private void loadModelMatrix(TerrainTile terrain) {

		Matrix4f transformationMatrix = Handler
				.createTransformationMatrix(new Vector3f(terrain.getX(), 0, terrain.getZ()), 0, 0, 0, 1);
		shader.loadTransformationMatrix(transformationMatrix);
	}
}
