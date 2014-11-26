package jrtr;

public abstract class GraphGroup implements GraphNode {

	@Override
	public boolean isLeaf() {
		return false;
	}
}
