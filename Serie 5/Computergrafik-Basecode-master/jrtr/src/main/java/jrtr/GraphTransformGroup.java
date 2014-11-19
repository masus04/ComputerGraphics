package jrtr;

import java.util.LinkedList;

import javax.vecmath.Matrix4f;

public class GraphTransformGroup extends GraphGroup {

	Matrix4f transformation;
	LinkedList<GraphTransformGroup> groups;
	LinkedList<GraphShapeNode> shapeNodes;
	LinkedList<GraphLightNode> lightNodes;

	public GraphTransformGroup(GraphNode parent, Matrix4f transformation) {
		this.transformation = transformation;

		groups = new LinkedList<GraphTransformGroup>();
		shapeNodes = new LinkedList<GraphShapeNode>();
		lightNodes = new LinkedList<GraphLightNode>();
	}

	public GraphTransformGroup(GraphNode parent, GraphTransformGroup transformGroup, LinkedList<GraphTransformGroup> groups,
			LinkedList<GraphShapeNode> shapeNodes, LinkedList<GraphLightNode> lightNodes) {
		
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

	public void add(Light light) {
		lightNodes.add(new GraphLightNode(this, light));
	}

	public void add(Shape shape) {
		shapeNodes.add(new GraphShapeNode(this, shape));
	}

	public void add(GraphTransformGroup group) {
		groups.add(group);
	}

	@Override
	public Matrix4f getTransformation() {
		return transformation;
	}
}
