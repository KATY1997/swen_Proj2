package mycontroller.strategy;

import mycontroller.BFS;
import mycontroller.CarStateMachine.CarState;
import tiles.MapTile;
import tiles.MapTile.Type;
import utilities.Coordinate;
import world.WorldSpatial;
import world.WorldSpatial.Direction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

import com.sun.org.apache.bcel.internal.generic.NEW;

import controller.CarController;
import mycontroller.PathFinderUtil;
import mycontroller.Sensor;

/**
 * common methods in both fuelstrategy and healthStratefgy
 * 
 * @author pengkedi
 *
 */
public abstract class PathStrategy implements IVarationStrategy {

	// How many minimum units the wall is away from the player.
	private int wallSensitivity = 1;

	private boolean isFollowingWall = false; // This is set to true when the car starts sticking to a wall.

	private boolean isMoving = false;
	// Car Speed to move at
	private final int CAR_MAX_SPEED = 1;

	//	/**
	//	 * find water or ice for the car
	//	 * @return
	//	 */
	//	public String findHealthTrap() {
	////		return PathFinderUtil.findHealthTrap();
	//	}
	//	
	//	public String findPath(CarState carState) {
	////		return PathFinderUtil.findPath(carState);
	//	}

	/**
	 * follow the wall when in EXPLOREMAP
	 */
	public String explore() {

//		if (Sensor.getInstance().getCurrentPos().equals(new Coordinate(23,2))) {
//			System.out.println("hhh");
//		}
		if (!isMoving) {
			isMoving = true;
			return "forward";
		}else {
			
			if (isFollowingWall) {
				// If wall no longer on left, turn left
				if(!checkFollowingWall(Sensor.getInstance().getOrientation(), Sensor.getInstance().getCurrentView())) {
					return "left";
				} else {
					// If wall on left and wall straight ahead, turn right
					if(checkWallAhead(Sensor.getInstance().getOrientation(), Sensor.getInstance().getCurrentView())) {
						return "right";
					}
				}
			} else {
				// Start wall-following (with wall on left) as soon as we see a wall straight ahead
				if(checkWallAhead(Sensor.getInstance().getOrientation(), Sensor.getInstance().getCurrentView())) {
					isFollowingWall = true;
					return "right";
				}
			}
			
			
		}

		return "forward";
	}

	/**
	 * find the shortest way to the nearest parcel
	 */
	public String findParcel() {
		if (!isMoving) {
			isMoving = true;
			return "forward";
		}else {
			
			int shortestStep = 999999;
			Coordinate firstDest = null;
			for (Coordinate parcelDestination : Sensor.getInstance().getParcels()) {
				ArrayList<Coordinate> path = BFS.shortestPath(parcelDestination);
				if (path.size() < shortestStep) {
					shortestStep = path.size();
					firstDest = parcelDestination;
				}
			}
			
			ArrayList<Coordinate> way = BFS.shortestPath(firstDest);
			Coordinate nextPos = way.get(way.size() - 2);
			
			return getCommand(Sensor.getInstance().getCurrentPos(), nextPos);
		}

	}

	/**
	 * determine the command of the car given next position
	 * 
	 * @param curPos
	 * @param nextPos
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
	 * @param orientation the orientation we are in based on WorldSpatial
	 * @param currentView what the car can currently see
	 * @return
	 */
	private boolean checkWallAhead(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView){
		switch(orientation){
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
	 * @param orientation
	 * @param currentView
	 * @return
	 */
	private boolean checkFollowingWall(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView) {
		
		switch(orientation){
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
	 * Check if the wall is on your right hand side given your orientation
	 * return true if a wall in your right
	 * @param orientation
	 * @param currentView
	 * @return
	 */
	private boolean checkRight(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView) {
		
		switch(orientation){
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
	 * Method below just iterates through the list and check in the correct coordinates.
	 * i.e. Given your current position is 10,10
	 * checkEast will check up to wallSensitivity amount of tiles to the right.
	 * checkWest will check up to wallSensitivity amount of tiles to the left.
	 * checkNorth will check up to wallSensitivity amount of tiles to the top.
	 * checkSouth will check up to wallSensitivity amount of tiles below.
	 */
	public boolean checkEast(HashMap<Coordinate, MapTile> currentView){
		// Check tiles to my right
		Coordinate currentPosition = Sensor.getInstance().getCurrentPos();
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y));
			if(tile.isType(MapTile.Type.WALL)){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkWest(HashMap<Coordinate,MapTile> currentView){
		// Check tiles to my left
		Coordinate currentPosition = Sensor.getInstance().getCurrentPos();
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x-i, currentPosition.y));
			if(tile.isType(MapTile.Type.WALL)){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkNorth(HashMap<Coordinate,MapTile> currentView){
		// Check tiles to towards the top
		Coordinate currentPosition = Sensor.getInstance().getCurrentPos();
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y+i));
			if(tile.isType(MapTile.Type.WALL)){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkSouth(HashMap<Coordinate,MapTile> currentView){
		// Check tiles towards the bottom
		Coordinate currentPosition = Sensor.getInstance().getCurrentPos();
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y-i));
			if(tile.isType(MapTile.Type.WALL)){
				return true;
			}
		}
		return false;
	}
}
