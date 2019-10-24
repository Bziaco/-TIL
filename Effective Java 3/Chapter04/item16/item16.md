# public 클래스에서는 public 필드가 아닌 접근자 메서드를 사용하라

## 1. 이처럼 퇴보한 클래스는 public이어서는 안된다.

```js
class Point {
    public double x;
    public double y;
}
```

- 위 Point클래스는 **데이터 필드에 직접 접근 할 수 있기 때문에 캡슐화의 이점을 제공하지 못한다.**

- API를 수정하지 않고는 내부 표현을 바꿀 수 없고 불변식을 보장할 수 없으며 외부에서 필드에 접근할 때 부수작업ㅇ르 수행할 수도 없다.

<br>

## 2. 접근자와 변경자(mutator) 메서드를 활용해 데이터를 캡슐화 한다.

```js
Class Point {
    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {return x;}
    public double getY() {return y;}

    public void setX(double x) {this.x = x;}
    public void setY(double y) {this.y = y;}
}
```

- public 클래스에서라면 이 방식이 맞다. **패키지 바깥에서 접근할 수 잇는 클래스라면 접근자를 제공**함으로써 클래스 내부 표현 방식을 언제든 바꿀수 있는 유연성을 얻을 수 있다.

<br>

## 3. package-private 클래스 혹은 private 중첩 클래스라면 데이터 필드를 노출해도 된다.

- 그 클래스가 표현하려는 추상 개념만 올바르게 표현해주면 된다.

- 이 방식은 클래스 선언 면에서나 이를 사용하는 클라이언트 코드 면에서나 접근자 방식보다 훨씬 깔끔하다.

- 클라이언트 코드가 내부에 묶이기는 하나 클라이언트도 결국 이 클래스를 포함하는 패키지 안에서만 동작하는 코드일 뿐이다.

- 따라서 패키지 바깥 코드는 전혀 손대지 않고도 데이터 표현 방식을 바꿀 수 있다.

- private 중첩 클래스의 경우라면 수정 범위가 더 좁아져서 이 클래스를 포함하는 외부 클래스까지로 제한 된다.

<br>

## 4. java.awt.package의 Point와 Dimension 클래스는 public 클래스 필드를 직접 노출을 했다!

- 이는 public 클래스 필드를 직접 노출하지 말라는 규칙을 어긴 사례이다.

- 내부를 노출한 Dimension 클래스의 심각한 성능 문제는 오늘날까지도 해결되지 못했다.

<br>

## 5. final 필드는 public으로 선언해도 되는가?

- **좋은 생각은 아니다.**

- API를 변경하지 않고는 표현 방식을 바꿀 수 없고 필드를 읽을 때 부수 작업을 수행할 수 없다는 단점은 여전하다.

- 하지만 불변식은 보장할 수 있게된다.

## 6. Conclusion

- public 클래스는 절대 가변 필드를 직접 노출해서는 안 된다.

- 불변 필드라면 노출해도 덜 위험하지만 완전히 안심할 수는 없다.

- 하지만 package-private 클래스나 private 중첩 클래스에서는 종종(불변이든 가변이든) 필드를 노출하는 편이 나을 때도 있다.