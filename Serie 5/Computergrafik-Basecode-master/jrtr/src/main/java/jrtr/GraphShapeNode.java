package jrtr;

import java.util.LinkedList;

import javax.vecmath.Matrix4f;

public class GraphShapeNode extends GraphLeaf {

	Shape shape;
	
	public GraphShapeNode(GraphNode parent, Shape shape){
		this.shape = shape;
	}
	
	@Override
	public void getShapeItems(LinkedList<RenderItem> items, Matrix4f transformation){
		Matrix4f trans = new Matrix4f();
		trans.mul(transformation, shape.getTransformation());
		
		items.add(new RenderItem(shape, trans));
	}

	@Override
	public Matrix4f getTransformation() {
		return shape.getTransformation();
	}
}
