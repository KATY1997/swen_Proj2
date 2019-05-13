package world;

public class WorldSpatial {
	
	public enum Direction { EAST, WEST, SOUTH, NORTH}
	
	public static enum RelativeDirection { LEFT, RIGHT };
	
	public final static int EAST_DEGREE_MIN = 0;
	public final static int EAST_DEGREE_MAX = 360;
	public final static int NORTH_DEGREE = 90;
	public final static int WEST_DEGREE = 180;
	public final static int SOUTH_DEGREE = 270;
	
	public static RelativeDirection opposite(RelativeDirection r) {
		return (r==RelativeDirection.RIGHT) ? RelativeDirection.LEFT : RelativeDirection.RIGHT;
	}
	
	public static Direction changeDirection(Direction d, RelativeDirection r) {
		switch (r) {
		case LEFT:
			switch (d) {
			case NORTH:
				return Direction.WEST;
			case EAST:
				return Direction.NORTH;
			case SOUTH:
				return Direction.EAST;
			case WEST:
				return Direction.SOUTH;
			}
		case RIGHT:
			switch (d) {
			case NORTH:
				return Direction.EAST;
			case EAST:
				return Direction.SOUTH;
			case SOUTH:
				return Direction.WEST;
			case WEST:
				return Direction.NORTH;
			}
		}
		return Direction.EAST; // Should never happen
	}
	
	public static Direction reverseDirection(Direction d) {
		switch (d) {
		case NORTH:
			return Direction.SOUTH;
		case EAST:
			return Direction.WEST;
		case SOUTH:
			return Direction.NORTH;
		case WEST:
			return Direction.EAST;
		}
		return Direction.EAST; // Should never happen
	}
	
	public static float rotation(Direction d) {
		switch (d) {
		case NORTH:
			return 90.0f;
		case EAST:
			return 0.0f;
		case SOUTH:
			return 270.0f;
		case WEST:
			return 180.0f;
		}
		return 0.0f; // Should never happen
	}
}
