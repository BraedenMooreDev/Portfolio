package dev.moore.fightTerrors.playState.items;

public class WieldableStats {
	
	private int actionValue, useTime; //Use Time is kept in milliseconds
	private float reach;
	private boolean[] availableSlots; //Ordered: Left Hip, Right Hip, Right Back, Left Back
	
	public WieldableStats(int actionValue, float reach, int useTime, boolean[] availableSlots) {
		
		this.actionValue = actionValue;
		this.reach = reach;
		this.useTime = useTime;
		this.availableSlots = availableSlots;
	}
	
	//GETTERS
	
	public int getActionValue() { return actionValue; }
	public float getReach() { return reach; }
	public int getUseTime() { return useTime; }
	public boolean[] getAvailableSlots() { return availableSlots; }
}
