package mycontroller;

import mycontroller.CarStateMachine.CarState;
import mycontroller.strategy.IVarationStrategy;
import utilities.Coordinate;

public class PathFinderUtil {

	private static String command = "forward";

	public static String findHealthTrap() {
		return command;
	}

	/**
	 * 
	 * @return
	 */
	public static String findPath(CarState carState, IVarationStrategy strategy) {
		String command = "forward";
		switch (carState) {
		case EXPLOREMAP:
			command =  strategy.explore();
			break;
		case FINDEXIT:
			command =  strategy.explore();
			break;
		case FINDPARCEL:
			command =  strategy.findParcel();
			break;
//			return strategy.explore();

		}
		return command;
	}

	/**
	 * find the cloest parcel in the parcel list
	 * 
	 * @return
	 */
	public static Coordinate findCloestParcel() {
		Coordinate closetParcel = null;
		Coordinate currPos = Sensor.getInstance().getCurrentPos();
		double minDistance = 9999999;

		for (Coordinate c : Sensor.getInstance().getParcels()) {
			double distance = Math.pow((currPos.x - c.x), 2) + Math.pow((currPos.y - c.y), 2);
			if (distance < minDistance) {
				minDistance = distance;
				closetParcel = c;
			}
		}

		return closetParcel;
	}


}
