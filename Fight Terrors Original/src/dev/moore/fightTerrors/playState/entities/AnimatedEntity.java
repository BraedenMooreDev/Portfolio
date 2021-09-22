package dev.moore.fightTerrors.playState.entities;

import org.lwjgl.util.vector.Vector3f;

import dev.moore.fightTerrors.playState.models.animated.AnimatedModel;

public class AnimatedEntity extends Entity {

	private AnimatedModel animModel;
	
	public AnimatedEntity(AnimatedModel animModel, Vector3f position, Vector3f rotation, float scale, float broadRange) {
		
		super(animModel.getTexturedModel(), position, rotation, scale, broadRange);
		
		this.animModel = animModel;
	}
	
	public void update() {
		
		super.update();
		animModel.update();
	}
	
	// GETTERS
	
	public AnimatedModel getAnimModel() { return animModel; }
}
