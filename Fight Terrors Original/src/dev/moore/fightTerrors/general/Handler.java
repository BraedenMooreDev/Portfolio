package dev.moore.fightTerrors.general;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import dev.moore.fightTerrors.playState.entities.Camera;

public class Handler {

	public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {

		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;

		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}

	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {

		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);

		return matrix;
	}

	public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation) {

		return createTransformationMatrix(translation, rotation, 1);
	}
	
	public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, float scale) {

		return createTransformationMatrix(translation, rotation.x, rotation.y, rotation.z, scale);
	}
	
	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {

		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();

		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1, 0, 0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0, 1, 0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0, 0, 1), matrix, matrix);
		Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);

		return matrix;
	}

	public static Matrix4f createViewMatrix(Camera camera) {

		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.setIdentity();

		Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
		Vector3f cameraPos = camera.getPosition();
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
		Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);

		return viewMatrix;
	}

	public static float clampFloat(float min, float max, float value) {

		return Math.max(min, Math.min(max, value));
	}

	public static Vector3f MultiplyVector3f(Vector3f a, Vector3f b) {

		return new Vector3f(a.x * b.x, a.y * b.y, a.z * b.z);
	}
	
	public static Vector3f AddVector3f(Vector3f a, Vector3f b) {
		
		return new Vector3f(a.x + b.x, a.y + b.y, a.z + b.z);
	}
	
	public static Vector3f SubVector3f(Vector3f a, Vector3f b) {
		
		return new Vector3f(a.x - b.x, a.y - b.y, a.z - b.z);
	}
	
	public static float DistanceVector3f(Vector3f a, Vector3f b) {
		
		return (float) Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y) + (a.z - b.z) * (a.z - b.z));
	}

	public static Quaternion toQuaternion(Vector3f rot) {

		// yaw (Z), pitch (Y), roll (X)
		float roll = (float) Math.toRadians(rot.x);
		float pitch = (float) Math.toRadians(rot.y);
		float yaw = (float) Math.toRadians(rot.z);

		// Abbreviations for the various angular functions
		float cy = (float) Math.cos(yaw * 0.5);
		float sy = (float) Math.sin(yaw * 0.5);
		float cp = (float) Math.cos(pitch * 0.5);
		float sp = (float) Math.sin(pitch * 0.5);
		float cr = (float) Math.cos(roll * 0.5);
		float sr = (float) Math.sin(roll * 0.5);

		Quaternion q = new Quaternion();
		q.w = cy * cp * cr + sy * sp * sr;
		q.x = cy * cp * sr - sy * sp * cr;
		q.y = sy * cp * sr + cy * sp * cr;
		q.z = sy * cp * cr - cy * sp * sr;
		return q;
	}

	public static Quaternion rotateQuaternionX(Quaternion q, float angle) { // Angle in degrees

		double a = Math.toRadians(angle) / 2;

		float sin = (float) Math.sin(a);
		float cos = (float) Math.cos(a);

		Quaternion newQ = new Quaternion();

		newQ.set(q.x * cos + q.w * sin, q.y * cos + q.z * sin, -q.y * sin + q.z * cos, -q.x * sin + q.w * cos);

		return newQ;
	}

	public static Quaternion rotateQuaternionY(Quaternion q, float angle) { // Angle in degrees

		double a = Math.toRadians(angle) / 2;

		float sin = (float) Math.sin(a);
		float cos = (float) Math.cos(a);

		Quaternion newQ = new Quaternion();

		newQ.set(q.x * cos - q.z * sin, q.y * cos + q.w * sin, q.x * sin + q.z * cos, -q.y * sin + q.w * cos);

		return newQ;
	}

	public static Quaternion rotateQuaternionZ(Quaternion q, float angle) { // Angle in degrees

		double a = Math.toRadians(angle) / 2;

		float sin = (float) Math.sin(a);
		float cos = (float) Math.cos(a);

		Quaternion newQ = new Quaternion();

		newQ.set(q.x * cos - q.y * sin, -q.x * sin + q.y * cos, q.z * cos + q.w * sin, -q.z * sin + q.w * cos);

		return newQ;
	}

	public static Vector3f toEulerAngle(Quaternion q) {

		float roll, pitch, yaw;

		// roll (x-axis rotation)
		double sinr_cosp = +2.0 * (q.w * q.x + q.y * q.z);
		double cosr_cosp = +1.0 - 2.0 * (q.x * q.x + q.y * q.y);
		roll = (float) Math.atan2(sinr_cosp, cosr_cosp);

		// pitch (y-axis rotation)
		double sinp = +2.0 * (q.w * q.y - q.z * q.x);
		if (Math.abs(sinp) >= 1)
			pitch = (float) Math.copySign(Math.PI / 2, sinp); // use 90 degrees if out of range
		else
			pitch = (float) Math.asin(sinp);

		// yaw (z-axis rotation)
		double siny_cosp = +2.0 * (q.w * q.z + q.x * q.y);
		double cosy_cosp = +1.0 - 2.0 * (q.y * q.y + q.z * q.z);
		yaw = (float) Math.atan2(siny_cosp, cosy_cosp);

		return new Vector3f((float) Math.toDegrees(roll), (float) Math.toDegrees(pitch), (float) Math.toDegrees(yaw));
	}
	
	//Needs reprogramming and calculations to be redone.
	
	public static float lerpFloat(float current, float target, float factor, float cap) {
		
		float result;
		float dif = target - current;
		
		if(dif < -cap / 2) {
			
			target += cap;
			result = (1 - factor) * current + factor * target;
			
			if(result >= cap)
				result -= cap;
			
		} else if(dif > cap / 2) {
			
			target -= cap;
			result = (1 - factor) * current + factor * target;
			
			if(result < 0)
				result += cap;
			
		} else {
			
			result = (1 - factor) * current + factor * target;
		}
		
		return result;
	}
}