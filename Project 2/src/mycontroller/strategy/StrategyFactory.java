package mycontroller.strategy;

import swen30006.driving.Simulation;

public class StrategyFactory {

	private static PathStrategy strategy;
	
	public static PathStrategy getStrategy(){
		if (Simulation.toConserve() == Simulation.StrategyMode.FUEL) {
			strategy = new FuelStrategy();
		}else if (Simulation.toConserve() == Simulation.StrategyMode.HEALTH) {
			strategy = new HealthStrategy();
		}
		
		return strategy;
	}
	
}
