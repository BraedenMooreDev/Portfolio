package dev.moore.fightTerrors.playState.entities.mobs;

public class MobStats {

	private int maxHealth, health, baseDefense, defense;
	private float speed, jumpPower;
	
	public MobStats(int maxHealth, int baseDefense, float speed, float jumpPower) {
		
		this.maxHealth = maxHealth;
		this.health = maxHealth;
		this.baseDefense = baseDefense;
		this.defense = baseDefense;
		this.speed = speed;
		this.jumpPower = jumpPower;
	}
	
	//GETTERS
	
	public int getMaxHealth() { return maxHealth; }
	public int getHealth() { return health; }
	public int getBaseDefense() { return baseDefense; }
	public int getDefense() { return defense; }
	public float getSpeed() { return speed; }
	public float getJumpPower() { return jumpPower; }
	
	//SETTERS
	
	public void setHealth(int health) { this.health = health; }
	public void setDefense(int defense) { this.defense = defense; }
	public void setSpeed(float speed) { this.speed = speed; }
	public void setJumpPower(float jumpPower) { this.jumpPower = jumpPower; }
}
