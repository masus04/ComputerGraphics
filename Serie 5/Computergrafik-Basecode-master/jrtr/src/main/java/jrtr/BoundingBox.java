package jrtr;

import java.util.LinkedList;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector4f;

public class BoundingBox {

	private Vector4f balancePoint;
	private float radius;

	/**
	 * 
	 * @param balancePoint in CAMERA COORDINATES
	 * @param radius
	 */
	public BoundingBox(Vector4f balancePoint, float radius) {		
		this.balancePoint = balancePoint;
		this.radius = radius;
	}

	public boolean isOverlapping(SceneManagerInterface sceneManager) {
		
		LinkedList<Plane> planes = calculatePlanes(sceneManager.getFrustum());

		for (Plane p:planes){
			if (!isInsidePlane(p))
				return false;
		}
		
		return true;
	}

	/**
	 * 
	 * @param p the plane being tested for collision
	 * @return true if the bounding box collides with the plane
	 */
	private boolean isInsidePlane(Plane p) {
		float distance = balancePoint.dot(p.normal) - p.distance;
		
		return distance < radius;
	}

	/**
	 * only works for aspectRatio = 1
	 */
	private LinkedList<Plane> calculatePlanes(Frustum frustum) {
		LinkedList<Plane> planes = new LinkedList<BoundingBox.Plane>();

		Plane plane = new Plane(new Vector4f(0, 0, -1, 0), frustum.getNearPlane());
		planes.add(plane); // near plane
		plane = new Plane(new Vector4f((0), 0, 1, 0), frustum.getFarPlane());
		planes.add(plane); // far plane

		Vector4f edgeVector = new Vector4f((float) (Math.tan(frustum.getVerticalFieldOfView()/2) * frustum.getFarPlane()),
				0, frustum.getFarPlane(), 0);
		edgeVector.normalize();
		
		plane = new Plane(new Vector4f(edgeVector.z, 0, -edgeVector.x, 0), 0);
		planes.add(plane); // right plane
		
		plane = new Plane(new Vector4f(0, edgeVector.z, -edgeVector.x, 0), 0);
		planes.add(plane); // upper plane
		
		plane = new Plane(new Vector4f(-edgeVector.z, 0, -edgeVector.x, 0), 0);
		planes.add(plane); // left plane

		plane = new Plane(new Vector4f(0, -edgeVector.z, -edgeVector.x, 0), 0);
		planes.add(plane); // lower plane

		return planes;
	}

	private class Plane {
		public Vector4f normal;
		public float distance;

		public Plane(Vector4f normal, float distance) {
			this.normal = normal;
			this.distance = distance;
		}
	}

}
