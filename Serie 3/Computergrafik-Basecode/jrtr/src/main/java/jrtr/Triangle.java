package jrtr;

import javax.vecmath.Vector4f;

public class Triangle {
	private static Vector4f p1;
	private static Vector4f p2;
	private static Vector4f p3;
	
	public Triangle(Vector4f p1, Vector4f p2, Vector4f p3){
		Triangle.p1 = p1;
		Triangle.p2 = p2;
		Triangle.p3 = p3;
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
		// TODO Auto-generated method stub
		
	}
	
}
