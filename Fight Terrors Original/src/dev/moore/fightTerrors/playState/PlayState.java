package dev.moore.fightTerrors.playState;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import dev.moore.fightTerrors.MainLoop;
import dev.moore.fightTerrors.general.DisplayMaster;
import dev.moore.fightTerrors.general.Loader;
import dev.moore.fightTerrors.general.MousePicker;
import dev.moore.fightTerrors.general.fontMeshCreator.FontType;
import dev.moore.fightTerrors.general.fontRendering.TextMaster;
import dev.moore.fightTerrors.general.guis.GuiRenderer;
import dev.moore.fightTerrors.general.guis.GuiTexture;
import dev.moore.fightTerrors.general.guis.components.GuiButton;
import dev.moore.fightTerrors.general.guis.components.GuiImage;
import dev.moore.fightTerrors.playState.entities.AnimatedEntity;
import dev.moore.fightTerrors.playState.entities.Camera;
import dev.moore.fightTerrors.playState.entities.Entity;
import dev.moore.fightTerrors.playState.entities.Light;
import dev.moore.fightTerrors.playState.entities.mobs.BaseMob;
import dev.moore.fightTerrors.playState.entities.mobs.MobStats;
import dev.moore.fightTerrors.playState.entities.mobs.Player;
import dev.moore.fightTerrors.playState.events.InteractionEvent;
import dev.moore.fightTerrors.playState.items.WieldableType;
import dev.moore.fightTerrors.playState.items.Item;
import dev.moore.fightTerrors.playState.items.WieldableItem;
import dev.moore.fightTerrors.playState.items.WieldableStats;
import dev.moore.fightTerrors.playState.models.animated.AnimatedModel;
import dev.moore.fightTerrors.playState.models.base.ModelData;
import dev.moore.fightTerrors.playState.models.base.ModelTexture;
import dev.moore.fightTerrors.playState.models.base.RawModel;
import dev.moore.fightTerrors.playState.models.base.TexturedModel;
import dev.moore.fightTerrors.playState.models.parsers.objParser;
import dev.moore.fightTerrors.playState.models.parsers.colladaParser.AnimatedModelLoader;
import dev.moore.fightTerrors.playState.particles.ParticleMaster;
import dev.moore.fightTerrors.playState.particles.ParticleSystem;
import dev.moore.fightTerrors.playState.particles.ParticleTexture;
import dev.moore.fightTerrors.playState.renderers.PlayStateRenderer;
import dev.moore.fightTerrors.playState.renderers.ShadowMapMasterRenderer;
import dev.moore.fightTerrors.playState.renderers.WaterRenderer;
import dev.moore.fightTerrors.playState.shaders.WaterShader;
import dev.moore.fightTerrors.playState.shadows.ShadowBox;
import dev.moore.fightTerrors.playState.terrains.TerrainTexture;
import dev.moore.fightTerrors.playState.terrains.TerrainTexturePack;
import dev.moore.fightTerrors.playState.terrains.TerrainTile;
import dev.moore.fightTerrors.playState.water.WaterFrameBuffers;
import dev.moore.fightTerrors.playState.water.WaterTile;

public class PlayState {
	
	public static final float ENTITY_RENDER_DISTANCE = 200f, GRAVITY_CONST = 2f;

	public List<TerrainTile> terrains = new ArrayList<TerrainTile>();
	public List<WaterTile> waters = new ArrayList<WaterTile>();
	public List<Entity> entities = new ArrayList<Entity>();
	public List<Item> items = new ArrayList<Item>();
	public List<AnimatedEntity> animEntities = new ArrayList<AnimatedEntity>();
	public List<BaseMob> baseMobs = new ArrayList<BaseMob>();
	public List<GuiTexture> guis = new ArrayList<GuiTexture>();
	public List<Light> lights = new ArrayList<Light>();

	private List<GuiTexture> pauseGuis = new ArrayList<GuiTexture>();

	private TerrainTile terrain = null;

	private WaterFrameBuffers fbos = null;
	private WaterRenderer waterRenderer = null;

	public Player player = null;

	private GuiRenderer guiRenderer = null;
	private TextMaster textMaster = null, pauseTextMaster = null;
	private FontType font = null;

	private ParticleSystem particleSystem = null;

	private MainLoop mainLoop = null;
	private Loader loader = null;
	public Camera camera = null;
	public PlayStateRenderer renderer = null;
	private Light sun = null;
	private MousePicker picker = null;

	private boolean changingState = false;

	private InteractionEvent iEvent = null;

	public PlayState(MainLoop mainLoop, Loader loader) {

		this.mainLoop = mainLoop;
		this.loader = loader;

		init();
	}

	private void init() {

		DisplayMaster.showLoadingDisplay();

		initTerrain(loader);

		initEntities(loader);

		sun = new Light(new Vector3f(10000, 10000, 10000), new Vector3f(1, 1, 1));
		lights.add(sun);
		
		renderer = new PlayStateRenderer(loader, camera);
		picker = new MousePicker(camera, renderer.getProjectionMatrix());

		initWater(loader, renderer);

		initGuis(loader, renderer);

		initPauseMenu(loader, renderer);

		initParticles(loader, renderer);

		DisplayMaster.unshowLoadingDisplay();
	}

	private void initTerrain(Loader loader) {

		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grass"));
		TerrainTexture rTexture = backgroundTexture;
		TerrainTexture gTexture = backgroundTexture;
		TerrainTexture bTexture = backgroundTexture;

		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

		terrain = new TerrainTile(0, 0, loader, texturePack, blendMap, 800);
		terrains.add(terrain);
	}

	public void initWater(Loader loader, PlayStateRenderer renderer) {

		TerrainTile terrain = terrains.get(0);

		fbos = new WaterFrameBuffers();
		WaterShader waterShader = new WaterShader();
		waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), fbos);
		WaterTile water = new WaterTile(0, 0, 0, terrain.getSize() / 2);
		waters.add(water);
	}

	public void reinitWater() {

		initWater(loader, renderer);
	}

	public BaseMob mob;
	
	private void initEntities(Loader loader) {

		TerrainTile terrain = terrains.get(0);

		AnimatedModel animatedPlayerModel = AnimatedModelLoader.loadEntity(loader, "animatedPlayerModel", "modelTexture");
		AnimatedEntity animatedPlayerEntity = new AnimatedEntity(animatedPlayerModel, new Vector3f(400, 0, 400), new Vector3f(0, 0, 0), 0.75f, 2f);
		player = new Player(animatedPlayerEntity, this, terrain, new MobStats(100, 0, 16, 45f));
		
		camera = new Camera(player);

		ModelData treeData = objParser.loadOBJ("pine");
		RawModel treeRawModel = loader.loadToVAO(treeData.getVertices(), treeData.getTextureCoords(), treeData.getNormals(), treeData.getIndices());
		treeRawModel.setData(treeData);
		TexturedModel treeModel = new TexturedModel(treeRawModel, new ModelTexture(loader.loadTexture("pine")));

		ModelData ttreeData = objParser.loadOBJ("lowPoly_Pine");
		RawModel ttreeRawModel = loader.loadToVAO(ttreeData.getVertices(), ttreeData.getTextureCoords(), ttreeData.getNormals(), ttreeData.getIndices());
		ttreeRawModel.setData(ttreeData);
		TexturedModel ttreeModel = new TexturedModel(ttreeRawModel, new ModelTexture(loader.loadTexture("modelTexture")));

//		ModelData fernData = OBJFileLoader.loadOBJ("fern");
//		RawModel fernRawModel = loader.loadToVAO(fernData.getVertices(), fernData.getTextureCoords(), fernData.getNormals(), fernData.getIndices());
//		fernRawModel.setData(fernData);
//		ModelTexture fernTexture = new ModelTexture(loader.loadTexture("fern"));
//		fernTexture.setAtlasSize(2);
//		TexturedModel fernModel = new TexturedModel(fernRawModel, fernTexture);

		ModelData rapierData = objParser.loadOBJ("rapier");
		RawModel rapierRawModel = loader.loadToVAO(rapierData.getVertices(), rapierData.getTextureCoords(), rapierData.getNormals(), rapierData.getIndices());
		rapierRawModel.setData(rapierData);
		TexturedModel rapierModel = new TexturedModel(rapierRawModel, new ModelTexture(loader.loadTexture("modelTexture")));

		ModelData swordData = objParser.loadOBJ("sword_");
		RawModel swordRawModel = loader.loadToVAO(swordData.getVertices(), swordData.getTextureCoords(), swordData.getNormals(), swordData.getIndices());
		swordRawModel.setData(swordData);
		TexturedModel swordModel = new TexturedModel(swordRawModel, new ModelTexture(loader.loadTexture("modelTexture")));
		
		ModelData shieldData = objParser.loadOBJ("circleShield");
		RawModel shieldRawModel = loader.loadToVAO(shieldData.getVertices(), shieldData.getTextureCoords(), shieldData.getNormals(), shieldData.getIndices());
		shieldRawModel.setData(shieldData);
		TexturedModel shieldModel = new TexturedModel(shieldRawModel, new ModelTexture(loader.loadTexture("modelTexture")));

		AnimatedModel animatedMobModel = AnimatedModelLoader.loadEntity(loader, "animatedPlayerModel", "modelTexture");
		AnimatedEntity animatedMobEntity = new AnimatedEntity(animatedMobModel, new Vector3f(420, terrain.getHeightOfTerrain(420, 400), 400), new Vector3f(0, 0, 0), 1f, 6f);
		mob = new BaseMob("Mob", animatedMobEntity, terrain, new MobStats(100, 0, 10, 10), 7f);
		animEntities.add(animatedMobEntity);
		baseMobs.add(mob);

		ModelData modData = objParser.loadOBJ("playerMaleFullStop");
		RawModel modRawModel = loader.loadToVAO(modData.getVertices(), modData.getTextureCoords(), modData.getNormals(), modData.getIndices());
		modRawModel.setData(modData);
		TexturedModel modModel = new TexturedModel(modRawModel, new ModelTexture(loader.loadTexture("modelTexture")));
		entities.add(new Entity(modModel, new Vector3f(450, terrain.getHeightOfTerrain(450, 450), 450), new Vector3f(0f, 0f, 0f), 1f, 6f));

		Random random = new Random();

		float x = 0, y = 0, z = 0;

		for (int j = 0; j < 4; j++) {

			x = random.nextFloat() * terrain.getSize();
			z = random.nextFloat() * terrain.getSize();
			y = terrain.getHeightOfTerrain(410, 410 + 10 * (j + 1)) + 0.5f;

			Entity rapier = new Entity(rapierModel, new Vector3f(410, y, 410 + 10 * (j + 1)),
					new Vector3f(0, 0, 0), 1f, 5f);
			rapier.addEvent(new InteractionEvent(this, 10f, 2.5f) {

				public void hover() {

					this.linkedEntity.isHovering(true);
				}

				public void unhover() {

					this.linkedEntity.isHovering(false);
				}

				public void clicked() {

					WieldableItem wieldableItem = new WieldableItem(playState, linkedEntity, "Rapier", this.linkedEntity.getRotation(), new Vector3f(1.3f, 3.2f, 0f), new Vector3f(0f, 175f, 0f), false, new Vector3f(0.75f, 3.5f, 1.25f), new Vector3f(-15f, -5f, 0f), false, new Vector3f(-0.8f, 7f, -0.9f), new Vector3f(-87f, -18f, 0f), false, new WieldableStats(5, 10f, 1000, new boolean[] { true, true, true, true })) {
						
						@Override
						public void used() {
							
							player.useWieldable(WieldableType.JAB, this);
						}
					};
					
					if(player.grabNewWieldable(wieldableItem))
						setEnabled(false);
				}

				public void unclicked() {}
			});
			entities.add(rapier);
		}
		
		for (int j = 0; j < 4; j++) {

			x = random.nextFloat() * terrain.getSize();
			z = random.nextFloat() * terrain.getSize();
			y = terrain.getHeightOfTerrain(410 + 10 * (j + 1), 410) + 0.5f;

			Entity sword = new Entity(swordModel, new Vector3f(410 + 10 * (j + 1), y, 410),
					new Vector3f(0, 0, 0), 1f, 5f);
			sword.addEvent(new InteractionEvent(this, 10f, 2.5f) {

				public void hover() {

					this.linkedEntity.isHovering(true);
				}

				public void unhover() {

					this.linkedEntity.isHovering(false);
				}

				public void clicked() {

					WieldableItem wieldableItem = new WieldableItem(playState, linkedEntity, "Sword", this.linkedEntity.getRotation(), new Vector3f(1.3f, 3.2f, 0f), new Vector3f(0f, 175f, 0f), false, new Vector3f(0.75f, 3.5f, 0.6f), new Vector3f(-15f, -5f, 0f), false, new Vector3f(-0.8f, 7f, -0.9f), new Vector3f(-87f, -18f, 0f), false, new WieldableStats(5, 15f, 1500, new boolean[] { true, true, true, true })) {
						
						@Override
						public void used() {
							
							player.useWieldable(WieldableType.SWING, this);
						}
					};
					
					if(player.grabNewWieldable(wieldableItem))
						setEnabled(false);
				}

				public void unclicked() {}
			});
			entities.add(sword);
		}

		for (int j = 0; j < 4; j++) {

			x = random.nextFloat() * terrain.getSize();
			z = random.nextFloat() * terrain.getSize();
			y = terrain.getHeightOfTerrain(410, 410 - 10 * (j + 1)) + 0.5f;

			Entity shield = new Entity(shieldModel, new Vector3f(410, y, 410 - 10 * (j + 1)),
					new Vector3f(0, 0, 90), 1f, 5f);
			shield.addEvent(new InteractionEvent(this, 10f, 2.5f) {

				public void hover() {

					this.linkedEntity.isHovering(true);
				}

				public void unhover() {

					this.linkedEntity.isHovering(false);
				}

				public void clicked() {

					WieldableItem wieldableItem = new WieldableItem(playState, linkedEntity, "Shield", this.linkedEntity.getRotation(), new Vector3f(1.55f, 3.45f, 0.2f), new Vector3f(0f, -20f, 2f), true, new Vector3f(1f, 3.35f, 0.15f), new Vector3f(0f, -5f, -3f), true, new Vector3f(0f, 5.5f, -1.2f), new Vector3f(0f, 90f, -4f), false, new WieldableStats(5, 3f, 0, new boolean[] {true, true, true, true})) {
						
						@Override
						public void used() {
							
							player.useWieldable(WieldableType.BLOCK, this);
						}
					};
					
					if(player.grabNewWieldable(wieldableItem));
						setEnabled(false);
				}

				public void unclicked() {}
			});
			entities.add(shield);
		}

//		x = random.nextFloat() * terrain.getSize();
//		z = random.nextFloat() * terrain.getSize();
//		y = terrain.getHeightOfTerrain(420, 420) - 0.5f;
//
//		entities.add(new Entity(treeModel, new Vector3f(420, y, 420), new Vector3f(0, random.nextFloat() * 360, 0), 0.75f + random.nextFloat() / 2, 7f));			
//
//		
//		x = random.nextFloat() * terrain.getSize();
//		z = random.nextFloat() * terrain.getSize();
//		y = terrain.getHeightOfTerrain(390, 390) - 0.5f;
//
//		entities.add(new Entity(ttreeModel, new Vector3f(390, y, 390), new Vector3f(0, random.nextFloat() * 360, 0), 0.75f + random.nextFloat() / 2, 7f));			

		for (int i = 0; i < terrain.getSize(); i++) {

			x = random.nextFloat() * terrain.getSize();
			z = random.nextFloat() * terrain.getSize();
			y = terrain.getHeightOfTerrain(x, z) - 0.5f;

			if (y > 0) {

				entities.add(new Entity(treeModel, new Vector3f(x, y, z), new Vector3f(0, random.nextFloat() * 360, 0),
						0.75f + random.nextFloat() / 2, 7f));
			}

//			x = random.nextFloat() * terrain.getSize();
//			z = random.nextFloat() * terrain.getSize();
//			y = terrain.getHeightOfTerrain(x, z) - 0.1f;
//
//			if(y > 0) {
//		
//				entities.add(new Entity(fernModel, random.nextInt(4), new Vector3f(x, y, z), new Vector3f(0, random.nextFloat() * 360, 0), Handler.clampFloat(0.25f, 0.9f, random.nextFloat()), 1f));
//			}
		}
	}

	private void initGuis(Loader loader, PlayStateRenderer renderer) {

		guiRenderer = new GuiRenderer(loader);

//		GuiTexture shadowMap = new GuiImage(null, null, new Vector2f(0.5f, 0.5f), new Vector2f(0.5f, 0.5f));
//		shadowMap.setTextureID(renderer.getShadowMapTexture());
//		guis.add(shadowMap);
		
		GuiTexture crosshair = new GuiImage(loader, "crosshair_circle", new Vector2f(0f, 0f), new Vector2f(0.006f, 0.01f));
		guis.add(crosshair);
		
		textMaster = new TextMaster(loader);
		font = loader.getFont();
	}

	private void initPauseMenu(Loader loader, PlayStateRenderer renderer) {

		pauseTextMaster = new TextMaster(loader);

		GuiImage tint = new GuiImage(loader, "tint", new Vector2f(0f, 0f), new Vector2f(1f, 1f));
		GuiImage pauseBg = new GuiImage(loader, "pauseBarBg", new Vector2f(-0.85f, 0.25f), new Vector2f(0.1f, 0.75f));
		GuiButton resumeBtn = new GuiButton(">", pauseTextMaster, loader, "pauseBtn", new Vector2f(-0.85f, 0.75f),
				new Vector2f(0.07f, 0.12f)) {

			public void clickAction() {

				mainLoop.changeState(MainLoop.State.PLAY);
			}
		};

		GuiButton settingsBtn = new GuiButton("S", pauseTextMaster, loader, "pauseBtn", new Vector2f(-0.85f, 0.4f),
				new Vector2f(0.07f, 0.12f)) {

			public void clickAction() {

				mainLoop.changeState(MainLoop.State.SETTINGS_PAUSE);
			}
		};

		GuiButton exitBtn = new GuiButton("E", pauseTextMaster, loader, "pauseBtn", new Vector2f(-0.85f, 0.05f),
				new Vector2f(0.07f, 0.12f)) {

			public void clickAction() {

				mainLoop.changeState(MainLoop.State.TITLE);
			}
		};

		pauseGuis.add(tint);
		pauseGuis.add(pauseBg);
		pauseGuis.add(resumeBtn);
		pauseGuis.add(settingsBtn);
		pauseGuis.add(exitBtn);
	}

	private void initParticles(Loader loader, PlayStateRenderer renderer) {

		ParticleMaster.init(loader, renderer.getProjectionMatrix());
		ParticleTexture particleTexture = new ParticleTexture(loader.loadTexture("cosmic"), 4);

		particleSystem = new ParticleSystem(particleTexture, 100, 10, 0.25f, 3, 1f);
		particleSystem.setDirection(new Vector3f(0, 1f, 0), 0.2f);
		particleSystem.setLifeError(0.1f);
		particleSystem.setSpeedError(0.25f);
		particleSystem.setScaleError(0.5f);
		particleSystem.randomizeRotation();
	}

	public void update() {

		player.update();
		camera.move();
		picker.update();

//		 particleSystem.generateParticles(Vector3f.add(player.getPosition(), new
//		 Vector3f(0, 2, 0), null));

		ParticleMaster.update(camera);

		if (ShadowBox.SHADOW_DISTANCE != 0 && ShadowMapMasterRenderer.SHADOW_MAP_SIZE != 0)
			renderer.renderShadowMap(player, entities, animEntities, sun);

		GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

		fbos.bindReflectionFrameBuffer();
		float distance = 2 * (camera.getPosition().y - waters.get(0).getHeight());
		camera.getPosition().y -= distance;
		camera.invertPitch();
		renderer.renderScene(player, entities, items, animEntities, terrains, lights, camera,
				new Vector4f(0, 1, 0, -waters.get(0).getHeight() + 1f));
		camera.getPosition().y += distance;
		camera.invertPitch();

		fbos.bindRefractionFrameBuffer();
		renderer.renderScene(player, entities, items, animEntities, terrains, lights, camera,
				new Vector4f(0, -1, 0, waters.get(0).getHeight() + 1f));

		GL11.glDisable(GL30.GL_CLIP_DISTANCE0);

		fbos.unbindCurrentFrameBuffer();

		renderer.renderScene(player, entities, items, animEntities, terrains, lights, camera, new Vector4f(0, -1, 0, 100000));

		waterRenderer.render(waters, camera, sun);

		ParticleMaster.render(camera);

		guiRenderer.render(guis);
		textMaster.render();

		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {

			if (!changingState) {
				mainLoop.changeState(MainLoop.State.PAUSE);
				changingState = true;
			}
		} else
			changingState = false;

//        List<Entity> closeEntites = BroadCollisionDetection.ScanAndPrune(player, entities, 2f);
//        
//        for(Entity e : closeEntites) {
//        	        	        	
//        	for(NarrowCollisionDetection collider : e.getModel().getRawModel().getColliders()) {
//        	        		
//        		if(collider != null) {
//        			        		
//        			System.out.println(collider.detectCollision());
//        			
//        			if(collider.detectCollision() != null) {
//        			
//        				System.out.println("COLLIDING");
//        				break;
//        			}
//        		} else {
//        			
//        			System.out.println("Missing Collider");
//        		}
//        	}
//        }
				
		try {

			for (Entity e : entities) {

				if (e != null)
					e.update();
			}
			
			for(AnimatedEntity e : animEntities) {
				
				if(e != null)
					e.update();
			}
			
			for(BaseMob e : baseMobs) {
				
				if(e != null)
					e.update();
			}
			
		} catch (ConcurrentModificationException ignore) {}
				
		DisplayMaster.updateDisplay();
	}

	public void pauseUpdate() {

		Mouse.setGrabbed(false);

		GL11.glDisable(GL11.GL_DEPTH_TEST);

		guiRenderer.render(pauseGuis);
		pauseTextMaster.render();

		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {

			if (!changingState) {
				mainLoop.changeState(MainLoop.State.PLAY);
				changingState = true;
			}
		} else
			changingState = false;

		DisplayMaster.updateDisplay();
	}

	public void cleanUp() {

		ParticleMaster.cleanUp();
		textMaster.cleanUp();
		pauseTextMaster.cleanUp();
		fbos.cleanUp();
		guiRenderer.cleapUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayMaster.closeDisplay();
	}

	// GETTERS

	public MousePicker getMousePicker() {
		return picker;
	}
}