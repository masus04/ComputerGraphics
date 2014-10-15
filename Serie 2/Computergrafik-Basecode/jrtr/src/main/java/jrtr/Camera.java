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
		Vector3f lookDir = new Vector3f();
		Vector3f orthogonal = new Vector3f();
		orthogonal.cross(lookDirection, up);
		orthogonal.normalize();

		if (direction == 'w' || direction == 'W')
			lookDir = new Vector3f(lookDirection);
		else if (direction == 's' || direction == 'S') {
			lookDir = new Vector3f(lookDirection);
			lookDir.negate();
		} else if (direction == 'a' || direction == 'A') {
			lookDir = new Vector3f(orthogonal);
		} else if (direction == 'd' || direction == 'D') {
			lookDir = new Vector3f(orthogonal);
			lookDir.negate();
		}

		lookDir.normalize();
		tmp.setTranslation(lookDir);
		cameraMatrix.add(tmp);
	}

	public void rotateCamera(Vector3f axis, float angle) {

		Matrix4f rotation = new Matrix4f();
		rotation.setIdentity();
		rotation.setRotation(new AxisAngle4f(axis, angle));
		cameraMatrix.invert();
		cameraMatrix.mul(rotation);

		updateUpVector();
		updateCenter();

		cameraMatrix.invert();
	}

	private void updateUpVector() {
		Vector4f xVector = new Vector4f();
		cameraMatrix.getColumn(0, xVector);
		Vector3f x = new Vector3f(xVector.x, xVector.y, xVector.z);

		Vector4f zVector = new Vector4f();
		cameraMatrix.getColumn(2, zVector);
		Vector3f z = new Vector3f(zVector.x, zVector.y, zVector.z);

		up.cross(x, z);
		up.normalize();
	}

	private void updateCenter() {
		Vector4f tmp = new Vector4f();
		cameraMatrix.getColumn(3, tmp);

		center = new Vector3f(tmp.x, tmp.y, tmp.z);
		center.normalize();
	}
}
