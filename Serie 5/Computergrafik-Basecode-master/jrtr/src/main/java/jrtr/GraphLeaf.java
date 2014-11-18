package jrtr;

public abstract class GraphLeaf extends GraphNode {
	
	public GraphLeaf(GraphNode parent){
		this.parent = parent;
	}
	
	@Override
	public GraphShapeNode getChildShape(int index) {
		return null;
	}

	@Override
	public GraphLightNode getChildLight(int index) {
		return null;
	}
	
	@Override
	public GraphGroup getChildGroup(int index) {
		return null;
	}
}
