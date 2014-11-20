package jrtr;

import java.util.Iterator;

import javax.vecmath.Matrix4f;

public class GraphSceneManager implements SceneManagerInterface {

	GraphTransformGroup root;
	
	private Camera camera;
	private Frustum frustum;
	
	public GraphSceneManager() {
		camera = new Camera();
		frustum = new Frustum();
		
		Matrix4f identity = new Matrix4f();
		identity.setIdentity();
		
		root = new GraphTransformGroup(identity);
		
	}
	
	@Override
	public SceneManagerIterator iterator() {
		return new GraphSceneIterator(root);
	}

	@Override
	public Iterator<Light> lightIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Camera getCamera() {
		return camera;
	}

	@Override
	public Frustum getFrustum() {
		return frustum;
	}

	public GraphTransformGroup getRoot() {
		return root;
	}

}
