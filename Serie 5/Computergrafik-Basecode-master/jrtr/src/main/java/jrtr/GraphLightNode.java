package jrtr;

import java.util.LinkedList;

import javax.vecmath.Matrix4f;

public class GraphLightNode extends GraphLeaf {

	private Light light;

	public GraphLightNode(Light light) {
		this.light = light;
	}

	/**
	 * returns the position of the light as a Matrix4f
	 */
	@Override
	public Matrix4f getTransformation() {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		matrix.setTranslation(light.position);

		return matrix;
	}

	@Override
	public void getShapeItems(LinkedList<RenderItem> items, Matrix4f transformation) {
	}
	
	@Override
	public void setTransformation(Matrix4f transformation) {
		// TODO: something usefull
	}

}
