package jrtr;

import java.util.LinkedList;

import javax.vecmath.Matrix4f;

public class GraphSceneIterator implements SceneManagerIterator {

	private GraphGroup root;
	private LinkedList<RenderItem> RenderItems;
	private int nextIndex;

	public GraphSceneIterator(GraphGroup root) {
		this.root = root;
		nextIndex = 0;

		RenderItems = extractRenderItems();
	}

	@Override
	public boolean hasNext() {
		return (nextIndex < RenderItems.size());
	}

	@Override
	public RenderItem next() {
		RenderItem item = null;

		if (hasNext()) {
			item = RenderItems.get(nextIndex);
			nextIndex++;
		}

		return item;
	}

	private LinkedList<RenderItem> extractRenderItems() {
		Matrix4f identity = new Matrix4f();
		identity.setIdentity();

		LinkedList<RenderItem> items = new LinkedList<RenderItem>();

		root.getShapeItems(items, identity);

		return items;
	}

}