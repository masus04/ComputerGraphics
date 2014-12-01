package simple;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector4f;

public class BezierRotation {
	
	private BezierCurve[] bCurves;
	
	/**
	 * as many control points as can be fitted into segments are taken into
	 * account. additional points are ignored
	 * 
	 * @param nPoints		number of points to calculate along each curve segment
	 * @param nRotations	the number of rotations around the axis of the Object
	 * 
	 * @param controlPoints	the control points for the whole curve
	 */
	public BezierRotation(int nPoints, Vector4f[] controlPoints, int nRotations){
		bCurves = new BezierCurve[nRotations];
		
		for (int i= 0; i< nRotations; i++){
			bCurves[i] = new BezierCurve(nPoints, rotate(controlPoints, ((float)i)/((float)nRotations)));
		}
	}

	
	
	private Vector4f[] rotate(Vector4f[] controlPoints, float angle) {		
		Matrix4f rotation = new Matrix4f();
		rotation.rotX((float) (Math.PI * 2.0 * angle));

		Vector4f[] results = controlPoints.clone();
		
		for (int i=0; i<results.length; i++){
			rotation.transform(results[i]);
		}
		
		return results;
	}
}
