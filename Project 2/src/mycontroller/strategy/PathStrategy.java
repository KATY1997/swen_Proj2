package mycontroller.strategy;

import java.util.ArrayList;
import java.util.HashMap;

import mycontroller.BFS;
import mycontroller.Sensor;
import tiles.MapTile;
import tiles.MapTile.Type;
import tiles.TrapTile;
import utilities.Coordinate;
import world.WorldSpatial;
import world.WorldSpatial.Direction;

/**
 * common methods in both FuelStrategy and HealthStratefgy, it contains
 * explore(), findParcel(), findExit() and findHealthTrap(), as well as some
 * util methods
 * 
 * @author kedi peng
 *
 */
public abstract class PathStrategy implements IStrategy {

	// How many minimum units the wall is away from the player.
	private int wallSensitivity = 1;

	private boolean isFollowingWall = false; // This is set to true when the car starts sticking to a wall.

	private boolean isMoving = false;

	// the type of traps that need to avoid when using BFS
	public static ArrayList<String> avoidTiles = new ArrayList<>();

	/**
	 * when in EXPLOREMAP state, just simply follow the wall
	 */
	public String explore() {

		if (!isMoving) {
			isMoving = true;
			return "forward";
		} else {
			if (isFollowingWall) {
				// If wall no longer on right, turn right
				if (!checkRight(Sensor.getInstance().getOrientation(), Sensor.getInstance().getCurrentView())) {
					return "right";
				} else {
					// If wall or water on left and wall straight ahead, turn right
					if (checkAhead(Sensor.getInstance().getOrientation(), Sensor.getInstance().getCurrentView())) {
						return "left";
					}
				}
			} else {
				// Start wall-following (with wall on left) as soon as we see a wall straight ahead
				if (checkAhead(Sensor.getInstance().getOrientation(), Sensor.getInstance().getCurrentView())) {
					isFollowingWall = true;
					return "left";
				}
			}
		}
		return "forward";
	}

	/**
	 * find the shortest way to the nearest parcel
	 */
	public String findParcel() {
		Coordinate nextPos = null;
		if (!isMoving) {
			isMoving = true;
			return "forward";
		} else {

			int shortestStep = 999999;
			Coordinate dest = null;
			// in case more than one parcel in the sensor's list, find the shortest one
			for (Coordinate parcelDestination : Sensor.getInstance().getParcels()) {
				ArrayList<Coordinate> path = BFS.findShortestPath(parcelDestination, avoidTiles);
				if (path != null && path.size() > 0 && path.size() < shortestStep) {
					shortestStep = path.size();
					dest = parcelDestination;
				}
			}

			if (dest != null) {
				// from parcel to current coordinate
				ArrayList<Coordinate> way = BFS.findShortestPath(dest, avoidTiles);
				nextPos = way.get(1);
				return getCommand(Sensor.getInstance().getCurrentPos(), nextPos);
			}
			return explore();
		}

	}

	/**
	 * find the shortest way to the exit using BFS
	 */
	public String findExit() {

		if (Sensor.getInstance().getExit() != null) {
			ArrayList<Coordinate> way = BFS.findShortestPath(Sensor.getInstance().getExit(), avoidTiles);
			if (way.size() == 0) {
				return explore();
			}
			Coordinate nextPos = way.size() >= 2 ? way.get(1) : way.get(0);
			return getCommand(Sensor.getInstance().getCurrentPos(), nextPos);
		}
		return explore();
	}

	/**
	 * find the way to nearest health trap
	 * 
	 * @return
	 */
	public String findHealthTrap() {
		if (!isMoving) {
			isMoving = true;
			return "forward";
		} else {

			int shortestStep = 999999;
			Coordinate destination = null;
			Coordinate nextPos = null;

			for (Coordinate c : Sensor.getInstance().getHealthTrap()) {
				ArrayList<Coordinate> path = BFS.findShortestPath(c, avoidTiles);
				if (path.size() < shortestStep) {
					destination = c;
				}
			}

			ArrayList<Coordinate> way = BFS.findShortestPath(destination, avoidTiles);
			nextPos = way.size() > 2 ? way.get(1) : way.get(0);
			return getCommand(Sensor.getInstance().getCurrentPos(), nextPos);

		}
	}

	/**
	 * determine the command of the car given next position
	 * 
	 * @param curPos
	 *            the first coordinate
	 * @param nextPos
	 *            the destination coordinate
	 * @return
	 */
	private String getCommand(Coordinate curPos, Coordinate nextPos) {
		Direction orientation = Sensor.getInstance().getOrientation();

		if (curPos.x > nextPos.x) {
			switch (orientation) {
			case NORTH:
				return "left";
			case SOUTH:
				return "right";
			case EAST:
				return "reverse";
			case WEST:
				return "forward";
			}
		} else if (curPos.x < nextPos.x) {
			switch (orientation) {
			case NORTH:
				return "right";
			case SOUTH:
				return "left";
			case EAST:
				return "forward";
			case WEST:
				return "reverse";
			}
		} else if (curPos.y > nextPos.y) {
			switch (orientation) {
			case NORTH:
				return "reverse";
			case SOUTH:
				return "forward";
			case EAST:
				return "right";
			case WEST:
				return "left";
			}
		} else if (curPos.y < nextPos.y) {
			switch (orientation) {
			case NORTH:
				return "forward";
			case SOUTH:
				return "reverse";
			case EAST:
				return "left";
			case WEST:
				return "right";
			}
		}
		return "forward";
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
	private boolean checkAhead(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView) {
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
	 * Check if the wall is on your right hand side given your orientation
	 * return true if a wall in your right
	 * 
	 * @param orientation
	 * @param currentView
	 * @return
	 */
	private boolean checkRight(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView) {

		switch (orientation) {
		case EAST:
			return checkSouth(currentView);
		case NORTH:
			return checkEast(currentView);
		case SOUTH:
			return checkWest(currentView);
		case WEST:
			return checkNorth(currentView);
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
	public boolean checkEast(HashMap<Coordinate, MapTile> currentView) {
		// Check tiles to my right
		Coordinate currentPosition = Sensor.getInstance().getCurrentPos();
		for (int i = 0; i <= wallSensitivity; i++) {
			MapTile tile = currentView.get(new Coordinate(currentPosition.x + i, currentPosition.y));
			if (avoidCheck(avoidTiles, tile)) {
				return true;
			}
		}
		return false;
	}

	public boolean checkWest(HashMap<Coordinate, MapTile> currentView) {
		// Check tiles to my left
		Coordinate currentPosition = Sensor.getInstance().getCurrentPos();
		for (int i = 0; i <= wallSensitivity; i++) {
			MapTile tile = currentView.get(new Coordinate(currentPosition.x - i, currentPosition.y));
			if (avoidCheck(avoidTiles, tile)) {
				return true;
			}
		}
		return false;
	}

	public boolean checkNorth(HashMap<Coordinate, MapTile> currentView) {
		// Check tiles to towards the top
		Coordinate currentPosition = Sensor.getInstance().getCurrentPos();
		for (int i = 0; i <= wallSensitivity; i++) {
			MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y + i));
			if (avoidCheck(avoidTiles, tile)) {
				return true;
			}
		}
		return false;
	}

	public boolean checkSouth(HashMap<Coordinate, MapTile> currentView) {
		// Check tiles towards the bottom
		Coordinate currentPosition = Sensor.getInstance().getCurrentPos();
		for (int i = 0; i <= wallSensitivity; i++) {
			MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y - i));
			if (avoidCheck(avoidTiles, tile)) {
				return true;
			}
		}
		return false;
	}

	public static boolean avoidCheck(ArrayList<String> avoid, MapTile tile) {
		if (tile.isType(Type.WALL)) {
			return true;
		} else if (tile.isType(Type.TRAP)) {
			TrapTile trapTile = (TrapTile) tile;
			if (avoid.contains(trapTile.getTrap())) {
				return true;
			}
		}
		return false;
	}

}
