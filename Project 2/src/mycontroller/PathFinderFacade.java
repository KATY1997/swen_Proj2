package mycontroller;

import org.omg.PortableServer.ServantLocatorPackage.CookieHolder;

import mycontroller.strategy.PathStrategy;
import mycontroller.strategy.StrategyFactory;
import utilities.Coordinate;

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
			strategy.removeAvoidTiles();
			if (Sensor.getInstance().detectIce()) {
				command = "brake";
			}else {
				command = strategy.findHealthTrap();
			}
//			command =  strategy.explore();
			strategy.addAvoidTiles();
			break;
		}
		return command;
	}

}
