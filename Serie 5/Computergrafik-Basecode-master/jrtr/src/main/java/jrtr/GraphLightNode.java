package jrtr;

import javax.vecmath.Matrix4f;

public class GraphLightNode extends GraphLeaf{

	Light light;
	
	public GraphLightNode(GraphNode parent, Light light){
		super(parent);
		this.light = light;
	}
	
	public Light getLight(){
		return light;
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

}
