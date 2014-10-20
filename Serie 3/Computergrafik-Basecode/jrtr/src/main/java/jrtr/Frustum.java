package jrtr;

import javax.vecmath.Matrix4f;

/**
 * Stores the specification of a viewing frustum, or a viewing
 * volume. The viewing frustum is represented by a 4x4 projection
 * matrix. You will extend this class to construct the projection 
 * matrix from intuitive parameters.
 * <p>
 * A scene manager (see {@link SceneManagerInterface}, {@link SimpleSceneManager}) 
 * stores a frustum.
 */
public class Frustum {

	private Matrix4f projectionMatrix;
	
	/**
	 * Construct a default viewing frustum. The frustum is given by a 
	 * default 4x4 projection matrix.
	 */
	public Frustum()
	{
		projectionMatrix = new Matrix4f();
		float f[] = {2.f, 0.f, 0.f, 0.f, 
					 0.f, 2.f, 0.f, 0.f,
				     0.f, 0.f, -1.02f, -2.02f,
				     0.f, 0.f, -1.f, 0.f};
		projectionMatrix.set(f);
	}
	
	/**
	 * Return the 4x4 projection matrix, which is used for example by 
	 * the renderer.
	 * 
	 * @return the 4x4 projection matrix
	 */
	public Matrix4f getProjectionMatrix()
	{
		return projectionMatrix;
	}
	
	public void setProjectionMatrix(float nearPlane,float  farPlane, float aspectRatio,float verticalFieldOfView){
		projectionMatrix = new Matrix4f();
		
		
		float[] col1 = {(float) (1/(aspectRatio*Math.tan(verticalFieldOfView/2))),0,0,0};
		projectionMatrix.setColumn(0, col1);
		
		float[] col2 = {0,(float) (1/(Math.tan(verticalFieldOfView/2))),0,0};
		projectionMatrix.setColumn(1, col2);
		
		float[] col3 = {0,0,(nearPlane+farPlane)/(nearPlane-farPlane),-1};
		projectionMatrix.setColumn(2, col3);
		
		float[]col4 = {0,0,2*(nearPlane+farPlane)/(nearPlane-farPlane),0};
		projectionMatrix.setColumn(3, col4);
		
		/*
		projectionMatrix.setElement(0, 0, (float) (1/(aspectRatio*Math.tan(verticalFieldOfView/2))));
		projectionMatrix.setElement(1, 1, (float) (1/(Math.tan(verticalFieldOfView/2))));
		projectionMatrix.setElement(2, 2, (nearPlane+farPlane)/(nearPlane-farPlane));
		projectionMatrix.setElement(2, 3, 2*(nearPlane+farPlane)/(nearPlane-farPlane));
		projectionMatrix.setElement(3, 2, -1);
		*/
	}
}
