package jrtr;

import javax.vecmath.*;

/**
 * Represents a 3D object. The shape references its geometry, that is, a
 * triangle mesh stored in a {@link VertexData} object, its {@link Material},
 * and a transformation {@link Matrix4f}.
 */
public class Shape {

	private Material material;
	private VertexData vertexData;
	private Matrix4f t;
	private BoundingBox boundingBox;

	/**
	 * Make a shape from {@link VertexData}. A shape contains the geometry (the
	 * {@link VertexData}), material properties for shading (a refernce to a
	 * {@link Material}), and a transformation {@link Matrix4f}.
	 * 
	 * 
	 * @param vertexData
	 *            the vertices of the shape.
	 */
	public Shape(VertexData vertexData)
	{
		this.vertexData = vertexData;
		t = new Matrix4f();
		t.setIdentity();

		material = null;
	}

	public VertexData getVertexData()
	{
		return vertexData;
	}

	public void setTransformation(Matrix4f t)
	{
		this.t = t;
	}

	public Matrix4f getTransformation()
	{
		return t;
	}

	/**
	 * Set a reference to a material for this shape.
	 * 
	 * @param material
	 *            the material to be referenced from this shape
	 */
	public void setMaterial(Material material)
	{
		this.material = material;
	}

	/**
	 * To be implemented in the "Textures and Shading" project.
	 */
	public Material getMaterial()
	{
		return material;
	}

	public void calculateBoundingSphere(Matrix4f completeTransformation, SceneManagerInterface sceneManager) {
		if (boundingBox == null){
			Matrix4f cTransformation = new Matrix4f(sceneManager.getCamera().getCameraMatrix());
			cTransformation.mul(completeTransformation);	// complete transformation in CAMERA COORDINATES
			cTransformation.mul(sceneManager.getFrustum().getProjectionMatrix(), cTransformation);
			
			float radius = calculateBoxRadius(calculateBalancePoint(cTransformation));
			
			boundingBox = new BoundingBox(calculateBalancePoint(cTransformation), radius);	// already in camCoord
		}
	}

	private float calculateBoxRadius(Vector4f balancePoint) {
		float[] positions = vertexData.getPositions();
		float maxRadius = 0;
		Vector4f point = new Vector4f();

		for (int i = 0; i < positions.length - 2; i += 3) {
			point = new Vector4f(positions[i]+balancePoint.x, positions[i + 1]+balancePoint.y, positions[i + 2]+balancePoint.z, 1);
			point.sub(balancePoint);

			if (point.length() > maxRadius)
				maxRadius = point.length();
		}
		return maxRadius;
	}
	
	/**
	 * 
	 * @return the balance point in world coordinates
	 */
	private Vector4f calculateBalancePoint(Matrix4f transformation) {
		Vector4f balancePoint = new Vector4f();
		
		transformation.getColumn(3, balancePoint);
		
		return balancePoint;
	}

	public boolean checkBoundingSphere(SceneManagerInterface sceneManager) {
		return boundingBox.isOverlapping(sceneManager);
	}
	
}
