package mycontroller.strategy;

import mycontroller.Sensor;
import mycontroller.CarStateMachine.CarState;
import tiles.MapTile.Type;
import utilities.Coordinate;
import world.WorldSpatial;
import world.WorldSpatial.Direction;

public class HealthStrategy extends PathStrategy {

	@Override
	public String update(CarState carState) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Direction update() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findParcel() {
		
		// TODO Auto-generated method stub
		return null;
	}
	
//	@Override
//	public String explore() {
//		String command  = super.explore();
//		// observe command
//		if (facingWater(command)) {
//			command = "reverse";
//		}
//		
//		return command;
//	}

	private boolean facingWater(String command) {
		Direction direction = Sensor.getInstance().getOrientation();
//		switch (direction) {
//		case NORTH:
//			checkNorth(Sensor.getInstance().getCurrentView(), Type.TRAP);
//			break;
//
//		default:
//			break;
//		}
		
		
		return false;
	}


}
