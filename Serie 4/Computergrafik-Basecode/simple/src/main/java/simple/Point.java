package simple;

public class Point {
	int x;
	int y;

	public Point(int i, int j) {
		x = i;
		y = j;
	}

	/**
	 * 
	 * @return the X index between the point and the index
	 */
	public int betweenX(Point p) {
		return x + (x + p.x) / 2;
	}

	/**
	 * 
	 * @return the Y index between the point and the index
	 */
	public int betweenY(Point p) {
		return y + (y + p.y) / 2;
	}

	/**
	 * 
	 * @return true if the Point is in bounds in MapCoordinates
	 */
	public boolean inBounds(int res) {
		return (x >= 0 && x <= res && y >= 0 && y <= res);
	}

	/**
	 * 
	 * @return true if the Point is in bounds in ObjectCoordinates
	 */
	public boolean inShape(int res) {
		return (x >= -res / 2 && x <= res / 2 && y >= -res / 2 && y <= res / 2);
	}
}
