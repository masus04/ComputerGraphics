package simple;

import jrtr.*;

import java.util.ArrayList;

import javax.vecmath.Vector4f;

public class Scene {
	private ArrayList<Shape> shapes;
	private RenderContext renderContext;

	public Scene(RenderContext renderContext) {
		this.renderContext = renderContext;

		shapes = new ArrayList<Shape>();
		//shapes.add(createBottle());
		
		shapes.add(createCandle());
	}

	private Shape createBottle() {

		ArrayList<Vector4f> controlPoints = new ArrayList<Vector4f>();
		controlPoints.add(new Vector4f(0, 0, 0, 1));
		controlPoints.add(new Vector4f(2, -1, 0, 1));
		controlPoints.add(new Vector4f(3, 0, 0, 1));
		controlPoints.add(new Vector4f(2, 2, 0, 1));
		
		controlPoints.add(new Vector4f(2, 2, 0, 1));
		controlPoints.add(new Vector4f(1.5f, 3, 0, 1));
		controlPoints.add(new Vector4f(2, 5, 0, 1));
		controlPoints.add(new Vector4f(2, 6, 0, 1));
		
		controlPoints.add(new Vector4f(2, 6, 0, 1));
		controlPoints.add(new Vector4f(2, 7, 0, 1));
		controlPoints.add(new Vector4f(0.75f, 9, 0, 1));
		controlPoints.add(new Vector4f(0.75f, 13, 0, 1));

		BezierRotation bottleRotation = new BezierRotation(100, controlPoints, 100, renderContext);

		Shape shape = bottleRotation.getShape();
		
		shape.setMaterial(makeMaterial("greenGlass"));
		
		return shape;
	}
	
	private Shape createCandle() {
		ArrayList<Vector4f> controlPoints = new ArrayList<Vector4f>();
		controlPoints.add(new Vector4f(0, 0, 0, 1));
		controlPoints.add(new Vector4f(2, -2, 0, 1));
		controlPoints.add(new Vector4f(4, 0, 0, 1));
		controlPoints.add(new Vector4f(2, 4, 0, 1));
		
		BezierRotation bottleRotation = new BezierRotation(100, controlPoints, 100, renderContext);

		Shape shape = bottleRotation.getShape();
		
		shape.setMaterial(makeMaterial("AppleTexture"));
		
		return shape;
	}

	public ArrayList<Shape> getShapes() {
		return shapes;
	}

	private Material makeMaterial(String textureName) {
		
		Shader diffuseShader = renderContext.makeShader();
		try {
			diffuseShader.load("../jrtr/shaders/diffuse.vert", "../jrtr/shaders/diffuse.frag");
		} catch (Exception e) {
			System.out.print("Problem with shader:\n");
			System.out.print(e.getMessage());
		}
		
		Material material = new Material();
		material.shader = diffuseShader;
		material.texture = renderContext.makeTexture();
		try {
			material.texture.load("../textures/" + textureName + ".jpg");
		} catch (Exception e) {
			System.out.print("Could not load texture.\n");
			System.out.print(e.getMessage());
		}
		
		return material;
	}
}
