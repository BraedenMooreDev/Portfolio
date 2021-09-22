package dev.moore.fightTerrors.playState.events;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import dev.moore.fightTerrors.playState.PlayState;
import dev.moore.fightTerrors.playState.entities.Camera;
import dev.moore.fightTerrors.playState.entities.mobs.Player;

public class InteractionEvent extends Event {

	private Player player;
	private Camera camera;

	private float range, radius;

	private boolean alreadyUnclicked = false;

	private boolean enabled = true;

	public InteractionEvent(PlayState playState, float range, float radius) {
		super(playState);

		this.range = range;
		this.radius = radius;

		player = playState.player;
		camera = playState.camera;
	}

	public void update() {

		if (camera == null || !enabled)
			return;

		Vector3f rayOrigin = camera.getPosition();
		Vector3f rayDirection;

		if (player.isInFirstPerson())
			rayDirection = playState.getMousePicker().calculateScreenRay(Display.getWidth() / 2,
					Display.getHeight() / 2);
		else
			rayDirection = playState.getMousePicker().getCurrRay();

		if (distanceBetween(player.getPosition(), linkedEntity.getPosition()) <= range) { // In interactable range

			Vector3f toSphereDirection = Vector3f.sub(linkedEntity.getPosition(), rayOrigin, null);

			float t = Vector3f.dot(toSphereDirection, rayDirection);
			float sphereY = distanceBetween(linkedEntity.getPosition(), Vector3f.add(rayOrigin,
					new Vector3f(rayDirection.x * t, rayDirection.y * t, rayDirection.z * t), null));

			if (sphereY < radius) {

//				Intersection Point Calculations
//
//				float sphereX = (float) Math.sqrt(radius * radius - sphereY * sphereY);
//			
//				float t1 = t - sphereX;
//				float t2 = t + sphereX; 
//				
//				float intersectionT = Math.min(t1, t2);

				if (Mouse.isButtonDown(0)) {

					if (alreadyUnclicked)
						clicked();
					alreadyUnclicked = false;

					return;
				} else {

					hover();
				}
			} else {

				unhover();
			}
		} else {

			unhover();
		}

		if (!alreadyUnclicked)
			unclicked();
		alreadyUnclicked = true;
	}

	public void hover() {

		System.err.println("Interaction Event has no 'Hover' action");
	}

	public void unhover() {

		System.err.println("Interaction Event has not 'Unhover' action");
	}

	public void clicked() {

		System.err.println("Interaction Event has no 'Clicked' action");
	}

	public void unclicked() {

		System.err.println("Interaction Event has no 'Unclicked' action");
	}

	private float distanceBetween(Vector3f a, Vector3f b) {

		float dx = a.x - b.x;
		float dy = a.y - b.y;
		float dz = a.z - b.z;

		return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	// SETTERS

	public void setEnabled(boolean a) {
		enabled = a;
	}
}
