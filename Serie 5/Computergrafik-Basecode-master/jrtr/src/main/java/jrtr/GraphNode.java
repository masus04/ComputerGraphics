package jrtr;

import javax.vecmath.Matrix4f;

public abstract class GraphNode {
	
	GraphNode parent;
	
	public abstract Matrix4f getTransformation();
	public abstract GraphShapeNode getChildShape(int index);
	public abstract GraphLightNode getChildLight(int index);
	public abstract GraphGroup getChildGroup(int index);
}
