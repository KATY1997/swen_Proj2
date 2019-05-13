package world;

import java.util.HashMap;
import java.util.Optional;
import java.lang.Math;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;

import swen30006.driving.Simulation;

import exceptions.NoStartLocationException;
import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;

import world.WorldSpatial.*;

/**
 * This class provides functionality for use within the simulation system. It is NOT intended to be
 * read or understood for SWEN30006 Part C. The lack of comments is intended to reinforce this.
 * We take no responsibility if you use time unproductively trying to understand this code.
 */
public class Car extends Sprite{

	// Logger
	private static Logger logger = LogManager.getFormatterLogger();

	private Direction currentOrientation = Direction.EAST;
	private int velocity = 0;
	private Optional<RelativeDirection> turning;
	private static enum Acceleration { FORWARD, REVERSE };
	private Optional<Acceleration> accelerating;
	
	private static final int MAX_FORWARD = 1; //8;
	private static final int MAX_BACKWARD = -1; //-4;
	private static final int MAX_TURNING = 5;

	private float rotation = 0;

	private static final int VELOCITY_EPSILON = 2;
	private static final int WALL_DAMAGE = 5;

	private float health;
	private float healthUsage;
	private float initialFuel;
	private float fuel;
	
	public static final int VIEW_SQUARE = 4;

	public final int targetParcels; // The number of parcels we need to find
	private int numParcels; // the number of parcels found

	Car(Sprite sprite, int targetParcels, int fuel, int health){
		super(sprite);
		this.targetParcels = targetParcels;
		resetControls();
		this.health = health;
		healthUsage = health;
		this.initialFuel = fuel;
		this.fuel = fuel;
		numParcels = 0;
		if (World.getStart() == null) {
			try {
				throw new NoStartLocationException();
			} catch (NoStartLocationException e) {
				e.printStackTrace();
			}
		} else {
			// System.out.println("Start - Coord: "+World.getStart());
			setX(World.getCarStart().x);
			setY(World.getCarStart().y);
		}
		// CAR_WIDTH = (int) sprite.getWidth();
		// CAR_HEIGHT = (int) sprite.getHeight();
		this.currentOrientation = WorldSpatial.Direction.EAST;
	}

	public void update(float delta0) {
			float delta=0.25f;
			if(Simulation.DEBUG_MODE){
				printDebug();
			}
			// logger.info("accelerating: %5s; turning: %5s", accelerating, turning);
			// Get the current tile
			MapTile currentTile = World.lookUp(getX(), getY());
			MapTile.Type currentType = currentTile.getType();
			
			/* Check if end of simulation condition met */
			// Already dead? You lose!
			if(health < 0.5){
				lose("NO HEALTH. GAME OVER. ESCAPE FAILED!!");
			}
			if (fuel < 0.5) {
				lose("RAN OUT OF FUEL. GAME OVER. ESCAPE FAILED!!");
			}
			// Made it to finish with the last parcel? You win!!!
			if(MapTile.Type.FINISH == currentType && hasEnoughParcels()) {
				if (Simulation.toConserve() == Simulation.StrategyMode.FUEL) {
					System.out.println("Fuel usage: " + (initialFuel - getFuel()));
				} else if (Simulation.toConserve() == Simulation.StrategyMode.HEALTH) {
					System.out.println("Health usage: "  + getHealthUsage());
				}
				
				Simulation.endGame(true);
			}
			// Can't move? You lose!
			if((velocity < VELOCITY_EPSILON) && MapTile.Type.TRAP == currentType && !((TrapTile) currentTile).canAccelerate()){
				lose("STUCK IN TRAP. GAME OVER. ESCAPE FAILED!!");
			}
			
			/* Update this car */
			
			applySteering(delta);
			setRotation(WorldSpatial.rotation(currentOrientation));

			// Apply the acceleration to velocity
			applyAcceleration(delta);

			applyVelocity(delta);


			resetControls();
	}

	private void lose(String message) {
		System.out.println(message);
		Simulation.endGame(false); // You lose!
	}
	
	public void reduceHealth(float damage) {
		health -= damage;
	}

	public void increaseHealth(float repair) {
		health += repair;
		healthUsage += repair;
	}
	
	protected void findParcel() {
		numParcels++;
	}
	
	public int numParcelsFound() {
		return numParcels;
	}
	
	public void applyForwardAcceleration(){
		accelerating = Optional.of(Acceleration.FORWARD);
	}

	public void applyReverseAcceleration(){
		accelerating = Optional.of(Acceleration.REVERSE);
	}

	public void brake(){
		if (velocity > 0) {
			applyReverseAcceleration();
		} else if (velocity < 0) {
			applyForwardAcceleration();
		}
	}

	public void turnLeft(){
		turning = Optional.of(RelativeDirection.LEFT);
	}

	public void turnRight() {
		turning = Optional.of(RelativeDirection.RIGHT);
	}

	private void applySteering(float delta){
		// Can't steer if you are on certain traps!
		MapTile currentTile = World.lookUp(getX(), getY());
		if (turning.isPresent() &&
				velocity != 0 &&
				Math.abs(velocity) <= MAX_TURNING &&
				(!currentTile.isType(MapTile.Type.TRAP) ||
						((TrapTile) currentTile).canTurn())) {
			if (velocity > 0) {
				currentOrientation = WorldSpatial.changeDirection(currentOrientation, turning.get());
			} else {
				currentOrientation = WorldSpatial.changeDirection(currentOrientation, WorldSpatial.opposite(turning.get()));
			}
		}
//		logger.info("Velocity: %5d; Orientation: %5s; Position: %5s; turning: %5s",
//				getVelocity(), currentOrientation, getPosition(), turning);
	}

	private void applyAcceleration(float delta) {
		// Can't accelerate if you are on certain traps!
		MapTile currentTile = World.lookUp(getX(), getY());
		if (accelerating.isPresent() && (!currentTile.isType(MapTile.Type.TRAP) || ((TrapTile) currentTile).canAccelerate())) {
			if (accelerating.get() == Acceleration.FORWARD) {
				if (velocity < 0) {
					velocity += 1;
				} else if (velocity == 0) {
					velocity = 1;
				} else { // (velocity > 0)
					velocity *= 2;
					if (velocity > MAX_FORWARD) velocity = MAX_FORWARD;
				}
			} else {
				if (velocity > 0) {
					velocity -= 1;
				} else if (velocity == 0) {
					velocity = -1;
				} else { // (velocity < 0)
					velocity *= 2;
					if (velocity < MAX_BACKWARD) velocity = MAX_BACKWARD;
				}
			}
		}
//		logger.info("Velocity: %5d; Orientation: %5s; Position: %5s; accelerating: %5s",
//				getVelocity(), currentOrientation, getPosition(), accelerating);
	}

	private void applyVelocity(float delta) {
		if (velocity == 0) {
			MapTile currentTile = World.lookUp(getX(), getY());
			// Check if you are standing on a trap!
			if (currentTile.isType(MapTile.Type.TRAP)) {
					((TrapTile) currentTile).applyTo(this, delta);
			}
		} else {
			int displacement = Math.abs(velocity);
			Direction orientation = velocity > 0 ? currentOrientation : WorldSpatial.reverseDirection(currentOrientation);
			tileStep(orientation, displacement, delta);
		}
	}
	
	private void tileStep(Direction d, int nSteps, float delta) {
		Coordinate dd = directionDelta(d);
		float nextx, nexty;
		MapTile nextTile;
		for (int i = 0; i < nSteps; i++) {
			fuel -= 1;
			
			nextx = getX()+dd.x;
			nexty = getY()+dd.y;
			nextTile = World.lookUp(nextx, nexty);
			if (nextTile.isType(MapTile.Type.WALL)) {
				reduceHealth(WALL_DAMAGE*(nSteps-i));
				velocity /= -2; // Bounce back at half speed
				break;
			}
			setX(nextx);
			setY(nexty);

			// Check if you are standing on a trap!
			if (nextTile.isType(MapTile.Type.TRAP)) {
				((TrapTile) nextTile).applyTo(this, delta);
				// if the trap is water and you just exhausted it, remove it
				if (nextTile instanceof tiles.WaterTrap) {
					// remove from rendered map
					changeToRoad(Simulation.map, Math.round(nextx), Math.round(nexty));
					// remove from internal map
					World.toRoad(nextx, nexty);
				} else if (nextTile instanceof tiles.ParcelTrap) {
					findParcel();
					changeToRoad(Simulation.map, Math.round(nextx), Math.round(nexty));
					World.toRoad(nextx, nexty);
				}
			}
		}
	}
	
	private void changeToRoad(TiledMap map, int x, int y) {
		TiledMapTileLayer l = (TiledMapTileLayer)map.getLayers().get("Trap");
		map.getLayers().remove(l);
		Cell newC = new Cell();
		TiledMapTileSet tileSet = map.getTileSets().getTileSet("road");
		TiledMapTile tile = tileSet.getTile(3);
		newC.setTile(tile);
		l.setCell(Math.round(x), Math.round(y), newC);
		map.getLayers().add(l);
	}

	private void resetControls(){
		turning = Optional.empty();
		accelerating = Optional.empty();
	}

	void draw(SpriteBatch spriteBatch){
		update(Gdx.graphics.getDeltaTime());
	}

	public void setVelocity(int v) { /* Better if this wasn't public but needed in traps */
		velocity = v;
	}
	
	public Coordinate directionDelta(Direction d) {
		switch (d) {
		case NORTH:
			return new Coordinate( 0,  1);
		case EAST:
			return new Coordinate( 1,  0);
		case SOUTH:
			return new Coordinate( 0, -1);
		case WEST:
			return new Coordinate(-1,  0);
		default:
			return new Coordinate( 0,  0); // Should never happen
		}
	}

	/** ACCESSIBLE METHODS **/
	
	public float getSpeed(){   // FIX Type
		return Math.abs(velocity);
	}

	public int getVelocity(){
		return velocity;
	}

	// Debug mode for the car
	public void printDebug(){
		MapTile tile = World.lookUp(getX(), getY());
		MapTile.Type tileType = tile.getType();
		String trapType = (tileType == MapTile.Type.TRAP ? "("+((TrapTile) tile).getTrap()+")":"");
		logger.info("Speed: %5.1f; Angle: %6s; Position: %5s; Parcels: %6s; Health: %5.1f; Health Usage: %5s; Fuel: %5.1f; Tile: %s%s",
				getSpeed(), getOrientation(), getPosition(), numParcels, getHealth(), healthUsage, getFuel(), tileType, trapType);
		System.out.printf("Car location: (%5.2f, %5.2f)%n", getX(), getY());
	}

	public HashMap<Coordinate,MapTile> getView(){
		int currentX = Math.round(getX());
		int currentY = Math.round(getY());

		HashMap<Coordinate,MapTile> subMap = new HashMap<Coordinate,MapTile>();
		for(int x = currentX - VIEW_SQUARE; x <= currentX+VIEW_SQUARE; x++){
			for(int y = currentY - VIEW_SQUARE; y <= currentY+VIEW_SQUARE; y++){
				MapTile tile = World.lookUp(x,y);
				subMap.put(new Coordinate(x,y),tile);
			}
		}
		// System.out.printf("subMap: %s%n", subMap);
		return subMap;
	}

	public String getPosition(){
		return Math.round(this.getX())+","+Math.round(this.getY());
	}
	
	public float getHealth(){
		return this.health;
	}
	
	public float getHealthUsage() {
		return this.healthUsage;
	}
	
	public float getFuel() {
		return this.fuel;
	}
	
	private boolean hasEnoughParcels() {
		return (numParcels >= targetParcels);
	}
	
	public float getAngle(){
		return (rotation % 360 + 360) % 360;
	}
	
	public WorldSpatial.Direction getOrientation(){
		return this.currentOrientation;
	}

}
