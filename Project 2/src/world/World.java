package world;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.ArrayList;
import java.util.TreeSet;
import java.lang.reflect.Constructor;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;

import controller.CarController;
import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
/**
 * This class provides functionality for use within the simulation system. It is NOT intended to be
 * read or understood for SWEN30006 Part C. The lack of comments is intended to reinforce this.
 * We take no responsibility if you use time unproductively trying to understand this code.
 */

public class World {
	
	private Car car;
	
	// Car's controller
	private static CarController controller;
	
	private static TiledMap map;
	
	public static int MAP_PIXEL_SIZE = 32;
	public static int MAP_HEIGHT;
	public static int MAP_WIDTH;
	
	private static String[] LAYER_NAME = {"Road","Utility","Trap","Wall"};
	
	private static HashMap<Coordinate,MapTile> mapTiles = new HashMap<Coordinate,MapTile>();
	private static HashMap<Coordinate,MapTile> providedMapTiles = new HashMap<Coordinate,MapTile>();
	private static Coordinate start, carStart;
	private static List<Coordinate> finish = new ArrayList<Coordinate>();
	
	public World(TiledMap map, String controllerName, Properties drivingProperties){
		World.map = map;
		TiledMapTileLayer roadLayer = (TiledMapTileLayer) getTiledMap().getLayers().get("Road");
		MAP_HEIGHT = roadLayer.getHeight();
		MAP_WIDTH = roadLayer.getWidth();
		int numParcels = Integer.parseInt(drivingProperties.getProperty("ParcelsNeeded"));
		initializeMap(map);
		
		car = new Car(new Sprite(new Texture("sprites/car2.png")), numParcels, Integer.parseInt(drivingProperties.getProperty("Fuel")), Integer.parseInt(drivingProperties.getProperty("Health")));
		// Set car size relative to the map scaling.
		car.setSize(car.getWidth()*(1f/MAP_PIXEL_SIZE), car.getHeight()*(1f/MAP_PIXEL_SIZE));
		car.setOriginCenter();

		// Add the car controller
		try {
			Class<?> clazz = Class.forName(controllerName);
			Class<?> [] params = new Class[] { Car.class };
			Constructor<?> constructor = clazz.getConstructor(params);
			controller = (CarController) constructor.newInstance(car);
		} catch (Exception e) {
//			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private int initializeMap(TiledMap map2) {  // Returns max(parcels in map)
		TreeSet<Integer> parcels = new TreeSet<Integer>();
		// Iterate through all layer names
		for(String layerName : LAYER_NAME){
			// Set the layer
			TiledMapTileLayer layer = (TiledMapTileLayer) getTiledMap().getLayers().get(layerName);
			
			// Iterate through the layers and input them into the hashtable
			// System.out.println(layerName+" width: "+layer.getWidth()+" height: "+layer.getHeight());
			for(int x = 0; x < layer.getWidth(); x++){
				for(int y = 0; y < layer.getHeight(); y++){
					Cell cell = layer.getCell(x, y);
					if(cell != null){
						MapTile newTile = null; // Only stays null if exception/exit
						int reverseYAxis = layer.getHeight()-y;
						Coordinate newCoord = new Coordinate(x, reverseYAxis);
						// System.out.println(layerName+" - Coord: "+newCoord+" ID: "+cell.getTile().getId());
						switch(layerName) {
						case "Trap":
							// assert(cell.getTile().getProperties().get("type") != null);
							String className = MapTile.tileNameSpace + (String) cell.getTile().getProperties().get("type");
							try {
								// newTile = (TrapTile) Class.forName( className ).newInstance();
								newTile = (TrapTile) Class.forName( className ).getConstructor().newInstance();
							} catch (Exception e) {
								e.printStackTrace();
								System.exit(1);
							}
							break;
						case "Utility":
							if(cell.getTile().getProperties().get("exit") != null){
								newTile = new MapTile(MapTile.Type.FINISH);
								finish.add(newCoord);
							} else {
								newTile = new MapTile(MapTile.Type.START);
								assert(null == start);
								carStart = new Coordinate(x, y);
								start = newCoord;
								// System.out.println("World Start - Coord: "+World.getStart());
							}
							break;
						case "Road":
							newTile = new MapTile(MapTile.Type.ROAD);
							break;
						case "Wall":
							newTile = new MapTile(MapTile.Type.WALL);
							break;
						}
						mapTiles.put(newCoord, newTile);
					}
				}
			}
		}
		// Check that parcels are a sequence
		assert(parcels.last()==parcels.size());
		assert(null != start);
		assert(finish.size() > 0);
		return parcels.size(); // the number of unique parcels
	}

	public void update(float delta){
		controller.update();

        // Update the car
        car.update(delta);
	}
	
	public void render(Batch batch){
		car.draw(batch);
	}
	
	protected static Coordinate getCarStart() {
		return carStart;
	}
	
	protected static Coordinate getStart() {
		return start;
	}
	
	protected static List<Coordinate> getFinish() {
		return finish;
	}
	
	protected static TiledMap getTiledMap(){
		return map;
	}
	
	protected static MapTile lookUp(double futureX, double futureY){
		int x = (int) Math.round(futureX);
		int y =  MAP_HEIGHT - (int) Math.round(futureY); // Convert Y coordinate
		Coordinate coord = new Coordinate(x,y);
		return mapTiles.containsKey(coord) ? mapTiles.get(coord) : new MapTile(MapTile.Type.EMPTY);
	}
	
	public Car getCar(){
		return this.car;
	}
	
	public static HashMap<Coordinate,MapTile> getMap(){
		if(providedMapTiles.keySet().size() == 0){ // Lazy initialisation
			for(Coordinate coord : mapTiles.keySet()){
				int reverseYAxis = MAP_HEIGHT-coord.y;
				Coordinate newCoord = new Coordinate(coord.x, reverseYAxis);
				MapTile current = mapTiles.get(coord);
				if (current.isType(MapTile.Type.TRAP)) current = new MapTile(MapTile.Type.ROAD);
				providedMapTiles.put(newCoord, current);
			}
		}
		return providedMapTiles;	
	}
	
	protected static void toRoad(float posX, float posY) {
		int coordx = (int) Math.round(posX);
		int coordy =  MAP_HEIGHT - (int) Math.round(posY); // Convert Y coordinate
		Coordinate coord = new Coordinate(coordx, coordy);
		mapTiles.put(coord, new MapTile(MapTile.Type.ROAD));
	}
}