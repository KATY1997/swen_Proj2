package mycontroller.strategy;

import utilities.Coordinate;
import world.World;
import world.WorldSpatial;

public interface IVarationStrategy {

	public WorldSpatial.Direction update();
	
}
