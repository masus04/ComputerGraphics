package jrtr;

import java.util.LinkedList;

public abstract class GraphGroup extends GraphNode {

	GraphTransformGroup transformGroup;
	LinkedList<GraphGroup> groups;
	LinkedList<GraphShapeNode> shapeNodes;
	LinkedList<GraphLightNode> lightNodes;

	public GraphGroup(GraphNode parent) {
		this.parent = parent;
		shapeNodes = new LinkedList<GraphShapeNode>();
		lightNodes = new LinkedList<GraphLightNode>();
	}

	public GraphGroup(GraphTransformGroup transformGroup, LinkedList<GraphGroup> groups,
			LinkedList<GraphShapeNode> shapeNodes, LinkedList<GraphLightNode> lightNodes) {

		this.transformGroup = transformGroup;
		this.groups = groups;
		this.shapeNodes = shapeNodes;
		this.lightNodes = lightNodes;
	}

	public GraphShapeNode getChildShape(int index) {
		if (index < shapeNodes.size())
			return shapeNodes.get(index);
		else return null;		
	}

	public GraphLightNode getChildLight(int index) {
		if (index < lightNodes.size())
			return lightNodes.get(index);
		else return null;
	}

	public GraphGroup getChildGroup(int index) {
		if (index < groups.size())
			return groups.get(index);
		else return null;
	}

	public void add(Light light) {
		lightNodes.add(new GraphLightNode(this, light));
	}

	public void add(Shape shape) {
		shapeNodes.add(new GraphShapeNode(this, shape));
	}

	public void add(GraphGroup group) {
		groups.add(group);
	}
}
