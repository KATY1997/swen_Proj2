package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import mycontroller.strategy.PathStrategy;
import tiles.MapTile;
import utilities.Coordinate;

/**
 * This class is used to find the shortest path to car's current position in the
 * map given a destination coordinate. It utilize the BFS algorithm
 * 
 * @author kedi peng
 *
 */
public class BFS {

	/**
	 * construct a shorest path from current position to destination position
	 * 
	 * @param destination
	 * @return
	 */
	public static ArrayList<Coordinate> findShortestPath(Coordinate parcel, ArrayList<String> avoid) {
		Queue<Coordinate> path = new LinkedList<>();
		HashMap<Coordinate, MapTile> worldMap = Sensor.getInstance().getWorldMap();
		Coordinate curPos = Sensor.getInstance().getCurrentPos();
		HashMap<Coordinate, Coordinate> previousTrack = new HashMap<>();	// to track the path

		// add current position as the start
		path.add(parcel);
		previousTrack.put(parcel, null);

		addSurrondingCoordinates(path, worldMap, previousTrack, parcel, avoid);

		while (!path.isEmpty()) {
			Coordinate newCoord = path.poll();
			// has find its goal
			if (newCoord.equals(curPos)) {
				return retrivePath(previousTrack, parcel, curPos);
			}
			addSurrondingCoordinates(path, worldMap, previousTrack, newCoord, avoid);
		}
		return new ArrayList<Coordinate>();
	}

	/**
	 * Get the shortest path from the previousTrack. It will construct an ArrayList of Coordinate 
	 * representing the way
	 * @param previousTrack
	 * @param currentPos
	 * @param destination
	 * @return
	 */
	private static ArrayList<Coordinate> retrivePath(HashMap<Coordinate, Coordinate> previousTrack,
			Coordinate currentPos, Coordinate destination) {
		ArrayList<Coordinate> way = new ArrayList<>();

		way.add(destination);
		int i = 0;

		while (previousTrack.containsKey(way.get(i)) && !way.contains(currentPos)) {
			way.add(previousTrack.get(way.get(i)));
			i++;
		}

		return way;
	}

	/**
	 * add coordinates that are surrounded by the current position. Avoid predefined types which are
	 * defined in each strategies
	 * @param path
	 * @param worldMap
	 * @param previousTrack
	 * @param curPos
	 * @param avoid
	 */
	private static void addSurrondingCoordinates(Queue<Coordinate> path, HashMap<Coordinate, MapTile> worldMap,
			HashMap<Coordinate, Coordinate> previousTrack, Coordinate curPos, ArrayList<String> avoid) {
		// add coordinates surround with currentPos
		worldMap.forEach((k, v) -> {

			if (k.x == curPos.x + 1 && k.y == curPos.y && !PathStrategy.avoidCheck(avoid, v)) {

				if (!previousTrack.containsKey(k)) {
					path.add(k);
					previousTrack.put(k, curPos);
				}
			} else if (k.x == curPos.x - 1 && k.y == curPos.y && !PathStrategy.avoidCheck(avoid, v)) {

				if (!previousTrack.containsKey(k)) {
					path.add(k);
					previousTrack.put(k, curPos);
				}
			} else if (k.x == curPos.x && k.y == curPos.y + 1 && !PathStrategy.avoidCheck(avoid, v)) {
				if (!previousTrack.containsKey(k)) {
					path.add(k);
					previousTrack.put(k, curPos);
				}
			} else if (k.x == curPos.x && k.y == curPos.y - 1 && !PathStrategy.avoidCheck(avoid, v)) {
				if (!previousTrack.containsKey(k)) {
					path.add(k);
					previousTrack.put(k, curPos);
				}
			}
		});

	}

}
