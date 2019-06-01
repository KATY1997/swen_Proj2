package mycontroller.strategy;

import swen30006.driving.Simulation;

/**
 * The StrategyFactory is used to create different strategies based on what need
 * to conserve in the driving.proporties
 * 
 * @author kedi peng
 *
 */
public class StrategyFactory {

	private static PathStrategy strategy;

	public static PathStrategy getStrategy() {
		if (Simulation.toConserve() == Simulation.StrategyMode.FUEL) {
			strategy = new FuelStrategy();
		} else if (Simulation.toConserve() == Simulation.StrategyMode.HEALTH) {
			strategy = new HealthStrategy();
		}

		return strategy;
	}

}
