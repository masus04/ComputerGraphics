package jrtr;

import javax.vecmath.Matrix4f;

public class GraphTransformGroup extends GraphGroup{

	Matrix4f transformation;
	
	public GraphTransformGroup(GraphNode parent, Matrix4f transformation){
		super(parent);
		this.transformation = transformation;
	}
	
	@Override
	public Matrix4f getTransformation() {
		return transformation;
	}

}
