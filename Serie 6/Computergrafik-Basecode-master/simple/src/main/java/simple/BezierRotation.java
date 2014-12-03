package simple;

import jrtr.*;

import java.util.ArrayList;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point2f;
import javax.vecmath.Vector4f;

public class BezierRotation {

	private BezierCurve bCurve;
	private float[] points, colors, normals, texCoord;
	private int[] indices;
	private ArrayList<Point2f> coords;

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
		int nSegments = controlPoints.size() / 4 % 4;

		// TODO: set test variable
		boolean test = false;

		if (test) {
			test(bCurve, nPoints, renderContext);
		}

		if (!test) {
			concatinateCurves(nRotations);
			calculateColors(nPoints, nRotations);
			calculateIndices(nPoints, nRotations, nSegments);
			createShape(points.length / 3, renderContext);
		}
	}

	public Shape getShape() {
		return shape;
	}

	private void test(BezierCurve bCurve, int nPoints, RenderContext renderContext) {
		bCurve.getPoints().add(new Vector4f(0, 1, 0, 1));
		bCurve.getNormals().add(new Vector4f(-1, 0, 0, 0));

		points = toFArray(bCurve.getPoints());
		colors = new float[points.length];
		normals = toFArray(bCurve.getNormals());
		indices = toIArray(testIndices(nPoints));

		for (int i = 0; i < colors.length; i++)
			colors[i] = 1;

		VertexData vData = renderContext.makeVertexData(points.length / 3);

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

	/**
	 * collects all points and normals from the existing curves and stores them
	 * in the points and normals member arrays.
	 */
	private void concatinateCurves(int nRotations) {
		ArrayList<Vector4f> points = new ArrayList<Vector4f>();
		ArrayList<Vector4f> normals = new ArrayList<Vector4f>();
		ArrayList<Float> texCoords = new ArrayList<Float>();
		
		for (int i = 0; i < nRotations; i++) {
			float angle = ((float) i) / ((float) nRotations);
			
			ArrayList<Vector4f> segmentPoints = rotateArray(bCurve.getPoints(), angle);
			points.addAll(segmentPoints);
			
			normals.addAll(rotateArray(bCurve.getNormals(), angle));
		
			texCoords.addAll(calculateTextureCoord(segmentPoints, angle));
		}

		this.points = toFArray(points);
		this.normals = toFArray(normals);
		this.texCoord = toFarray(texCoords);
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
			for (int i = 0; i < 6 * (nPoints * nSegments - 1); i += 6) {

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

	/**
	 * 
	 * @param segmentPoints	the points of a segment to calculate texcoord for
	 * @param angle the angle the original curve is rotated. to be used as a v coordinate for texcoord
	 */
	private ArrayList<Float> calculateTextureCoord(ArrayList<Vector4f> segmentPoints, float angle) {
		ArrayList<Float> texCoord = new ArrayList<Float>();
		
		for (int i=0; i< segmentPoints.size(); i++){
			texCoord.add(segmentPoints.get(i).y);
			texCoord.add(angle);
		}
		
		return texCoord;
	}

	private void createShape(int size, RenderContext renderContext) {
		VertexData vData = renderContext.makeVertexData(size);

		vData.addElement(points, VertexData.Semantic.POSITION, 3);
		vData.addElement(colors, VertexData.Semantic.COLOR, 3);
		vData.addElement(normals, VertexData.Semantic.NORMAL, 3);
		vData.addElement(texCoord, VertexData.Semantic.TEXCOORD, 2);
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

	private float[] toFArray(ArrayList<Vector4f> vectors) {
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
	
	private float[] toFarray(ArrayList<Float> floats) {
		float[] array = new float[floats.size()];
		
		for (int i=0; i<floats.size(); i++)
			array[i] = floats.get(i);
		
		return array;
	}

	private int[] toIArray(ArrayList<Integer> integers) {
		int[] ints = new int[integers.size()];

		for (int i = 0; i < integers.size(); i++) {
			ints[i] = integers.get(i);
		}
		return ints;
	}
}
