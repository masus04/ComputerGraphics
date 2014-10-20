package jrtr;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

public class Triangle {
	private static Vector4f p1;
	private static Vector4f p2;
	private static Vector4f p3;

	private static Matrix3f edgeFunctionsMatrix;
	
	public Triangle(Vector4f p1, Vector4f p2, Vector4f p3){
		Triangle.p1 = p1;
		Triangle.p2 = p2;
		Triangle.p3 = p3;
		
		calculateEdgeFunction();
	}
	
	public Vector4f getP1(){
		return p1;
	}
	
	public Vector4f getP2(){
		return p2;
	}
	
	public Vector4f getP3(){
		return p3;
	}

	public void calculateEdgeFunction() {
		edgeFunctionsMatrix = new Matrix3f();
		
		edgeFunctionsMatrix.setRow(0, new Vector3f(p1.x, p1.y, p1.z));
		edgeFunctionsMatrix.setRow(1, new Vector3f(p2.x, p2.y, p2.z));
		edgeFunctionsMatrix.setRow(2, new Vector3f(p3.x, p3.y, p3.z));
		
		edgeFunctionsMatrix.invert();
	}
	
	public boolean isInside(Vector4f point){
		Matrix3f vectorM = new Matrix3f();
		Vector3f vector = new Vector3f();
		vectorM.setColumn(0, new Vector3f(point.x, point.y, point.z));
		
		vectorM.mul(edgeFunctionsMatrix, vectorM);
		vectorM.getColumn(0, vector);
		
		return (vector.x > 0 && vector.y > 0 && vector.z > 0);
	}
}
