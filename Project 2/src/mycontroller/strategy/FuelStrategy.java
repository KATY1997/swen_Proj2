package mycontroller.strategy;

import mycontroller.CarStateMachine.CarState;
import mycontroller.Sensor;
import utilities.Coordinate;
import world.WorldSpatial.Direction;

/**
 * strategy towards reducing fuel usage
 * @author pengkedi
 *
 */
public class FuelStrategy extends PathStrategy {


	public FuelStrategy() {
	}

	@Override
	public String update(CarState carState) {
		
//		if (carState == CarState.FINDHEALTH) {
//			return this.findHealthTrap();
//		}else {
//			return this.findPath(carState);
//		}
		
		return null;

	}


	@Override
	public Direction update() {
		// TODO Auto-generated method stub
		return null;
	}


}
