package simple;

import jrtr.*;

import javax.swing.*;

import java.awt.Point;
import java.awt.event.*;

import javax.vecmath.*;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Implements a simple application that opens a 3D rendering window and
 * shows a rotating cube.
 */
public class SimpleTask3 {
	static RenderPanel renderPanel;
	static RenderContext renderContext;
	static Shader normalShader;
	static Shader diffuseShader;
	static Material material;
	static Shape shape;
	static float currentstep, basicstep;

	/**
	 * An extension of {@link GLRenderPanel} or {@link SWRenderPanel} to provide
	 * a call-back function for initialization. Here we construct a simple 3D
	 * scene and start a timer task to generate an animation.
	 */
	public final static class SimpleRenderPanel extends GLRenderPanel {
		static SimpleSceneManager sceneManager;

		/**
		 * Initialization call-back. We initialize our renderer here.
		 * 
		 * @param r
		 *            the render context that is associated with this render
		 *            panel
		 */
		public void init(RenderContext r) {
			renderContext = r;
			Shape[] shapes = null;

			sceneManager = new SimpleSceneManager();

			int geomObj = 4;

			// choose Task
			if (geomObj == 0) {
				shape = initCube();
				sceneManager.addShape(shape);
			}

			else if (geomObj == 1) {
				shape = initCylinder(30);
				sceneManager.addShape(shape);
			}

			else if (geomObj == 2) {
				shape = initTorus(3, 1, 40);
				sceneManager.addShape(shape);
			}

			else if (geomObj == 3) {
				shapes = buildTruck();

				Matrix4f translation = new Matrix4f();
				translation.setIdentity();
				translation.setTranslation(new Vector3f(0, 0, 0));

				for (Shape shape : shapes) {
					Matrix4f mat = new Matrix4f(shape.getTransformation());
					mat.mul(translation);		// translate shape to orbit
					shape.setTransformation(mat);
					sceneManager.addShape(shape);
				}
			}

			else if (geomObj == 4) {
				int resolution = 32;

				Landscape landscape = new Landscape(resolution, 50, 0, 0, 0, 0);
				landscape.setSnowAndWaterLine(2, 0.01);
				
				shape = landscape.getShape(renderContext);
				shape.getTransformation().setScale((float) (5 / Math.sqrt(resolution)));
				sceneManager.addShape(shape);

				//camerasetup
				Camera camera = sceneManager.getCamera();
				Frustum frustum = sceneManager.getFrustum();

				camera.setCenterOfProjection(0, 0, 40);
				camera.setLookAtPoint(0, 0, 0);
				camera.setUpVector(0, 1, 0);

				frustum.setProjectionMatrix(1, 100, 1, (float) (Math.PI / 3));

			}

			// Make a scene manager and add the object

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
			if (geomObj < 3)
				timer.scheduleAtFixedRate(new AnimationTask(), 0, 10);

			else if (geomObj == 3)
				timer.scheduleAtFixedRate(new TruckAnimationTask(shapes), 0, 10);
		}

		public static class TruckAnimationTask extends TimerTask {

			Shape[] shapes;
			Matrix4f translation;
			Matrix4f translationI;

			public TruckAnimationTask(Shape[] shapes) {
				this.shapes = shapes;
				translation = new Matrix4f();
				translation.setIdentity();
				translation.setTranslation(new Vector3f(0, 0, 3));

				for (Shape shape : shapes) {
					Matrix4f mat = new Matrix4f(shape.getTransformation());
					mat.mul(translation);
					shape.setTransformation(mat);
				}
			}

			public void run() {

				Matrix4f mat;
				Matrix4f rotY = new Matrix4f();

				for (Shape shape : shapes) {
					rotY.rotY(currentstep);

					mat = new Matrix4f(shape.getTransformation());
					mat.mul(rotY);
					shape.setTransformation(mat);
				}

				// Trigger redrawing of the render window
				renderPanel.getCanvas().repaint();
			}
		}

		/**
		 * A timer task that generates an animation. This task triggers the
		 * redrawing of the 3D scene every time it is executed.
		 */
		public static class AnimationTask extends TimerTask {
			public void run() {
				// Update transformation by rotating with angle "currentstep"
				Matrix4f t = shape.getTransformation();
				Matrix4f rotX = new Matrix4f();
				rotX.rotX(currentstep);
				Matrix4f rotY = new Matrix4f();
				rotY.rotY(currentstep);
				t.mul(rotX);
				t.mul(rotY);
				shape.setTransformation(t);

				// Trigger redrawing of the render window
				renderPanel.getCanvas().repaint();
			}
		}

		/**
		 * A mouse listener for the main window of this application. This can be
		 * used to process mouse events.
		 */
		public static class SimpleMouseListener implements MouseListener, MouseMotionListener {

			private static Vector3f p1;
			private static Vector3f p2;

			Matrix4f transformation;

			public void mousePressed(MouseEvent e) {
				p1 = transformTo3d(new Point(e.getX(), e.getY()));

				transformation = sceneManager.getCamera().getCameraMatrix();
			}

			public void mouseDragged(MouseEvent e) {

				p2 = transformTo3d(new Point(e.getX(), e.getY()));

				Vector3f axis = new Vector3f();
				axis.cross(p1, p2);
				float angle = p1.angle(p2);

				sceneManager.getCamera().rotateCamera(axis, angle);
				
				renderPanel.getCanvas().repaint();
				
				p1 = new Vector3f(p2);
			}

			public void mouseReleased(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {
			}

			private static Vector3f transformTo3d(Point p) {
				float width = renderPanel.getCanvas().getWidth();
				float height = renderPanel.getCanvas().getHeight();

				Vector3f p3d = new Vector3f();

				p3d.x = (4 * p.x / (width + height)) - 1;
				p3d.y = 1 - (4 * p.y / (width + height));

				//p3d.x = p3d.x - 1;									// set origin to center
				//p3d.y = 1 - p3d.y;									// flip y coordinate

				float z2 = 1 - p3d.x * p3d.x - p3d.y * p3d.y;		// calculate z coordinate
				p3d.z = (float) (z2 > 0 ? Math.sqrt(z2) : 0);

				p3d.normalize();

				return p3d;
			}
		}

		/**
		 * A key listener for the main window. Use this to process key events.
		 * Currently this provides the following controls: 's': stop animation
		 * 'p': play animation '+': accelerate rotation '-': slow down rotation
		 * 'd': default shader 'n': shader using surface normals 'm': use a
		 * material for shading
		 */
		public static class SimpleKeyListener implements KeyListener {

			int speed = 1;

			public void keyPressed(KeyEvent e) {
				switch (e.getKeyChar()) {
				case 't': {
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
				case 'r': {
					// Remove material from shape, and set "default" shader
					shape.setMaterial(null);
					renderContext.useDefaultShader();
					break;
				}
				case 'm': {
					// Set a material for more complex shading of the shape
					if (shape.getMaterial() == null) {
						shape.setMaterial(material);
					} else {
						shape.setMaterial(null);
						renderContext.useDefaultShader();
					}
					break;
				}

				case 'w': {
					sceneManager.getCamera().moveCamera('w');
					//SimpleRenderPanel.setCameraTranslation(new Vector3f(0, 0, speed));
					break;
				}

				case 'a': {
					sceneManager.getCamera().moveCamera('a');
					break;
				}

				case 's': {
					sceneManager.getCamera().moveCamera('s');
					break;
				}

				case 'd': {
					sceneManager.getCamera().moveCamera('d');
					break;
				}

				}

				// Trigger redrawing
				renderPanel.getCanvas().repaint();
			}

			public void keyReleased(KeyEvent e) {
			}

			public void keyTyped(KeyEvent e) {
			}

		}

		/**
		 * The main function opens a 3D rendering window, implemented by the
		 * class {@link SimpleRenderPanel}. {@link SimpleRenderPanel} is then
		 * called backed for initialization automatically. It then constructs a
		 * simple 3D scene, and starts a timer task to generate an animation.
		 */
		public static void main(String[] args) {
			// Make a render panel. The init function of the renderPanel
			// (see above) will be called back for initialization.
			renderPanel = new SimpleRenderPanel();

			// Make the main window of this application and add the renderer to
			// it
			JFrame jframe = new JFrame("simple");
			jframe.setSize(500, 500);
			jframe.setLocationRelativeTo(null); // center of screen
			jframe.getContentPane().add(renderPanel.getCanvas());// put the
																	// canvas
																	// into a
																	// JFrame
																	// window

			// Add a mouse and key listener
			SimpleMouseListener mouseListener = new SimpleMouseListener();
			
			renderPanel.getCanvas().addMouseListener(mouseListener);
			renderPanel.getCanvas().addMouseMotionListener(mouseListener);
			renderPanel.getCanvas().addKeyListener(new SimpleKeyListener());
			renderPanel.getCanvas().setFocusable(true);

			jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jframe.setVisible(true); // show window
		}

		public static void setCameraTranslation(Vector3f translation) {
			Matrix4f cameraMatrix = sceneManager.getCamera().getCameraMatrix();
			Matrix4f tmp = new Matrix4f();
			tmp.setTranslation(translation);
			cameraMatrix.add(tmp);
		}

	}

	private static Shape initCube() {
		// Make a simple geometric object: a cube

		// The vertex positions of the cube
		float v[] = { -1, -1, 1, 1, -1, 1, 1, 1, 1, -1, 1, 1, // front face
				-1, -1, -1, -1, -1, 1, -1, 1, 1, -1, 1, -1, // left face
				1, -1, -1, -1, -1, -1, -1, 1, -1, 1, 1, -1, // back face
				1, -1, 1, 1, -1, -1, 1, 1, -1, 1, 1, 1, // right face
				1, 1, 1, 1, 1, -1, -1, 1, -1, -1, 1, 1, // top face
				-1, -1, 1, -1, -1, -1, 1, -1, -1, 1, -1, 1 }; // bottom face

		float[] positions = v; // 72

		// The vertex normals
		float n[] = { 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, // front face
				-1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, // left face
				0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, // back face
				1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, // right face
				0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, // top face
				0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0 }; // bottom face

		float[] normals = n; // 72

		// The vertex colors
		float c[] = { 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1,
				0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0,
				0, 1 };

		float[] colors = c; // 72

		// Texture coordinates
		float uv[] = { 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0,
				1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1 };

		float[] textureCoordinates = uv; // 48

		// The triangles (three vertex indices for each triangle)
		int ind[] = { 0, 2, 3, 0, 1, 2, // front face
				4, 6, 7, 4, 5, 6, // left face
				8, 10, 11, 8, 9, 10, // back face
				12, 14, 15, 12, 13, 14, // right face
				16, 18, 19, 16, 17, 18, // top face
				20, 22, 23, 20, 21, 22 }; // bottom face

		int[] indices = ind; // 36

		// Construct a data structure that stores the vertices, their
		// attributes, and the triangle mesh connectivity
		VertexData vertexData = renderContext.makeVertexData(24);
		vertexData.addElement(colors, VertexData.Semantic.COLOR, 3);
		vertexData.addElement(positions, VertexData.Semantic.POSITION, 3);
		vertexData.addElement(normals, VertexData.Semantic.NORMAL, 3);
		vertexData.addElement(textureCoordinates, VertexData.Semantic.TEXCOORD, 2);

		vertexData.addIndices(indices);

		Shape shape = new Shape(vertexData);

		return shape;

	}

	private static Shape initCylinder(int resolution) {
		int res = resolution;
		int Radius = 1;
		int Height = 2;

		float[] positions;
		float[] colors;
		int[] indices;

		// Vertices

		positions = new float[6 * (res + 1)];

		for (int i = 0; i < 3 * res; i += 3) {
			// Upper Circle
			positions[i] = (float) (Math.cos(i / 3 * 2 * Math.PI / res) * Radius); // x
			positions[i + 1] = Height / 2; // y
			positions[i + 2] = (float) (Math.sin(i / 3 * 2 * Math.PI / res) * Radius); // z
		}

		for (int i = 0; i < 3 * res; i += 3) {
			// Lower Circle
			positions[i + (3 * res)] = (float) Math.cos(i / 3 * 2 * Math.PI / res) * Radius;
			positions[i + 1 + (3 * res)] = -Height / 2;
			positions[i + 2 + (3 * res)] = (float) Math.sin(i / 3 * 2 * Math.PI / res) * Radius;
		}

		// Upper Center
		positions[6 * res] = 0;
		positions[6 * res + 1] = Height / 2;
		positions[6 * res + 2] = 0;

		// Lower Center
		positions[6 * res + 3] = 0;
		positions[6 * res + 4] = -Height / 2;
		positions[6 * res + 5] = 0;

		// Colors

		colors = new float[6 * (res + 1)];

		for (int i = 0; i < 6 * res; i += 6) {
			colors[i] = 0; // White
			colors[i + 1] = 0;
			colors[i + 2] = 0;

			colors[i + 3] = 1; // Black
			colors[i + 4] = 1;
			colors[i + 5] = 1;
		}

		// Upper Center
		colors[6 * res] = 1;
		colors[6 * res + 1] = 1;
		colors[6 * res + 2] = 1;

		// Lower Center
		colors[6 * res + 3] = 1;
		colors[6 * res + 4] = 1;
		colors[6 * res + 5] = 1;

		// Indices

		indices = new int[12 * res];

		// Top Circle

		for (int i = 0; i < (3 * res); i += 3) {
			indices[i] = (i / 3);
			indices[i + 1] = (i / 3 + 1) % res;
			indices[i + 2] = 2 * res;
		}

		// Bottom Circle

		for (int i = 0; i < 3 * res; i += 3) {
			indices[i + (3 * res)] = i / 3 + res;
			indices[i + 1 + (3 * res)] = (i / 3 + 1) % res + res;
			indices[i + 2 + (3 * res)] = 2 * res + 1;
		}

		// Mantle

		for (int i = 0; i < 6 * res; i += 6) {
			indices[i + (6 * res)] = i / 6;
			indices[i + (6 * res) + 1] = (i / 6 + 1) % res;
			indices[i + (6 * res) + 2] = (i / 6 + 1) % res + res;

			indices[i + (6 * res) + 3] = i / 6;
			indices[i + (6 * res) + 4] = (i / 6 + 1) % res + res;
			indices[i + (6 * res) + 5] = (i / 6) % res + res;
		}

		// Construct a data structure that stores the vertices, their
		// attributes, and the triangle mesh connectivity
		VertexData vertexData = renderContext.makeVertexData(2 * (res + 1));
		vertexData.addElement(colors, VertexData.Semantic.COLOR, 3);
		vertexData.addElement(positions, VertexData.Semantic.POSITION, 3);

		vertexData.addIndices(indices);

		Shape shape = new Shape(vertexData);

		return shape;
	}

	private static Shape initTorus(float RMajor, float RMinor, int res) {

		float[] positions = calculateTorusPositions(RMajor, RMinor, res);
		float[] colors = calculateTorusColors(res);
		int[] indices = calculateTorusIndices(res);

		VertexData vertexData = renderContext.makeVertexData(res * res);
		vertexData.addElement(colors, VertexData.Semantic.COLOR, 3);
		vertexData.addElement(positions, VertexData.Semantic.POSITION, 3);

		vertexData.addIndices(indices);

		Shape shape = new Shape(vertexData);

		return shape;
	}

	private static int[] calculateTorusIndices(int res) {
		int[] indices = new int[6 * res * res];

		for (int j = 0; j < res; j++) {
			for (int i = 0; i < 6 * res; i += 6) {

				indices[i + j * 6 * res + 0] = (i / 6 + j * res) % (res * res);
				indices[i + j * 6 * res + 1] = (((i / 6 + 1) % res) + j * res) % (res * res);
				indices[i + j * 6 * res + 2] = (((i / 6 + 1) % res) + res + j * res) % (res * res);

				indices[i + j * 6 * res + 3] = (i / 6 + j * res) % (res * res);
				indices[i + j * 6 * res + 4] = (((i / 6 + 1) % res) + res + j * res) % (res * res);
				indices[i + j * 6 * res + 5] = ((i / 6) % res + res + j * res) % (res * res);
			}
		}

		return indices;
	}

	private static float[] calculateTorusColors(int res) {
		float[] colors = new float[3 * res * res];

		/* for (int i = 0; i < 3 * res * res; i += 6) {
		 * colors[i + 0] = 1;
		 * colors[i + 1] = 1;
		 * colors[i + 2] = 1;
		 * 
		 * colors[i + 3] = 0;
		 * colors[i + 4] = 0;
		 * colors[i + 5] = 0;
		 * } */

		for (int i = 0; i < 3 * res * res; i += 3) {

			colors[i + 0] = (float) (Math.random());
			colors[i + 1] = (float) (Math.random());
			colors[i + 2] = (float) (Math.random());
		}

		return colors;
	}

	private static float[] calculateTorusPositions(float RMajor, float RMinor, int res) {
		float[] positions = new float[3 * res * res];

		for (int j = 0; j < res; j++) {

			for (int i = 0; i < 3 * res; i += 3) {
				float p = (float) (2 * i / 3 * Math.PI / res);
				float t = (float) (2 * j * Math.PI / res);

				positions[i + j * 3 * res + 0] = (float) ((RMajor + RMinor * Math.cos(p)) * Math.cos(t)); // x
				positions[i + j * 3 * res + 1] = (float) (RMinor * Math.sin(p)); // y
				positions[i + j * 3 * res + 2] = (float) ((RMajor + RMinor * Math.cos(p)) * Math.sin(t)); // z
			}
		}

		return positions;
	}

	private static Shape[] buildTruck() {
		Shape[] shapes = initTruck();

		Matrix4f matrix = new Matrix4f(shapes[1].getTransformation());				// front cube
		matrix.setScale((float) 2 / 3);
		matrix.setTranslation(new Vector3f((float) 5 / 3, (float) -0.38, 0));
		shapes[1].setTransformation(matrix);

		matrix = new Matrix4f(shapes[2].getTransformation());							// back right wheel
		matrix.rotX((float) (Math.PI / 2));
		matrix.setScale((float) 1 / 3);
		matrix.setTranslation(new Vector3f((float) -0.5 / 3, -1, (float) 1.5));
		shapes[2].setTransformation(matrix);

		matrix = new Matrix4f(shapes[3].getTransformation());							// front right wheel
		matrix.rotX((float) (Math.PI / 2));
		matrix.setScale((float) 1 / 3);
		matrix.setTranslation(new Vector3f((float) 1.5, -1, (float) 1.5));
		shapes[3].setTransformation(matrix);

		matrix = new Matrix4f(shapes[4].getTransformation());							// back left wheel
		matrix.rotX((float) (Math.PI / 2));
		matrix.setScale((float) 1 / 3);
		matrix.setTranslation(new Vector3f((float) -0.5 / 3, -1, (float) -1.5));
		shapes[4].setTransformation(matrix);

		matrix = new Matrix4f(shapes[5].getTransformation());							// front left wheel
		matrix.rotX((float) (Math.PI / 2));
		matrix.setScale((float) 1 / 3);
		matrix.setTranslation(new Vector3f((float) 1.5, -1, (float) -1.5));
		shapes[5].setTransformation(matrix);

		return shapes;
	}

	private static Shape[] initTruck() {
		Shape[] shapes = new Shape[6];

		shapes[0] = initCube();
		shapes[1] = initCube();
		shapes[2] = initTorus(1, (float) 0.3, 25);
		shapes[3] = initTorus(1, (float) 0.3, 25);
		shapes[4] = initTorus(1, (float) 0.3, 25);
		shapes[5] = initTorus(1, (float) 0.3, 25);

		return shapes;
	}

}
