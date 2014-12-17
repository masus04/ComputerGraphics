package simple;

import jrtr.*;

import java.util.ArrayList;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

public class Scene {
	private ArrayList<Shape> shapes;
	private RenderContext renderContext;

	public Scene(RenderContext renderContext) {
		this.renderContext = renderContext;

		shapes = new ArrayList<Shape>();
		shapes.add(createBottle());
		shapes.add(createCandle());
		shapes.add(createTable());
		//shapes.add(Torus.createTorus(3, 1, 8, renderContext));

		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		matrix.setTranslation(new Vector3f(1.25f, -2, 3));
		matrix.setScale(0.25f);
		shapes.get(0).setTransformation(matrix);

		matrix = new Matrix4f();
		matrix.setIdentity();
		matrix.setTranslation(new Vector3f(-1, 0.25f, 0));
		matrix.setScale(0.3f);
		shapes.get(1).setTransformation(matrix);
		
		matrix = new Matrix4f();
		matrix.setIdentity();
		matrix.setTranslation(new Vector3f(0, -3, 0));
		shapes.get(2).setTransformation(matrix);
		
		
		/*matrix = new Matrix4f();
		matrix.setIdentity();
		matrix.setTranslation(new Vector3f(0, 3, 0));
		matrix.setScale(0.75f);
		shapes.get(3).setTransformation(matrix);
		*/
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

	private Shape createTable() {
		ArrayList<Vector4f> controlPoints = new ArrayList<Vector4f>();
		controlPoints.add(new Vector4f(1.5f, 0, 0, 1));
		controlPoints.add(new Vector4f(0.75f, 0, 0, 1));
		controlPoints.add(new Vector4f(0.5f, 0.75f, 0, 1));
		controlPoints.add(new Vector4f(0.5f, 1, 0, 1));

		controlPoints.add(new Vector4f(0.5f, 1, 0, 1));
		controlPoints.add(new Vector4f(0.5f, 2.5f, 0, 1));
		controlPoints.add(new Vector4f(2f, 3f, 0, 1));
		controlPoints.add(new Vector4f(3f, 3, 0, 1));

		/*
		 * controlPoints.add(new Vector4f(3f, 3, 0, 1)); controlPoints.add(new
		 * Vector4f(0, 2.5f, 0, 1)); controlPoints.add(new Vector4f(0, 1.5f, 0,
		 * 1)); controlPoints.add(new Vector4f(3f, 0, 0, 1));
		 */

		BezierRotation bottleRotation = new BezierRotation(100, controlPoints, 100, renderContext);

		Shape shape = bottleRotation.getShape();

		Matrix4f transformation = new Matrix4f();
		transformation.setIdentity();
		transformation.setTranslation(new Vector3f(0, -1.5f, 0));

		shape.setTransformation(transformation);

		shape.setMaterial(makeMaterial("wood"));

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
