package dev.moore.fightTerrors.playState.items;

import org.lwjgl.util.vector.Vector3f;

import dev.moore.fightTerrors.playState.PlayState;
import dev.moore.fightTerrors.playState.entities.Entity;

public class Item {

	public static final int STACK_MAX = 100;

	protected PlayState playState;
	protected Entity linkedEntity;

	protected String name;

	protected int sheathVertexID;
	protected Vector3f sheathPos, sheathRot;

	protected int wieldVertexID;
	protected Vector3f wieldPos, wieldRot;
	
	protected boolean sheathed;
	
	protected Vector3f onGroundRot;
	
	public Item(PlayState playState, Entity linkedEntity, String name) {

		this.playState = playState;
		this.linkedEntity = linkedEntity;
		this.name = name;
	}

	// SETTERS
	
	public void setSheathVertexID(int id) {
		
		this.sheathVertexID = id;
	}
	
	public void setSheathPos(Vector3f pos) {
		
		this.sheathPos = pos;
	}
	
	public void setSheathRot(Vector3f rot) {
		
		this.sheathRot = rot;
	}
	
	public void setWieldVertexID(int id) {
		
		this.wieldVertexID = id;
	}
	
	public void setWieldPos(Vector3f pos) {
		
		this.wieldPos = pos;
	}
	
	public void setWieldRot(Vector3f rot) {
		
		this.wieldRot = rot;
	}
	
	public void setSheathed(boolean sheathed) {
		
		this.sheathed = sheathed;
	}
	
	public void setOnGroundRot(Vector3f rot) {
		
		this.onGroundRot = rot;
	}
	
	// GETTERS

	public String getName() {
		return name;
	}
	
	public Entity getEntity() {
		
		return linkedEntity;
	}

	public int getSheathVertexID() {
		
		return sheathVertexID;
	}
	
	public Vector3f getSheathPos() {
		
		return sheathPos;
	}
	
	public Vector3f getSheathRot() {
		
		return sheathRot;
	}

	public int getWieldVertexID() {
		
		return wieldVertexID;
	}
	
	public Vector3f getWieldPos() {
		
		return wieldPos;
	}
	
	public Vector3f getWieldRot() {
		
		return wieldRot;
	}
	
	public boolean isSheathed() {
		
		return sheathed;
	}
	
	public Vector3f getOnGroundRot() {
		
		return onGroundRot;
	}
}
