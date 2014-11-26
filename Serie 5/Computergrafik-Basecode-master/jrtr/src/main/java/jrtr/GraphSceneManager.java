package jrtr;

import java.util.Iterator;

public class GraphSceneManager implements SceneManagerInterface {

	GraphTransformGroup root;
	
	private Camera camera;
	private Frustum frustum;
	
	public GraphSceneManager() {
		camera = new Camera();
		frustum = new Frustum();
		
		root = new GraphTransformGroup();
		
	}
	
	@Override
	public SceneManagerIterator iterator() {
		return new GraphSceneIterator(root, this);
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
