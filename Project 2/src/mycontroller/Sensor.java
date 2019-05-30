package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import controller.CarController;
import tiles.MapTile;
import tiles.TrapTile;
import tiles.MapTile.Type;
import utilities.Coordinate;
import world.WorldSpatial;

/**
 * A pure fabrication class called sensor to store informations in the map
 * @author pengkedi
 *
 */
public class Sensor {
	
	private static Sensor sensor;
	private static ArrayList<Coordinate> parcels = new ArrayList<>();
	private static HashMap<Coordinate, MapTile> currentView;
	private static HashMap<Coordinate, MapTile> worldMap;
	private static Coordinate exit;
	private static Coordinate currentPos;
	private static WorldSpatial.Direction orientation;
	
	
	

	private Sensor(){}
	
	public static Sensor getInstance(){
		if (sensor ==null) {
			sensor = new Sensor();
		}
		return sensor;
	}
	
	/**
	 * update sensor's record each step
	 * @param currentView
	 */
	public void update(CarController controller){
		this.currentView = controller.getView();
		this.currentPos = new Coordinate(controller.getPosition());
		this.orientation = controller.getOrientation();
		
		if (this.worldMap == null) {
			this.worldMap = controller.getMap();
		}else{
			updateWorldMap();
		}
		
		scanCurrentView();
		
//		for (Coordinate coordinate : parcels) {
//			System.out.println("parcels");
//			System.out.println(coordinate);
//		}
		
		
		// remove parcel from parcel list if a parcel has been collected
		
		for (int i = 0; i < parcels.size(); i++) {
			if (parcels.get(i).equals(currentPos)) {
				this.parcels.remove(parcels.get(i));
			}
		}
		
	}
	
	/**
	 * since the world map only record road and walls initially, 
	 * update the world map each time the car moved, so the map now record traps' location
	 * @return
	 */
	private void updateWorldMap() {
		currentView.forEach((k,v)-> {
			// find those tile which is not ROAD or WALL
			if (!v.isType(Type.ROAD) || !v.isType(Type.WALL)) {
					this.worldMap.replace(k, v);
			}
		} );
//		for (Coordinate c : currentView.keySet()) {
//			// find those tile which is not ROAD or WALL
//			if (!currentView.get(c).isType(Type.ROAD) || !currentView.get(c).isType(Type.WALL)) {
//				this.worldMap.replace(c, currentView.get(c));
//			}
//		}
		
	}

	public HashMap<Coordinate, MapTile> getCurrentView(){
		return this.currentView;
	}
	
	/**
	 * scan the currentView to locate parcel and store it in car's sensor, if there is any
	 * store location of EXIT
	 */
	public static void scanCurrentView() {
		currentView.forEach((k,v)->{
			if (v.isType(Type.TRAP)) {
				TrapTile pTrap = (TrapTile) v;
				if (pTrap.getTrap().equals("parcel")) {
					sensor.addParcel(k);
				}
			}else if (v.isType(Type.FINISH)) {
				sensor.setExit(k);
			}
		});
//		for (Coordinate c : currentView.keySet()) {
//			if (currentView.get(c).getType().equals("parcel")) {
//				sensor.addParcel(c);
//			}else if (currentView.get(c).isType(Type.FINISH)) {
//				sensor.setExit(c);
//			}
//		}
	}
	
	/**
	 * store the location of parcels in the car's sensor
	 * @param coordinate
	 * @param parcelTrap
	 */
	public void addParcel(Coordinate coordinate){
		if (!parcels.contains(coordinate)) {
			this.parcels.add(coordinate);
		}
	}
	
	/**
	 * remove the parcel from the list as long as it has been collected
	 * @param coordinate
	 */
	public void removeParcels(Coordinate coordinate){
		this.parcels.remove(coordinate);
	}
	
	public ArrayList<Coordinate> getParcels() {
		return this.parcels;
	}

	public Coordinate getCurrentPos() {
		return currentPos;
	}

	public WorldSpatial.Direction getOrientation() {
		return orientation;
	}

	public Coordinate getExit() {
		return exit;
	}

	public void setExit(Coordinate exit) {
		this.exit = exit;
	}
	
	public HashMap<Coordinate, MapTile> getWorldMap() {
		return worldMap;
	}
	
	
}
