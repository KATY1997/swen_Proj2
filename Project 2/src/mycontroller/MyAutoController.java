package mycontroller;

import controller.CarController;
import mycontroller.strategy.PathStrategy;
import mycontroller.strategy.StrategyFactory;
import world.Car;

public class MyAutoController extends CarController {

	private PathStrategy strategy;

	public MyAutoController(Car car) {
		super(car);
		this.strategy = StrategyFactory.getStrategy();
	}

	@Override
	public void update() {

		Sensor.getInstance().update(this);
		CarStateMachine.getInstance().update(this);

		// using facade, return a command
		String command = PathFinderUtil.findPath(CarStateMachine.getInstance().getCarState(),strategy);
		
//		String command = strategy.update(CarStateMachine.getInstance().getCarState());

		parseCommand(command);
	}

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
		}
	}

}
