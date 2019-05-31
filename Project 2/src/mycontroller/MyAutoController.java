package mycontroller;

import controller.CarController;
import utilities.Coordinate;
import world.Car;

public class MyAutoController extends CarController {
	
	PathFinderFacade facade;

	public MyAutoController(Car car) {
		super(car);
		facade = new PathFinderFacade();
	}

	@Override
	public void update() {

		// update sensor and carState each move
		Sensor.getInstance().update(this);
		CarStateMachine.getInstance().update(this);

		if (Sensor.getInstance().getCurrentPos().equals(new Coordinate(3, 8))) {
			System.out.println("test");
		}
		
		// using facade, return a command
		String command = facade.findPath();
		System.out.println(command);
		
		parseCommand(command);
	}

	/**
	 * parce the returned command into car's operation
	 * @param command
	 */
	private void parseCommand(String command) {
		switch (command) {
		case "left":
			turnLeft();
			break;
		case "right":
			turnRight();
			break;
		case "forward":
			applyForwardAcceleration();
			break;
		case "reverse":
			applyReverseAcceleration();
			break;
		case "brake":
			applyBrake();
			break;
		default:
			applyForwardAcceleration();
			break;
		}
	}

}
