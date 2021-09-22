package dev.moore.fightTerrors.playState.models.parsers.colladaParser;

import dev.moore.fightTerrors.general.Loader;
import dev.moore.fightTerrors.playState.models.animated.AnimatedModel;
import dev.moore.fightTerrors.playState.models.animated.Joint;
import dev.moore.fightTerrors.playState.models.animated.MeshData;
import dev.moore.fightTerrors.playState.models.base.ModelTexture;
import dev.moore.fightTerrors.playState.models.base.RawModel;
import dev.moore.fightTerrors.playState.models.base.TexturedModel;
import dev.moore.fightTerrors.playState.models.parsers.colladaParser.colladaLoader.ColladaLoader;
import dev.moore.fightTerrors.playState.models.parsers.colladaParser.dataStructures.AnimatedModelData;
import dev.moore.fightTerrors.playState.models.parsers.colladaParser.dataStructures.JointData;
import dev.moore.fightTerrors.playState.models.parsers.colladaParser.dataStructures.SkeletonData;

public class AnimatedModelLoader {

	/**
	 * Creates an AnimatedEntity from the data in an entity file. It loads up the
	 * collada model data, stores the extracted data in a VAO, sets up the joint
	 * heirarchy, and loads up the entity's texture.
	 * 
	 * @param modelFileName - the file containing the data for the model.
	 * @param textureFileName - the texture to be applied to the model.
	 * @return The animated entity (no animation applied though)
	 */
	public static AnimatedModel loadEntity(Loader loader, String modelFileName, String textureFileName) {
		
		AnimatedModelData entityData = ColladaLoader.loadColladaModel(modelFileName, 3);
		MeshData data = entityData.getMeshData();

		RawModel model = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getJointIds(), data.getVertexWeights(), data.getIndices());
		model.setData(data);
		ModelTexture texture = new ModelTexture(loader.loadTexture(textureFileName));		
		TexturedModel texModel = new TexturedModel(model, texture);
		
		SkeletonData skeletonData = entityData.getJointsData();
		Joint headJoint = createJoints(skeletonData.headJoint);
		return new AnimatedModel(texModel, headJoint, skeletonData.jointCount);
	}

	/**
	 * Constructs the joint-hierarchy skeleton from the data extracted from the
	 * collada file.
	 * 
	 * @param data - the joints data from the collada file for the head joint.
	 * @return The created joint, with all its descendants added.
	 */
	private static Joint createJoints(JointData data) {
				
		Joint joint = new Joint(data.index, data.nameId, data.bindLocalTransform);
		
		for (JointData child : data.children) {
			
			joint.addChild(createJoints(child));
		}
		
		return joint;
	}
}
