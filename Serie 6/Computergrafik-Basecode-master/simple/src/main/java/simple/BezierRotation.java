package simple;

import jrtr.*;

import java.util.ArrayList;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector4f;

public class BezierRotation {

	private BezierCurve bCurve;
	private float[] points;
	private float[] colors;
	private float[] normals;
	private int[] indices;

	private Shape shape;

	/**
	 * as many control points as can be fitted into segments are taken into
	 * account. additional points are ignored
	 * 
	 * @param nPoints
	 *            number of points to calculate along each curve segment
	 * @param nRotations
	 *            the number of rotations around the axis of the Object
	 * 
	 * @param controlPoints
	 *            the control points for the whole curve
	 */
	public BezierRotation(int nPoints, ArrayList<Vector4f> controlPoints, int nRotations, RenderContext renderContext) {
		bCurve = new BezierCurve(nPoints, controlPoints);
		int nSegments = controlPoints.size()/4 %4;

		// bCurves = new ArrayList<BezierCurve>();

		// TODO: set test variable
		boolean test = false;

		/*
		 * for (int i = 0; i < nRotations; i++) { bCurves.add(new
		 * BezierCurve(nPoints, rotateArray(controlPoints, ((float) i) /
		 * ((float) nRotations)))); }
		 */

		if (test) {
			test(bCurve, nPoints, renderContext);
		}

		if (!test) {
			concatinateCurves(nRotations);
			calculateColors(nPoints, nRotations);
			calculateIndices(nPoints, nRotations, nSegments);
			createShape(points.length/3, renderContext);
		}
	}

	private void test(BezierCurve bCurve, int nPoints, RenderContext renderContext) {
		bCurve.getPoints().add(new Vector4f(0, 1, 0, 1));
		bCurve.getNormals().add(new Vector4f(-1,0,0,0));

		points = toBArray(bCurve.getPoints());
		colors = new float[points.length];
		normals = toBArray(bCurve.getNormals());
		indices = toIArray(testIndices(nPoints));

		for (int i = 0; i < colors.length; i++)
			colors[i] = 1;

		VertexData vData = renderContext.makeVertexData(points.length/3);

		vData.addElement(points, VertexData.Semantic.POSITION, 3);
		vData.addElement(colors, VertexData.Semantic.COLOR, 3);
		vData.addElement(normals, VertexData.Semantic.NORMAL, 3);
		vData.addIndices(indices);

		shape = new Shape(vData);
	}

	private ArrayList<Integer> testIndices(int nPoints) {
		ArrayList<Integer> indices = new ArrayList<Integer>();

		for (int i = 0; i < points.length - 1; i++) {
			indices.add(i + 1);
			indices.add(i);
			indices.add(nPoints);
		}

		return indices;
	}

	public Shape getShape() {
		return shape;
	}

	/**
	 * collects all points and normals from the existing curves and stores them
	 * in the points and normals member arrays.
	 */
	private void concatinateCurves(int nRotations) {
		ArrayList<Vector4f> points = new ArrayList<Vector4f>();
		ArrayList<Vector4f> normals = new ArrayList<Vector4f>();

		for (int i = 0; i < nRotations; i++) {
			points.addAll(rotateArray(bCurve.getPoints(), ((float) i) / ((float) nRotations)));
			normals.addAll(rotateArray(bCurve.getNormals(), ((float) i) / ((float) nRotations)));
		}

		/*for (BezierCurve c : bCurves) {
			points.addAll(c.getPoints());
			normals.addAll(c.getNormals());
		}*/

		this.points = toBArray(points);
		this.normals = toBArray(normals);
	}

	/**
	 * set all colors to white
	 */
	private void calculateColors(int nPoints, int nRotations) {
		this.colors = new float[points.length];

		for (int i = 0; i < colors.length; i++) {
			colors[i] = 1;
		}
	}

	// TODO: not yet finished
	private void calculateIndices(int nPoints, int nRotations, int nSegments) {
		ArrayList<Integer> indices = new ArrayList<Integer>();

		for (int j = 0; j < nRotations; j++) {
			for (int i = 0; i < 6 * (nPoints *nSegments - 1); i += 6) {

				indices.add((i / 6 + (j * nPoints * nSegments)));
				indices.add(((i / 6 + (j * nPoints * nSegments)) + 1 + (nPoints * nSegments)) % ((nPoints * nSegments) * nRotations));
				indices.add(((i / 6 + (j * nPoints * nSegments)) + (nPoints * nSegments)) % ((nPoints * nSegments) * nRotations));

				indices.add((i / 6 + (j * nPoints * nSegments)));
				indices.add(((i / 6 + (j * nPoints * nSegments)) + 1) % ((nPoints * nSegments) * nRotations));
				indices.add(((i / 6 + (j * nPoints * nSegments) + 1) + (nPoints * nSegments)) % ((nPoints * nSegments) * nRotations));
			}
		}
		this.indices = toIArray(indices);
	}

	private void createShape(int size, RenderContext renderContext) {
		VertexData vData = renderContext.makeVertexData(size);

		vData.addElement(points, VertexData.Semantic.POSITION, 3);
		vData.addElement(colors, VertexData.Semantic.COLOR, 3);
		vData.addElement(normals, VertexData.Semantic.NORMAL, 3);
		vData.addIndices(indices);

		shape = new Shape(vData);
	}

	/**
	 * @return a rotated array by 1/angle. the original array is not changed
	 */
	private ArrayList<Vector4f> rotateArray(ArrayList<Vector4f> vectors, float angle) {

		ArrayList<Vector4f> results = new ArrayList<Vector4f>();

		for (Vector4f v : vectors) {
			results.add(rotate(v, angle));
		}

		return results;
	}

	private Vector4f rotate(Vector4f v, float angle) {
		Matrix4f rotation = new Matrix4f();
		rotation.setIdentity();
		rotation.rotY((float) (Math.PI * 2 * angle));

		Vector4f result = new Vector4f(v);

		rotation.transform(result);

		return result;
	}

	private float[] toBArray(ArrayList<Vector4f> vectors) {
		float[] result = new float[vectors.size() * 3];
		int i = 0;

		for (Vector4f v : vectors) {
			result[i + 0] = v.x;
			result[i + 1] = v.y;
			result[i + 2] = v.z;
			i += 3;
		}

		return result;
	}

	private int[] toIArray(ArrayList<Integer> integers) {
		int[] ints = new int[integers.size()];

		for (int i = 0; i < integers.size(); i++) {
			ints[i] = integers.get(i);
		}
		return ints;
	}
}
