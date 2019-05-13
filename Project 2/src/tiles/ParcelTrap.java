package tiles;

import world.Car;

public class ParcelTrap extends TrapTile {
	public String getTrap() { return "parcel"; }
	
	public void applyTo(Car car, float delta) {
	}
	
	public boolean canAccelerate() {
		return true;
	}
	
	public boolean canTurn() {
		return true;
	}
}

