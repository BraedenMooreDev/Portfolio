package dev.moore.fightTerrors.playState.models.parsers.colladaParser.colladaLoader;

import dev.moore.fightTerrors.playState.models.animated.MeshData;
import dev.moore.fightTerrors.playState.models.parsers.colladaParser.dataStructures.AnimatedModelData;
import dev.moore.fightTerrors.playState.models.parsers.colladaParser.dataStructures.AnimationData;
import dev.moore.fightTerrors.playState.models.parsers.colladaParser.dataStructures.SkeletonData;
import dev.moore.fightTerrors.playState.models.parsers.colladaParser.dataStructures.SkinningData;
import dev.moore.fightTerrors.playState.models.parsers.colladaParser.xmlParser.XmlNode;
import dev.moore.fightTerrors.playState.models.parsers.colladaParser.xmlParser.XmlParser;

public class ColladaLoader {

	public static AnimatedModelData loadColladaModel(String colladaFileName, int maxWeights) {
		XmlNode node = XmlParser.loadXmlFile(colladaFileName);

		SkinLoader skinLoader = new SkinLoader(node.getChild("library_controllers"), maxWeights);
		SkinningData skinningData = skinLoader.extractSkinData();

		SkeletonLoader jointsLoader = new SkeletonLoader(node.getChild("library_visual_scenes"), skinningData.jointOrder);		
		SkeletonData jointsData = jointsLoader.extractBoneData();
		
		GeometryLoader g = new GeometryLoader(node.getChild("library_geometries"), skinningData.verticesSkinData);
		MeshData meshData = g.extractModelData();

		return new AnimatedModelData(meshData, jointsData);
	}

	public static AnimationData loadColladaAnimation(String colladaFileName) {
		XmlNode node = XmlParser.loadXmlFile(colladaFileName);
		XmlNode animNode = node.getChild("library_animations");
		XmlNode jointsNode = node.getChild("library_visual_scenes");
		AnimationFileLoader loader = new AnimationFileLoader(animNode, jointsNode);
		AnimationData animData = loader.extractAnimation();
		return animData;
	}

}
