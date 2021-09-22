package dev.moore.fightTerrors.playState.items;

import org.lwjgl.util.vector.Vector3f;

import dev.moore.fightTerrors.playState.PlayState;
import dev.moore.fightTerrors.playState.entities.Entity;

public class WieldableItem extends Item {

	private Vector3f handWieldPos, handWieldRot, hipSheathPos, hipSheathRot, backSheathPos, backSheathRot;
	private boolean wield180Flip, hipSheath180Flip, backSheath180Flip;
	public boolean waitingRelease = false, beingUsed = false;
	private WieldableStats stats;
	long lastTimeUsed;
	
	public WieldableItem(PlayState playState, Entity linkedEntity, String name, Vector3f onGroundRot, Vector3f wieldPos, Vector3f wieldRot, boolean wield180Flip,
			Vector3f hipSheathPos, Vector3f hipSheathRot, boolean hipSheath180Flip, Vector3f backSheathPos, Vector3f backSheathRot, boolean backSheath180Flip, WieldableStats stats) {
		super(playState, linkedEntity, name);

		this.setOnGroundRot(onGroundRot);
		this.handWieldPos = wieldPos;
		this.handWieldRot = wieldRot;
		this.wield180Flip = wield180Flip;
		this.hipSheathPos = hipSheathPos;
		this.hipSheathRot = hipSheathRot;
		this.hipSheath180Flip = hipSheath180Flip;
		this.backSheathPos = backSheathPos;
		this.backSheathRot = backSheathRot;
		this.backSheath180Flip = backSheath180Flip;
		this.stats = stats;
		
		lastTimeUsed = System.currentTimeMillis();
	}

	public void use() { //Used mainly for controlling the use time of the item.
		
		if(System.currentTimeMillis() - lastTimeUsed > getStats().getUseTime()) {
			
			used();
			lastTimeUsed = System.currentTimeMillis();
		}
	}
	
	public void used() {} //To Be Overrode with item's functionality.
			
	// GETTERS

	public Entity getLinkedEntity() {
		return linkedEntity;
	}

	public Vector3f getHandWieldPos() {
		return handWieldPos;
	}

	public Vector3f getHandWieldRot() {
		return handWieldRot;
	}
	
	public boolean doesWieldFlip180() {
		return wield180Flip;
	}

	public Vector3f getHipSheathPos() {
		return hipSheathPos;
	}

	public Vector3f getHipSheathRot() {
		return hipSheathRot;
	}
	
	public boolean doesHipSheathFlip180() {
		return hipSheath180Flip;
	}

	public Vector3f getBackSheathPos() {
		return backSheathPos;
	}

	public Vector3f getBackSheathRot() {
		return backSheathRot;
	}
	
	public boolean doesBackSheathFlip180() {
		return backSheath180Flip;
	}

	public WieldableStats getStats() {
		return stats;
	}
	
	public boolean isBeingUsed() { return beingUsed; }
	
	// SETTERS
	
	public void nowBeingUsed(boolean beingUsed) { this.beingUsed = beingUsed; }
}
