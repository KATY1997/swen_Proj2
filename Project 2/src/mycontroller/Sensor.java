package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import controller.CarController;
import tiles.MapTile;
import tiles.MapTile.Type;
import tiles.TrapTile;
import utilities.Coordinate;
import world.WorldSpatial;

/**
 * A pure fabrication class called sensor to store informations in the map, the
 * sensor will store what the car has seen(currentView) and put it into
 * worldMap, it also contains some functions like to check whether the car
 * stands on ice.
 * 
 * @author kedi peng
 *
 */
public class Sensor {

	private static Sensor sensor;
	private ArrayList<Coordinate> parcels = new ArrayList<>();
	private ArrayList<Coordinate> healthTraps = new ArrayList<>();
	private HashMap<Coordinate, MapTile> currentView;
	private HashMap<Coordinate, MapTile> worldMap;
	private Coordinate exit;
	private Coordinate currentPos;
	private WorldSpatial.Direction orientation;

	private Sensor() {}

	public static Sensor getInstance() {
		if (sensor == null) {
			sensor = new Sensor();
		}
		return sensor;
	}

	/**
	 * update sensor's record each step
	 * 
	 * @param currentView
	 */
	public void update(CarController controller) {
		this.currentView = controller.getView();
		this.currentPos = new Coordinate(controller.getPosition());
		this.orientation = controller.getOrientation();

		if (this.worldMap == null) {
			this.worldMap = controller.getMap();
		} else {
			updateWorldMap();
		}

		scanCurrentView();

		// remove parcel from parcel list if a parcel has been collected
		for (int i = 0; i < parcels.size(); i++) {
			if (parcels.get(i).equals(currentPos)) {
				this.parcels.remove(parcels.get(i));
			}
		}

		//		// remove water from healthTrap list if a healthTile been collected
		//		if (currentView.get(currentPos).isType(Type.TRAP)) {
		//			TrapTile tile =(TrapTile) currentView.get(currentPos);
		//		}
		//		for (int i = 0; i < healthTraps.size(); i++) {
		//			if (healthTraps.get(i).equals(currentPos) ) {
		//				this.healthTraps.remove(parcels.get(i));
		//			}
		//		}

	}

	/**
	 * since the world map only record road and walls initially, update the
	 * world map each time the car moved, so the map now record traps' location
	 * 
	 * @return
	 */
	private void updateWorldMap() {
		currentView.forEach((k, v) -> {
			if (worldMap.containsKey(k)) {
				this.worldMap.replace(k, v);
			}
		});

	}

	public HashMap<Coordinate, MapTile> getCurrentView() {
		return this.currentView;
	}

	/**
	 * scan the currentView to locate parcel and store it in car's sensor, if
	 * there is any store location of EXIT
	 */
	public void scanCurrentView() {
		this.currentView.forEach((k, v) -> {
			if (v.isType(Type.TRAP)) {
				TrapTile tile = (TrapTile) v;
				if (tile.getTrap().equals("parcel")) {
					sensor.addParcel(k);
				} else if ((tile.getTrap().equals("water") || tile.getTrap().equals("health")) && healthTraps.size() < 5) {
					
					if (!healthTraps.contains(k)) {
						healthTraps.add(k);
					}
				}
			} else if (v.isType(Type.FINISH)) {
				sensor.setExit(k);
			}
		});
	}

	/**
	 * store the location of parcels in the car's sensor
	 * 
	 * @param coordinate
	 * @param parcelTrap
	 */
	public void addParcel(Coordinate coordinate) {
		if (!parcels.contains(coordinate)) {
			this.parcels.add(coordinate);
		}
	}

	/**
	 * remove the parcel from the list as long as it has been collected
	 * 
	 * @param coordinate
	 */
	public void removeParcels(Coordinate coordinate) {
		this.parcels.remove(coordinate);
	}

	public ArrayList<Coordinate> getParcels() {
		return this.parcels;
	}

	public Coordinate getCurrentPos() {
		return this.currentPos;
	}

	public WorldSpatial.Direction getOrientation() {
		return this.orientation;
	}

	public Coordinate getExit() {
		return this.exit;
	}

	public void setExit(Coordinate exit) {
		this.exit = exit;
	}

	public HashMap<Coordinate, MapTile> getWorldMap() {
		return this.worldMap;
	}

	/**
	 * Return the coordinates of health trap in the world map
	 * 
	 * @return true if their is any
	 */
	public ArrayList<Coordinate> getHealthTrap() {
		return this.healthTraps;
	}

	/**
	 * detect if the car is standing on ice
	 * 
	 * @return true if the car stands on ice
	 */
	public boolean detectIce() {
		if (currentView.get(currentPos).isType(Type.TRAP)) {
			TrapTile tile = (TrapTile) currentView.get(currentPos);
			if (tile.getTrap().equals("health")) {
				return true;
			}
		}
		return false;
	}

	public void removeHealthTrap(Coordinate c){
		if (healthTraps.contains(c)) {
			healthTraps.remove(c);
		}
	}
}
