package jrtr;

import javax.vecmath.Matrix4f;

public interface GraphNode {
	
	public Matrix4f getTransformation();
	
	public GraphNode getChildShape(int index);
	public GraphNode getChildLight(int index);
}
