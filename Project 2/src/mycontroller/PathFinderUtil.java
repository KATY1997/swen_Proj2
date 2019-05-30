package mycontroller;

import java.util.HashMap;

import controller.CarController;
import mycontroller.CarStateMachine.CarState;
import mycontroller.strategy.IVarationStrategy;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class PathFinderUtil {

	private static String command = "forward";
	// How many minimum units the wall is away from the player.
	private static int wallSensitivity = 1;

	private static boolean isFollowingWall = false; // This is set to true when the car starts sticking to a wall.

	// Car Speed to move at
	private static final int CAR_MAX_SPEED = 1;

	public static String findHealthTrap() {

		return command;
	}

	/**
	 * 
	 * @return
	 */
	public static String findPath(CarState carState, IVarationStrategy strategy) {
		switch (carState) {
		case EXPLOREMAP:
			return strategy.explore();
		case FINDEXIT:
		case FINDPARCEL:
			return strategy.findParcel();
			
//			return strategy.explore();

		}
		return strategy.explore();
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

	/**
	 * Check if you have a wall in front of you!
	 * 
	 * @param orientation
	 *            the orientation we are in based on WorldSpatial
	 * @param currentView
	 *            what the car can currently see
	 * @return
	 */
	private boolean checkWallAhead(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView) {
		switch (orientation) {
		case EAST:
			return checkEast(currentView);
		case NORTH:
			return checkNorth(currentView);
		case SOUTH:
			return checkSouth(currentView);
		case WEST:
			return checkWest(currentView);
		default:
			return false;
		}
	}

	/**
	 * Check if the wall is on your left hand side given your orientation
	 * 
	 * @param orientation
	 * @param currentView
	 * @return
	 */
	private static boolean checkFollowingWall(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView) {

		switch (orientation) {
		case EAST:
			return checkNorth(currentView);
		case NORTH:
			return checkWest(currentView);
		case SOUTH:
			return checkEast(currentView);
		case WEST:
			return checkSouth(currentView);
		default:
			return false;
		}
	}

	/**
	 * Method below just iterates through the list and check in the correct
	 * coordinates. i.e. Given your current position is 10,10 checkEast will
	 * check up to wallSensitivity amount of tiles to the right. checkWest will
	 * check up to wallSensitivity amount of tiles to the left. checkNorth will
	 * check up to wallSensitivity amount of tiles to the top. checkSouth will
	 * check up to wallSensitivity amount of tiles below.
	 */
	public static boolean checkEast(HashMap<Coordinate, MapTile> currentView) {
		// Check tiles to my right
		Coordinate currentPosition = Sensor.getInstance().getCurrentPos();
		for (int i = 0; i <= wallSensitivity; i++) {
			MapTile tile = currentView.get(new Coordinate(currentPosition.x + i, currentPosition.y));
			if (tile.isType(MapTile.Type.WALL)) {
				return true;
			}
		}
		return false;
	}

	public static boolean checkWest(HashMap<Coordinate, MapTile> currentView) {
		// Check tiles to my left
		Coordinate currentPosition = Sensor.getInstance().getCurrentPos();
		for (int i = 0; i <= wallSensitivity; i++) {
			MapTile tile = currentView.get(new Coordinate(currentPosition.x - i, currentPosition.y));
			if (tile.isType(MapTile.Type.WALL)) {
				return true;
			}
		}
		return false;
	}

	public static boolean checkNorth(HashMap<Coordinate, MapTile> currentView) {
		// Check tiles to towards the top
		Coordinate currentPosition = Sensor.getInstance().getCurrentPos();
		for (int i = 0; i <= wallSensitivity; i++) {
			MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y + i));
			if (tile.isType(MapTile.Type.WALL)) {
				return true;
			}
		}
		return false;
	}

	public static boolean checkSouth(HashMap<Coordinate, MapTile> currentView) {
		// Check tiles towards the bottom
		Coordinate currentPosition = Sensor.getInstance().getCurrentPos();
		for (int i = 0; i <= wallSensitivity; i++) {
			MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y - i));
			if (tile.isType(MapTile.Type.WALL)) {
				return true;
			}
		}
		return false;
	}

}
