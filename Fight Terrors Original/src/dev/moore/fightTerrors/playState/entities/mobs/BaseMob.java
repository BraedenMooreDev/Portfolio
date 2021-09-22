package dev.moore.fightTerrors.playState.entities.mobs;

import org.lwjgl.util.vector.Vector3f;

import dev.moore.fightTerrors.general.DisplayMaster;
import dev.moore.fightTerrors.general.Handler;
import dev.moore.fightTerrors.playState.PlayState;
import dev.moore.fightTerrors.playState.entities.AnimatedEntity;
import dev.moore.fightTerrors.playState.terrains.TerrainTile;

public class BaseMob extends AnimatedEntity { //Should actually be an animatedEntity subclass, but to keep it simple for now, I'm using a static entity.

	private String name;
	private TerrainTile terrain;
	private MobStats stats;
	
	private float height;
	
	protected boolean inMotion, inWater, inAir;
	
	public BaseMob(String name, AnimatedEntity animEntity, TerrainTile terrain, MobStats stats, float height) {
		super(animEntity.getAnimModel(), animEntity.getPosition(), animEntity.getRotation(), animEntity.getScale(), animEntity.getBroadRange());
		
		this.name = name;
		this.terrain = terrain;
		this.stats = stats;
		this.height = height;
	} 
	
	public void update() {
		
		super.update();
		
		updateMoveState(terrain);
	}
	
	//Updates the variables such as whether the player is in motion, air or water.
	
	private void updateMoveState(TerrainTile terrain) {
	
		float terrainHeight = terrain.getHeightOfTerrain(getPosition().x, getPosition().z);

		if(getVelocity().length() <= 0.01f)
			inMotion = false;
		else
			inMotion = true;

		if(getPosition().y <= terrainHeight)
			inAir = false;

		if(getPosition().y <= -height * 0.9f)
			inWater = true;
		else
			inWater = false;
	}
	
	public void moveWithVelocity(Vector3f velocity) {
					
		float terrainHeight = terrain.getHeightOfTerrain(getPosition().x, getPosition().z);
		
		Vector3f deltaV = new Vector3f();
		float ft = DisplayMaster.getFrameTime(); //Smaller and faster way to access

		if(isInWater())
			getPosition().y = -height * 0.9f;
		if(getPosition().y < terrainHeight)
			getPosition().y = terrainHeight;
		
		velocity.x = Handler.clampFloat(-getStats().getSpeed(), getStats().getSpeed(), velocity.x);
		velocity.z = Handler.clampFloat(-getStats().getSpeed(), getStats().getSpeed(), velocity.z);
		
		float fowardM = velocity.z * ft; //Forward movement
		float strafeM = velocity.x * ft; //Strafe (Sideways) movement
		deltaV.x = (float) (fowardM * Math.sin(Math.toRadians(super.getRotation().y))) + (float) (strafeM * Math.sin(Math.toRadians(super.getRotation().y + 90f)));
		deltaV.z = (float) (fowardM * Math.cos(Math.toRadians(super.getRotation().y))) + (float) (strafeM * Math.cos(Math.toRadians(super.getRotation().y + 90f)));

		deltaV.x = Math.max(-getStats().getSpeed(), Math.min(getStats().getSpeed(), deltaV.x));
		deltaV.z = Math.max(-getStats().getSpeed(), Math.min(getStats().getSpeed(), deltaV.z));

		if (Math.sqrt(deltaV.x * deltaV.x + deltaV.z * deltaV.z) > getStats().getSpeed() * ft) {

			final float DIAGONAL_CONST = getStats().getSpeed() / (float) Math.sqrt(2);

			deltaV.x = Math.max(-getStats().getSpeed(), Math.min(DIAGONAL_CONST, deltaV.x));
			deltaV.z = Math.max(-getStats().getSpeed(), Math.min(DIAGONAL_CONST, deltaV.z));
		}
		
		super.changeVelocity(deltaV.x, 0f, deltaV.z);
	}
	
	public void hurt(int value) {
				
		int result = getStats().getHealth() - value;
		
		if(result <= 0)
			System.out.println(name + " has died");
		
		stats.setHealth(result);
	}
	
	public void heal(int value) {
		
		int result = getStats().getHealth() + value;
		
		result = Math.min(result, getStats().getMaxHealth());
		
		stats.setHealth(result);
	}
	
	//GETTERS
	
	public String getName() { return name; }
	public MobStats getStats() { return stats; }
	
	public boolean isInMotion() { return inMotion; }
	public boolean isInWater() { return inWater; }
	public boolean isInAir() { return inAir; }
	
	//SETTERS
	
	public void setInMotion(boolean a) { inMotion = a; }
	public void setInWater(boolean a) { inWater = a;}
	public void setInAir(boolean a) { inAir = a; }
}
