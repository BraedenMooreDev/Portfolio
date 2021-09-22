package dev.moore.fightTerrors.playState.renderers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import dev.moore.fightTerrors.general.DisplayMaster;
import dev.moore.fightTerrors.general.Handler;
import dev.moore.fightTerrors.general.Loader;
import dev.moore.fightTerrors.playState.PlayState;
import dev.moore.fightTerrors.playState.entities.AnimatedEntity;
import dev.moore.fightTerrors.playState.entities.Camera;
import dev.moore.fightTerrors.playState.entities.Entity;
import dev.moore.fightTerrors.playState.entities.Light;
import dev.moore.fightTerrors.playState.entities.mobs.Player;
import dev.moore.fightTerrors.playState.items.Item;
import dev.moore.fightTerrors.playState.models.base.TexturedModel;
import dev.moore.fightTerrors.playState.shaders.AnimatedModelShader;
import dev.moore.fightTerrors.playState.shaders.ItemShader;
import dev.moore.fightTerrors.playState.shaders.StaticShader;
import dev.moore.fightTerrors.playState.shaders.TerrainShader;
import dev.moore.fightTerrors.playState.shadows.ShadowBox;
import dev.moore.fightTerrors.playState.terrains.TerrainTile;

public class PlayStateRenderer {

	public static final float FOV = 70, NEAR_PLANE = 0.1f, FAR_PLANE = 1000f;

	private static Vector3f fogColor = SkyboxRenderer.DAY_FOG_COLOR;

	private Matrix4f projectionMatrix;
	private StaticShader shader = new StaticShader();
	public EntityRenderer renderer;
	private ItemShader itemShader = new ItemShader();
	private ItemRenderer itemRenderer;
	private AnimatedModelShader animShader = new AnimatedModelShader();
	public AnimatedModelRenderer animRenderer;
	public TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader = new TerrainShader();

	public Map<TexturedModel, List<AnimatedEntity>> animEntities = new HashMap<TexturedModel, List<AnimatedEntity>>();
	public Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	public Map<TexturedModel, List<Item>> items = new HashMap<TexturedModel, List<Item>>();
	private List<TerrainTile> terrains = new ArrayList<TerrainTile>();

	private SkyboxRenderer skyboxRenderer;
	private ShadowMapMasterRenderer shadowMapRenderer;

	public PlayStateRenderer(Loader loader, Camera camera) {

		enableCulling();

		createProjectionMatrix();
				
		renderer = new EntityRenderer(shader, projectionMatrix);
		itemRenderer = new ItemRenderer(itemShader, projectionMatrix);
		animRenderer = new AnimatedModelRenderer(animShader, projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
		skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
		shadowMapRenderer = new ShadowMapMasterRenderer(camera);

//		shader.loadShadowEdgeFilter(5);
//		itemShader.loadShadowEdgeFilter(5);
//		animShader.loadShadowEdgeFilter(5);
//		terrainShader.loadShadowEdgeFilter(5);
	}

	public static void enableCulling() {

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}

	public static void disableCulling() {

		GL11.glDisable(GL11.GL_CULL_FACE);
	}

	boolean cmdDone = false;

	public void renderScene(Player player, List<Entity> entities, List<Item> items, List<AnimatedEntity> animEntities, List<TerrainTile> terrains, List<Light> lights,
			Camera camera, Vector4f clipPlane) {

		renderer.setPlayer(player);

		if (player != null)
			processAnimEntity(player, player.getPosition());

		for (TerrainTile terrain : terrains) {

			processTerrain(terrain);
		}

		for (Entity entity : entities) {
			
			processEntity(entity, player.getPosition());
		}
		
		for(Item item : items) {
			
			processItem(item);
		}
		
		for(AnimatedEntity animEntity : animEntities) {
			
			processAnimEntity(animEntity, player.getPosition());
		}

		render(lights, camera, clipPlane);

		if (Keyboard.isKeyDown(Keyboard.KEY_GRAVE)) {

			if (!cmdDone) {

				String command = JOptionPane.showInputDialog("Developer Command Input", null);

				if (command != null) {

					command = command.toLowerCase();

					if (command.startsWith("time = ")) {

						skyboxRenderer.setTime(Integer.parseInt(command.substring(7)));
						cmdDone = true;
					} else if (command.startsWith("toggle fps")) {

						DisplayMaster.printFPS = !DisplayMaster.printFPS;
						cmdDone = true;
					}
				}
			}

		} else {

			cmdDone = false;
		}
	}

	public void render(List<Light> lights, Camera camera, Vector4f clipPlane) {

		prepare();
		
		shader.start();
		shader.loadClipPlane(clipPlane);
		shader.loadSkyColor(fogColor.x, fogColor.y, fogColor.z);
		shader.loadLights(lights);
		shader.loadViewMatrix(camera);
		shader.loadShadowMapSize(ShadowMapMasterRenderer.SHADOW_MAP_SIZE);
		shader.loadShadowEdgeFilter(ShadowMapMasterRenderer.SHADOW_EDGE_SMOOTHING);
		shader.loadShadowDistance(ShadowBox.SHADOW_DISTANCE);
		shader.loadShadowMap(getShadowMapTexture());
		renderer.render(entities, shadowMapRenderer.getToShadowMapSpaceMatrix());
		shader.stop();

		itemShader.start();
		itemShader.loadClipPlane(clipPlane);
		itemShader.loadSkyColor(fogColor.x, fogColor.y, fogColor.z);
		itemShader.loadLights(lights);
		itemShader.loadViewMatrix(camera);
		itemShader.loadShadowMapSize(ShadowMapMasterRenderer.SHADOW_MAP_SIZE);
		itemShader.loadShadowEdgeFilter(ShadowMapMasterRenderer.SHADOW_EDGE_SMOOTHING);
		itemShader.loadShadowDistance(ShadowBox.SHADOW_DISTANCE);
		itemShader.loadShadowMap(getShadowMapTexture());
		itemRenderer.render(items, shadowMapRenderer.getToShadowMapSpaceMatrix(), renderer.getPlayer());
		itemShader.stop();
		
		animShader.start();
		animShader.loadClipPlane(clipPlane);
		animShader.loadSkyColor(fogColor.x, fogColor.y, fogColor.z);
		animShader.loadLights(lights);
		animShader.loadViewMatrix(camera);
		animShader.loadShadowMapSize(ShadowMapMasterRenderer.SHADOW_MAP_SIZE);
		animShader.loadShadowEdgeFilter(ShadowMapMasterRenderer.SHADOW_EDGE_SMOOTHING);
		animShader.loadShadowDistance(ShadowBox.SHADOW_DISTANCE);
		animShader.loadShadowMap(getShadowMapTexture());
		animRenderer.render(animEntities, shadowMapRenderer.getToShadowMapSpaceMatrix());
		animShader.stop();
		
		terrainShader.start();
		terrainShader.loadClipPlane(clipPlane);
		terrainShader.loadSkyColor(fogColor.x, fogColor.y, fogColor.z);
		terrainShader.loadLights(lights);
		terrainShader.loadViewMatrix(camera);
		terrainShader.loadShadowMapSize(ShadowMapMasterRenderer.SHADOW_MAP_SIZE);
		terrainShader.loadShadowEdgeFilter(ShadowMapMasterRenderer.SHADOW_EDGE_SMOOTHING);
		terrainShader.loadShadowDistance(ShadowBox.SHADOW_DISTANCE);
		terrainShader.loadShadowMap(getShadowMapTexture());
		terrainRenderer.render(terrains, shadowMapRenderer.getToShadowMapSpaceMatrix());
		terrainShader.stop();
		
		skyboxRenderer.render(camera, fogColor);
		
		items.clear();
		animEntities.clear();
		entities.clear();
		terrains.clear();
	}

	public void processAnimEntity(AnimatedEntity entity, Vector3f playerPos) {

		TexturedModel entityModel = entity.getModel();
		List<AnimatedEntity> batch = animEntities.get(entityModel);

		if(Handler.DistanceVector3f(entity.getPosition(), playerPos) <= PlayState.ENTITY_RENDER_DISTANCE) {
			
			if (batch != null) {

				batch.add(entity);
			} else {

				List<AnimatedEntity> newBatch = new ArrayList<AnimatedEntity>();
				newBatch.add(entity);
				animEntities.put(entityModel, newBatch);
			}
		}
	}
	
	public void processEntity(Entity entity, Vector3f playerPos) {

		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = entities.get(entityModel);
		
		if(Handler.DistanceVector3f(entity.getPosition(), playerPos) <= PlayState.ENTITY_RENDER_DISTANCE) {
			
			if (batch != null) {

				batch.add(entity);
			} else {

				List<Entity> newBatch = new ArrayList<Entity>();
				newBatch.add(entity);
				entities.put(entityModel, newBatch);
			}
		}
	}

	public void processItem(Item item) {

		TexturedModel itemModel = item.getEntity().getModel();
		List<Item> batch = items.get(itemModel);

		if (batch != null) {

			batch.add(item);
		} else {

			List<Item> newBatch = new ArrayList<Item>();
			newBatch.add(item);
			items.put(itemModel, newBatch);
		}
	}
	
	public void processTerrain(TerrainTile terrain) {

		terrains.add(terrain);
	}

	public void prepare() {

		GL32.glProvokingVertex(GL32.GL_FIRST_VERTEX_CONVENTION);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(fogColor.x, fogColor.y, fogColor.z, 1);
	}

	public void renderShadowMap(Player player, List<Entity> entityList, List<AnimatedEntity> animEntityList, Light sun) {

		prepare();

		processAnimEntity(player, player.getPosition());

		for (Entity entity : entityList) {

			processEntity(entity, player.getPosition());
		}
		
		for(AnimatedEntity entity : animEntityList) {
			
			processAnimEntity(entity, player.getPosition());
		}

		shadowMapRenderer.render(entities, animEntities, sun);
		entities.clear();
		animEntities.clear();
		items.clear();
	}

	public int getShadowMapTexture() {

		return shadowMapRenderer.getShadowMap();
	}

	public void cleanUp() {

		shader.cleanUp();
		itemShader.cleanUp();
		animShader.cleanUp();
		terrainShader.cleanUp();
		shadowMapRenderer.cleanUp();
	}

	private void createProjectionMatrix() {

		projectionMatrix = new Matrix4f();

		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) (1f / Math.tan(Math.toRadians(FOV / 2f)));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
	}

	// GETTERS

	public static Vector3f getFogColor() {
		return fogColor;
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	// SETTERS

	public static void setFogColor(Vector3f fogColor) {
		PlayStateRenderer.fogColor = fogColor;
	}
}
