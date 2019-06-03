package mycontroller;

import mycontroller.strategy.PathStrategy;
import mycontroller.strategy.StrategyFactory;

/**
 * The facade class is used to handle the interaction of strategies
 * @author kedi peng
 *
 */
public class PathFinderFacade {

	private PathStrategy strategy;
	private String command = "forward";
	
	public PathFinderFacade() {
		this.strategy = StrategyFactory.getStrategy();
	}

	/**
	 * This method will switch the state of the car, call corresponding method
	 * based on the state
	 * @return
	 */
	public String findPath() {
	
		switch (CarStateMachine.getInstance().getCarState()) {
		case EXPLOREMAP:
			command =  strategy.explore();
			break;
		case FINDEXIT:
			command =  strategy.findExit();
			break;
		case FINDPARCEL:
			command =  strategy.findParcel();
			break;
		case FINDHEALTH:
//			if (Sensor.getInstance().detectIce()) {
//				command = "brake";
//			}else {
//				command = strategy.findHealthTrap();
//			}
			command =  strategy.explore();
			break;
		}
		return command;
	}

}
