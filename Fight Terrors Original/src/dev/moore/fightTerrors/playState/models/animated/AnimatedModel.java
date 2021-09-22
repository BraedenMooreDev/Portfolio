package dev.moore.fightTerrors.playState.models.animated;

import org.lwjgl.util.vector.Matrix4f;

import dev.moore.fightTerrors.playState.models.base.TexturedModel;

/**
 * 
 * This class represents an entity in the world that can be animated. It
 * contains the model's VAO which contains the mesh data, the texture, and the
 * root joint of the joint hierarchy, or "skeleton". It also holds an int which
 * represents the number of joints that the model's skeleton contains, and has
 * its own {@link Animator} instance which can be used to apply animations to
 * this entity.
 * 
 * @author Karl
 *
 */
public class AnimatedModel {

	private final TexturedModel texturedModel;

	private final Joint rootJoint;
	private final int jointCount;

	private final Animator animator;

	public AnimatedModel(TexturedModel texturedModel, Joint rootJoint, int jointCount) {
		
		this.texturedModel = texturedModel;
		this.rootJoint = rootJoint;
		this.jointCount = jointCount;
		this.animator = new Animator(this);
		rootJoint.calcInverseBindTransform(new Matrix4f());
	}

	public void update() {
		
		animator.update();
	}

	public void doAnimation(Animation animation) {
		
		animator.doAnimation(animation);
	}

	public void setAnimation(Animation animation) {
		
		animator.setAnimation(animation);
	}
	
	public Matrix4f[] getJointTransforms() {
		
		Matrix4f[] jointMatrices = new Matrix4f[jointCount];
		addJointsToArray(rootJoint, jointMatrices);
		return jointMatrices;
	}

	private void addJointsToArray(Joint joint, Matrix4f[] jointMatrices) {
				
		jointMatrices[joint.index] = joint.getAnimatedTransform();
		
		for (Joint childJoint : joint.children) {
			
			addJointsToArray(childJoint, jointMatrices);
		}
	}

	// GETTERS
	
	public TexturedModel getTexturedModel() { return texturedModel; }
	
	public Joint getRootJoint() { return rootJoint; }
	
	public int getJointCount() { return jointCount; }
}
