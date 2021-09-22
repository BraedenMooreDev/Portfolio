package dev.moore.fightTerrors.playState.entities;

import java.util.ArrayList;
import java.util.List;

import dev.moore.fightTerrors.general.Handler;
import org.lwjgl.util.vector.Vector3f;

import dev.moore.fightTerrors.playState.events.Event;
import dev.moore.fightTerrors.playState.models.base.TexturedModel;

public class Entity {

	private TexturedModel model;
	private Vector3f position, rotation, velocity;
	private float scale, broadRange;
	private List<Event> events = new ArrayList<Event>();

	private int textureIndex = 0;
	private boolean hovering;
	private float originalY = Integer.MAX_VALUE;

	public Entity(TexturedModel model, Vector3f position, Vector3f rotation, float scale, float broadRange) {

		this.model = model;
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		this.broadRange = broadRange;

		velocity = new Vector3f(0f, 0f, 0f);
	}

	public Entity(TexturedModel model, int textureIndex, Vector3f position, Vector3f rotation, float scale,
			float broadRange) {

		this.model = model;
		this.textureIndex = textureIndex;
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		this.broadRange = broadRange;

		velocity = new Vector3f(0f, 0f, 0f);
	}

	public void update() {

		for (Event e : events) {

			e.update();
		}

		position.x += velocity.x;
		position.y += velocity.y;
		position.z += velocity.z;

		if(isHovering()) {

			if (originalY == Integer.MAX_VALUE)
				originalY = position.y;

			position.y = Handler.lerpFloat(position.y, originalY + 2f, 0.1f, 0.1f);
			rotation.y += 1f;

		} else if(originalY != Integer.MAX_VALUE) {

			position.y = Handler.lerpFloat(position.y, originalY, 0.1f, 0.1f);
		}
	}

	public void addEvent(Event e) {

		e.linkEntity(this);
		events.add(e);
	}

	public void changeVelocity(float newX, float newY, float newZ) {

		this.velocity.x = newX;
		this.velocity.y = newY;
		this.velocity.z = newZ;
	}

	public void changePosition(float dx, float dy, float dz) {

		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;
	}

	public void changeRotation(float dx, float dy, float dz) {

		this.rotation.x += dx;
		this.rotation.y += dy;
		this.rotation.z += dz;
	}

	// GETTERS

	public List<Event> getEvents() {
		return events;
	}
	
	public TexturedModel getModel() {
		return model;
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public Vector3f getVelocity() {
		return velocity;
	}

	public float getScale() {
		return scale;
	}

	public float getBroadRange() {
		return broadRange;
	}

	public float getTextureXOffset() {
		return (float) ((int) (textureIndex % model.getTexture().getAtlasSize()))
				/ (float) (model.getTexture().getAtlasSize());
	}

	public float getTextureYOffset() {
		return (float) ((int) (textureIndex / model.getTexture().getAtlasSize()))
				/ (float) (model.getTexture().getAtlasSize());
	}

	public boolean isHovering() {
		return hovering;
	}

	// SETTERS

	public void setModel(TexturedModel model) {
		this.model = model;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}

	public void setVelocity(Vector3f velocity) {
		this.velocity = velocity;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public void isHovering(boolean state) { this.hovering = state; }
}