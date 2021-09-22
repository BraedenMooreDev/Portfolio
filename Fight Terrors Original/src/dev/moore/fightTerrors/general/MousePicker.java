package dev.moore.fightTerrors.general;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import dev.moore.fightTerrors.playState.entities.Camera;

public class MousePicker {

	private Vector3f currRay;

	private Matrix4f projectionMatrix, viewMatrix;
	private Camera camera;

	public MousePicker(Camera camera, Matrix4f projectionMatrix) {

		this.camera = camera;
		this.projectionMatrix = projectionMatrix;
		this.viewMatrix = Handler.createViewMatrix(camera);
	}

	public void update() {

		viewMatrix = Handler.createViewMatrix(camera);
		currRay = calculateScreenRay(Mouse.getX(), Mouse.getY());
	}

	public Vector3f calculateScreenRay(float screenX, float screenY) {

		Vector2f normalizedCoords = getNormalizedDeviceCoords(screenX, screenY);
		Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1f, 1f);
		Vector4f eyeCoords = toEyeCoords(clipCoords);
		Vector3f worldRay = toWorldCoords(eyeCoords);

		return worldRay;
	}

	private Vector3f toWorldCoords(Vector4f eyeCoords) {

		Matrix4f invertedView = Matrix4f.invert(viewMatrix, null);
		Vector4f rayWorld = Matrix4f.transform(invertedView, eyeCoords, null);
		Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
		mouseRay.normalise();

		return mouseRay;
	}

	private Vector4f toEyeCoords(Vector4f clipCoords) {

		Matrix4f invertedProjecion = Matrix4f.invert(projectionMatrix, null);
		Vector4f eyeCoords = Matrix4f.transform(invertedProjecion, clipCoords, null);

		return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
	}

	private Vector2f getNormalizedDeviceCoords(float mouseX, float mouseY) {

		float x = (2f * mouseX) / Display.getWidth() - 1;
		float y = (2f * mouseY) / Display.getHeight() - 1;

		return new Vector2f(x, y);
	}

	// GETTERS

	public Vector3f getCurrRay() {
		return currRay;
	}
}
