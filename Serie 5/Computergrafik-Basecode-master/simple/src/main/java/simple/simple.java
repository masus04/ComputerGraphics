package simple;

import jrtr.*;

import javax.swing.*;

import java.awt.event.*;

import javax.vecmath.*;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Implements a simple application that opens a 3D rendering window and shows a
 * rotating cube.
 */
public class simple
{
	private static RenderPanel renderPanel;
	private static RenderContext renderContext;
	private static Shader normalShader;
	private static Shader diffuseShader;
	private static Material material;
	private static GraphSceneManager sceneManager;
	private static Shape shape;
	private static float currentstep, basicstep;

	private static float robotScale;

	/**
	 * An extension of {@link GLRenderPanel} or {@link SWRenderPanel} to provide
	 * a call-back function for initialization. Here we construct a simple 3D
	 * scene and start a timer task to generate an animation.
	 */
	public final static class SimpleRenderPanel extends GLRenderPanel
	{
		/**
		 * Initialization call-back. We initialize our renderer here.
		 * 
		 * @param r
		 *            the render context that is associated with this render
		 *            panel
		 */
		public void init(RenderContext r)
		{
			renderContext = r;

			sceneManager = new GraphSceneManager();

			// -------------------------- robot -------------------------- //
			robotScale = 0.5f;

			RobotFactory factory = new RobotFactory();
			factory.makeRobot(sceneManager.getRoot(), renderContext, robotScale);

			sceneManager.getRoot().getTransformation().setTranslation(new Vector3f(3, 0, 0));

			// -------------------------- robot -------------------------- //

			// Add the scene to the renderer
			renderContext.setSceneManager(sceneManager);

			// Load some more shaders
			normalShader = renderContext.makeShader();
			try {
				normalShader.load("../jrtr/shaders/normal.vert", "../jrtr/shaders/normal.frag");
			} catch (Exception e) {
				System.out.print("Problem with shader:\n");
				System.out.print(e.getMessage());
			}

			diffuseShader = renderContext.makeShader();
			try {
				diffuseShader.load("../jrtr/shaders/diffuse.vert", "../jrtr/shaders/diffuse.frag");
			} catch (Exception e) {
				System.out.print("Problem with shader:\n");
				System.out.print(e.getMessage());
			}

			// Make a material that can be used for shading
			material = new Material();
			material.shader = diffuseShader;
			material.texture = renderContext.makeTexture();
			try {
				material.texture.load("../textures/plant.jpg");
			} catch (Exception e) {
				System.out.print("Could not load texture.\n");
				System.out.print(e.getMessage());
			}

			// Register a timer task
			Timer timer = new Timer();
			basicstep = 0.01f;
			currentstep = basicstep;
			timer.scheduleAtFixedRate(new AnimationTask(), 0, 10);
		}
	}

	/**
	 * A timer task that generates an animation. This task triggers the
	 * redrawing of the 3D scene every time it is executed.
	 */
	public static class AnimationTask extends TimerTask
	{
		int steps, limit;
		float speed, cycle;

		public AnimationTask() {
			cycle = 1;
			speed = 0.01f;
			limit = (int) (cycle / speed);

			initAnimation();
		}

		private void initAnimation() {
			moveArms(limit);
			// fore arm
			Matrix4f rotation = new Matrix4f();
			rotation.rotX(-limit);
			moveForeArm(sceneManager.getRoot().getGraphGroup(1), rotation);

			rotation = new Matrix4f();
			rotation.rotX(-limit);
			moveForeArm(sceneManager.getRoot().getGraphGroup(2), rotation);
			//

			moveLegs(-limit);
		}

		public void run()
		{
			moveTorso(speed / 2);

			if (steps > limit)
				steps = -limit;

			if (0 <= steps && steps < limit) {
				moveArms(speed);
				moveLegs(-speed);
			} else if (-limit < steps && steps < 0) {
				moveArms(-speed);
				moveLegs(speed);
			}
			steps++;
		}

		private void moveTorso(float angleSpeed) {
			Matrix4f rotation = new Matrix4f();
			rotation.rotY(angleSpeed);

			Matrix4f transformation = sceneManager.getRoot().getTransformation();

			rotation.mul(transformation);
			sceneManager.getRoot().setTransformation(rotation);

			renderPanel.getCanvas().repaint();
		}

		private void moveArms(float speed) {
			GraphGroup leftArm = sceneManager.getRoot().getGraphGroup(1);
			GraphGroup rightArm = sceneManager.getRoot().getGraphGroup(2);

			moveArm(leftArm, speed);
			moveArm(rightArm, -speed);

		}

		private void moveArm(GraphGroup arm, float speed) {
			// upper arm
			Matrix4f rotation = new Matrix4f();
			rotation.rotX(speed);

			Matrix4f transformation = arm.getTransformation();

			transformation.mul(rotation);

			// fore arm
			moveForeArm(arm, rotation);

		}

		private void moveForeArm(GraphGroup arm, Matrix4f rotation) {
			Matrix4f transformation;
			arm = arm.getGraphGroup(0);

			Matrix4f translation = new Matrix4f();
			translation.setIdentity();
			translation.setTranslation(new Vector3f(0, 0.5f * robotScale, 0));

			transformation = arm.getTransformation();

			Matrix4f tmp = new Matrix4f();

			tmp.mul(rotation, translation);
			translation.invert();
			rotation.mul(translation, tmp);

			rotation.mul(transformation);
			arm.setTransformation(rotation);
		}

		private void moveLegs(float speed) {
			GraphGroup leftLeg = sceneManager.getRoot().getGraphGroup(3);
			GraphGroup rightLeg = sceneManager.getRoot().getGraphGroup(4);

			moveLeg(leftLeg, speed);
			moveLeg(rightLeg, -speed);
		}

		private void moveLeg(GraphGroup leg, float speed) {
			Matrix4f rotation = new Matrix4f();
			rotation.rotX(speed);

			Matrix4f transformation = leg.getTransformation();

			transformation.mul(rotation);
		}
	}

	/**
	 * A mouse listener for the main window of this application. This can be
	 * used to process mouse events.
	 */
	public static class SimpleMouseListener implements MouseListener
	{
		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mouseClicked(MouseEvent e) {
		}
	}

	/**
	 * A key listener for the main window. Use this to process key events.
	 * Currently this provides the following controls: 's': stop animation 'p':
	 * play animation '+': accelerate rotation '-': slow down rotation 'd':
	 * default shader 'n': shader using surface normals 'm': use a material for
	 * shading
	 */
	public static class SimpleKeyListener implements KeyListener
	{
		public void keyPressed(KeyEvent e)
		{
			switch (e.getKeyChar())
			{
			case 's': {
				// Stop animation
				currentstep = 0;
				break;
			}
			case 'p': {
				// Resume animation
				currentstep = basicstep;
				break;
			}
			case '+': {
				// Accelerate roation
				currentstep += basicstep;
				break;
			}
			case '-': {
				// Slow down rotation
				currentstep -= basicstep;
				break;
			}
			case 'n': {
				// Remove material from shape, and set "normal" shader
				shape.setMaterial(null);
				renderContext.useShader(normalShader);
				break;
			}
			case 'd': {
				// Remove material from shape, and set "default" shader
				shape.setMaterial(null);
				renderContext.useDefaultShader();
				break;
			}
			case 'm': {
				// Set a material for more complex shading of the shape
				if (shape.getMaterial() == null) {
					shape.setMaterial(material);
				} else
				{
					shape.setMaterial(null);
					renderContext.useDefaultShader();
				}
				break;
			}
			}

			// Trigger redrawing
			renderPanel.getCanvas().repaint();
		}

		public void keyReleased(KeyEvent e)
		{
		}

		public void keyTyped(KeyEvent e)
		{
		}

	}

	/**
	 * The main function opens a 3D rendering window, implemented by the class
	 * {@link SimpleRenderPanel}. {@link SimpleRenderPanel} is then called
	 * backed for initialization automatically. It then constructs a simple 3D
	 * scene, and starts a timer task to generate an animation.
	 */
	public static void main(String[] args)
	{
		// Make a render panel. The init function of the renderPanel
		// (see above) will be called back for initialization.
		renderPanel = new SimpleRenderPanel();

		// Make the main window of this application and add the renderer to it
		JFrame jframe = new JFrame("simple");
		jframe.setSize(500, 500);
		jframe.setLocationRelativeTo(null); // center of screen
		jframe.getContentPane().add(renderPanel.getCanvas());// put the canvas
																// into a JFrame
																// window

		// Add a mouse and key listener
		renderPanel.getCanvas().addMouseListener(new SimpleMouseListener());
		renderPanel.getCanvas().addKeyListener(new SimpleKeyListener());
		renderPanel.getCanvas().setFocusable(true);

		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setVisible(true); // show window
	}
}
