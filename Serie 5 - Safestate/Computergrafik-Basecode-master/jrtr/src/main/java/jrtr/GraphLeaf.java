package jrtr;

public abstract class GraphLeaf implements GraphNode {

	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public GraphGroup getGraphGroup(int index) {
		return null;
	}
}
