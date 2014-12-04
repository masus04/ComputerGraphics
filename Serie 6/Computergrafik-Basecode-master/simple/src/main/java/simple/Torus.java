package simple;

import jrtr.RenderContext;
import jrtr.Shape;
import jrtr.VertexData;

public class Torus {

	private static Shape shape;
	
	public Torus(RenderContext renderContext){
		initTorus(3, 1, 10, renderContext);
	}
	
	public Shape getShape(){
		return shape;
	}
	
	private static Shape initTorus(float RMajor, float RMinor, int res, RenderContext renderContext) {

		float[] positions = calculateTorusPositions(RMajor, RMinor, res);
		float[] colors = calculateTorusColors(res);
		int[] indices = calculateTorusIndices(res);

		VertexData vertexData = renderContext.makeVertexData(res * res);
		vertexData.addElement(colors, VertexData.Semantic.COLOR, 3);
		vertexData.addElement(positions, VertexData.Semantic.POSITION, 3);

		vertexData.addIndices(indices);

		shape = new Shape(vertexData);

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
