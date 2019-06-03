package mycontroller;

import org.apache.logging.log4j.core.util.SystemNanoClock;
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
			if (strategy.canFlag) {
				System.out.println("can");
//				strategy.avoidTiles.remove("water");
//				command =  strategy.findParcel();
//				strategy.avoidTiles.add("water");
				strategy.canFlag = false;
			}
//			
			
			break;
		case FINDHEALTH:
			strategy.removeAvoidTiles();
			if (Sensor.getInstance().detectIce()  ) {
				command = "brake";
				strategy.isMoving = true;
			}else {
				command = strategy.findHealthTrap();
				System.out.println(command + "}");
			}
//			command =  strategy.explore();
			//strategy.addAvoidTiles();
			break;
		}
		System.out.println(command + "{{{{{");
		return command;
	}

}
