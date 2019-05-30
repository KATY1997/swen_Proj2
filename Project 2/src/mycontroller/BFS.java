package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import com.sun.xml.internal.bind.v2.model.core.ID;

import tiles.MapTile;
import tiles.MapTile.Type;
import utilities.Coordinate;

public class BFS {

	/**
	 * construct a shorest path from current position to destination position
	 * 
	 * @param destination
	 * @return
	 */
	public static ArrayList<Coordinate> shortestPath(Coordinate destination) {
		Queue<Coordinate> path = new LinkedList<>();
		HashMap<Coordinate, MapTile> worldMap = Sensor.getInstance().getWorldMap();
		Coordinate curPos = Sensor.getInstance().getCurrentPos();
		HashMap<Coordinate, Coordinate> previousTrack = new HashMap<>();
		HashMap<Coordinate, Integer> distance = new HashMap<>();

		// add current position as the start
		path.add(curPos);

		addSurrondCoordinates(path, worldMap, previousTrack, curPos);

		while (!path.isEmpty()) {
			Coordinate newCoord = path.poll();
			// has find its goal
			if (newCoord.equals(destination)) {
				return retrivePath(previousTrack,curPos,destination);
			}
			addSurrondCoordinates(path, worldMap, previousTrack, newCoord);
		}
		return null;

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
			HashMap<Coordinate, Coordinate> previousTrack, Coordinate curPos) {
		// add coordinates surround with currentPos
		worldMap.forEach((k, v) -> {
			if (k.x == curPos.x + 1 && k.y == curPos.y && !v.isType(Type.WALL)) {
				path.add(k);
				if (!previousTrack.containsKey(k)) {
					previousTrack.put(k, curPos);
				}
			} else if (k.x == curPos.x - 1 && k.y == curPos.y && !v.isType(Type.WALL)) {
				path.add(k);
				if (!previousTrack.containsKey(k)) {
					previousTrack.put(k, curPos);
				}
			} else if (k.x == curPos.x && k.y == curPos.y + 1 && !v.isType(Type.WALL)) {
				path.add(k);
				if (!previousTrack.containsKey(k)) {
					previousTrack.put(k, curPos);
				}
			} else if (k.x == curPos.x && k.y == curPos.y - 1 && !v.isType(Type.WALL)) {
				path.add(k);
				if (!previousTrack.containsKey(k)) {
					previousTrack.put(k, curPos);
				}
			}
		});

	}
}
