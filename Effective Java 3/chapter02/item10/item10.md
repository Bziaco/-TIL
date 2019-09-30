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
