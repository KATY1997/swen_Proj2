package tiles;

import world.Car;

public class WaterTrap extends TrapTile {
	public static final int Yield = 5;
	
	public String getTrap() { return "water"; }
	
	public void applyTo(Car car, float delta) {
		car.increaseHealth(WaterTrap.Yield);
	}
	
	public boolean canAccelerate() {
		return true;
	}
	
	public boolean canTurn() {
		return true;
	}
}

