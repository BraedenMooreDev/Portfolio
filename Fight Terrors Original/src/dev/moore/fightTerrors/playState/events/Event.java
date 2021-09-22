package dev.moore.fightTerrors.playState.events;

import dev.moore.fightTerrors.playState.PlayState;
import dev.moore.fightTerrors.playState.entities.Entity;

public abstract class Event {

	protected PlayState playState;
	protected Entity linkedEntity;

	public Event(PlayState playState) {

		this.playState = playState;
	}

	public void linkEntity(Entity entity) {

		linkedEntity = entity;
	}

	public abstract void update();
}
