package mycontroller.strategy;

import mycontroller.CarStateMachine.CarState;
import world.WorldSpatial;

/**
 * 
 * @author pengkedi
 *
 */
public interface IStrategy {

	
	public String explore();

	public String findParcel();

	public String findExit();
	
	public String findHealthTrap();
	
}
