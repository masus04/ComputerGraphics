package jrtr;

import java.util.LinkedList;

import javax.vecmath.Matrix4f;

public class GraphTransformGroup extends GraphGroup {

	private Matrix4f transformation;
	private LinkedList<GraphTransformGroup> groups;
	private LinkedList<GraphShapeNode> shapeNodes;
	private LinkedList<GraphLightNode> lightNodes;

	public GraphTransformGroup() {
		Matrix4f identity = new Matrix4f();
		identity.setIdentity();

		this.transformation = identity;

		groups = new LinkedList<GraphTransformGroup>();
		shapeNodes = new LinkedList<GraphShapeNode>();
		lightNodes = new LinkedList<GraphLightNode>();
	}

	public GraphTransformGroup(Matrix4f transformation) {
		this();

		this.transformation = transformation;
	}

	public GraphTransformGroup(GraphTransformGroup transformGroup) {
		this.transformation = transformGroup.getTransformation();
		this.groups = transformGroup.groups;
		this.shapeNodes = transformGroup.shapeNodes;
		this.lightNodes = transformGroup.lightNodes;
	}

	public GraphTransformGroup(Matrix4f transformation, LinkedList<GraphTransformGroup> groups,
			LinkedList<GraphShapeNode> shapeNodes, LinkedList<GraphLightNode> lightNodes) {

		this.transformation = transformation;
		this.groups = groups;
		this.shapeNodes = shapeNodes;
		this.lightNodes = lightNodes;
	}

	@Override
	public void getShapeItems(LinkedList<RenderItem> items, Matrix4f transformation) {
		Matrix4f trans = new Matrix4f();
		trans.mul(transformation, getTransformation());

		for (GraphShapeNode s : shapeNodes) {
			s.getShapeItems(items, trans);
		}

		for (GraphGroup g : groups) {
			g.getShapeItems(items, trans);
		}
	}

	public GraphLightNode add(Light light) {
		GraphLightNode lightNode = new GraphLightNode(light);
		lightNodes.add(lightNode);
		return lightNode;
	}

	public GraphShapeNode add(Shape shape) {
		GraphShapeNode shapeNode = new GraphShapeNode(shape);
		shapeNodes.add(shapeNode);
		return shapeNode;
	}

	/**
	 * 
	 * @param shape
	 *            the shape to be added
	 * @param frustum
	 *            the frustum used in the scene
	 * 
	 *            the shape is only added to the graph if it is located inside
	 *            the view frustum
	 * @param trans
	 */
	public GraphShapeNode add(Shape shape, SceneManagerInterface sceneManager, Matrix4f trans) {
		GraphShapeNode shapeNode = new GraphShapeNode(shape);
		if (trans == null) {
			trans = new Matrix4f();
			trans.setIdentity();
		}

		shapeNode.setTransformation(trans);

		if (shapeNode.viewFrustumCulling(sceneManager))
			shapeNodes.add(shapeNode);
		return shapeNode;
	}

	public GraphShapeNode add(Shape shape, Matrix4f trans) {
		GraphShapeNode shapeNode = new GraphShapeNode(shape);
		shapeNode.setTransformation(trans);
		shapeNodes.add(shapeNode);
		return shapeNode;
	}

	public void add(GraphTransformGroup group) {
		groups.add(group);
	}

	@Override
	public Matrix4f getTransformation() {
		return transformation;
	}

	@Override
	public void setTransformation(Matrix4f transformation) {
		this.transformation = transformation;
	}

	@Override
	public GraphGroup getGraphGroup(int index) {
		return groups.get(index);
	}
}
