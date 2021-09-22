package dev.moore.fightTerrors.general.guis;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import dev.moore.fightTerrors.general.Handler;
import dev.moore.fightTerrors.general.Loader;
import dev.moore.fightTerrors.playState.models.base.RawModel;

public class GuiRenderer {

	private final RawModel quad;
	private GuiShader shader;
	private Loader loader;

	public GuiRenderer(Loader loader) {

		this.loader = loader;
		float[] positions = { -1, 1, -1, -1, 1, 1, 1, -1 };
		quad = loader.loadToVAO(positions, 2);
		shader = new GuiShader();
	}

	public void render(List<GuiTexture> guis) {

		shader.start();
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		for (GuiTexture gui : guis) {

			if (!gui.hasTexture()) {

				gui.setTextureID(loader.loadTexture(gui.getTextureFileName()));
			}

			gui.update();

			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.getTextureID());
			Matrix4f matrix = Handler.createTransformationMatrix(gui.getPosition(), gui.getScale());
			shader.loadTransformation(matrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}

	public void cleapUp() {

		shader.cleanUp();
	}
}
