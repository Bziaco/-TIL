<h1>equals는 일반 규약을 지켜 재정의 하라</h1>

<h2>equals를 재정의 하지 않아도 되는 경우</h2>

<h3>1. 각 인스턴스가 본질적으로 고유하다.</h3>

<h3>2. 인스턴스의 '논리적 통치성'을 검사할 일이 없다.</h3>

<h3>3. 상위 클래스에서 재정의한 equals가 하위 클래스에도 딱 들어 맞는다.</h3>

<h3>4. 클래스가 private이거나 package-private이고 equals 메서드를 호출할 일이 없다.</h3>

- 실수로라도 equals가 호출되는 것을 막고자 한다면 아래와 같이 정의하라.
  ```
  @Override
  public boolean equals(Object o) {
      throw new AssertionError(); //호출 금지
  }
  ```

</br>

<h2>equals 메서드 재정의 일반 규약</h2>

- reflexivity(반사성) : null이 아닌 모든 참조값 x에 대해 x.equals(x)는 true다.

- symmetry(대칭성) : null이 아닌 모든 참조값 x,y에 대해 x.equals(y)가 true면 y.equals(x)도 true다.

- transitivity(추이성) : null이 아닌 모든 참조 값 x,y,z에 대해 x.equals(y)가 true이고 y.equals(z)도 true면 a.equals(z)도 true다.

- consistency(일관성) : null이 아닌 모든 참조값 x,y에 대해 x.equals(y)를 반복해서 호출하면 항상 true를 반환하거나 항상 false를 반환한다.

- null-아님 : null이 아닌 모든 참조값 x에 대해 x.equals(null)은 false다.

</br>

<h3>symmetry(대칭성)</h3>

```
public class CaseInsensitiveString {
    private final String s;

    public CaseInsensitiveString(String s) {
        this.s = Objects.requireNonNull(s);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CaseInsensitiveString) {
            return s.equalsIgnoreCase(((CaseInsensitiveString) o).s);
        }
        if (o instanceof String) {
            return e.equalsIgnoreCase((String) o);
        }
        return false;
    }
}
```

```
public class CaseInsensitiveStringTest {

    public static void main(String[] args) {
        CaseInsensitiveString cis = new CaseInsensitiveString("Polish");
        String text = "Polish";
        System.out.println(cis.equals(text)); //true
    }

}
```

- 위 코드는 true를 반납하지만 text.equals(cis)는 false를 반납한다. String에서 위 클래스를 정의 해줬을 리가 없기 때문이다.

```
List<CaseInsensitiveString> list = new ArrayList<>();
list.add(cis);
```

- list.contains(s)를 호출하면 현재 JDK에서는 true를 반납하지만 다른 JDK버전에서는 false를 반환하기도 한다. 일관된 결과를 반환히지 않은 일은 프로그램에 있어 상당히 치명적이다.

- 이 문제를 해결하기 위해서는 아래와 같이 equals 메서드의 파라미터가 CaseInsensitiveString와 같은 객체인지 검사하고 반사성을 검사한다.

```
@Override
public boolean equals(Object o) {
    return 0 instanceof CaseInsenitiveString && ((CaseInsenitiveString) o).s.equalsIgnoreCase(s);
}
```

<br>

<h3>transitivity(추이성)</h3>

```
public class Point {
    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point)) {
            return false;
        }
        Point p = (Point)o;
        return p.x == x && p.y == y;
    }
}
```

```
public class ColorPoint extends Point {
    private final Color color;

    public ColorPoint(int x, int y, Color color) {
        super(x, y);
        this.color = color;
    }
}
```

- 상위 클래스에는 없는 새로운 필드를 하위 클래스에 추가하는 상황이다. 현재 equals 메서드는 Point의 구현이 상속되어 하위 color에 대한 색상정보는 무시된채 비교를 수행한다. equals 규약을 어긴 것은 아니지만 중요한 정보를 놓치게 되니 좋지 않은 상황이다.

- 아래 코드처럼 비교 대상이 또 다른 ColorPoint이고 위치와 섹상이 같을 때만 true를 반환하는 equals를 생각해보자

```
@Override
public boolean equals(Object o) {
    if(!(o instanceof ColorPoint)) {
        return false;
    }
    return super.equals(o) && ((ColorPoint) o).color = color;
}
```

- 위 메서드는 일반 Point를 ColorPoint에 비교한 결과가 그 둘을 바꿔 비교한 결과가 다를 수 있다.
- Point의 equals는 색상을 무시하고 ColorPoint의 equals는 입력 매개변수의 클래스 종류가 다르다며 매번 false만 반환할 것이다.
- 각각의 인스턴스를 하나씩 만들어 보자

```
Point p = new Point(1,2);
ColorPoint cp = new ColorPoint(1,2,Color.RED);
```

- p.equals(cp)는 true를 cp.equals(p)는 false를 반환한다.
- 그렇다면 ColorPoint.equals가 Point와 비교할 때는 색상을 무시하도록 하면 될까?

```
@Override
public boolean equals(Object o) {
    if(!(o instanceof Point)) {
        return false;
    }
    if(!(o instanceof ColorPoint)) {
        return o.equals(this);
    }
    return super.equals(o) && ((ColorPoint) o).color = color;
}
```

- 이 방식은 대칭성은 지켜주지만 추이성을 깨버린다.

```
ColorPoint p1 = new ColorPoint(1,2,Color.RED);
Point p2 = new Point(1,2);
ColorPoint p2 = new ColorPoint(1,2,Color.BLUE);
```

- p1과 p2, p2와 p3는 true지만 p1과 p3는 false를 리턴한다. 이는 색상까지 고려했기 때문이다.
- 또한 이 방식은 재귀에 빠져 StackOverflowError를 일으킬 수 있다.
- 구체 클래스를 확장해 새로운 값을 추가하면서 equals 규약을 만족시킬 방법은 존재하지 않는다. 그렇다면 해법은 무엇일까? equals 안의 instanceof 검사를 getClass 검사로 바꾸면 규약도 지키고 값도 추가하면서 구체 클래스를 상속할 수 있는 방법이 있을 수 있다고 생각이 든다.

```
@Override
public boolean equals(Object o) {
    if (o == null || o.getClass != getClass()) {
        return false;
    }
    Point p = (Point) o;
    return p.x == x && p.y == y;
}
```

- 이번 equals는 같은 구현 클래스의 객체와 비교할때만 true를 반환한다. 그러나 여전히 추이성에 어긋난다.
- Point의 하위 클래스는 정의상 여전히 Point이므로 어디서든 Point로써 활용될 수 있어야 한다. 그러나 이 방식에서는 그렇지 못하다.
- 예를 들어 주어진 점이 (반지름이 1인) 단위 원 안에 있는지를 판별하는 메서드가 필요하다고 가정해 보자. 다음은 이를 구현한 코드이다.

```
private static final Set<Point> unitCircle = Set.of(
    new Point(1,0), new Point(0,1),
    new Point(-1,0), new Point(0,-1));

public static boolean onUnitCircle(Point p) {
    return unitCircle.contatin(p);
}
```

- 이 기능을 구현하는 가장 빠른 방법은 아니지만 이제 값을 추가하지 않는 방식으로 Point를 확장하겠다. 만들어진 인스턴스의 개수를 생성자에서 세보도록 하자.

```
public class CounterPoint extends Point {
    private static final AtomicInteger counter = new AtomicInteger();

    public CounterPoint(int x, int y) {
        super(x,y);
        counter.incrementAndGet();
    }
    public static int numberCreated() { return counter.get(); }
}
```

- 리스코프 치환 원칙(Liskov substitution princicple)에 따르면 어떤 타입에 있어 중요한 속성이라면 그 하위 타입에서도 마찬가지로 중요하다. 따라서 그 타입의 모든 메서드가 하위 타입에서도 똑같이 잘 작동해야 한다.

- 그러나 CounterPoint의 인스턴스를 onUnitCircle 메서드에 넘기면 false를 반환할 것이다. Point 클래스의 equals를 getClass를 사용해 작성했기 때문이다.

- 이유는 컬렉션 구현체에서 주어진 원소를 담고 있는지를 확인하는 방법에 있다. onUnitCircle에서 사용한 Set을 포함하여 대부분의 컬렉션은 이 작업에 equals 메서드를 이용하는데 CounterPoint의 인스턴스는 어떤 Point와도 같을 수 없기 때문이다.

- 반면 instanceof 기반으로 구현했다면 제대로 작동할 것이다.

- 구체 클래스의 하위 클래스에서 값을 추가할 방법은 없지만 상속 대신 컴포지션을 사용하여 우회하는 방법이 있다.

- Point를 상속하는 대신 Point를 ColorPoint의 private 필드로 두고 ColorPoint와 같은 위치의 일반 Point를 반환하는 뷰(view) 메서드를 public으로 추가하는 방식이다.

```
package effectivejava3.chapter03.item10;

import java.util.Objects;

public class ColorPoint {
	private final Point point;
	private final Color color;

	public ColorPoint(int x, int y, Color color) {
		point = new Point(x,y);
		this.color = Objects.requireNonNull(color);
	}

	public Point asPoint() {
		return point;
	}

	@Override
	public boolean  equals(Object o) {
		if(!(o instanceof ColorPoint)) {
			return false;
		}

		ColorPoint cp = (ColorPoint) o;
		return cp.point.equals(point) && cp.color.equals(color);
	}
}
```

- 자바 라이브러리에 있는 java.sql.Timestamp는 java.util.Date를 확장한 후 nanoseconds필드를 추가 했다. 그 결과 Timestamp의 equals는 대칭성을 위배한다. 이와 같은 설계는 실수이니 절대 따라 해서는 안된다.

> 추상 클래스의 하위 클래스에서라면 equals 규약을 지키면서도 값을 추가 할 수 있다.

- 상위 클래스를 직접 인스턴스로 만드는게 불가능 하다면 지금까지 이야기한 문제들은 일어나지 않는다.

<br>

<h3>consistency(일관성)</h3>

- 두 객체가 같다면 (어느 하나 혹은 두 객체 모두가 수정되지 않는 한) 영원히 같아야 한다.

- equals가 한번 같다고 한 객체와는 영원히 같다고 답하고 다르다고 한 객체와는 영원히 다르다고 답하도록 만들어야 한다.

- java.net.URL의 equals는 주어진 URL과 매핑된 호스트의 아이피주소를 이용해 비교하는데 그 결과가 항상 같다고 보장할 수 없다. URL의 equals 구현은 실수이니 절대 따라해서는 안된다.

- 이러한 문제를 피하려면 equals는 항시 메모리에 존재하는 객체만을 사용한 결정적 계산만 수행해야 한다.

<br>

<h3>null-아님</h3>

- 모든 객체가 null과 같지 않아야 한다는 뜻이다.

- 아래는 null인지 확인 하는 코드 예제이다.

```
@Override
public boolean equals(Object o) {
    if( o == null)
        return false;
}
```

- 하지만 이러한 검사는 필요치 않다. 아래 코드처럼 형변환에 앞서 instanceof 연산자로 입력 매개변수가 올바른 타입인지 검사하는 쪽이 낫다

```
public boolean equals(Object o) {
    if( o instanceof MyType)
        return false;
    MyType mt = (MyType) o;
    ...
}
```

- equals가 타입을 확인하지 않으면 잘못된 타입이 인수로 주어졌을 때 ClassCastException을 던져 일반 규약을 위배하게 된다.

<br>

<h2>equals 메서드 구현방법 단계별 정의</h2>

<h3>1. == 연산자를 사용해 입력이 자기 자신의 참조인지 확인한다.</h3>

<h3>2. instanceof 연산자로 입력이 올바른 타입인지 확인한다.</h3>

- 가끔 euqlas가 정의된 클래스가 특정 인터페이스 인경우도 있다. 이런 인터페이스를 구현한 클래스라면 equals에서 해당 인터페이스를 사용해야한다. Set, List, Map, Map.Entry등의 컬렉션 인터페이스들이 여기해 해당한다.

<h3>3. 입력을 올바른 타입으로 형변환 한다.</h3>

<h3>4. 입력 객체와 자기 자신의 대응되는 핵심 필드들이 모두 일치하는지 하나씩 검사한다.</h3>

<br>

<h2>주의 사항</h2>

> float와 double을 제외한 기본 타입 필드는 == 연산자로 비교

- 참조타입 필드는 각각의 equals 메서드로 비교하며 float과 double 필드는 Float.NaN, -0.0f, 특수한 부동소수값 등을 다뤄야 하기 때문에 각각 Float.compare(float, float)와 Double.compare(double, double)로 비교한다.

- Float.equals와 Double.equals 메서드를 대신 사용가능하나 오토박싱을 수반할 수 있어 성능이 좋지 않다.

<br>

> null을 정상값으로 취급하는 참조 타입 필드가 존재하는 경우

- 이런 필드는 정적 메서드인 Objects.equals(Object, Object)로 비교해 NullPointerException 발생을 예방한다.

<br>

> 비교할 필드 순서에 따라 equals 성능을 좌우하기도 한다.

- 최상의 성능을 바란다면 다를 가능성이 더 크거나 비교하는 비용이 싼 필드를 먼저 비교한다.

<br>

> equals를 다 구현 했다면 대칭적, 추이성, 일관적인가를 자문해보자

- 반사성과 null-아님은 거의 없기에 대칭성, 추이성, 일관적인 것만 비교해도 된다.

<br>

> equals를 재정의할 땐 hashcode도 반드시 재정의 하자

<br>

> Object 외의 타입을 매개변수로 받은 equals 메서드는 선언하지 말자
