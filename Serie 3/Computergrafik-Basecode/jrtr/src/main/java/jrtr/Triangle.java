package jrtr;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.SingularMatrixException;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

public class Triangle {
	private static Vector4f p1, p2, p3;

	private boolean isTransformed = false;

	private static Matrix3f edgeFunctionsMatrix;

	public Triangle(Vector4f p1, Vector4f p2, Vector4f p3) {
		Triangle.p1 = p1;
		Triangle.p2 = p2;
		Triangle.p3 = p3;

		calculateEdgeFunction();
	}

	public Vector4f getP1() {
		return p1;
	}

	public Vector4f getP2() {
		return p2;
	}

	public Vector4f getP3() {
		return p3;
	}

	public Matrix3f calculateEdgeFunction() {
		edgeFunctionsMatrix = new Matrix3f();

		edgeFunctionsMatrix.setRow(0, new Vector3f(p1.x, p1.y, p1.w));
		edgeFunctionsMatrix.setRow(1, new Vector3f(p2.x, p2.y, p2.w));
		edgeFunctionsMatrix.setRow(2, new Vector3f(p3.x, p3.y, p3.w));

		try {
			edgeFunctionsMatrix.invert();
		} catch (SingularMatrixException e) {
			return edgeFunctionsMatrix = null;
		}

		return edgeFunctionsMatrix;
	}

	public boolean isInside(Vector4f point) {
		Matrix3f vectorM = new Matrix3f();
		Vector3f vector = new Vector3f();
		vectorM.setColumn(0, new Vector3f(point.x, point.y, point.z));

		vectorM.mul(edgeFunctionsMatrix, vectorM);
		vectorM.getColumn(0, vector);

		return (vector.x > 0 && vector.y > 0 && vector.z > 0);
	}

	/**
	 * transforms all points with the parameter transformation matrix
	 * 
	 * @param transformation
	 */
	public void transform(Matrix4f transformation) {
		if (!isTransformed) {

			transformation.transform(p1);
			transformation.transform(p2);
			transformation.transform(p3);

			isTransformed = true;
		}
	}

	public boolean isTransformed() {
		return isTransformed;
	}

	public double alphaFunction(Vector4f pixel) {
		return edgeFunctions(pixel, 0);
	}

	public double betaFunction(Vector4f pixel) {
		return edgeFunctions(pixel, 1);
	}

	public double gammaFunction(Vector4f pixel) {
		return edgeFunctions(pixel, 2);
	}

	private double edgeFunctions(Vector4f pixel, int abg) {
		float[] floats = new float[3];
		edgeFunctionsMatrix.getRow(abg, floats);

		return floats[0] * pixel.x / pixel.w + floats[1] * pixel.y / pixel.w + floats[2];
	}

	/**
	 * calculates if a pixel is inside the triangle using the edgefunctions
	 */
	public boolean isdrawn(Vector4f pixel) {
		if (edgeFunctionsMatrix == null)			// singular edgefunctionmatrix
			return false;

		if (edgeFunctionsMatrix.determinant() < 0)	// backface culling
			return false;

		if (!isVisible())
			return false;
			
		return (alphaFunction(pixel) > 0 && betaFunction(pixel) > 0 && gammaFunction(pixel) > 0);
	}

	public boolean isVisible() {
		return (p1.w > 0 && p2.w > 0 && p3.w > 0);
	}
	
	/**
	 * 
	 * @return an array containing the left, right, upper and lower border of the bounding box.
	 * 
	 * [leftBorder, rightBorder, upperBorder, lowerBorder]
	 */
	public float[] getBoundingBox(){
		float[] values = new float[4];
		
		values[0] = min(p1.x, p2.x, p3.x);
		values[1] = max(p1.x, p2.x, p3.x);
		values[2] = min(p1.y, p2.y, p3.y);
		values[3] = max(p1.y, p2.y, p3.y);
	
		return values;
	}
	
	

	private static float min(float a, float b, float c) {
	      if (b < a) {
	          a = b;
	      }
	      if (c < a) {
	          a = c;
	      }
	      return a;
	  }
	
	private float max(float a, float b, float c) {
		 if (b > a) {
	          a = b;
	      }
	      if (c > a) {
	          a = c;
	      }
	      
	      return a;
	}

	
	/**
	 * 
	 * @param pixel
	 * @return calculates the color to the pixel inside the triangle
	 */
	public int color(Vector4f pixel) {
		// TODO Auto-generated method stub
		return 0;
	}
}
