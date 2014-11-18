package jrtr;

import java.util.LinkedList;

public abstract class GraphGroup implements GraphNode {

	GraphTransformGroup transformGroup;
	LinkedList<GraphGroup> groups;
	LinkedList<GraphShapeNode> shapeNodes;
	LinkedList<GraphLightNode> lightNodes;

	public GraphGroup() {
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

	public GraphGroup getGraphGroup(int index) {
		return groups.get(index);
	}

	public GraphShapeNode getChildShape(int index) {
		return shapeNodes.get(index);
	}

	public GraphLightNode getChildLight(int index) {
		return lightNodes.get(index);
	}

	public void add(GraphLightNode light) {
		lightNodes.add(light);
	}

	public void add(GraphShapeNode shape) {
		shapeNodes.add(shape);
	}

	public void add(GraphGroup group) {
		groups.add(group);
	}
}
