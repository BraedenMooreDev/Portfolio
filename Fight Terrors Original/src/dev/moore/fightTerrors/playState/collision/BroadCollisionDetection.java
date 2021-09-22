package dev.moore.fightTerrors.playState.collision;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import dev.moore.fightTerrors.playState.entities.Entity;
import dev.moore.fightTerrors.playState.entities.mobs.Player;

public class BroadCollisionDetection {

	public static List<Entity> ScanAndPrune(Player player, List<Entity> entities, float padding) {

		List<Entity> thoseInRange = new ArrayList<Entity>();

		for (Entity e : entities) {

			float rangeSum = player.getBroadRange() + e.getBroadRange() + padding;

			if (calcDistance(player.getPosition(), e.getPosition()) < rangeSum)
				thoseInRange.add(e);
		}

		return thoseInRange;
	}

	private static float calcDistance(Vector3f pos1, Vector3f pos2) {

		float dx = pos1.x - pos2.x;
		float dy = pos1.y - pos2.y;
		float dz = pos1.z - pos2.z;

		return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
	}
}
