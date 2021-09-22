package dev.moore.fightTerrors.playState.models.base;

import dev.moore.fightTerrors.playState.collision.NarrowCollisionDetection;
import dev.moore.fightTerrors.playState.models.animated.MeshData;

public class RawModel {

	private int vaoID;
	private int vertexCount;
	private MeshData meshData;
	private ModelData modelData;
	private NarrowCollisionDetection[] colliders;

	public RawModel(int vaoID, int vertexCount) {

		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
	}

	// GETTERS

	public int getVaoID() {
		return vaoID;
	}

	public int getVertexCount() {
		return vertexCount;
	}
	
	public MeshData getMeshData() {
		return meshData;
	}

	public ModelData getModelData() {
		return modelData;
	}

	public NarrowCollisionDetection[] getColliders() {
		return colliders;
	}

	// SETTERS

	public void setData(MeshData data) {
		
		this.meshData = data;
	}
	
	public void setData(ModelData data) {
		this.modelData = data;
	}

	public void setColliders(NarrowCollisionDetection[] colliders) {
		this.colliders = colliders;
	}
}