package simple;

import jrtr.*;

public class Cylinder {
	
	private static RenderContext renderContext;
	private Shape shape;
	
	
	public Cylinder(RenderContext renderContext, int resolution, float radius, float height){
		Cylinder.renderContext = renderContext;
		
		shape = initCylinder(resolution, radius, height);
	}
	
	public Shape getShape(){
		return shape;
	}
	
	private static Shape initCylinder(int resolution, float radius, float height) {
		int res = resolution;

		float[] positions;
		float[] colors;
		int[] indices;

		// Vertices

		positions = new float[6 * (res + 1)];

		for (int i = 0; i < 3 * res; i += 3) {
			// Upper Circle
			positions[i] = (float) (Math.cos(i / 3 * 2 * Math.PI / res) * radius); // x
			positions[i + 1] = height / 2; // y
			positions[i + 2] = (float) (Math.sin(i / 3 * 2 * Math.PI / res) * radius); // z
		}

		for (int i = 0; i < 3 * res; i += 3) {
			// Lower Circle
			positions[i + (3 * res)] = (float) Math.cos(i / 3 * 2 * Math.PI / res) * radius;
			positions[i + 1 + (3 * res)] = -height / 2;
			positions[i + 2 + (3 * res)] = (float) Math.sin(i / 3 * 2 * Math.PI / res) * radius;
		}

		// Upper Center
		positions[6 * res] = 0;
		positions[6 * res + 1] = height / 2;
		positions[6 * res + 2] = 0;

		// Lower Center
		positions[6 * res + 3] = 0;
		positions[6 * res + 4] = -height / 2;
		positions[6 * res + 5] = 0;

		// Colors

		colors = new float[6 * (res + 1)];

		for (int i = 0; i < 6 * res; i += 6) {
			colors[i] = 0; // White
			colors[i + 1] = 0;
			colors[i + 2] = 0;

			colors[i + 3] = 1; // Black
			colors[i + 4] = 1;
			colors[i + 5] = 1;
		}

		// Upper Center
		colors[6 * res] = 1;
		colors[6 * res + 1] = 1;
		colors[6 * res + 2] = 1;

		// Lower Center
		colors[6 * res + 3] = 1;
		colors[6 * res + 4] = 1;
		colors[6 * res + 5] = 1;

		// Indices

		indices = new int[12 * res];

		// Top Circle

		for (int i = 0; i < (3 * res); i += 3) {
			indices[i] = (i / 3);
			indices[i + 1] = (i / 3 + 1) % res;
			indices[i + 2] = 2 * res;
		}

		// Bottom Circle

		for (int i = 0; i < 3 * res; i += 3) {
			indices[i + (3 * res)] = i / 3 + res;
			indices[i + 1 + (3 * res)] = (i / 3 + 1) % res + res;
			indices[i + 2 + (3 * res)] = 2 * res + 1;
		}

		// Mantle

		for (int i = 0; i < 6 * res; i += 6) {
			indices[i + (6 * res)] = i / 6;
			indices[i + (6 * res) + 1] = (i / 6 + 1) % res;
			indices[i + (6 * res) + 2] = (i / 6 + 1) % res + res;

			indices[i + (6 * res) + 3] = i / 6;
			indices[i + (6 * res) + 4] = (i / 6 + 1) % res + res;
			indices[i + (6 * res) + 5] = (i / 6) % res + res;
		}

		// Construct a data structure that stores the vertices, their
		// attributes, and the triangle mesh connectivity
		VertexData vertexData = renderContext.makeVertexData(2 * (res + 1));
		vertexData.addElement(colors, VertexData.Semantic.COLOR, 3);
		vertexData.addElement(positions, VertexData.Semantic.POSITION, 3);

		vertexData.addIndices(indices);

		Shape shape = new Shape(vertexData);

		return shape;
	}

}
