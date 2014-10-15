package simple;

import jrtr.Shape;
import jrtr.VertexData;

public class Landscape {

	private static float[][] map;
	private static int factor;
	private static int res;

	private float[] positions;
	private float[] colors;
	private int[] indices;

	public Landscape() {

	}

	public Landscape(int resolution, int factor) {
		res = resolution;
		this.factor = factor;
		calculateLandscapeMap();
		calculateLandscapePositions();
		calculateLandscapeColors();
		calculateLandscapeIndices();
	}

	public Landscape(int resolution, int factor, int height0, int height1, int height2, int height3) {
		res = resolution;
		this.factor = factor;
		calculateLandscapeMap(height0, height1, height2, height3);
		calculateLandscapePositions();
		calculateLandscapeColors();
		calculateLandscapeIndices();
	}

	public static float[][] calculateLandscapeMap() {
		float rand = (float) Math.random() * factor;

		return calculateLandscapeMap(rand, rand, rand, rand);
	}

	public static float[][] calculateLandscapeMap(float height0, float height1, float height2, float height3) {
		if ((res & (res - 1)) != 0) {
			System.out.println("resolution is not a power of 2");
			return null;
		}

		factor = factor;

		map = new float[res + 1][res + 1];

		for (int i = 0; i < res + 1; i++)
			for (int j = 0; j < res + 1; j++)
				map[i][j] = Float.MIN_VALUE;

		map[0][0] = height0;										// p1 -- p2
		map[0][res] = height1;										// '     '
		map[res][0] = height2;										// '     '
		map[res][res] = height3;									// p3 -- p4

		hLandscapeMap(new Point(0, 0), res, 1);

		hLandscapeEdgePoints();

		return map;
	}

	private static void hLandscapeMap(Point p, int span, int depth) {
		depth += 1;

		if (span < 2)
			return;

		// middle of square
		map[p.x + span / 2][p.y + span / 2] = calculateValue(p, span, depth);

		// side points
		map[p.x][p.y + span / 2] = calculateValue(p, new Point(p.x + span, p.y), new Point(p.x + span / 2, p.y + span / 2),
				new Point(p.x + span / 2, p.y - span / 2), depth);

		map[p.x + span / 2][p.y] = calculateValue(p, new Point(p.x, p.y + span), new Point(p.x + span / 2, p.y + span / 2),
				new Point(p.x - span / 2, p.y + span / 2), depth);

		map[p.x + span / 2][p.y + span] = calculateValue(new Point(p.x + span, p.y + span),
				new Point(p.x + span, p.y + span), new Point(p.x + span / 2, p.y + span / 2), new Point(p.x + span / 2,
						(int) (p.y + span * 1.5)), depth);

		map[p.x + span][p.y + span / 2] = calculateValue(new Point(p.x + span, p.y), new Point(p.x + span, p.y + span),
				new Point(p.x + span / 2, p.y + span / 2), new Point((int) (p.x + span * 1.5), p.y + span / 2), depth);

		// recursion
		hLandscapeMap(new Point(p.x, p.y), span / 2, depth++);
		hLandscapeMap(new Point(p.x, p.y + span / 2), span / 2, depth++);
		hLandscapeMap(new Point(p.x + span / 2, p.y), span / 2, depth++);
		hLandscapeMap(new Point(p.x + span / 2, p.y + span / 2), span / 2, depth++);
	}

	private static void hLandscapeEdgePoints() {
		// TODO Auto-generated method stub
	}

	private static float calculateValue(Point p1, Point p2, Point p3, Point p4, int depth) {
		float value = map[p1.x][p1.y] + map[p2.x][p2.y] + map[p3.x][p3.y];

		if (p4.inBounds(res) && map[p4.x][p4.y] != Float.MAX_VALUE) {
			value += map[p4.x][p4.y];
			value /= 4;
		} else
			value /= 3;

		value += (float) (Math.random() - 0.5) * (float) factor / (float) depth;

		return value;
	}

	private static float calculateValue(Point p, int span, int depth) {
		return ((map[p.x][p.y] + map[p.x][p.y + span] + map[p.x + span][p.y] + map[p.x + span][p.y + span]) / 4)
				+ (float) (Math.random() - 0.5) * (float) factor / (float) depth;
	} 

	public float[] calculateLandscapePositions() {
		positions = new float[(res + 1) * (res + 1) * 3];

		for (int x = 0; x < res + 1; x++)
			for (int z = 0; z < 3 * (res + 1); z += 3) {

				// TODO: translation correct?
				positions[3 * x * (res + 1) + z + 0] = x - res / 2;
				positions[3 * x * (res + 1) + z + 1] = map[x][z / 3];
				positions[3 * x * (res + 1) + z + 2] = z / 3 - res / 2;
			}

		return positions;
	}

	public float[] calculateLandscapeColors() {
		colors = new float[(res + 1) * (res + 1) * 3];

		for (int i = 0; i < 3 * (res + 1) * (res + 1); i += 3) {		// all green
			colors[i + 0] = 0;
			colors[i + 1] = 1;
			colors[i + 2] = 0;
		}

		return colors;
	}

	public float[] setSnowLine(double d) {
		colors = new float[(res + 1) * (res + 1) * 3];

		for (int i = 0; i < 3 * (res + 1) * (res + 1); i += 3) {

			if (positions[i + 1] < d) {
				colors[i + 0] = 0;
				colors[i + 1] = (float) 0.75;
				colors[i + 2] = 0;
			} else {
				colors[i + 0] = 1;
				colors[i + 1] = 1;
				colors[i + 2] = 1;
			}
		}

		return colors;
	}

	public int[] calculateLandscapeIndices() {
		indices = new int[res * res * 6];

		for (int i = 0; i < res; i++)
			for (int j = 0; j < 6 * res; j += 6) {
				indices[6 * i * res + j + 0] = i * (res + 1) + j / 6;
				indices[6 * i * res + j + 1] = i * (res + 1) + j / 6 + res + 1;
				indices[6 * i * res + j + 2] = i * (res + 1) + j / 6 + 1;

				indices[6 * i * res + j + 3] = i * (res + 1) + j / 6 + res + 1;
				indices[6 * i * res + j + 4] = i * (res + 1) + j / 6 + res + 2;
				indices[6 * i * res + j + 5] = i * (res + 1) + j / 6 + 1;
			}

		return indices;
	}

	public Shape getShape(jrtr.RenderContext renderContext) {
		VertexData vertexData = renderContext.makeVertexData((res + 1) * (res + 1));
		vertexData.addElement(colors, VertexData.Semantic.COLOR, 3);
		vertexData.addElement(positions, VertexData.Semantic.POSITION, 3);
		vertexData.addIndices(indices);

		Shape shape = new Shape(vertexData);

		return shape;
	}
}
