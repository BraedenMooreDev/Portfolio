package dev.moore.fightTerrors.playState.collision;

import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Vector3f;

import dev.moore.fightTerrors.playState.collision.CollisionData.Type;

public class NarrowCollisionDetection {

	public Vector3f ellipsoidPositionIR, ellipsoidPositionIE, triangleP1R, triangleP2R, triangleP3R, triangleP1E,
			triangleP2E, triangleP3E, P2P1E, P2P3E, planeIntersectionPointR, planeIntersectionPointE, planeNormal,
			planeUnitNormal, velocityR, velocityE;

	private float planeConstantD, planeConstantP, distance, t0, t1;

	private Matrix3f toESpaceMatrix = new Matrix3f();

	private CollisionData vertexData, faceData, edgeData;

	public NarrowCollisionDetection(Vector3f position, Vector3f velocity, Vector3f P1, Vector3f P2, Vector3f P3,
			float ellipsoidMaxX, float ellipsoidMaxY, float ellipsoidMaxZ) {

		this.ellipsoidPositionIR = position;
		this.velocityR = velocity;
		this.triangleP1R = P1;
		this.triangleP2R = P2;
		this.triangleP3R = P3;

		this.toESpaceMatrix.m00 = (1 / ellipsoidMaxX);
		this.toESpaceMatrix.m11 = (1 / ellipsoidMaxY);
		this.toESpaceMatrix.m22 = (1 / ellipsoidMaxZ);
	}

	public void sendValuesToESpace() {

		triangleP1E = Matrix3f.transform(toESpaceMatrix, triangleP1R, triangleP1E);
		triangleP2E = Matrix3f.transform(toESpaceMatrix, triangleP2R, triangleP2E);
		triangleP3E = Matrix3f.transform(toESpaceMatrix, triangleP3R, triangleP3E);

		velocityE = Matrix3f.transform(toESpaceMatrix, velocityR, velocityE);
		ellipsoidPositionIE = Matrix3f.transform(toESpaceMatrix, ellipsoidPositionIR, ellipsoidPositionIE);
	}

	public void constructCollisionValues() {

		P2P1E = Vector3f.sub(triangleP1E, triangleP2E, P2P1E);
		P2P3E = Vector3f.sub(triangleP3E, triangleP2E, P2P3E);

		planeNormal = Vector3f.cross(P2P3E, P2P1E, planeNormal);
		planeUnitNormal = planeNormal.normalise(planeUnitNormal);

		planeConstantD = (planeNormal.x * triangleP2E.x * -1) + (planeNormal.y * triangleP2E.y * -1)
				+ (planeNormal.z * triangleP2E.z * -1);
		planeConstantP = (float) (planeConstantD / (Math.sqrt(
				(planeNormal.x * planeNormal.x) + (planeNormal.y * planeNormal.y) + (planeNormal.z * planeNormal.z))));

		distance = Vector3f.dot(planeUnitNormal, ellipsoidPositionIE) + planeConstantP;

		if (Vector3f.dot(planeUnitNormal, velocityE) == 0.0) {

			t0 = 0;
			t1 = 1;
		} else {

			try {

				t0 = (1 - distance) / (Vector3f.dot(planeUnitNormal, velocityE));
			} catch (IllegalArgumentException e) {

				t0 = 0;
				t1 = 1;
			}

			try {

				t1 = (-1 - distance) / (Vector3f.dot(planeUnitNormal, velocityE));
			} catch (IllegalArgumentException e) {

				t0 = 0;
				t1 = 1;
			}
		}
		if (t1 < t0) {

			float temp = t1;
			t1 = t0;
			t0 = temp;
		}
	}

	@SuppressWarnings("unused")
	public CollisionData detectCollision() {

		sendValuesToESpace();
		constructCollisionValues();

		if (Vector3f.dot(planeUnitNormal, velocityE) == 0) {

			if (Math.abs(distance) > 1) {

				return null;
			}
		} else if (((t0 < 0) || (t0 > 1)) && (t1 < 0 || t1 > 1)) {

			return null;
		}

		planeIntersectionPointE = Vector3f.sub(ellipsoidPositionIE, (Vector3f) planeUnitNormal,
				planeIntersectionPointE);
		Vector3f velocityExt0 = new Vector3f(velocityE.x * t0, velocityE.y * t0, velocityE.z * t0);
		planeIntersectionPointE = Vector3f.add(planeIntersectionPointE, velocityExt0, planeIntersectionPointE);

		if (checkPositionWithTriangle(planeIntersectionPointE, triangleP1E, triangleP2E, triangleP3E)) {

			faceData = new CollisionData(planeIntersectionPointE, distance, Type.FACE);
			return faceData;
		} else {

			Vector3f ellipsoidVertexDistance = new Vector3f(), vertexEllipsoidDistance = new Vector3f();

			float vertexTime1, vertexTime2, smallestSolutionVertex = 10005;

			Vector3f collisionPoint = new Vector3f();

			float a = velocityE.lengthSquared();

			ellipsoidVertexDistance = Vector3f.sub(ellipsoidPositionIE, triangleP1E, ellipsoidVertexDistance);

			float b = 2 * Vector3f.dot(velocityE, ellipsoidVertexDistance);

			vertexEllipsoidDistance = Vector3f.sub(triangleP1E, ellipsoidPositionIE, vertexEllipsoidDistance);

			float c = vertexEllipsoidDistance.lengthSquared() - 1;

			if ((b * b) - (4 * a * c) >= 0) {

				vertexTime1 = -b + (float) Math.sqrt((double) (b * b) - (4 * a * c)) / (2 * a);
				vertexTime2 = -b - (float) Math.sqrt((double) (b * b) - (4 * a * c)) / (2 * a);

				if ((vertexTime1 < vertexTime2) && (vertexTime1 <= t1) && (vertexTime1 >= t0)
						&& ((vertexTime1 < smallestSolutionVertex) || (smallestSolutionVertex == 10005))) {

					smallestSolutionVertex = vertexTime1;
					collisionPoint = triangleP1E;

				} else if ((vertexTime2 < vertexTime1) && (vertexTime1 <= t1) && (vertexTime2 >= t0)
						&& ((vertexTime2 < smallestSolutionVertex) || (smallestSolutionVertex == 10005))) {

					smallestSolutionVertex = vertexTime2;
					collisionPoint = triangleP1E;
				}
			}

			ellipsoidVertexDistance = Vector3f.sub(ellipsoidPositionIE, triangleP2E, ellipsoidVertexDistance);

			b = 2 * Vector3f.dot(velocityE, ellipsoidVertexDistance);

			vertexEllipsoidDistance = Vector3f.sub(triangleP2E, ellipsoidPositionIE, vertexEllipsoidDistance);

			c = vertexEllipsoidDistance.lengthSquared() - 1;

			if ((b * b) - (4 * a * c) >= 0) {

				vertexTime1 = -b + (float) Math.sqrt((double) (b * b) - (4 * a * c)) / (2 * a);
				vertexTime2 = -b - (float) Math.sqrt((double) (b * b) - (4 * a * c)) / (2 * a);

				if ((vertexTime1 < vertexTime2) && (vertexTime1 <= t1) && (vertexTime1 >= t0)
						&& ((vertexTime1 < smallestSolutionVertex) || (smallestSolutionVertex == 10005))) {

					smallestSolutionVertex = vertexTime1;
					collisionPoint = triangleP2E;

				} else if ((vertexTime2 < vertexTime1) && (vertexTime1 <= t1) && (vertexTime2 >= t0)
						&& ((vertexTime2 < smallestSolutionVertex) || (smallestSolutionVertex == 10005))) {

					smallestSolutionVertex = vertexTime2;
					collisionPoint = triangleP2E;
				}
			}

			ellipsoidVertexDistance = Vector3f.sub(ellipsoidPositionIE, triangleP3E, ellipsoidVertexDistance);

			b = 2 * Vector3f.dot(velocityE, ellipsoidVertexDistance);

			vertexEllipsoidDistance = Vector3f.sub(triangleP3E, ellipsoidPositionIE, vertexEllipsoidDistance);

			c = vertexEllipsoidDistance.lengthSquared() - 1;

			if ((b * b) - (4 * a * c) >= 0) {

				vertexTime1 = -b + (float) Math.sqrt((double) (b * b) - (4 * a * c)) / (2 * a);
				vertexTime2 = -b - (float) Math.sqrt((double) (b * b) - (4 * a * c)) / (2 * a);

				if ((vertexTime1 < vertexTime2) && (vertexTime1 <= t1) && (vertexTime1 >= t0)
						&& ((vertexTime1 < smallestSolutionVertex) || (smallestSolutionVertex == 10005))) {

					smallestSolutionVertex = vertexTime1;
					collisionPoint = triangleP3E;

				} else if ((vertexTime2 < vertexTime1) && (vertexTime1 <= t1) && (vertexTime2 >= t0)
						&& ((vertexTime2 < smallestSolutionVertex) || (smallestSolutionVertex == 10005))) {

					smallestSolutionVertex = vertexTime2;
					collisionPoint = triangleP3E;
				}
			}

			if (smallestSolutionVertex != 10005) {

				float vertexDistance = smallestSolutionVertex * velocityE.length();

				vertexData = new CollisionData(collisionPoint, vertexDistance, Type.VERTEX);
			}

			Vector3f edge = new Vector3f(), baseToVertex = new Vector3f(), edgeIntersectionPoint = new Vector3f(),
					fromEdgePoint = new Vector3f(), smallestEdge = new Vector3f();

			float intersectionDistance, edgeTime1, edgeTime2, smallestSolutionEdge = 10005, smallerSolutionEdge,
					smallerF = -1, smallestF = 0;

			edge = Vector3f.sub(triangleP2E, triangleP1E, edge);
			baseToVertex = Vector3f.sub(triangleP1E, ellipsoidPositionIE, baseToVertex);

			a = edge.lengthSquared() * -1 * velocityE.lengthSquared()
					+ (Vector3f.dot(edge, velocityE) * Vector3f.dot(edge, velocityE));
			b = edge.lengthSquared() * 2 * Vector3f.dot(velocityE, baseToVertex)
					- (2 * Vector3f.dot(edge, velocityE) * Vector3f.dot(edge, baseToVertex));
			c = edge.lengthSquared() * (1 - baseToVertex.lengthSquared())
					+ (Vector3f.dot(edge, baseToVertex) * Vector3f.dot(edge, baseToVertex));

			if ((b * b) - (4 * a * c) >= 0) {

				edgeTime1 = -b + (float) Math.sqrt((double) (b * b) - (4 * a * c)) / (2 * a);
				edgeTime2 = -b - (float) Math.sqrt((double) (b * b) - (4 * a * c)) / (2 * a);

				if (edgeTime1 <= edgeTime2) {

					smallerSolutionEdge = edgeTime1;
				} else {

					smallerSolutionEdge = edgeTime2;
				}

				if (smallerSolutionEdge >= 0 && smallerSolutionEdge <= 1) {

					smallerF = ((Vector3f.dot(edge, velocityE) * smallerSolutionEdge)
							- Vector3f.dot(edge, baseToVertex)) / edge.lengthSquared();
				}

				if (smallerF >= 0 && smallerF <= 1 && smallerSolutionEdge < smallestSolutionEdge) {

					smallestF = smallerF;
					smallestSolutionEdge = smallerSolutionEdge;
					fromEdgePoint = triangleP1E;
					smallestEdge = edge;
				}
			}

			smallerF = -1;

			edge = Vector3f.sub(triangleP3E, triangleP2E, edge);
			baseToVertex = Vector3f.sub(triangleP2E, ellipsoidPositionIE, baseToVertex);

			a = edge.lengthSquared() * -1 * velocityE.lengthSquared()
					+ (Vector3f.dot(edge, velocityE) * Vector3f.dot(edge, velocityE));
			b = edge.lengthSquared() * 2 * Vector3f.dot(velocityE, baseToVertex)
					- (2 * Vector3f.dot(edge, velocityE) * Vector3f.dot(edge, baseToVertex));
			c = edge.lengthSquared() * (1 - baseToVertex.lengthSquared())
					+ (Vector3f.dot(edge, baseToVertex) * Vector3f.dot(edge, baseToVertex));

			if ((b * b) - (4 * a * c) >= 0) {

				edgeTime1 = -b + (float) Math.sqrt((double) (b * b) - (4 * a * c)) / (2 * a);
				edgeTime2 = -b - (float) Math.sqrt((double) (b * b) - (4 * a * c)) / (2 * a);

				if (edgeTime1 <= edgeTime2) {

					smallerSolutionEdge = edgeTime1;
				} else {

					smallerSolutionEdge = edgeTime2;
				}

				if (smallerSolutionEdge >= 0 && smallerSolutionEdge <= 1) {

					smallerF = ((Vector3f.dot(edge, velocityE) * smallerSolutionEdge)
							- Vector3f.dot(edge, baseToVertex)) / edge.lengthSquared();
				}

				if (smallerF >= 0 && smallerF <= 1 && smallerSolutionEdge < smallestSolutionEdge) {

					smallestF = smallerF;
					smallestSolutionEdge = smallerSolutionEdge;
					fromEdgePoint = triangleP2E;
					smallestEdge = edge;
				}
			}

			smallerF = -1;

			edge = Vector3f.sub(triangleP1E, triangleP3E, edge);
			baseToVertex = Vector3f.sub(triangleP3E, ellipsoidPositionIE, baseToVertex);

			a = edge.lengthSquared() * -1 * velocityE.lengthSquared()
					+ (Vector3f.dot(edge, velocityE) * Vector3f.dot(edge, velocityE));
			b = edge.lengthSquared() * 2 * Vector3f.dot(velocityE, baseToVertex)
					- (2 * Vector3f.dot(edge, velocityE) * Vector3f.dot(edge, baseToVertex));
			c = edge.lengthSquared() * (1 - baseToVertex.lengthSquared())
					+ (Vector3f.dot(edge, baseToVertex) * Vector3f.dot(edge, baseToVertex));

			if ((b * b) - (4 * a * c) >= 0) {

				edgeTime1 = -b + (float) Math.sqrt((double) (b * b) - (4 * a * c)) / (2 * a);
				edgeTime2 = -b - (float) Math.sqrt((double) (b * b) - (4 * a * c)) / (2 * a);

				if (edgeTime1 <= edgeTime2) {

					smallerSolutionEdge = edgeTime1;
				} else {

					smallerSolutionEdge = edgeTime2;
				}

				if (smallerSolutionEdge >= 0 && smallerSolutionEdge <= 1) {

					smallerF = ((Vector3f.dot(edge, velocityE) * smallerSolutionEdge)
							- Vector3f.dot(edge, baseToVertex)) / edge.lengthSquared();
				}

				if (smallerF >= 0 && smallerF <= 1 && smallerSolutionEdge < smallestSolutionEdge) {

					smallestF = smallerF;
					smallestSolutionEdge = smallerSolutionEdge;
					fromEdgePoint = triangleP3E;
					smallestEdge = edge;
				}
			}

			if (smallestSolutionEdge != 10005) {

				edgeIntersectionPoint = Vector3f.add(fromEdgePoint, (Vector3f) edge.scale(smallestF),
						edgeIntersectionPoint);
				intersectionDistance = smallestSolutionEdge * velocityE.length();

				edgeData = new CollisionData(edgeIntersectionPoint, intersectionDistance, Type.EDGE);
			}
		}

		if (edgeData != null || vertexData != null) {

			try {

				if (edgeData.getDistance() < vertexData.getDistance()) {

					return edgeData;
				}
			} catch (NullPointerException e) {
			}

			try {

				if (vertexData.getDistance() < edgeData.getDistance()) {

					return vertexData;
				}
			} catch (NullPointerException e) {
			}

			if (edgeData != null) {

				try {

					return edgeData;

				} catch (NullPointerException e) {
				}
			}

			if (vertexData != null) {

				try {

					return vertexData;

				} catch (NullPointerException e) {
				}
			}
		}

		return null;
	}

	public boolean checkPositionWithTriangle(Vector3f position, Vector3f P1, Vector3f P2, Vector3f P3) {

		float angles = 0;

		Vector3f v1 = new Vector3f();
		Vector3f.sub(position, P1, v1);
		v1.normalise();
		Vector3f v2 = new Vector3f();
		Vector3f.sub(position, P2, v2);
		v2.normalise();
		Vector3f v3 = new Vector3f();
		Vector3f.sub(position, P3, v3);
		v3.normalise();

		angles += Math.acos(Vector3f.dot(v1, v2));
		angles += Math.acos(Vector3f.dot(v1, v3));
		angles += Math.acos(Vector3f.dot(v3, v2));

		return Math.abs(angles - 2 * Math.PI) <= 0.005;
	}
}
