package jrtr;

import java.util.LinkedList;

import javax.vecmath.Matrix4f;

public abstract interface GraphNode {
	
	public abstract Matrix4f getTransformation();
	public abstract void getShapeItems(LinkedList<RenderItem> RenderItems, Matrix4f transformation);
	public abstract boolean isLeaf();
}
