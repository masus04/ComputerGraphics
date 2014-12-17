package simple;

import jrtr.*;

import javax.swing.*;

import java.awt.event.*;

import javax.vecmath.*;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Implements a simple application that opens a 3D rendering window and shows a
 * rotating cube.
 */
public class simple
{
	static RenderPanel renderPanel;
	static RenderContext renderContext;
	static Shader normalShader;
	static Shader diffuseShader;
	static Material material;
	static SimpleSceneManager sceneManager;
	static Shape shape;
	static float currentstep, basicstep;
	static int task;
	static MeshData meshData;

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

			// Make a simple geometric object: a cube

			// The vertex positions of the cube
			float v[] = { -1, -1, 1, 1, -1, 1, 1, 1, 1, -1, 1, 1, // front face
					-1, -1, -1, -1, -1, 1, -1, 1, 1, -1, 1, -1, // left face
					1, -1, -1, -1, -1, -1, -1, 1, -1, 1, 1, -1, // back face
					1, -1, 1, 1, -1, -1, 1, 1, -1, 1, 1, 1, // right face
					1, 1, 1, 1, 1, -1, -1, 1, -1, -1, 1, 1, // top face
					-1, -1, 1, -1, -1, -1, 1, -1, -1, 1, -1, 1 }; // bottom face

			// The vertex normals
			float n[] = { 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, // front face
					-1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, // left face
					0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, // back face
					1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, // right face
					0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, // top face
					0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0 }; // bottom face

			// The vertex colors
			float c[] = { 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0,
					0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0,
					1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0,
					0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0,
					0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1,
					0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1 };

			// Texture coordinates
			float uv[] = { 0, 0, 1, 0, 1, 1, 0, 1,
					0, 0, 1, 0, 1, 1, 0, 1,
					0, 0, 1, 0, 1, 1, 0, 1,
					0, 0, 1, 0, 1, 1, 0, 1,
					0, 0, 1, 0, 1, 1, 0, 1,
					0, 0, 1, 0, 1, 1, 0, 1 };

			// Construct a data structure that stores the vertices, their
			// attributes, and the triangle mesh connectivity
			VertexData vertexData = renderContext.makeVertexData(24);
			vertexData.addElement(c, VertexData.Semantic.COLOR, 3);
			vertexData.addElement(v, VertexData.Semantic.POSITION, 3);
			vertexData.addElement(n, VertexData.Semantic.NORMAL, 3);
			vertexData.addElement(uv, VertexData.Semantic.TEXCOORD, 2);

			// The triangles (three vertex indices for each triangle)
			int indices[] = { 0, 2, 3, 0, 1, 2, // front face
					4, 6, 7, 4, 5, 6, // left face
					8, 10, 11, 8, 9, 10, // back face
					12, 14, 15, 12, 13, 14, // right face
					16, 18, 19, 16, 17, 18, // top face
					20, 22, 23, 20, 21, 22 }; // bottom face

			vertexData.addIndices(indices);

			// Make a scene manager and add the object
			sceneManager = new SimpleSceneManager();
			renderContext.setSceneManager(sceneManager);

			task = 3;

			if (task == 0) {
				shape = new Shape(vertexData);
				sceneManager.addShape(shape);
			}

			if (task == 1) {
				ArrayList<Vector4f> controlPoints = new ArrayList<Vector4f>();
				controlPoints.add(new Vector4f(0, 0, 0, 1));
				controlPoints.add(new Vector4f(1, 0, 0, 1));
				controlPoints.add(new Vector4f(1, 2, 0, 1));
				controlPoints.add(new Vector4f(0.5f, 2, 0, 1));

				controlPoints.add(new Vector4f(0.5f, 2, 0, 1));
				controlPoints.add(new Vector4f(1, 2, 0, 1));
				controlPoints.add(new Vector4f(1, 4, 0, 1));
				controlPoints.add(new Vector4f(0, 4, 0, 1));

				BezierRotation bRotation = new BezierRotation(30, controlPoints, 30, renderContext);
				shape = bRotation.getShape();

				sceneManager.addShape(shape);
			}

			if (task == 2 || task == 4) {
				Torus torus = new Torus(3, 1, 8, renderContext);
				shape = torus.getShape();

				/*
				 * try { shape = new Shape(ObjReader.read(
				 * "C:/Users/Masus04/workspace/ComputerGraphics/Serie 6/Computergrafik-Basecode-master/obj/teapot.obj"
				 * , 1, renderContext)); } catch (Exception e) {
				 * System.out.println("could not read object"); }
				 */

				meshData = new MeshData(shape.getVertexData(), renderContext);
				// meshData.loop();

				// shape = new Shape(meshData.getVertexData());

				sceneManager.addShape(shape);
			}

			if (task == 3) {
				Scene scene = new Scene(renderContext);

				for (Shape s : scene.getShapes())
					sceneManager.addShape(s);

				/*Light light = new Light();
				light.direction = new Vector3f(0, 0, 10);
				sceneManager.addLight(light);

				light = new Light();
				light.direction = new Vector3f(0, 10, 0);
				sceneManager.addLight(light);
				 */
			}

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
				material.texture.load("../textures/wood.jpg");
			} catch (Exception e) {
				System.out.print("Could not load texture.\n");
				System.out.print(e.getMessage());
			}

			// Register a timer task
			Timer timer = new Timer();
			basicstep = 0.01f;
			currentstep = basicstep;
			if (task == 0 || task == 1 || task == 2 || task == 4)
				timer.scheduleAtFixedRate(new AnimationTask(), 0, 10);
		}
	}

	/**
	 * A timer task that generates an animation. This task triggers the
	 * redrawing of the 3D scene every time it is executed.
	 */
	public static class AnimationTask extends TimerTask
	{
		int counter;

		public AnimationTask() {
			counter = 0;
		}

		public void run()
		{
			// subdivision
			if (task == 4) {
				counter++;
				if (counter % 500 == 0) {

					meshData = new MeshData(shape.getVertexData(), renderContext);

					Matrix4f t = shape.getTransformation();
					shape = meshData.loop();
					shape.setTransformation(t);

					sceneManager = new SimpleSceneManager();
					sceneManager.addShape(shape);

					renderContext.setSceneManager(sceneManager);
				}
			}

			// Update transformation by rotating with angle "currentstep"
			Matrix4f t = shape.getTransformation();
			Matrix4f rot = new Matrix4f();

			rot.rotX(currentstep);
			t.mul(rot);

			rot.rotY(currentstep);
			t.mul(rot);

			shape.setTransformation(t);

			// Trigger redrawing of the render window
			renderPanel.getCanvas().repaint();
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
			case ' ': {
				Matrix4f t = shape.getTransformation();

				shape = meshData.loop();

				shape.setTransformation(t);

				sceneManager = new SimpleSceneManager();
				sceneManager.addShape(shape);

				renderContext.setSceneManager(sceneManager);
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
