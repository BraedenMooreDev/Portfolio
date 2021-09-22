package dev.moore.fightTerrors.playState.entities.mobs;

import java.lang.reflect.Field;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import dev.moore.fightTerrors.general.DisplayMaster;
import dev.moore.fightTerrors.general.Handler;
import dev.moore.fightTerrors.playState.PlayState;
import dev.moore.fightTerrors.playState.entities.AnimatedEntity;
import dev.moore.fightTerrors.playState.entities.Entity;
import dev.moore.fightTerrors.playState.events.InteractionEvent;
import dev.moore.fightTerrors.playState.items.WieldableType;
import dev.moore.fightTerrors.playState.items.Item;
import dev.moore.fightTerrors.playState.items.WieldableItem;
import dev.moore.fightTerrors.playState.items.WieldableStats;
import dev.moore.fightTerrors.playState.models.animated.AnimatedModel;
import dev.moore.fightTerrors.playState.models.animated.Animation;
import dev.moore.fightTerrors.playState.models.parsers.colladaParser.AnimationLoader;
import dev.moore.fightTerrors.playState.terrains.TerrainTile;

public class Player extends BaseMob {

	public static final float NORM_HEIGHT = 7f, EYE_OFFSET = -1.25f, WIND_MIN_TIME = 0f, WIND_MAX_TIME = 1f, WIND_MIN_JUMP = 1f, WIND_MAX_JUMP = 2f;
	public static final float WIND_M = (WIND_MAX_JUMP - WIND_MIN_JUMP) / (WIND_MAX_TIME - WIND_MIN_TIME), WIND_B = WIND_MIN_JUMP - (WIND_M * WIND_MIN_TIME);
	public static float height = NORM_HEIGHT;
	public static float firstPersonSensitivity = 0.1f, thirdPersonSensitivity = 0.5f;

	private float yVel = 0;
	
	private Random random;
	private TerrainTile terrain;
	private PlayState playState;

	private boolean firstPerson = false, jumping = false;

	public WieldableItem[] wieldables = new WieldableItem[4]; // L Hip (R Hand), R Hip (L Hand), R Back (R Hand), L Back (L Hand)
	private WieldableItem[] hand = new WieldableItem[2]; // L Hand, R Hand
	public Item[] wearables = new Item[2]; // Head, Torso
	public Item[] forgeables = new Item[16]; // Pouch

	public float[] leftHipVertex, rightHipVertex, leftBackVertex, rightBackVertex, leftHandVertex, rightHandVertex;
		
	private Animation idleAnim, walkAnim, windAnim, jumpAnim, fallAnim, swimAnim;
	
	public int idleStartTime = 0, randomOrbitSign = 1;
	
	public Player(AnimatedEntity animEntity, PlayState playState, TerrainTile terrain, MobStats stats) {
		
		super("Player", animEntity, terrain, stats, 7f);

		this.random = new Random();
		this.terrain = terrain;
		this.playState = playState;
		
		idleAnim = AnimationLoader.loadAnimation("animatedPlayerIdle", true);
		walkAnim = AnimationLoader.loadAnimation("animatedPlayerWalk", true);
		windAnim = AnimationLoader.loadAnimation("animatedPlayerWind", false);
		jumpAnim = AnimationLoader.loadAnimation("animatedPlayerJump", false);
		fallAnim = AnimationLoader.loadAnimation("animatedPlayerFall", false);
		swimAnim = AnimationLoader.loadAnimation("animatedPlayerSwim", true);
		
		leftHipVertex = calcAttachmentVertex(new Vector3f(0.5f, 4f, 0f));
		rightHipVertex = calcAttachmentVertex(new Vector3f(-0.5f, 4f, 0f));
		leftBackVertex = calcAttachmentVertex(new Vector3f(1f, 6.5f, -0.5f));
		rightBackVertex = calcAttachmentVertex(new Vector3f(-1f, 6.5f, -0.5f));
		leftHandVertex = calcAttachmentVertex(new Vector3f(5.5f, 8f, 0f));
		rightHandVertex = calcAttachmentVertex(new Vector3f(-5.5f, 8f, 0f));
	}

	private boolean windingJump = false;
	private long startWindTime = 0;

	public void update() {

		super.update();
		
		//move(terrain);

		if(inMotion && !inAir && !inWater)
			getAnimModel().setAnimation(walkAnim);
		else if(windingJump)
			getAnimModel().setAnimation(windAnim);
		else if(inAir && jumping)
			getAnimModel().setAnimation(jumpAnim);
		else if(inAir && !jumping)
			getAnimModel().setAnimation(fallAnim);
		else if(inWater)
			getAnimModel().setAnimation(swimAnim);
		else
			getAnimModel().setAnimation(idleAnim);
		
		updateInputs();
		updateWieldables();
	}

//	private void move(TerrainTile terrain) {
//
//		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) && !inAir && !inWater && !windingJump) {
//
//			windingJump = true;
//
//			if(isInFirstPerson() && !inMotion)
//				height = PRE_JUMP_HEIGHT;
//
//			if(!inMotion)
//				getAnimModel().doAnimation(windAnim);
//		}
//
//		if(!windingJump)
//			height = NORM_HEIGHT;
//
//		float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
//
//		if(!Keyboard.isKeyDown(Keyboard.KEY_SPACE) && windingJump) {
//
//			jumping = true;
//			inAir = true;
//			windingJump = false;
//			getAnimModel().doAnimation(jumpAnim);
//			//yVel = getStats().getJumpPower();
//		}
//
//		//yVel += GRAVITY * DisplayMaster.getFrameTime();
//
//		if(this.getPosition().y < -NORM_HEIGHT * 0.95f)
//			yVel /= 2;
//
//		super.changePosition(0, yVel * DisplayMaster.getFrameTime(), 0);
//
//		if(this.getPosition().y < terrainHeight) {
//
//			yVel = 0;
//			//getPosition().y = terrainHeight;
//			inAir = false;
//			inWater = false;
//		}
//
//		if(this.getPosition().y < -NORM_HEIGHT * 0.8f) {
//
//			yVel = 0;
//			//getPosition().y = -NORM_HEIGHT * 0.9f;
//			inAir = false;
//			inWater = true;
//		}
//
//		if(yVel < 0 && inAir && jumping) {
//
//			jumping = false;
//			getAnimModel().doAnimation(fallAnim);
//		}
//
//		if((Mouse.isButtonDown(2) && !Keyboard.isKeyDown(Keyboard.KEY_C)) || firstPerson) {
//
//			float rot = -Mouse.getDX();
//			super.changeRotation(0, rot, 0);
//		}
//
//		if(getRotation().y > 360)
//			getRotation().y %= 360;
//
//		if(!isInMotion() && !Mouse.isButtonDown(2) && !isInFirstPerson()) {
//
//			System.out.println();
//
//			if(idleStartTime == 0) {
//
//				idleStartTime = (int) System.currentTimeMillis();
//				randomOrbitSign = random.nextBoolean() ? -1 : 1;
//			}
//
//		} else
//			idleStartTime = 0;
//	}

	boolean jumpKeyHeld = false;
	Vector3f moveAcceleration = new Vector3f();
	float yVelocity = 0f;

	//Updates all of the inputs for the player character such as movement, and interaction.	
	private void updateInputs() {
		
/*
		============== Movement Controls ==============
				
		For each seperate movement key, change the movement velocity accordingly to be able to move the player mob in the desired way.


TODO:	Add input control for Jump action, as well as a general function in BaseMob to make the entity jump.

TODO:	Replace all of the hardcoded Control Keys in the the movement to controls that are set through the settings and read from a custom 'settings' file.

*/
		float s = getStats().getSpeed() * 0.5f; //Easier way to refer to the speed stat

		//If forward or backward keys are pressed, accelerate the velocity on this axis by the speed
		//in which ever direction, otherwise set the velocity on this axis to zero.
		
		if(Keyboard.isKeyDown(Keyboard.KEY_W))
			moveAcceleration.z += s;
		else if(Keyboard.isKeyDown(Keyboard.KEY_S))
			moveAcceleration.z -= s;
		else if(moveAcceleration.z != 0)
			moveAcceleration.z *= 0.5f;
		
		//If left or right keys are pressed, accelerate the velocity on this axis by the speed
		//in which ever direction, otherwise set the velocity on this axis to zero.
		
		if(Keyboard.isKeyDown(Keyboard.KEY_A))
			moveAcceleration.x += s;
		else if(Keyboard.isKeyDown(Keyboard.KEY_D))
			moveAcceleration.x -= s;
		else if(moveAcceleration.x != 0)
			moveAcceleration.x *= 0.5f;
		
		//If jump key is pressed, accelerate the velocity on the vertical axis by the jump power;

		System.out.println((System.nanoTime() - startWindTime) / 1_000_000_000f);

		if(!isInAir() && !isInWater()) {

			if(jumping)
				return;

			if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {

				if(!windingJump) {
					startWindTime = System.nanoTime();
					windingJump = true;
				}

				if(System.nanoTime() - startWindTime >= WIND_MAX_TIME * 1_000_000_000f) {

					System.out.println("MAXIMUM JUMP POWER");
				}

			} else if(windingJump) {

				float windMultiplier = Math.min(Math.max(System.nanoTime() - startWindTime, WIND_MIN_TIME * 1_000_000_000f), WIND_MAX_TIME * 1_000_000_000f) / 1_000_000_000f * WIND_M + WIND_B;
				yVelocity = getStats().getJumpPower() * windMultiplier;
				setInAir(true);
				jumping = true;
				windingJump = false;

			} else {

				windingJump = false;
				jumping = false;
				startWindTime = System.nanoTime();
			}
		} else {

			jumping = false;
		}
		
		yVelocity -= PlayState.GRAVITY_CONST;
		
		super.changePosition(0, yVelocity * DisplayMaster.getFrameTime(), 0);
		super.moveWithVelocity(moveAcceleration); //Sends the desired move velocity to the super class (BaseMob) to move the entity.

/*
 		============== Camera Controls ==============
 		
 		Allows the user to manipulate the camera angles with orbit mode or normal player looking, can also zoom in and out as well as go into 1st or 3rd person.
 */
		
		if((Mouse.isButtonDown(2) && !Keyboard.isKeyDown(Keyboard.KEY_C)) || firstPerson) { //Normal Player Looking (Camera Movement locked to player rotation)

			float rot = -Mouse.getDX() * (firstPerson ? firstPersonSensitivity : thirdPersonSensitivity);
			super.changeRotation(0, rot, 0);
		}
		
		if(getRotation().y > 360)
			getRotation().y %= 360;

		if(!isInMotion() && !Mouse.isButtonDown(2) && !isInFirstPerson() && !windingJump && !jumping) { //Idle orbit camera after a while of inactivity
						
			if(idleStartTime == 0) {
				
				idleStartTime = (int) System.currentTimeMillis();
				randomOrbitSign = random.nextBoolean() ? -1 : 1;
			}
				
		} else
			idleStartTime = 0;
/*		
  		============== Hand Controls ==============
 
		Only one hand's item can be used at once, so if I'm swinging a sword with my left hand, I can't used my right hand
		this prevents the player from using a shield and being able to constantly attack at all times. Would be pretty over powered.
*/
	
		//Is there an item in the left hand?
		if(hand[0] != null) {
			
			//Is the opposite hand empty or not being used?
			if(hand[1] == null || !hand[1].isBeingUsed()) {
			
				if(Mouse.isButtonDown(0)) { //Left Click
				
					hand[0].use();
					hand[0].nowBeingUsed(true);
				} else if(hand[0].isBeingUsed()) {
				
					hand[0].nowBeingUsed(false);
				}
			}
		}
		
		//IS there an item in the right hand?
		if(hand[1] != null) {
			
			//Is the opposite hand empty or not being used?
			if(hand[0] == null || !hand[0].isBeingUsed()) {
				
				if(Mouse.isButtonDown(1)) { //Right Click
					
					hand[1].use();
					hand[1].nowBeingUsed(true);
				} else if(hand[1].isBeingUsed()) {
					
					hand[1].nowBeingUsed(false);
				}
			}
		}		
	}
	
	/* 
	 * TODO: Set Sheath Vertex Matrix after animation movements are applied
	 * 		 
	 *     - Find the vertex that is closest to desired sheath position and
	 *     store the id from the array.
	 *     
	 *     - Load the 'sheathMatrix' into item shader after calculating it
	 *     from the stored vertex's position and the sheatheRot. 
	 *     
	 * TODO: In itemVertexShader glsl file, calculate position of item in world
	 * 
	 *     - Multiply playerTransformationMatrix by the sheathVertexMatrix * pos
	 */

	int[] handKey = {Keyboard.KEY_Q, Keyboard.KEY_E};
	int[] handWieldableId = {-1, -1};
	boolean[] keyDown = {false, false};
	int[] lastUsed = {1, 0};

	private void updateWieldables() {

		for(int i = 0; i < hand.length; i++) {

			if(Keyboard.isKeyDown(handKey[i])) {

				if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) { // Sheathe

					if(handWieldableId[i] != -1) lastUsed[i] = handWieldableId[i];
					sheathItem(i, 1 - i);
					sheathItem(i, 3 - i);

				} else if(Keyboard.isKeyDown(Keyboard.KEY_BACK)) {

					if(wieldables[1 - i] != null && handWieldableId[i] == 1 - i) dropWieldable(i,1 - i);
					else if(wieldables[3 - i] != null && handWieldableId[i] == 3 - i) dropWieldable(i,3 - i);

				} else if(!keyDown[i]) {

					if (wieldables[1 - i] != null && wieldables[3 - i] != null) {

						if (hand[i] == null) {

							wieldItem(i, lastUsed[i]);
						} else {

							if (handWieldableId[i] == 1 - i) {

								sheathItem(i, 1 - i);
								wieldItem(i, 3 - i);
							} else {

								sheathItem(i, 3 - i);
								wieldItem(i, 1 - i);
							}
						}
					} else if (wieldables[1 - i] != null && wieldables[3 - i] == null) {

						if (hand[i] == null) {

							wieldItem(i, 1 - i);
						}
					} else if (wieldables[1 - i] == null && wieldables[3 - i] != null) {

						if (hand[i] == null) {

							wieldItem(i, 3 - i);
						}
					}

					keyDown[i] = true;
				}
			} else {

				keyDown[i] = false;
			}
		}
	}

	private void wieldItem(int handId, int wieldableId) {

		WieldableItem item = wieldables[wieldableId];

		if(item == null)
			return;

		item.setSheathed(false);
		hand[handId] = item;
		handWieldableId[handId] = wieldableId;
	}

	private void sheathItem(int handId, int wieldableId) {

		WieldableItem item = wieldables[wieldableId];
		hand[handId] = null;
		handWieldableId[handId] = -1;

		if(item == null)
			return;

		item.setSheathed(true);
	}

	public boolean grabNewWieldable(WieldableItem newItem) { // Picks up item and puts it in the first available slot, if they are all full, you can't pick the item up

		for (int i = 0; i < wieldables.length; i++) {
			
			if (wieldables[i] == null && newItem.getStats().getAvailableSlots()[i]) {

				wieldables[i] = newItem;
				wieldables[i].setSheathed(true);
				playState.entities.remove(newItem.getEntity());
				playState.items.add(newItem);
				
				Vector3f sheathPos = i >= 2 ? Handler.MultiplyVector3f(newItem.getBackSheathPos(), new Vector3f(-((i - 2) * 2 - 1), 1, 1)) : Handler.MultiplyVector3f(newItem.getHipSheathPos(), new Vector3f(-(i * 2 - 1), 1, 1));
				Vector3f sheathRot = i >= 2 ? Handler.MultiplyVector3f(newItem.getBackSheathRot(), new Vector3f(1, -((i - 2) * 2 - 1), 1)) : newItem.doesHipSheathFlip180() ? Handler.AddVector3f(newItem.getHipSheathRot(), new Vector3f(0, -(180 + newItem.getHipSheathRot().y * 2) * (i % 2), 0)) : Handler.MultiplyVector3f(newItem.getHipSheathRot(), new Vector3f(1, -(i * 2 - 1), 1));
				newItem.setSheathPos(sheathPos);
				newItem.setSheathRot(sheathRot);
				newItem.setSheathVertexID((int) (i >= 2 ? (i == 2 ? rightBackVertex[4] : leftBackVertex[4]) : (i == 0 ? leftHipVertex[4] : rightHipVertex[4])));
								
				Vector3f wieldPos = Handler.MultiplyVector3f(newItem.getHandWieldPos(), new Vector3f(i % 2 * 2 - 1, 1, 1));
				Vector3f wieldRot = newItem.doesWieldFlip180() ? Handler.AddVector3f(newItem.getHandWieldRot(), new Vector3f(0, -(180 + newItem.getHandWieldRot().y * 2) * ((i + 1) % 2), 0)) : Handler.MultiplyVector3f(newItem.getHandWieldRot(), new Vector3f(1, (i % 2 * 2 - 1), 1));					
				newItem.setWieldPos(wieldPos);
				newItem.setWieldRot(wieldRot);
				newItem.setWieldVertexID((int) (i % 2 == 0 ? leftHandVertex[4] : rightHandVertex[4]));
				
				return true;
			}
		}

		return false;
	}
	
	private void dropWieldable(int handId, int wieldableId) {
		
		WieldableItem item = wieldables[wieldableId];
		item.setSheathed(false);
		item.setSheathPos(null);
		item.setSheathRot(null);
		item.setSheathVertexID(0);
		item.setWieldPos(null);
		item.setWieldRot(null);
		item.setWieldVertexID(0);

		hand[handId] = null;
		handWieldableId[handId] = -1;
		wieldables[wieldableId] = null;
		playState.items.remove(item);

		Entity entity = item.getLinkedEntity();
		entity.setPosition(new Vector3f(getPosition().x, terrain.getHeightOfTerrain(getPosition().x, getPosition().z) + 0.5f, getPosition().z));
		entity.setRotation(item.getOnGroundRot());
		
		InteractionEvent ie = (InteractionEvent) entity.getEvents().get(0);
		ie.setEnabled(true);
		
		playState.entities.add(entity);
	}
		
	public void useWieldable(WieldableType type, WieldableItem item) {

		WieldableStats stats = item.getStats();
		
		switch(type) {
		
			case JAB:
			
				System.out.println("You jabbed with your " + item.getName());
				
				for(BaseMob mob : playState.baseMobs) {
										
					float distance = Handler.DistanceVector3f(mob.getPosition(), getPosition());
					
					if(distance <= stats.getReach()) {
						
						float angleToMob = (float) Math.toDegrees(Math.atan2(mob.getPosition().x - getPosition().x, mob.getPosition().z - getPosition().z));
						
						if(Math.abs(getRotation().y - angleToMob) <= 5f) {
							
							mob.hurt(stats.getActionValue());
							
							System.out.println(mob.getStats().getHealth() + "/" + mob.getStats().getMaxHealth());
						}
					}
				}
				
				break;
				
			case SWING:
				
				System.out.println("You swung with your " + item.getName());
				
				for(BaseMob mob : playState.baseMobs) {
					
					float distance = Handler.DistanceVector3f(mob.getPosition(), getPosition());
					
					if(distance <= stats.getReach()) {
						
						float angleToMob = (float) Math.toDegrees(Math.atan2(mob.getPosition().x - getPosition().x, mob.getPosition().z - getPosition().z));
					
						float angleDiff = Math.abs(getRotation().y - angleToMob);
						
						float calc = -0.0045f * ((angleDiff - 30f) * (angleDiff - 30f) * (angleDiff - 30f)) + 18f;
						float dmgFactor = Handler.clampFloat(0, 100, calc) / 100;
						
						//System.out.println(dmgFactor + " : " + calc + " : " + angleDiff);
						
						mob.hurt((int) ((float) stats.getActionValue() * (float) dmgFactor));
					}
				}
				
				break;
				
			case BLOCK:
				
				System.out.println("You blocked with your " + item.getName());
				
				getStats().setDefense(getStats().getBaseDefense() + item.getStats().getActionValue());
				
				break;
		}		
	}
	
	public float[] calcAttachmentVertex(Vector3f vertexPos) {
		
		float[] v = getModel().getRawModel().getMeshData().getVertices();
		float[] closestVertex = {0, 0, 0, Float.MAX_VALUE, 0};
		//Stored as: {x, y, z, distance, id}
		
		for(int j = 0; j < v.length; j += 3) {

			float d = (v[j] - vertexPos.x) * (v[j] - vertexPos.x) + (v[j + 1] - vertexPos.y) * (v[j + 1] - vertexPos.y) + (v[j + 2] - vertexPos.z) * (v[j + 2] - vertexPos.z);
			
			if(d < closestVertex[3]) {
				
				closestVertex[0] = v[j];
				closestVertex[1] = v[j + 1];
				closestVertex[2] = v[j + 2];
				closestVertex[3] = d;
				closestVertex[4] = j / 3;
			}
		}
		
		//System.out.println("<Player.java : 524> Vertex (" + closestVertex[4] + ") " + closestVertex[0] + ", " + closestVertex[1] + ", " + closestVertex[2]);
		return closestVertex;
	}

//	private void oldMove(Terrain terrain) {
//				
//		if(swimming && currMaxSpeed == getStats().getSpeed()) {
//			
//			lerpedSpeed *= SWIM_SPEED_FACTOR;
//			currSpeed *= SWIM_SPEED_FACTOR;
//			lerpedStrafeSpeed *= SWIM_SPEED_FACTOR;
//			currStrafeSpeed *= SWIM_SPEED_FACTOR;
//		}
//		
//		currMaxSpeed = swimming == true ? (getStats().getSpeed() * SWIM_SPEED_FACTOR) : getStats().getSpeed();
//				
//		if(Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_S)) {
//			
//			if(lerpedSpeed < currMaxSpeed) {
//	        	 
//	        	 lerpedSpeed += ACCELERATION;
//	         }
//			
//			if(Keyboard.isKeyDown(Keyboard.KEY_W))
//				currSpeed = lerpedSpeed;
//			else if(Keyboard.isKeyDown(Keyboard.KEY_S))
//				currSpeed = -lerpedSpeed;
//		} else {
//			
//			if(lerpedSpeed > DECELERATION && Math.abs(currSpeed) > DECELERATION) {
//	        	 
//	        	 lerpedSpeed -= DECELERATION;
//	        	 currSpeed = (currSpeed / Math.abs(currSpeed)) * lerpedSpeed;
//	         } else {
//	        	 
//	        	 lerpedSpeed = 0;
//	        	 currSpeed = 0;
//	         }	
//		}
//	       
//		if(Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_D)) {
//			
//			if(lerpedStrafeSpeed < currMaxSpeed) {
//	        	 
//	        	 lerpedStrafeSpeed += ACCELERATION;
//	         }
//			
//			if(Keyboard.isKeyDown(Keyboard.KEY_A))
//				currStrafeSpeed = lerpedStrafeSpeed;
//			else if(Keyboard.isKeyDown(Keyboard.KEY_D))
//				currStrafeSpeed = -lerpedStrafeSpeed;
//		} else {
//			
//			if(lerpedStrafeSpeed > DECELERATION && Math.abs(currStrafeSpeed) > DECELERATION) {
//	        	 
//	        	 lerpedStrafeSpeed -= DECELERATION;
//	        	 currStrafeSpeed = (currStrafeSpeed / Math.abs(currStrafeSpeed)) * lerpedStrafeSpeed;
//	         } else {
//	        	 
//	        	 lerpedStrafeSpeed = 0;
//	        	 currStrafeSpeed = 0;
//	         }
//		}
//		 
//		 if(Mouse.isButtonDown(1) || firstPerson) {
//			 
//			 float rot = -Mouse.getDX();
//			 super.changeRotation(0, rot, 0);
//		 }
//		 
//		 if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
//			 
//			 jump();
//		 }
//		 
//		 float distance = currSpeed * DisplayMaster.getFrameTimeSeconds();
//			float strafeDistance = currStrafeSpeed * DisplayMaster.getFrameTimeSeconds();
//			float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotation().y))) + (float) (strafeDistance * Math.sin(Math.toRadians(super.getRotation().y + 90f)));
//			float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotation().y))) + (float) (strafeDistance * Math.cos(Math.toRadians(super.getRotation().y + 90f)));
//			
//			float pseudospeed = (float) Math.sqrt(dx * dx + dz * dz);
//			
//			if(pseudospeed > (getStats().getSpeed() * DisplayMaster.getFrameTimeSeconds())) {
//				
//				super.changePosition(dx * (DIAGONAL_SPEED / getStats().getSpeed()), 0, dz * (DIAGONAL_SPEED / getStats().getSpeed()));
//				
//			} else {
//				
//				super.changePosition(dx, 0, dz);
//			}
//
//			upwardSpeed += GRAVITY * DisplayMaster.getFrameTimeSeconds();
//
//			if(super.getPosition().y < -HEIGHT * 0.75f)
//				upwardSpeed /= 2;
//			
//			super.changePosition(0, upwardSpeed * DisplayMaster.getFrameTimeSeconds(), 0);
//			
//			float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
//
//			if(super.getPosition().y < terrainHeight) {
//				
//				upwardSpeed = 0;
//				isInAir = false;
//				swimming = false;
//				super.getPosition().y = terrainHeight;
//			}
//			
//			if(super.getPosition().y < -HEIGHT * 0.75f) {
//					
//				upwardSpeed = 0;
//				swimming = true;
//				super.getPosition().y = -HEIGHT * 0.75f;
//					
//			}	
//	}

//	private void jump() {
//		
//		if(!isInAir && !swimming) {
//			
//			this.upwardSpeed = JUMP_POWER;
//			isInAir = true;
//		}
//	}

	// GETTERS

	public boolean isInFirstPerson() {
		return firstPerson;
	}
	
	public TerrainTile getTerrain() {
		
		return terrain;
	}
	
	// SETTERS

	public void goIntoFirstPerson(boolean val) {
		firstPerson = val;
	}
}
