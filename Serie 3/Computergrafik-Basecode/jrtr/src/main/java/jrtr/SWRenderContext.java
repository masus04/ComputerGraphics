package jrtr;

import jrtr.RenderContext;

import java.awt.Color;
import java.awt.image.*;
import java.util.ArrayList;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4f;

/**
 * A skeleton for a software renderer. It works in combination with {@link SWRenderPanel}, which
 * displays the output image. In project 3
 * you will implement your own rasterizer in this class.
 * <p>
 * To use the software renderer, you will simply replace {@link GLRenderPanel} with
 * {@link SWRenderPanel} in the user application.
 */
public class SWRenderContext implements RenderContext {

	private SceneManagerInterface sceneManager;
	private BufferedImage colorBuffer;
	private float[][] zBuffer;
	private ArrayList<Triangle> triangles;

	private static Matrix4f c, p, m, d, transformation;

	public void setSceneManager(SceneManagerInterface sceneManager) {
		this.sceneManager = sceneManager;
	}

	/**
	 * This is called by the SWRenderPanel to render the scene to the
	 * software frame buffer.
	 */
	public void display() {
		if (sceneManager == null)
			return;

		beginFrame();

		SceneManagerIterator iterator = sceneManager.iterator();
		while (iterator.hasNext()) {
			draw(iterator.next());
		}

		endFrame();
	}

	/**
	 * This is called by the {@link SWJPanel} to obtain the color buffer that
	 * will be displayed.
	 */
	public BufferedImage getColorBuffer() {
		return colorBuffer;
	}

	/**
	 * Set a new viewport size. The render context will also need to store
	 * a viewport matrix, which you need to reset here.
	 */
	public void setViewportSize(int width, int height) {
		colorBuffer = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		zBuffer = new float[colorBuffer.getWidth()][colorBuffer.getHeight()];

		for (int i = 0; i < colorBuffer.getWidth(); i++)
			for (int j = 0; j < colorBuffer.getHeight(); j++)
				zBuffer[i][j] = Float.MIN_VALUE;
	}

	/**
	 * Clear the framebuffer here.
	 */
	private void beginFrame() {
	}

	private void endFrame() {
	}

	/**
	 * The main rendering method. You will need to implement this to draw
	 * 3D objects.
	 */
	private void draw(RenderItem renderItem) {
		extractTriangles(renderItem);
		initTransformationMatrix(renderItem);

		int task = 0;

		if (task == 0) {						// put all vertices in triangles for transformation
			drawVertices(renderItem);
		} else if (task == 1)					// put vertices into triangles according to indices
			drawTriangleVertices(triangles);
		else if (task == 2)
			drawTriangles(triangles);

	}

	/**
	 * draws a pixel p if it is in front of the pixel painted there before.
	 * 
	 */
	private void drawPixel(Vector4f p) {
		if (zBuffer[(int) p.x][(int) p.y] < 1 / p.w) {
			colorBuffer.setRGB((int) (p.x / p.w), (int) (p.y / p.w), Color.WHITE.getRGB());
			zBuffer[(int) p.x][(int) p.y] = 1 / p.w;
		}
	}

	private void drawVertices(RenderItem renderItem) {

		float[] vertices = renderItem.getShape().getVertexData().getPositions();
		ArrayList<Triangle> triangles = new ArrayList<Triangle>();

		for (int i = 0; i < vertices.length - 8; i += 9) {
			Vector4f p1 = new Vector4f(new Vector4f(vertices[i + 0], vertices[i + 1], vertices[i + 2], 1));
			Vector4f p2 = new Vector4f(new Vector4f(vertices[i + 3], vertices[i + 4], vertices[i + 5], 1));
			Vector4f p3 = new Vector4f(new Vector4f(vertices[i + 6], vertices[i + 7], vertices[i + 8], 1));

			triangles.add(new Triangle(p1, p2, p3));
		}

		drawTriangleVertices(triangles);

	}

	private void drawTriangleVertices(ArrayList<Triangle> triangles) {
		Vector4f p;

		for (Triangle t : triangles) {

			t.transform(transformation);

			p = new Vector4f(t.getP1());
			if (inBounds(p, p.w))
				drawPixel(p);

			p = new Vector4f(t.getP2());
			if (inBounds(p, p.w))
				drawPixel(p);

			p = new Vector4f(t.getP3());
			if (inBounds(p, p.w))
				drawPixel(p);
		}
	}

	private void drawTriangles(ArrayList<Triangle> triangles) {
		float[] boundingBox;
		Vector4f pixel;

		for (Triangle t : triangles) {
			if (t.isVisible()) {

				boundingBox = t.getBoundingBox();

				for (int i = (int) boundingBox[0]; i < (int) boundingBox[1]; i++)
					for (int j = (int) boundingBox[2]; j < (int) boundingBox[3]; j++) {
						// loop over all pixels inside the bounding box
						pixel = new Vector4f(i, j, 0, 1);
						if (t.isdrawn(pixel))
							colorBuffer.setRGB((int) pixel.x, (int) pixel.y, t.colorAt(pixel).getRGB());
					}
			}
		}
	}

	/**
	 * calculates the transformation Matrix4f
	 * 
	 * @param renderItem
	 */
	private void initTransformationMatrix(RenderItem renderItem) {
		m = renderItem.getShape().getTransformation();
		c = new Matrix4f(sceneManager.getCamera().getCameraMatrix());
		p = sceneManager.getFrustum().getProjectionMatrix();
		d = calculateDMatrix();

		Matrix4f matrix = new Matrix4f(m);
		matrix.mul(c, m);
		matrix.mul(p, matrix);
		matrix.mul(d, matrix);

		transformation = matrix;
	}

	private Matrix4f calculateDMatrix() {
		Matrix4f matrix = new Matrix4f();

		matrix.setRow(0, new Vector4f(colorBuffer.getWidth() / 2, 0, 0, colorBuffer.getWidth() / 2));
		matrix.setRow(1, new Vector4f(0, -colorBuffer.getHeight() / 2, 0, colorBuffer.getHeight() / 2));
		matrix.setRow(2, new Vector4f(0, 0, 0.5f, 0.5f));
		matrix.setRow(3, new Vector4f(0, 0, 0, 1));

		return matrix;
	}

	private ArrayList<Triangle> extractTriangles(RenderItem renderItem) {
		Triangle triangle;
		triangles = new ArrayList<Triangle>();

		float[] positions = renderItem.getShape().getVertexData().getPositions();
		int[] indices = renderItem.getShape().getVertexData().getIndices();
		float[] colors = renderItem.getShape().getVertexData().getColors();
		//float[] normals = renderItem.getShape().getVertexData().getNormals();

		Vector4f p1, p2, p3;
		Vector3d c1, c2, c3;
		for (int i = 0; i < indices.length; i += 3) {
			p1 = new Vector4f(positions[indices[i + 0] * 3 + 0], positions[indices[i + 0] * 3 + 1],
					positions[indices[i + 0] * 3 + 2], 1);
			c1 = new Vector3d(colors[indices[i + 0] * 3 + 0], colors[indices[i + 0] * 3 + 1], colors[indices[i + 0] * 3 + 2]);

			p2 = new Vector4f(positions[indices[i + 1] * 3 + 0], positions[indices[i + 1] * 3 + 1],
					positions[indices[i + 1] * 3 + 2], 1);
			c2 = new Vector3d(colors[indices[i + 1] * 3 + 0], colors[indices[i + 1] * 3 + 1], colors[indices[i + 1] * 3 + 2]);

			p3 = new Vector4f(positions[indices[i + 2] * 3 + 0], positions[indices[i + 2] * 3 + 1],
					positions[indices[i + 2] * 3 + 2], 1);
			c3 = new Vector3d(colors[indices[i + 2] * 3 + 0], colors[indices[i + 2] * 3 + 1], colors[indices[i + 2] * 3 + 2]);

			triangle = new Triangle(p1, p2, p3, c1, c2, c3);
			//triangle.calculateEdgeFunction();

			triangles.add(triangle);
		}

		return triangles;
	}

	/**
	 * Does nothing. We will not implement shaders for the software renderer.
	 */
	public Shader makeShader() {
		return new SWShader();
	}

	/**
	 * Does nothing. We will not implement shaders for the software renderer.
	 */
	public void useShader(Shader s) {
	}

	/**
	 * Does nothing. We will not implement shaders for the software renderer.
	 */
	public void useDefaultShader() {
	}

	/**
	 * Does nothing. We will not implement textures for the software renderer.
	 */
	public Texture makeTexture() {
		return new SWTexture();
	}

	public VertexData makeVertexData(int n) {
		return new SWVertexData(n);
	}

	private boolean inBounds(Vector4f p, float w) {
		int width = colorBuffer.getWidth();
		int height = colorBuffer.getHeight();

		return (p.x / w) >= 0 && (p.x / w) < width && (p.y / w) < height && (p.y / w) >= 0;
	}
}
