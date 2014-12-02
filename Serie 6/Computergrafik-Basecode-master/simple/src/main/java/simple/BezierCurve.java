package simple;

import java.util.ArrayList;

import javax.vecmath.Vector4f;

public class BezierCurve {
	private ArrayList<Segment> segments;
	private ArrayList<Vector4f> points;
	private ArrayList<Vector4f> normals;

	/**
	 * as many control points as can be fitted into segments are taken into
	 * account. additional points are ignored
	 * 
	 * @param nPoints
	 *            number of points to calculate along each curve segment
	 * @param controlPoints
	 *            the control points for the whole curve
	 */
	public BezierCurve(int nPoints, ArrayList<Vector4f> controlPoints) {
		segments = new ArrayList<Segment>();

		for (int i = 0; i < controlPoints.size() - 3; i += 4) {
			segments.add(new Segment(controlPoints.get(i), controlPoints.get(i + 1), controlPoints.get(i + 2),
					controlPoints.get(i + 3)));
		}

		calculatePoints(nPoints);
	}

	private void calculatePoints(int nPoints) {
		for (Segment s : segments)
			s.calculatePoints(nPoints);

		concatinateSegments();
	}

	private void concatinateSegments() {
		points = new ArrayList<Vector4f>();
		normals = new ArrayList<Vector4f>();

		for (Segment s : segments) {
			points.addAll(s.getPoints());
			normals.addAll(s.getNormals());
		}
	}

	private Vector4f mul(Vector4f v, float f) {
		return new Vector4f(v.x * f, v.y * f, v.z * f, v.w);
	}

	public ArrayList<Vector4f> getPoints() {
		return points;
	}

	public ArrayList<Vector4f> getNormals() {
		return normals;
	}

	public class Segment {
		private Vector4f p0, p1, p2, p3;
		private ArrayList<Vector4f> points, normals;

		public Segment(Vector4f p0, Vector4f p1, Vector4f p2, Vector4f p3) {
			this.p0 = p0;
			this.p1 = p1;
			this.p2 = p2;
			this.p3 = p3;
		}

		public ArrayList<Vector4f> getPoints() {
			return points;
		}

		public ArrayList<Vector4f> getNormals() {
			return normals;
		}

		public void calculatePoints(int nPoints) {
			points = new ArrayList<Vector4f>();
			normals = new ArrayList<Vector4f>();

			for (float i = 0; i < nPoints; i++)
				deCasteljau(i, nPoints);
		}

		private void deCasteljau(float i, float nPoints) {
			float t = i / (nPoints-1);

			Vector4f q0, q1, q2, r0, r1, x, normal;

			q0 = linearLinterpolation(t, p0, p1);
			q1 = linearLinterpolation(t, p1, p2);
			q2 = linearLinterpolation(t, p2, p3);

			r0 = linearLinterpolation(t, q0, q1);
			r1 = linearLinterpolation(t, q1, q2);

			x = linearLinterpolation(t, r0, r1);

			normal = new Vector4f();
			normal.sub(r0, r1);

			points.add(x);
			normals.add(new Vector4f(-normal.y, normal.x, normal.z, normal.w));
		}

		private Vector4f linearLinterpolation(float t, Vector4f p1, Vector4f p2) {
			Vector4f result = mul(p1, (float) (1.0 - t));
			result.add(mul(p2, t));

			return result;
		}
	}

}
