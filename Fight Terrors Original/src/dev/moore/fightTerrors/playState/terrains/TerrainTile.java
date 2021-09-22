package dev.moore.fightTerrors.playState.terrains;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import dev.moore.fightTerrors.general.Handler;
import dev.moore.fightTerrors.general.Loader;
import dev.moore.fightTerrors.playState.models.base.RawModel;

public class TerrainTile {

	private float size;
	private float x, z;
	private RawModel model;
	private TerrainTexturePack texturePack;
	private TerrainTexture blendMap;
	private float[][] heights;

	private TerrainGenerator generator;

	public TerrainTile(int gridX, int gridZ, Loader loader, TerrainTexturePack texturePack, TerrainTexture blendMap,
			float size) {

		this.texturePack = texturePack;
		this.blendMap = blendMap;
		this.x = gridX * size;
		this.z = gridZ * size;
		this.size = size;
		this.model = generateTerrain(loader);
	}

	private RawModel generateTerrain(Loader loader) {

		generator = new TerrainGenerator();

		int VERTEX_COUNT = 128;

		heights = new float[VERTEX_COUNT][VERTEX_COUNT];
		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count * 2];
		int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];
		int vertexPointer = 0;

		for (int i = 0; i < VERTEX_COUNT; i++) {
			for (int j = 0; j < VERTEX_COUNT; j++) {

				float height = getHeight(j, i, generator);
				heights[j][i] = height;

				vertices[vertexPointer * 3] = (float) j / ((float) VERTEX_COUNT - 1) * size;
				vertices[vertexPointer * 3 + 1] = height;
				vertices[vertexPointer * 3 + 2] = (float) i / ((float) VERTEX_COUNT - 1) * size;
				Vector3f normal = calculateNormal(j, i, generator);
				normals[vertexPointer * 3] = normal.x;
				normals[vertexPointer * 3 + 1] = normal.y;
				normals[vertexPointer * 3 + 2] = normal.z;
				textureCoords[vertexPointer * 2] = (float) j / ((float) VERTEX_COUNT - 1);
				textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) VERTEX_COUNT - 1);
				vertexPointer++;
			}
		}

		int pointer = 0;

		for (int gz = 0; gz < VERTEX_COUNT - 1; gz++) {
			for (int gx = 0; gx < VERTEX_COUNT - 1; gx++) {

				int topLeft = (gz * VERTEX_COUNT) + gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}

		return loader.loadToVAO(vertices, textureCoords, normals, indices);
	}

	private Vector3f calculateNormal(int x, int z, TerrainGenerator generator) {

		float heightL = getHeight(x - 1, z, generator);
		float heightR = getHeight(x + 1, z, generator);
		float heightD = getHeight(x, z - 1, generator);
		float heightU = getHeight(x, z + 1, generator);

		Vector3f normal = new Vector3f(heightL - heightR, 1f, heightD - heightU);
		normal.normalise();

		return normal;
	}

	private float getHeight(int x, int z, TerrainGenerator generator) {

		return generator.generateHeight(x, z);
	}

	// GETTERS

	public float getSize() {

		return size;
	}

	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}

	public RawModel getModel() {
		return model;
	}

	public TerrainTexturePack getTexturePack() {
		return texturePack;
	}

	public TerrainTexture getBlendMap() {
		return blendMap;
	}

	public float getHeightOfTerrain(float worldX, float worldZ) {

		float terrainX = worldX - this.x;
		float terrainZ = worldZ - this.z;
		float gridSquareSize = size / ((float) heights.length - 1);
		int gridX = (int) Math.floor(terrainX / gridSquareSize);
		int gridZ = (int) Math.floor(terrainZ / gridSquareSize);

		if (gridX < 0 || gridX >= heights.length - 1 || gridZ < 0 || gridZ >= heights.length - 1) {

			return 0;
		}

		float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
		float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;

		float answer;

		if (xCoord <= (1 - zCoord))
			answer = Handler.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0),
					new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(0, heights[gridX][gridZ + 1], 1),
					new Vector2f(xCoord, zCoord));
		else
			answer = Handler.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0),
					new Vector3f(1, heights[gridX + 1][gridZ + 1], 1), new Vector3f(0, heights[gridX][gridZ + 1], 1),
					new Vector2f(xCoord, zCoord));

		return answer;
	}
}
