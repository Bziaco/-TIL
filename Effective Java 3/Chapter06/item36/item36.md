# 비트 필드 대신 EnumSet을 사용하라

## 비트 필드 열거 상수 - 구닥다리 기법!

```js
public class Text {
    public class Text {
    public static final int STYLE_BOLD =          1 << 0; //1
    public static final int STYLE_ITALIC =        1 << 1; //2
    public static final int STYLE_UNDERLINE =     1 << 2; //4
    public static final int STYLE_STRIKETHROUGH = 1 << 3; //8
    
    //매개 변수 styles는 0개 이상의 STYLE_ 상수를 비트별 OR 한 값이다.
    public void applyStyle(int styles) {...}
}
}
```

위와 같은 식으로 비트별 OR을 사용해 여러 상수를 하나의 집합으로 모을수 있으며 이렇게 만들어진 집합을 비트 필드(bit field)라 한다.

비트 필드를 사용하면 비트별 연산을 사용해 여러 연산을 효율적으로 수행할 수 있으나 결국 비트 필드도 정수 열거 상수의 단점을 그대로 지니며 추가로 다음과 같은 문제까지 않고 있다. 필트 필드 값이 그대로 출력되면 단순한 정수 열거 상수를 출력할 때보다 해석하기가 훨씬 어렵다. 모든 원소를 순회하기로 어렵고 최대 몇 비트가 필요한지 알기도 어렵다. 

하지만 이제 EnumSet이란 더 아은 대안이 있다. 

<br>

## EnumSet

java.util 패키지의 EnumSet 클래스는 열거 타입 상수의 값으로 구성된 집합을 효과적으로 표현해준다. Set 인터페이스를 완벽히 구현하며 타입 안전하고 다른 어떤 Set 구현체와도 함께 사용할 수 있다. 하지만 EnumSet의 내부는 비트 벡터로 구현되어 있다. 원소가 총 64개 이하라면 즉 대부분의 경우에 EnumSet 전체를 long 변수 하나로 표현하여 비트 필드에 비견되는 성능을 보여준다.

`EnumSet - 비트 필드를 대체하는 현대적 기법`

```js
public class Text {
    public enum Style { BOLD, ITALIC, UNDERLINE, STRIKETHROUGH }

    //어떤 Set을 넘겨도 되나, EnumSet이 가장 좋다.
    public void applyStyles(Set<Style> styles) {...}
}
```

다음 코드는 applyStyles 메서드에 EnumSet 인스턴스를 건네는 클라리언트 코드다.

```js
text.applyStyles(EnumSet.of(Style.BOLD, Style.ITALIC));
```

applyStyles가 EnumSet 대신 Set을 받은 이유는 이왕이면 인터페이스로 받는게 일반적으로 좋은 습관이다. 이렇게 하면 특이한 클라이언트가 다른 Set 구현체를 넘기더라도 처리할 수 있기 때문이다.

<br>

## Conclusion

열거할 수 있는 타입을 한데 모아 집합 형태로 사용한다고 해도 비트 필드를 사용할 이유는 없다. EnumSet 클래스가 비트 필드 수준의 명료함과 성능을 제공하고 열거 타입의 장점까지 선사하기 때문이다. EnumSet의 유일한 단점이라면 자바11까지 불변 EnumSet을 만들수 없다는 것이다.