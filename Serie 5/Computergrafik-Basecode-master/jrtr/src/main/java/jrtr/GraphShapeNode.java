package jrtr;

import javax.vecmath.Matrix4f;

public class GraphShapeNode extends GraphLeaf {

	Shape shape;
	
	public GraphShapeNode(GraphNode parent, Shape shape){
		super(parent);
		this.shape = shape;
	}
	
	public Shape getShape(){
		return shape;
	}

	@Override
	public Matrix4f getTransformation() {
		return shape.getTransformation();
	}
}
