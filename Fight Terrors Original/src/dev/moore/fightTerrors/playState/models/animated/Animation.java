package dev.moore.fightTerrors.playState.models.animated;

/**
 * 
 * * Represents an animation that can applied to an {@link AnimatedModel} . It
 * contains the length of the animation in seconds, and a list of
 * {@link KeyFrame}s.
 * 
 * @author Karl
 * 
 *
 */
public class Animation {

	private final String name;
	private final float length;// in seconds
	private final KeyFrame[] keyFrames;
	private final boolean loop;
	
	/**
	 * @param name			  - stores the name of the animation
	 * @param lengthInSeconds - the total length of the animation in seconds.
	 * @param frames          - all the keyframes for the animation, ordered by time
	 *                        of appearance in the animation.
	 * @param loop			  - defines whether the animation will loop while it is updated, or only play through once.
	 */
	public Animation(String name, float lengthInSeconds, KeyFrame[] frames, boolean loop) {
		this.name = name;
		this.keyFrames = frames;
		this.length = lengthInSeconds;
		this.loop = loop;
	}

	/**
	 * @return The name of the animation
	 */
	public String getName() { return name; }

	/**
	 * @return The length of the animation in seconds.
	 */
	public float getLength() {
		return length;
	}

	/**
	 * @return An array of the animation's keyframes. The array is ordered based on
	 *         the order of the keyframes in the animation (first keyframe of the
	 *         animation in array position 0).
	 */
	public KeyFrame[] getKeyFrames() {
		return keyFrames;
	}

	public boolean shouldLoop() { return loop; }

}
