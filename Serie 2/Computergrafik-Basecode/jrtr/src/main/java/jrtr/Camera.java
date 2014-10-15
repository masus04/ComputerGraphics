package jrtr;

import javax.vecmath.*;

/**
 * Stores the specification of a virtual camera. You will extend
 * this class to construct a 4x4 camera matrix, i.e., the world-to-
 * camera transform from intuitive parameters.
 * 
 * A scene manager (see {@link SceneManagerInterface}, {@link SimpleSceneManager})
 * stores a camera.
 */
public class Camera {

	Vector3f center;
	Vector3f lookAt;
	Vector3f up;

	Vector3f lookDirection;

	private Matrix4f cameraMatrix;

	/**
	 * Construct a camera with a default camera matrix. The camera
	 * matrix corresponds to the world-to-camera transform. This default
	 * matrix places the camera at (0,0,10) in world space, facing towards
	 * the origin (0,0,0) of world space, i.e., towards the negative z-axis.
	 */
	public Camera() {
		cameraMatrix = new Matrix4f();

		this.setCenterOfProjection(0, 0, 40);
		this.setLookAtPoint(0, 0, 0);
		this.setUpVector(0, 1, 0);
		calculateCameraMatrix();

		lookDirection = new Vector3f();
		lookDirection.sub(center, lookAt);
		lookDirection.y = 0;
		lookDirection.normalize();
	}

	public Camera(Vector3f centerOfProjection, Vector3f lookAtVector, Vector3f upVector, Vector3f centerOfprojection) {
		cameraMatrix = new Matrix4f();

		this.setCenterOfProjection(centerOfprojection.x, centerOfprojection.y, centerOfProjection.z);
		this.setLookAtPoint(lookAtVector.x, lookAtVector.y, lookAtVector.z);
		this.setUpVector(upVector.x, upVector.y, upVector.z);
		calculateCameraMatrix();

		lookDirection = new Vector3f();
		lookDirection.sub(center, lookAt);
		lookDirection.y = 0;
		lookDirection.normalize();
	}

	/**
	 * Return the camera matrix, i.e., the world-to-camera transform. For example,
	 * this is used by the renderer.
	 * 
	 * @return the 4x4 world-to-camera transform matrix
	 */
	public Matrix4f getCameraMatrix() {
		return new Matrix4f(cameraMatrix);
	}

	public void setCenterOfProjection(float x, float y, float z) {
		center = new Vector3f(x, y, z);

		if (center != null && lookAt != null && up != null)
			calculateCameraMatrix();
	}

	public void setLookAtPoint(float x, float y, float z) {
		lookAt = new Vector3f(x, y, z);

		if (center != null && lookAt != null && up != null)
			calculateCameraMatrix();
	}

	public void setUpVector(float x, float y, float z) {
		up = new Vector3f(x, y, z);

		if (center != null && lookAt != null && up != null)
			calculateCameraMatrix();
	}

	private void calculateCameraMatrix() {
		Vector3f vector1 = new Vector3f();
		Vector3f vector2 = new Vector3f();
		Vector3f vector3 = new Vector3f();

		vector3 = new Vector3f(center);					// Z-Axis
		vector3.sub(lookAt);
		vector3.normalize();

		vector1.cross(up, vector3);			// X-Axis
		vector1.normalize();

		vector2.cross(vector3, vector1);	// Y-Axis

		cameraMatrix.setColumn(0, vector1.x, vector1.y, vector1.z, 0);
		cameraMatrix.setColumn(1, vector2.x, vector2.y, vector2.z, 0);
		cameraMatrix.setColumn(2, vector3.x, vector3.y, vector3.z, 0);
		cameraMatrix.setColumn(3, center.x, center.y, center.z, 1);

		cameraMatrix.setElement(3, 3, 1);

		cameraMatrix.invert();

	}

	public void moveCamera(char direction) {
		Matrix4f tmp = new Matrix4f();
		Vector3f look2D = new Vector3f();
		Vector3f orthogonal2D = new Vector3f();
		orthogonal2D.cross(lookDirection, up);
		orthogonal2D.normalize();
		
		if (direction == 'w')
			look2D = new Vector3f(lookDirection.x, 0, lookDirection.z);
		else if (direction == 's')
			look2D = new Vector3f(-lookDirection.x, 0, -lookDirection.z);
		else if (direction == 'a')
			look2D = new Vector3f(-orthogonal2D.x, 0, -orthogonal2D.z);
		else if (direction == 'd')
			look2D = new Vector3f(orthogonal2D.x, 0, orthogonal2D.z);
		
		look2D.normalize();
		tmp.setTranslation(look2D);
		cameraMatrix.add(tmp);
	}
	
	public void rotateCamera(Vector3f axis, float angle){
		Matrix4f rotation = new Matrix4f();
		rotation.setIdentity();
		rotation.setRotation(new AxisAngle4f(axis, -angle/10));

		Vector3f translation = new Vector3f();
		cameraMatrix.get(translation);
		cameraMatrix.setTranslation(new Vector3f());
		
		rotation.mul(cameraMatrix);
		
		cameraMatrix.setTranslation(translation);
		
		cameraMatrix = rotation;
	}
}
