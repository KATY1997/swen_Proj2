package mycontroller.strategy;

import controller.CarController;
import mycontroller.CarStateMachine.CarState;
import utilities.Coordinate;
import world.World;
import world.WorldSpatial;

public interface IVarationStrategy {

	public String update(CarState carState);
	
	public WorldSpatial.Direction update();

	public String explore();

	public String findParcel();
	
}
