package tiles;

import world.Car;

public class MudTrap extends TrapTile {
	
	// public static final float SLOWDOWN_FACTOR = 0.6f;
	
	public String getTrap() { return "mud"; }
	
	public void applyTo(Car car, float delta) {
//		int currentSpeed = car.getVelocity();
//		float reduction = currentSpeed*SLOWDOWN_FACTOR*delta;
//		car.setVelocity(reduction);
		car.setVelocity(0);
	}
	
	public boolean canAccelerate() {
		return false;
	}
	
	public boolean canTurn() {
		return true;
	}
}
