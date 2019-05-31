package mycontroller;

import com.sun.xml.internal.bind.v2.model.core.ID;

import mycontroller.strategy.PathStrategy;
import mycontroller.strategy.StrategyFactory;

public class PathFinderFacade {

	private PathStrategy strategy;
	private String command = "forward";
	
	public PathFinderFacade() {
		this.strategy = StrategyFactory.getStrategy();
	}

	/**
	 * 
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
//			if (Sensor.getInstance().standOnIce()) {
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
