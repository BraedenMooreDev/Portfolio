package dev.moore.fightTerrors.playState.entities;

import java.awt.event.KeyEvent;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import dev.moore.fightTerrors.general.Handler;
import dev.moore.fightTerrors.playState.entities.mobs.Player;

public class Camera {

	private static final float IDLE_ORBIT_SPEED = 0.1f, TIME_TO_IDLE = 5000;
	private static final float ZOOM_EDGE = 2f;

	private final float sensitivity = 0.01f;
	private Vector3f position = new Vector3f(100, 35, 50);
	private float pitch = 10;
	private float yaw = 0;
	private float distanceFromPlayer = 50, storedDistance = distanceFromPlayer;
	
	private Player player;

	public Camera(Player player) {

		this.player = player;
		yaw = 180 - player.getRotation().y;
	}

	boolean once = false;

	public void move() {

		if (player.isInFirstPerson()) { // 1st Person

			calculateZoom();
			calculatePitch();

			yaw = 180 - player.getRotation().y;
			
			calculateCameraPosition(Player.EYE_OFFSET, Player.height, 180 - yaw);

		} else { // 3rd Person
			
			calculateZoom();
			calculatePitch();
			
			float hd = (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
			
			float height = player.getTerrain().getHeightOfTerrain(position.x, position.z) - player.getPosition().y - Player.NORM_HEIGHT + 2f;
			
			minTheta = (float) Math.toDegrees(Math.atan(height / hd));
			
			if (pitch < minTheta)
				pitch = minTheta;
			else if (pitch > 85)
				pitch = 85;

			if(Mouse.isButtonDown(2) && Keyboard.isKeyDown(Keyboard.KEY_C)) {

				float rot = Mouse.getDX();
				yaw += rot;
								
			} else {

				if(player.idleStartTime != 0 && (int) System.currentTimeMillis() - player.idleStartTime >= TIME_TO_IDLE)
					yaw += player.randomOrbitSign * IDLE_ORBIT_SPEED;
				else {

					float targetYaw = 180 - player.getRotation().y;
					yaw = Handler.lerpFloat(yaw, targetYaw, 0.25f, 360);
				}
			}
				
			float horizontalDistance = calculateHorizontalDistance() + Player.EYE_OFFSET;
			float verticalDistance = calculateVerticalDistance() + Player.height;

			calculateCameraPosition(horizontalDistance, verticalDistance, 180 - yaw);
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_F1)) {

			if (!once) {

				player.goIntoFirstPerson(!player.isInFirstPerson());

				distanceFromPlayer = player.isInFirstPerson() ? ZOOM_EDGE : storedDistance;

				once = true;
			}

		} else {

			once = false;
		}

		Mouse.setGrabbed(player.isInFirstPerson());
	}

	private void calculateCameraPosition(float horizontalDistance, float verticalDistance, float rotY) {

		float xOffset = (float) (horizontalDistance * Math.sin(Math.toRadians(rotY)));
		float zOffset = (float) (horizontalDistance * Math.cos(Math.toRadians(rotY)));

		position.x = player.getPosition().x - xOffset;
		position.z = player.getPosition().z - zOffset;
		position.y = player.getPosition().y + verticalDistance;
	}

	private float calculateHorizontalDistance() {
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}

	private float calculateVerticalDistance() {
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}

	private void calculateZoom() {

		float zoomLevel = Mouse.getDWheel() * 0.1f;
		distanceFromPlayer -= zoomLevel;
		distanceFromPlayer = Handler.clampFloat(0, 300, distanceFromPlayer);

		if (distanceFromPlayer <= ZOOM_EDGE) {

			player.goIntoFirstPerson(true);

		} else if (distanceFromPlayer > ZOOM_EDGE) {

			storedDistance = distanceFromPlayer;
			player.goIntoFirstPerson(false);
		}
	}

	float minTheta = 0;
	
	private void calculatePitch() {

		if (player.isInFirstPerson()) {

			float pitchChange = Mouse.getDY() * Player.firstPersonSensitivity;
			pitch -= pitchChange;
			
			if (pitch < -85)
				pitch = -85;
			else if (pitch > 85)
				pitch = 85;

		} else {
			
			if (Mouse.isButtonDown(2)) {

				float pitchChange = Mouse.getDY() * Player.thirdPersonSensitivity;
				pitch -= pitchChange;
			}
		}
	}

	public void invertPitch() {

		this.pitch = -pitch;
	}

	// GETTERS

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}
}