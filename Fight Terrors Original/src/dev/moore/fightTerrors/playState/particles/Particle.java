package dev.moore.fightTerrors.playState.particles;

import dev.moore.fightTerrors.playState.PlayState;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import dev.moore.fightTerrors.general.DisplayMaster;
import dev.moore.fightTerrors.playState.entities.Camera;
import dev.moore.fightTerrors.playState.entities.mobs.Player;

public class Particle {

	private Vector3f position, velocity;

	private float gravityEffect, lifeLength, rotation, scale, elapsedTime = 0, blend, distance;

	private ParticleTexture texture;

	private Vector2f texOffset1 = new Vector2f(), texOffset2 = new Vector2f();

	public Particle(ParticleTexture texture, Vector3f position, Vector3f velocity, float gravityEffect,
			float lifeLength, float rotation, float scale) {

		super();
		this.texture = texture;
		this.position = position;
		this.velocity = velocity;
		this.gravityEffect = gravityEffect;
		this.lifeLength = lifeLength;
		this.rotation = rotation;
		this.scale = scale;

		ParticleMaster.addParticle(this);
	}

	Vector3f change = new Vector3f();

	protected boolean update(Camera camera) {

		velocity.y += PlayState.GRAVITY_CONST * gravityEffect * DisplayMaster.getFrameTime();
		change.set(velocity);
		change.scale(DisplayMaster.getFrameTime());
		Vector3f.add(change, position, position);
		distance = Vector3f.sub(camera.getPosition(), position, null).lengthSquared();
		updateTextureCoordInfo();
		elapsedTime += DisplayMaster.getFrameTime();
		return elapsedTime < lifeLength;
	}

	private void updateTextureCoordInfo() {

		float lifeFactor = elapsedTime / lifeLength;
		int stageCount = texture.getNumberOfRows() * texture.getNumberOfRows();
		float atlasProgression = lifeFactor * stageCount;

		int index1 = (int) Math.floor(atlasProgression);
		int index2 = index1 < stageCount - 1 ? index1 + 1 : index1;

		this.blend = atlasProgression % 1;

		setTextureOffset(texOffset1, index1);
		setTextureOffset(texOffset2, index2);
	}

	private void setTextureOffset(Vector2f offset, int index) {

		int column = index % texture.getNumberOfRows();
		int row = index / texture.getNumberOfRows();

		offset.x = (float) column / texture.getNumberOfRows();
		offset.y = (float) row / texture.getNumberOfRows();
	}

	// GETTERS

	public float getDistance() {
		return distance;
	}

	public float getBlend() {
		return blend;
	}

	public Vector2f getTexOffset1() {
		return texOffset1;
	}

	public Vector2f getTexOffset2() {
		return texOffset2;
	}

	public ParticleTexture getTexture() {
		return texture;
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getRotation() {
		return rotation;
	}

	public float getScale() {
		return scale;
	}
}
