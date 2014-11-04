package simple;

import javax.vecmath.Vector3f;

import jrtr.Shape;
import jrtr.VertexData;

public class Landscape {

	private static float[][] map;
	private static int factor;
	private static int res;

	private float[] positions;
	private float[] colors;
	private float[] normals;
	private int[] indices;

	public Landscape(int resolution, int factor) {
		boolean ok = true;
		while (ok) {
			ok = false;
			res = resolution;
			Landscape.factor = factor;
			calculateLandscapeMap();
			calculateLandscapePositions();
			calculateLandscapeColors();
			try {
				calculateLandscapeIndices();
			} catch (Exception e) {
				ok = true;
			}
		}
	}

	public Landscape(int resolution, int factor, int height0, int height1, int height2, int height3) {
		boolean ok = true;
		while (ok) {
			ok = false;
			res = resolution;
			Landscape.factor = factor;
			calculateLandscapeMap(height0, height1, height2, height3);
			calculateLandscapePositions();
			calculateLandscapeColors();
			calculateLandscapeIndices();
			try {
				calculateLandscapeIndices();
			} catch (Exception e) {
				ok = true;
			}
		}
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

		map = new float[res + 1][res + 1];

		map[0][0] = height0;										// p1 -- p2
		map[0][res] = height1;										// '     '
		map[res][0] = height2;										// '     '
		map[res][res] = height3;									// p3 -- p4

		diamond(res, 1);

		return map;
	}

	private static void diamond(int span, int depth) {
		if (span < 1)
			return;

		for (int i = 0; i < res; i += span)
			for (int j = 0; j < res; j += span)
				map[i][j] = calculateDiamond(new Point(i, j), span, depth);

		square(span, depth + 5);
	}

	private static void square(int span, int depth) {
		for (int i = 0; i < res; i += span)
			for (int j = 0; j < res; j += span) {
				calculateSquare(new Point(i, j), span, depth);
			}
		diamond(span / 2, depth + 5);
	}

	private static float calculateDiamond(Point p, int span, int depth) {
		float value = (((map[p.x][p.y] + map[p.x][p.y + span] + map[p.x + span][p.y] + map[p.x + span][p.y + span]) / 4) + (float) (Math
				.random() - 0.5) * (float) factor / (float) depth);

		map[p.x + span / 2][p.y + span / 2] = value;

		return value;
	}

	private static void calculateSquare(Point p, int span, int depth) {

		// x p1 x
		// p2   p4
		// x p3 x

		// p1
		float value = map[p.x][p.y] + map[p.x + span][p.y] + map[p.x + span / 2][p.y + span / 2];

		if (new Point(p.x + span / 2, p.y - span / 2).inBounds(res)) {
			value += map[p.x + span / 2][p.y - span / 2];
			value /= 4;
		} else
			value /= 3;

		value += (float) (Math.random() - 0.5) * (float) factor / (float) depth;

		map[p.x + span / 2][p.y] = value;

		// p2
		value = map[p.x][p.y] + map[p.x][p.y + span] + map[p.x + span / 2][p.y + span / 2];

		if (new Point(p.x - span / 2, p.y + span / 2).inBounds(res)) {
			value += map[p.x - span / 2][p.y + span / 2];
			value /= 4;
		} else
			value /= 3;

		value += (float) (Math.random() - 0.5) * (float) factor / (float) depth;

		map[p.x][p.y + span / 2] = value;

		// p3
		value = map[p.x + span][p.y] + map[p.x + span][p.y + span] + map[p.x + span / 2][p.y + span / 2];

		if (new Point(p.x + span / 2, (int) (p.y + span * 1.5)).inBounds(res)) {
			value += map[(p.x + span / 2)][(int) (p.y + span * 1.5)];
			value /= 4;
		} else
			value /= 3;

		value += (float) (Math.random() - 0.5) * (float) factor / (float) depth;

		map[p.x + span / 2][p.y + span] = value;

		// p4
		value = map[p.x][p.y + span] + map[p.x + span][p.y + span] + map[p.x + span / 2][p.y + span / 2];

		if (new Point((int) (p.x + span * 1.5), p.y + span / 2).inBounds(res)) {
			value += map[(int) (p.x + span * 1.5)][p.y + span / 2];
			value /= 4;
		} else
			value /= 3;

		value += (float) (Math.random() - 0.5) * (float) factor / (float) depth;

		map[p.x + span][p.y + span / 2] = value;
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

	private static float calculateValue(Point p1, Point p2, Point p3, Point p4, int depth) {
		float value = map[p1.x][p1.y] + map[p2.x][p2.y] + map[p3.x][p3.y];

		if (p4.inBounds(res)) {
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

	private float[] calculateLandscapePositions() {
		positions = new float[(res + 1) * (res + 1) * 3];

		for (int x = 0; x < res + 1; x++)
			for (int z = 0; z < 3 * (res + 1); z += 3) {

				positions[3 * x * (res + 1) + z + 0] = x - res / 2;
				positions[3 * x * (res + 1) + z + 1] = map[x][z / 3];
				positions[3 * x * (res + 1) + z + 2] = z / 3 - res / 2;

			}

		return positions;
	}

	private float[] calculateLandscapeColors() {
		colors = new float[(res + 1) * (res + 1) * 3];

		for (int i = 0; i < 3 * (res + 1) * (res + 1); i += 3) {		// all green
			colors[i + 0] = 0;
			colors[i + 1] = 1;
			colors[i + 2] = 0;
		}

		return colors;
	}

	private int[] calculateLandscapeIndices() {
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

	private float[] calculateLandscapeNormals() {
		normals = new float[3 * (res + 1) * (res + 1)];

		Vector3f p1;
		Vector3f p2;
		Vector3f p3;

		Vector3f line1 = new Vector3f();
		Vector3f line2 = new Vector3f();

		Vector3f normal = new Vector3f();

		for (int i = 0; i < 3 * (res + 1) * (res + 1); i += 3) {
			p1 = new Vector3f(positions[i], positions[i + 1], positions[i + 2]);
			try {
				p2 = new Vector3f(positions[i + 3], positions[i + 4], positions[i + 5]);
			} catch (ArrayIndexOutOfBoundsException e) {
				p2 = p1;
				p1 = new Vector3f(positions[i - 3], positions[i - 2], positions[i - 1]);
			}

			try {
				p3 = new Vector3f(positions[i + (res + 1)], positions[i + (res + 1) + 1], positions[i + (res + 1) + 2]);
			} catch (ArrayIndexOutOfBoundsException e) {
				p3 = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
			}

			if (!(new Point((int) p2.x, (int) p2.y).inShape(res))) {						// right boundary
				p2 = p1;
				p1 = new Vector3f(positions[i - 3], positions[i - 2], positions[i - 1]);
			}

			if (!(new Point((int) p3.x, (int) p3.y).inShape(res))) {						// bottom boundary
				p3 = p2;
				p2 = new Vector3f(positions[i - 3 * (res + 1)], positions[i + 1 - 3 * (res + 1)], positions[i + 2 - 3
						* (res + 1)]);
			}

			line1.sub(p2, p1);
			line2.sub(p3, p1);

			normal.cross(line2, line1);

			normals[i] = normal.x;
			normals[i + 1] = normal.y;
			normals[i + 2] = normal.z;
		}

		return normals;
	}

	/**
	 * 
	 * @param d
	 *            whith which the average heihgt is multiplied to get the snow line
	 *            factor * average height = snow line;
	 * @return the color array to the landscape
	 */
	public void setSnowAndWaterLine(double snow, double water) {
		colors = new float[(res + 1) * (res + 1) * 3];
		int average = 0;

		for (int i = 0; i < 3 * (res + 1) * (res + 1); i += 3)
			average += positions[i + 1];

		average /= (res + 1) * (res + 1);

		for (int i = 0; i < 3 * (res + 1) * (res + 1); i += 3) {

			if (positions[i + 1] > (snow* average)) {
				colors[i + 0] = 1;
				colors[i + 1] = 1;
				colors[i + 2] = 1;
			} else if (positions[i + 1] < (water* average)) {
				colors[i + 0] = 0;
				colors[i + 1] = 0;
				colors[i + 2] = 1;
			} else {
				colors[i + 0] = 0;
				colors[i + 1] = (float) 0.75;
				colors[i + 2] = 0;
			}
		}
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
