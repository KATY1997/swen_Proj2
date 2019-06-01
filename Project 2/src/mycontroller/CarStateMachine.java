package mycontroller;

import controller.CarController;

/**
 * Represent the state of the car, handle state switch if meet change
 * requirements. The car could be in one of the four states: EXPLOREMAP,
 * FINDPARCEL, FINDEXIT, FINDHEALTH
 * 
 * @author kedi peng
 *
 */
public class CarStateMachine {
	private static CarStateMachine carStateMachine = null;
	private CarState carState;
	private CarState previousState;
	private static final int MIN_HEALTH = 10; // minimum health limit when car need to find health
	private static final int HEALTH_THRESHOLD = 30; // a limit that the car do not need to find health anymore

	private CarStateMachine() {
		carState = CarState.EXPLOREMAP; // construct as EXPLOREMAP in default
	}

	// the car could be in one of the following state
	public enum CarState {
		EXPLOREMAP, FINDPARCEL, FINDEXIT, FINDHEALTH
	};

	public static CarStateMachine getInstance() {
		if (carStateMachine == null) {
			carStateMachine = new CarStateMachine();
		}
		return carStateMachine;
	}

	/**
	 * update the car state every time update() method being called in
	 * MyAutoController
	 */
	public void update(CarController controller) {
		System.out.println(carState);
		switch (carState) {
		case EXPLOREMAP:
			if (controller.numParcelsFound() < controller.numParcels()
					&& Sensor.getInstance().getParcels().size() > 0) {
				this.carState = CarState.FINDPARCEL;
				this.previousState = CarState.EXPLOREMAP;
				//				System.out.println("switched");
			} else if (controller.getHealth() <= MIN_HEALTH) {
				this.carState = CarState.FINDHEALTH;
				this.previousState = CarState.EXPLOREMAP;
			}
			break;
		case FINDPARCEL:
			if ((Sensor.getInstance().getParcels().size() == 0
					&& controller.numParcelsFound() < controller.numParcels())) {
				this.carState = CarState.EXPLOREMAP;
				this.previousState = CarState.FINDPARCEL;
			} else if (controller.numParcelsFound() == controller.numParcels()
					&& Sensor.getInstance().getExit() != null) {
				this.carState = CarState.FINDEXIT;
				this.previousState = CarState.FINDPARCEL;
			} else if (controller.getHealth() <= MIN_HEALTH) {
				this.carState = CarState.FINDHEALTH;
				this.previousState = CarState.FINDPARCEL;
			}
			break;
		case FINDEXIT:
			if (controller.getHealth() <= MIN_HEALTH) {
				this.carState = CarState.FINDHEALTH;
				this.previousState = CarState.FINDEXIT;
			}
			break;
		case FINDHEALTH:
			if (controller.getHealth() >= HEALTH_THRESHOLD) {
				this.carState = previousState;
				this.previousState = CarState.FINDHEALTH;
			}
		}
	}

	public CarState getCarState() {
		return this.carState;
	}

}
