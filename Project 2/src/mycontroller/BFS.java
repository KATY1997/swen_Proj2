package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import mycontroller.strategy.PathStrategy;
import tiles.MapTile;
import tiles.TrapTile;
import tiles.MapTile.Type;
import utilities.Coordinate;
import world.World;

public class BFS {

	/**
	 * construct a shorest path from current position to destination position
	 * 
	 * @param destination
	 * @return
	 */
	public static ArrayList<Coordinate> shortestPath(Coordinate parcel, ArrayList<String> avoid) {
		Queue<Coordinate> path = new LinkedList<>();
		HashMap<Coordinate, MapTile> worldMap = Sensor.getInstance().getWorldMap();
		Coordinate curPos = Sensor.getInstance().getCurrentPos();
		HashMap<Coordinate, Coordinate> previousTrack = new HashMap<>();

		// add current position as the start
		path.add(parcel);
		previousTrack.put(parcel, null);
		
		addSurrondCoordinates(path, worldMap, previousTrack, parcel, avoid);

		while (!path.isEmpty()) {
			Coordinate newCoord = path.poll();
			// has find its goal
			if (newCoord.equals(curPos)) {
				return retrivePath(previousTrack,parcel,curPos);
			}
			addSurrondCoordinates(path, worldMap, previousTrack, newCoord,avoid);
		}
		return new ArrayList<Coordinate>();

	}
	
	private static ArrayList<Coordinate> retrivePath(HashMap<Coordinate, Coordinate> previousTrack, Coordinate currentPos,Coordinate destination) {
		ArrayList<Coordinate> way = new ArrayList<>();
		
		way.add(destination);
		int i = 0;
		
		while (previousTrack.containsKey(way.get(i))&& !way.contains(currentPos)) {
			way.add(previousTrack.get(way.get(i)));
			i++;
		}
		
		return way;
	}

	private static void addSurrondCoordinates(Queue<Coordinate> path, HashMap<Coordinate, MapTile> worldMap,
			HashMap<Coordinate, Coordinate> previousTrack, Coordinate curPos, ArrayList<String> avoid) {
		// add coordinates surround with currentPos
		worldMap.forEach((k, v) -> {
//			if (Sensor.getInstance().getCurrentPos().equals(new Coordinate(20, 17))) {
//				System.out.println("test");
//			}
//			ArrayList<Coordinate> surrounds = new ArrayList<>();
//			Coordinate eastPoint = new Coordinate(k.x+1, k.y);
//			Coordinate westPoint = new Coordinate(k.x-1, k.y);
//			Coordinate northPoint = new Coordinate(k.x, k.y+1);
//			Coordinate southPoint = new Coordinate(k.x, k.y-1);
//			
//			if (worldMap.containsKey(eastPoint)) {
//				if (!PathStrategy.needToAvoid(avoid, worldMap.get(eastPoint)) && !previousTrack.containsKey(k)) {
//					path.add(k);
//					previousTrack.put(k, curPos);
//				}
//			}
//			if (worldMap.containsKey(westPoint)) {
//				if (!PathStrategy.needToAvoid(avoid, worldMap.get(westPoint)) && !previousTrack.containsKey(k)) {
//					path.add(k);
//					previousTrack.put(k, curPos);
//				}
//			}
//			if (worldMap.containsKey(northPoint)) {
//				if (!PathStrategy.needToAvoid(avoid, worldMap.get(northPoint)) && !previousTrack.containsKey(k)) {
//					path.add(k);
//					previousTrack.put(k, curPos);
//				}
//			}
//			if (worldMap.containsKey(southPoint)) {
//				if (!PathStrategy.needToAvoid(avoid, worldMap.get(southPoint)) && !previousTrack.containsKey(k)) {
//					path.add(k);
//					previousTrack.put(k, curPos);
//				}
//			}
			
			if (k.x == curPos.x + 1 && k.y == curPos.y && !PathStrategy.needToAvoid(avoid, v)) {

				if (!previousTrack.containsKey(k)) {
					path.add(k);
					previousTrack.put(k, curPos);
				}
			} else if (k.x == curPos.x - 1 && k.y == curPos.y && !PathStrategy.needToAvoid(avoid, v)) {

				if (!previousTrack.containsKey(k)) {
					path.add(k);
					previousTrack.put(k, curPos);
				}
			} else if (k.x == curPos.x && k.y == curPos.y + 1 && !PathStrategy.needToAvoid(avoid, v)) {
				if (!previousTrack.containsKey(k)) {
					path.add(k);
					previousTrack.put(k, curPos);
				}
			} else if (k.x == curPos.x && k.y == curPos.y - 1 && !PathStrategy.needToAvoid(avoid, v)) {
				if (!previousTrack.containsKey(k)) {
					path.add(k);
					previousTrack.put(k, curPos);
				}
			}
		});

	}
	
	
}
