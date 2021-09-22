package dev.moore.fightTerrors.playState.collision;

import org.lwjgl.util.vector.Vector3f;

public class CollisionData {

	public enum Type {
		NULL, FACE, VERTEX, EDGE
	}

	private Vector3f collisionPosition;
	private float collisionDistance;
	private Type type;

	public CollisionData(Vector3f position, float distance, Type type) {

		this.collisionPosition = position;
		this.collisionDistance = distance;
		this.type = type;
	}

	// SETTERS

	public void setPosition(Vector3f position) {
		this.collisionPosition = position;
	}

	public void setDistance(float distance) {
		this.collisionDistance = distance;
	}

	public void setType(Type type) {
		this.type = type;
	}

	// GETTERS

	public Vector3f getPosition() {
		return collisionPosition;
	}

	public float getDistance() {
		return collisionDistance;
	}

	public Type getType() {
		return type;
	}
}