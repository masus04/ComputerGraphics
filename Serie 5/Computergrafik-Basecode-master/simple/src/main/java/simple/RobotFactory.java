package simple;

import jrtr.GraphTransformGroup;

public class RobotFactory {

	private GraphTransformGroup root;
	
	public RobotFactory(GraphTransformGroup root){
		this.root = root;
		
		makeRobot();
	}

	private void makeRobot() {
		makeTorso();
		makeArms();
		makeLegs();
		makeHead();
	}

	private void makeHead() {
		// TODO Auto-generated method stub
		
	}

	private void makeLegs() {
		// TODO Auto-generated method stub
		
	}

	private void makeArms() {
		// TODO Auto-generated method stub
		
	}

	private void makeTorso() {
		// TODO Auto-generated method stub
		
	}
	
}
