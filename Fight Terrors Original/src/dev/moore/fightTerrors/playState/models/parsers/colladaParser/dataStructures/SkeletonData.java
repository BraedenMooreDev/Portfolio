package dev.moore.fightTerrors.playState.models.parsers.colladaParser.dataStructures;

public class SkeletonData {

	public final int jointCount;
	public final JointData headJoint;

	public SkeletonData(int jointCount, JointData headJoint) {
		this.jointCount = jointCount;
		this.headJoint = headJoint;
	}

}
