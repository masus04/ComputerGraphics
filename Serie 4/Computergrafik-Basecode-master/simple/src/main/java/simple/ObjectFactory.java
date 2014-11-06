package simple;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import jrtr.RenderContext;
import jrtr.Shape;
import jrtr.VertexData;

public class ObjectFactory {

	static RenderContext renderContext;

	public ObjectFactory(RenderContext renderContext) {
		this.renderContext = renderContext;
	}

	public Shape createLandscape(int resolution, int factor) {
		Landscape landscape = new Landscape(resolution, factor);

		Landscape.calculateLandscapeMap();
		return landscape.getShape(renderContext);
	}

	public Shape createCylinder(int resolution) {

		int res = resolution;
		int Radius = 1;
		int Height = 2;

		float[] positions;
		float[] colors;
		int[] indices;

		// Vertices

		positions = new float[6 * (res + 1)];

		for (int i = 0; i < 3 * res; i += 3) {
			// Upper Circle
			positions[i] = (float) (Math.cos(i / 3 * 2 * Math.PI / res) * Radius); // x
			positions[i + 1] = Height / 2; // y
			positions[i + 2] = (float) (Math.sin(i / 3 * 2 * Math.PI / res) * Radius); // z
		}

		for (int i = 0; i < 3 * res; i += 3) {
			// Lower Circle
			positions[i + (3 * res)] = (float) Math.cos(i / 3 * 2 * Math.PI / res) * Radius;
			positions[i + 1 + (3 * res)] = -Height / 2;
			positions[i + 2 + (3 * res)] = (float) Math.sin(i / 3 * 2 * Math.PI / res) * Radius;
		}

		// Upper Center
		positions[6 * res] = 0;
		positions[6 * res + 1] = Height / 2;
		positions[6 * res + 2] = 0;

		// Lower Center
		positions[6 * res + 3] = 0;
		positions[6 * res + 4] = -Height / 2;
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

	public static Shape createTorus(float RMajor, float RMinor, int res) {

		float[] positions = calculateTorusPositions(RMajor, RMinor, res);
		float[] colors = calculateTorusColors(res);
		int[] indices = calculateTorusIndices(res);

		VertexData vertexData = renderContext.makeVertexData(res * res);
		vertexData.addElement(colors, VertexData.Semantic.COLOR, 3);
		vertexData.addElement(positions, VertexData.Semantic.POSITION, 3);

		vertexData.addIndices(indices);

		Shape shape = new Shape(vertexData);

		return shape;
	}

	private static int[] calculateTorusIndices(int res) {
		int[] indices = new int[6 * res * res];

		for (int j = 0; j < res; j++) {
			for (int i = 0; i < 6 * res; i += 6) {

				indices[i + j * 6 * res + 0] = (i / 6 + j * res) % (res * res);
				indices[i + j * 6 * res + 1] = (((i / 6 + 1) % res) + j * res) % (res * res);
				indices[i + j * 6 * res + 2] = (((i / 6 + 1) % res) + res + j * res) % (res * res);

				indices[i + j * 6 * res + 3] = (i / 6 + j * res) % (res * res);
				indices[i + j * 6 * res + 4] = (((i / 6 + 1) % res) + res + j * res) % (res * res);
				indices[i + j * 6 * res + 5] = ((i / 6) % res + res + j * res) % (res * res);
			}
		}

		return indices;
	}

	private static float[] calculateTorusColors(int res) {
		float[] colors = new float[3 * res * res];

		/* for (int i = 0; i < 3 * res * res; i += 6) {
		 * colors[i + 0] = 1;
		 * colors[i + 1] = 1;
		 * colors[i + 2] = 1;
		 * 
		 * colors[i + 3] = 0;
		 * colors[i + 4] = 0;
		 * colors[i + 5] = 0;
		 * } */

		for (int i = 0; i < 3 * res * res; i += 3) {

			colors[i + 0] = (float) (Math.random());
			colors[i + 1] = (float) (Math.random());
			colors[i + 2] = (float) (Math.random());
		}

		return colors;
	}

	private static float[] calculateTorusPositions(float RMajor, float RMinor, int res) {
		float[] positions = new float[3 * res * res];

		for (int j = 0; j < res; j++) {

			for (int i = 0; i < 3 * res; i += 3) {
				float p = (float) (2 * i / 3 * Math.PI / res);
				float t = (float) (2 * j * Math.PI / res);

				positions[i + j * 3 * res + 0] = (float) ((RMajor + RMinor * Math.cos(p)) * Math.cos(t)); // x
				positions[i + j * 3 * res + 1] = (float) (RMinor * Math.sin(p)); // y
				positions[i + j * 3 * res + 2] = (float) ((RMajor + RMinor * Math.cos(p)) * Math.sin(t)); // z
			}
		}

		return positions;
	}
	
}
