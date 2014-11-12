package simple;

import java.util.ArrayList;
import java.util.Arrays;

import javax.vecmath.Vector3f;

import jrtr.RenderContext;
import jrtr.VertexData;

public class Cylinder {
	
	double h;
	double r;
	int res;
	RenderContext renderContext;
	float xm,zm,ym;
	ArrayList<Float> v = new ArrayList<Float>();
	ArrayList<Float> v2 = new ArrayList<Float>();
	ArrayList<Float> c = new ArrayList<Float>();
	ArrayList<Float> c2 = new ArrayList<Float>();
	ArrayList<Float> n = new ArrayList<Float>();
	ArrayList<Float> tex = new ArrayList<Float>();
	
	public Cylinder(double h, double r, int res, RenderContext renderContext, float xm, float zm, float ym) {
		this.h = h;
		this.r = r;
		this.res = res;
		this.renderContext = renderContext;
		this.xm = xm;
		this.ym = ym;
		this.zm = zm;
	}
	
	public VertexData makeCylinder() {		
		
		for (int j = 1; j >= -1; j=j-2) {
			float xm = 0;
			float ym = 0;
			float zm = (float) (j * h/2);
			float alpha = (float) Math.toRadians((double)360/res);


			v.add(xm);
			v.add(ym);
			v.add(zm);

			c.add((float) (Math.random()));
			c.add((float) (Math.random()));
			c.add((float) (Math.random()));

			float x = 0;
			float y =(float) r;
			float z = zm;

			for (int i = 1; i <= res; i++) {
				v.add(x); v2.add(x);
				v.add(y); v2.add(y);
				v.add(z); v2.add(z);
				
				c.add((float) (Math.random())); c2.add((float) (Math.random()));
				c.add((float) (Math.random())); c2.add((float) (Math.random()));
				c.add((float) (Math.random())); c2.add((float) (Math.random()));

				float tempx = x;
				float tempy = y;

				x = (float) (tempx * Math.cos(alpha) - tempy * Math.sin(alpha));
				y = (float) (tempx * Math.sin(alpha) + tempy * Math.cos(alpha));
			}
		}
		
		v.addAll(v2);
		c.addAll(c2);
		
		float[] cr = getFloats(c);
		float[] vr = getFloatsVectors(v);
		int[] indicesr = calcIndices();
		float[] tex = calculateTexCoords();
		System.out.println("" + Arrays.toString(indicesr));
		calcNormals();
		float[] nr = getFloats(n);
		
		System.out.println("" + Arrays.toString(nr));
		System.out.println(nr.length);
		
		
		
		VertexData vertexData = renderContext.makeVertexData(2*(res + 1) + 2*res);
		vertexData.addElement(cr, VertexData.Semantic.COLOR, 3);
		vertexData.addElement(vr, VertexData.Semantic.POSITION, 3);
		vertexData.addElement(nr, VertexData.Semantic.NORMAL, 3);
		vertexData.addElement(tex, VertexData.Semantic.TEXCOORD, 2);
		

		vertexData.addIndices(indicesr);
		
		return vertexData;
	}
	
	public int[] calcIndices() {
		ArrayList<Integer> indices = new ArrayList<Integer>();

		for (int j = 1; j >= -1; j=j-2) {
			for (int i = 2; i <= res; i++) {
				if (j == 1) {
					indices.add(0);
					indices.add(i-1 + res*2 + 1);
					indices.add(i + res*2 + 1);
				} else {
					indices.add(res + 1);
					indices.add(res + i + res*2);
					indices.add(res + i + 1 + res*2);
				}				
			}
			if (j == 1) {
				indices.add(0);
				indices.add(res + res*2 + 1);
				indices.add(1 + res*2 + 1);
			} else {
				indices.add(res + 1);
				indices.add(2 * res + 1 + res*2);
				indices.add(res + 2 + res*2);
			}				
		}
		
		// mantel
		for (int i=1; i <= res; i++) {
			if (i == res) {
				indices.add(i);
				indices.add(1);
				indices.add(res + i + 1);
			} else {
				indices.add(i);
				indices.add(i + 1);
				indices.add(res + i + 1);
			}			
		}
		
		for (int i=res+2; i <= 2*res + 1; i++) {
			if (i == 2*res + 1) {
				indices.add(i);
				indices.add(res+2);
				indices.add(1);
			} else {
				indices.add(i);
				indices.add(i + 1);
				indices.add(i - res);
			}
		}
		return getInteger(indices);
	}
	
	public float[] getFloats(ArrayList<Float> values) {
	    int length = values.size();
	    float[] result = new float[length];
	    for (int i = 0; i < length; i++) {
	      result[i] = values.get(i).floatValue();
	      //System.out.println(values.get(i).floatValue());
	    }
	    
	    return result;
	}
	
	public float[] getFloatsVectors(ArrayList<Float> values) {
	    int length = values.size();
	    float[] result = new float[length];
	    for (int i = 0; i < length; i++) {
	    	if (i % 3 == 0) { 
	    		result[i] = values.get(i).floatValue() + xm;
	    	} else if (i % 3 == 1) {
	    		result[i] = values.get(i).floatValue() + ym;
	    	} else if (i % 3 == 2) {
	    		result[i] = values.get(i).floatValue() + zm;
	    	}
	      /*System.out.println(values.get(i).floatValue());*/
	    }
	    
	    return result;
	}
	
	public int[] getInteger(ArrayList<Integer> values) {
	    int length = values.size();
	    int[] result = new int[length];
	    for (int i = 0; i < length; i++) {
	      result[i] = values.get(i).intValue();
	      //System.out.println(values.get(i).intValue());
	    }
	    return result;
	}
	
	private float[] calculateTexCoords() {
	 
		 tex.add(1/2f);
		 tex.add(1/2f);
		 for(int i = 0; i<res; i++) {
			 tex.add(i/(float)(res-1));
			 tex.add(0f);
		 }
		 tex.add(1/2f);
		 tex.add(1/2f);
		 for(int i = 0; i<res; i++) {
			 tex.add(i/(float)(res-1));
			 tex.add(1f);
		 }
		 for(int i = 0; i<res; i++) {
			 tex.add(i/(float)(res-1));
			 tex.add(0f);
		 }
		 for(int i = 0; i<res; i++) {
			 tex.add(i/(float)(res-1));
			 tex.add(0f);
		 }
		 return getFloats(tex);
	}
	
	private void calcNormals() {
		ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
		for (int i = 0; i < v.size(); i=i+3) {
			vertices.add(new Vector3f(v.get(i), v.get(i+1), v.get(i+2)));
		}
		Vector3f a = new Vector3f(vertices.get(0));
		Vector3f b = new Vector3f(vertices.get(1));
		Vector3f c = new Vector3f(vertices.get(2));
		
		Vector3f v1 = new Vector3f(b.x-a.x, b.y-a.y, b.z-a.z);
		Vector3f v2 = new Vector3f(c.x-a.x, c.y-a.y, c.z-a.z);
		
		Vector3f normup = new Vector3f();
		
		normup.cross(v1, v2);
		normup.normalize();
		n.add(normup.x);
		n.add(normup.y);
		n.add(normup.z);
		
		for (int i = 1; i <= res; i++) {
			a = new Vector3f(vertices.get(0));
			b = new Vector3f(vertices.get(i));			
			v2 = new Vector3f(b.x-a.x, b.y-a.y, b.z-a.z);
			v2.normalize();
			
			n.add(v2.x);
			n.add(v2.y);
			n.add(v2.z);			
		}
		
		
		a = new Vector3f(vertices.get(res+1));
		b = new Vector3f(vertices.get(res+2));
		c = new Vector3f(vertices.get(res+3));
		
		v2 = new Vector3f(b.x-a.x, b.y-a.y, b.z-a.z);
		v1 = new Vector3f(c.x-a.x, c.y-a.y, c.z-a.z);
		
		Vector3f normdown = new Vector3f();
		
		normdown.cross(v1, v2);
		normdown.normalize();
		n.add(normdown.x);
		n.add(normdown.y);
		n.add(normdown.z);
		
		for (int i = res+2; i <= 2 * res + 1; i++) {
			a = new Vector3f(vertices.get(res+1));
			b = new Vector3f(vertices.get(i));
			v2 = new Vector3f(b.x-a.x, b.y-a.y, b.z-a.z);
			v2.normalize();			

			n.add(v2.x);
			n.add(v2.y);
			n.add(v2.z);
		}
		
		for(int i = 0; i<res; i++) {
			n.add(normup.x);
			n.add(normup.y);
			n.add(normup.z);
		}
		for(int i = 0; i<res; i++) {
			n.add(normdown.x);
			n.add(normdown.y);
			n.add(normdown.z);
		}
		
	}
}
