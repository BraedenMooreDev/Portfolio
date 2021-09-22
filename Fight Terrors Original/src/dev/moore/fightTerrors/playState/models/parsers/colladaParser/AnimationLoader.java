package dev.moore.fightTerrors.playState.models.parsers.colladaParser;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import dev.moore.fightTerrors.playState.models.animated.Animation;
import dev.moore.fightTerrors.playState.models.animated.JointTransform;
import dev.moore.fightTerrors.playState.models.animated.KeyFrame;
import dev.moore.fightTerrors.playState.models.animated.Quaternion;
import dev.moore.fightTerrors.playState.models.parsers.colladaParser.colladaLoader.ColladaLoader;
import dev.moore.fightTerrors.playState.models.parsers.colladaParser.dataStructures.AnimationData;
import dev.moore.fightTerrors.playState.models.parsers.colladaParser.dataStructures.JointTransformData;
import dev.moore.fightTerrors.playState.models.parsers.colladaParser.dataStructures.KeyFrameData;

/**
 * This class loads up an animation collada file, gets the information from it,
 * and then creates and returns an {@link Animation} from the extracted data.
 * 
 * @author Karl
 *
 */
public class AnimationLoader {

	/**
	 * Loads up a collada animation file, and returns and animation created from the
	 * extracted animation data from the file.
	 * 
	 * @param colladaFileName - the collada file containing data about the desired
	 *                    animation.
	 * @return The animation made from the data in the file.
	 */
	public static Animation loadAnimation(String colladaFileName, boolean shouldLoop) {

		AnimationData animationData = ColladaLoader.loadColladaAnimation(colladaFileName);
		KeyFrame[] frames = new KeyFrame[animationData.keyFrames.length];
		for (int i = 0; i < frames.length; i++) {
			frames[i] = createKeyFrame(animationData.keyFrames[i]);
		}
		return new Animation(colladaFileName.replace("animatedPlayer", ""), animationData.lengthSeconds, frames, shouldLoop);
	}

	/**
	 * Creates a keyframe from the data extracted from the collada file.
	 * 
	 * @param data - the data about the keyframe that was extracted from the collada
	 *             file.
	 * @return The keyframe.
	 */
	private static KeyFrame createKeyFrame(KeyFrameData data) {
		Map<String, JointTransform> map = new HashMap<String, JointTransform>();
		for (JointTransformData jointData : data.jointTransforms) {
			JointTransform jointTransform = createTransform(jointData);
			map.put(jointData.jointNameId, jointTransform);
		}
		return new KeyFrame(data.time, map);
	}

	/**
	 * Creates a joint transform from the data extracted from the collada file.
	 * 
	 * @param data - the data from the collada file.
	 * @return The joint transform.
	 */
	private static JointTransform createTransform(JointTransformData data) {
		Matrix4f mat = data.jointLocalTransform;
		Vector3f translation = new Vector3f(mat.m30, mat.m31, mat.m32);
		Quaternion rotation = Quaternion.fromMatrix(mat);
		return new JointTransform(translation, rotation);
	}

}