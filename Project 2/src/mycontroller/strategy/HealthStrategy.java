package mycontroller.strategy;

public class HealthStrategy extends PathStrategy {
	
	public HealthStrategy() {
		avoid.add("water");
		avoid.add("health");
		searchRange = 200;
	}

}
