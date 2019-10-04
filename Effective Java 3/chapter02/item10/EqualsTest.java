package effectivejava3.chapter03.item10;

public class EqualsTest {

	public static void main(String[] args) {
		CounterPoint counterPoint = new CounterPoint(0, -1);

		boolean valueOf = Point.onUnitCircle(counterPoint);
		System.out.println(CounterPoint.numberCreated());

		System.out.println(valueOf);

	}

}
