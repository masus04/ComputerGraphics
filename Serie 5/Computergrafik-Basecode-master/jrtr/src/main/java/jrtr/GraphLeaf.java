package jrtr;

public abstract class GraphLeaf implements GraphNode {
	@Override
	public GraphNode getChildShape(int index) {
		return null;
	}

	@Override
	public GraphNode getChildLight(int index) {
		return null;
	}
}
