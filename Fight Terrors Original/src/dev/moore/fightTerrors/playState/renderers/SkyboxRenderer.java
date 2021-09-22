package dev.moore.fightTerrors.playState.renderers;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import dev.moore.fightTerrors.general.DisplayMaster;
import dev.moore.fightTerrors.general.Loader;
import dev.moore.fightTerrors.playState.entities.Camera;
import dev.moore.fightTerrors.playState.models.base.RawModel;
import dev.moore.fightTerrors.playState.shaders.SkyboxShader;

public class SkyboxRenderer {

	private static final float SIZE = 500f;

	private static final float[] VERTICES = { -SIZE, SIZE, -SIZE, -SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE,
			-SIZE, SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE,

			-SIZE, -SIZE, SIZE, -SIZE, -SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, SIZE, SIZE, -SIZE,
			-SIZE, SIZE,

			SIZE, -SIZE, -SIZE, SIZE, -SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, -SIZE, SIZE, -SIZE,
			-SIZE,

			-SIZE, -SIZE, SIZE, -SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, -SIZE, SIZE, -SIZE, -SIZE,
			SIZE,

			-SIZE, SIZE, -SIZE, SIZE, SIZE, -SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, -SIZE, SIZE, SIZE, -SIZE, SIZE,
			-SIZE,

			-SIZE, -SIZE, -SIZE, -SIZE, -SIZE, SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, -SIZE, -SIZE, SIZE, SIZE,
			-SIZE, SIZE };

	public static final Vector3f DAY_FOG_COLOR = new Vector3f(0.55f, 0.62f, 0.69f), NIGHT_FOG_COLOR = new Vector3f(0.05f, 0.05f, 0.1f);

	private static String[] DAY_TEXTURE_FILES = { "right", "left", "top", "bottom", "back", "front" };
	private static String[] NIGHT_TEXTURE_FILES = { "nightRight", "nightLeft", "nightTop", "nightBottom", "nightBack", "nightFront" };

	private RawModel cube;
	private int dayTexture, nightTexture;
	private SkyboxShader shader;
	private float time = 10000;

	public SkyboxRenderer(Loader loader, Matrix4f projectionMatrix) {

		cube = loader.loadToVAO(VERTICES, 3);
		dayTexture = loader.loadCubeMap(DAY_TEXTURE_FILES);
		nightTexture = loader.loadCubeMap(NIGHT_TEXTURE_FILES);
		shader = new SkyboxShader();
		shader.start();
		shader.connectTextureUnits();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}

	public void render(Camera camera, Vector3f fogColor) {

		shader.start();
		shader.loadViewMatrix(camera);
		GL30.glBindVertexArray(cube.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		bindTextures();
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.getVertexCount());
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}

	String day, hour, minute;

	private void bindTextures() {

		time += DisplayMaster.getFrameTime() * 10f;
		time %= 24000;
		String t = String.format("%05d", (int) time);

		hour = t.substring(0, 2);

		if (hour == "00")
			hour = "12";

		int texture1, texture2;
		float blendFactor;
		Vector3f fog1, fog2;

		if (time >= 0 && time < 5000) { // NIGHT

			texture1 = nightTexture;
			texture2 = nightTexture;
			fog1 = NIGHT_FOG_COLOR;
			fog2 = NIGHT_FOG_COLOR;
			blendFactor = (time - 0) / (5000 - 0);

		} else if (time >= 5000 && time < 8000) { // DAWN

			texture1 = nightTexture;
			texture2 = dayTexture;
			fog1 = NIGHT_FOG_COLOR;
			fog2 = DAY_FOG_COLOR;
			blendFactor = (time - 5000) / (8000 - 5000);

		} else if (time >= 8000 && time < 18000) { // DAY

			texture1 = dayTexture;
			texture2 = dayTexture;
			fog1 = DAY_FOG_COLOR;
			fog2 = DAY_FOG_COLOR;
			blendFactor = (time - 8000) / (18000 - 8000);

		} else if (time >= 18000 && time < 21000) { // DUSK

			texture1 = dayTexture;
			texture2 = nightTexture;
			fog1 = DAY_FOG_COLOR;
			fog2 = NIGHT_FOG_COLOR;
			blendFactor = (time - 18000) / (21000 - 18000);

		} else { // NIGHT

			texture1 = nightTexture;
			texture2 = nightTexture;
			fog1 = NIGHT_FOG_COLOR;
			fog2 = NIGHT_FOG_COLOR;
			blendFactor = (time - 21000) / (24000 - 21000);
		}

		float x = ((1 - blendFactor) * fog1.x) + (blendFactor * fog2.x);
		float y = ((1 - blendFactor) * fog1.y) + (blendFactor * fog2.y);
		float z = ((1 - blendFactor) * fog1.z) + (blendFactor * fog2.z);

		Vector3f fogColor = new Vector3f(x, y, z);
		PlayStateRenderer.setFogColor(fogColor);

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture1);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture2);
		shader.loadFogColor(fogColor);
		shader.loadBlendFactor(blendFactor);
	}

	// SETTERS

	public void setTime(int time) {
		this.time = time;
	}
}
