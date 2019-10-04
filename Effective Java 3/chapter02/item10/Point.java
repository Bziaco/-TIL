package effectivejava3.chapter03.item10;

import java.util.Objects;
import java.util.Set;

public class Point {
	private int x;
	private int y;

	private static final Set<Point> unitCircle = Set.of(new Point(1, 0), new Point(0, 1), new Point(-1, 0),
			new Point(0, -1));

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean equals(Object o) {
		// if( !(o instanceof Point) )
		// return false;
		if (o == null || o.getClass() != getClass())
			return false;
		Point p = (Point) o;
		return p.x == x && p.y == y;
	}

	public static boolean onUnitCircle(Point p) {
		return unitCircle.contains(p);
	}

}
