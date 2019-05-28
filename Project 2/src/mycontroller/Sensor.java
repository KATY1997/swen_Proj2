package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import tiles.MapTile;
import tiles.ParcelTrap;
import utilities.Coordinate;

public class Sensor {
	
	private static Sensor sensor = null;
	public static HashMap<Coordinate, ParcelTrap> parcels;
	public static HashMap<Coordinate, MapTile> currentView;
	public static Coordinate exit;
	
	private Sensor(){
		
	};
	
	public static Sensor getInstance(HashMap<Coordinate, MapTile> currentView){
		if (sensor ==null) {
			sensor = new Sensor();
		}
		sensor.currentView = currentView;
		return sensor;
	}
	
}
