package tiles;

import world.Car;

public class LavaTrap extends TrapTile {
	public static final int HealthDelta = 20;
	
	public String getTrap() { return "lava"; }

	public void applyTo(Car car, float delta) {
		car.reduceHealth(HealthDelta * delta);
	}
	
	public boolean canAccelerate() {
		return true;
	}
	
	public boolean canTurn() {
		return true;
	}
}
