package jrtr;

import javax.vecmath.Matrix4f;

public class GraphTransformGroup extends GraphGroup{

	Matrix4f transformation;
	
	public GraphTransformGroup(Matrix4f transformation){
		this.transformation = transformation;
	}
	
	@Override
	public Matrix4f getTransformation() {
		return transformation;
	}

}
