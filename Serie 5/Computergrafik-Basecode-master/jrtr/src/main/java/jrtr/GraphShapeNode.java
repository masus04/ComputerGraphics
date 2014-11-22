package jrtr;

import java.util.LinkedList;

import javax.vecmath.Matrix4f;

public class GraphShapeNode extends GraphLeaf {

	private Shape shape;
	private Matrix4f transformation;
	
	public GraphShapeNode(Shape shape){
		this.shape = shape;
		this.transformation = shape.getTransformation();
	}
	
	@Override
	public void getShapeItems(LinkedList<RenderItem> items, Matrix4f transformation){
		Matrix4f trans = new Matrix4f();
		trans.mul(transformation, getTransformation());
		
		items.add(new RenderItem(shape, trans));
	}

	@Override
	public Matrix4f getTransformation() {
		return transformation;
	}
	
	@Override
	public void setTransformation(Matrix4f transformation) {
		this.transformation = transformation;
	}
	
	/**
	 * 
	 * @return true if the Shape lies at least partly inside the view frustum and is therefore displayed
	 */
	public boolean viewFrustumCulling(Frustum frustum){
		return shape.getBoundingBox().isOverlapping(frustum);
	}
}
