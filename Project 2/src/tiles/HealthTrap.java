package tiles;

import world.Car;

public class HealthTrap extends TrapTile {
	public static final int HealthDelta = 5;
	
	public String getTrap() { return "health"; }
	
	public void applyTo(Car car, float delta) {
		car.increaseHealth(HealthDelta * delta);
	}
	
	public boolean canAccelerate() {
		return true;
	}
	
	public boolean canTurn() {
		return true;
	}
}
