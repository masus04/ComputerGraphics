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
	
	public GraphTransformGroup(GraphTransformGroup transformGroup){
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

	public void add(Light light) {
		lightNodes.add(new GraphLightNode(light));
	}

	public void add(Shape shape) {
		shapeNodes.add(new GraphShapeNode(shape));
	}

	public void add(GraphTransformGroup group) {
		groups.add(group);
	}

	@Override
	public Matrix4f getTransformation() {
		return transformation;
	}

	public void setTransformation(Matrix4f transformation) {
		this.transformation = transformation;
	}
}
