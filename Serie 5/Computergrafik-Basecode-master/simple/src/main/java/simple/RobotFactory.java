package simple;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import jrtr.GraphTransformGroup;
import jrtr.RenderContext;

public class RobotFactory {

	private RenderContext renderContext;
	private float scale;

	public void makeRobot(GraphTransformGroup root, RenderContext renderContext, float scale) {
		this.renderContext = renderContext;
		this.scale = scale;

		makeTorso(root);
	}

	private void makeTorso(GraphTransformGroup root) {
		Cylinder cylinder = new Cylinder(renderContext, (int) (50 * scale), 1 * scale, 3 * scale);
		root.add(cylinder.getShape());
		
		makeHead(root);
		makeArms(root);
		makeLegs(root);
	}

	private void makeHead(GraphTransformGroup root) {
		GraphTransformGroup headGroup = new GraphTransformGroup();

		// create shape
		Cube cube = new Cube(renderContext);
		cube.setScale(0.66f * scale);
		headGroup.add(cube.getShape());

		headGroup.setTransformation(makeTranslationMatrix(0, 2.5f * scale, 0));

		root.add(headGroup);
	}

	private void makeArms(GraphTransformGroup root) {
		// left arm
		GraphTransformGroup leftArm = makeArm();
		leftArm.setTransformation(makeTranslationMatrix(-1.5f * scale, 0.75f * scale, 0));

		root.add(leftArm);

		// left arm
		GraphTransformGroup rightArm = makeArm();
		rightArm.setTransformation(makeTranslationMatrix(1.5f * scale, 0.75f * scale, 0));

		root.add(rightArm);

	}

	private GraphTransformGroup makeArm() {
		// upper arm
		GraphTransformGroup upperArm = new GraphTransformGroup();
		Cylinder cylinder = new Cylinder(renderContext, (int) (25 * scale), 0.2f * scale, 1 * scale);
		upperArm.add(cylinder.getShape());

		// forearm
		GraphTransformGroup foreArm = new GraphTransformGroup();
		cylinder = new Cylinder(renderContext, (int) (25 * scale), 0.2f * scale, 1 * scale);
		foreArm.add(cylinder.getShape());

		foreArm.setTransformation(makeTranslationMatrix(0, -1.25f * scale, 0));

		upperArm.add(foreArm);

		// DEBUG ONLY
		// root.add(upperArm);
		// /DEBUG ONLY

		return upperArm;
	}

	private void makeLegs(GraphTransformGroup root) {
		GraphTransformGroup leftLeg = makeLeg();
		leftLeg.setTransformation(makeTranslationMatrix(-0.66f * scale, -2.33f * scale, 0));

		root.add(leftLeg);
		
		GraphTransformGroup rightLeg = makeLeg();
		rightLeg.setTransformation(makeTranslationMatrix(0.66f * scale,  -2.33f * scale, 0));
		
		root.add(rightLeg);
	}

	private GraphTransformGroup makeLeg() {
		// upper leg
		GraphTransformGroup upperLeg = new GraphTransformGroup();
		Cylinder cylinder = new Cylinder(renderContext, (int) (25 * scale), 0.33f * scale, 1.25f * scale);

		upperLeg.add(cylinder.getShape());

		// lower leg
		GraphTransformGroup lowerLeg = new GraphTransformGroup();
		cylinder = new Cylinder(renderContext, (int) (25 * scale), 0.33f * scale, 1.25f * scale);
		lowerLeg.add(cylinder.getShape());
		lowerLeg.setTransformation(makeTranslationMatrix(0, -1.5f *scale, 0));
		
		upperLeg.add(lowerLeg);

		return upperLeg;
	}

	/**
	 * @return an identity matrix translated by x, y and z
	 */
	private Matrix4f makeTranslationMatrix(float x, float y, float z) {
		Matrix4f transformation = new Matrix4f();
		transformation.setIdentity();
		transformation.setTranslation((new Vector3f(x, y, z)));
		return transformation;
	}

}
