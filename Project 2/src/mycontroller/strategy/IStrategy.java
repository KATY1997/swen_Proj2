package mycontroller.strategy;

/**
 * An interface that defined methods in strategies
 * @author kedi peng
 *
 */
public interface IStrategy {

	
	public String explore();

	public String findParcel();

	public String findExit();
	
	public String findHealthTrap();
	
}
