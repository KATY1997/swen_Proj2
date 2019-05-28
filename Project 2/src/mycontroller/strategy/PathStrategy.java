package mycontroller.strategy;

import utilities.Coordinate;
import world.WorldSpatial;

public abstract class PathStrategy implements IVarationStrategy {

	public abstract WorldSpatial.Direction update();
	
	// add common methods

}
