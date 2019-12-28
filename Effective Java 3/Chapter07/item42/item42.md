# 익명 클래스보다는 람다를 사용하라

## _익명 클래스의 인스턴스를 함수 객체로 사용하는 것은 낡은 기법이다._

```js
Collections.sort(words, new Comparator<String>() {
    public int compare(String s1, String s2) {
        return Integer.compare(s1.length(), s2.length());
    })
});
```

위 코드에서는 Comparator 인터페이스가 정렬을 담당하는 추상 전략을 뜻하며 문자열을 정렬하는 구체적인 전략을 익명 클래스로 구현했다. 하지만 익명 클래스 방식은 코드가 너무 길기 때문에 자바는 함수형 프로그래밍에 적합하지 않았다.

<br>

## _람다_

람다는 함수나 익명 클래스와 개념은 비슷하지만 코드는 훨씬 간결하다. 다음은 익명 클래스를 사용한 앞의 코드를 람다 방식으로 바꾼 모습이다.

`람다식을 함수 객체로 사용 - 익명 클래스 대체`

```js
Collections.sort(words, (s1, s2) -> Integer.compare(s1.length(), s2.length()));
```

여기서 람다, 매개변수(s1,s2), 반환값의 타입은 각각 (Comoarator<String>, String, int)지만 코드에서는 언급이 없다. 우리 대신 컴파일러가 문맥을 살펴 타입을 추론해준 것이다. 상황에 따라 컴파일러가 타입을 결정하지 못할 수도 있는데 그럴 떼는 프로그래머가 직접 명시해야 한다. 타입을 명시해야 코드가 더 명확할 때를 제외하고는 람다의 모든 매개변수 타입은 생략하자.

람다 자리에 비교자 생성 메서드를 사용하면 이 코드를 더 간결하게 만들 수 있다.

```js
Collections.sort(words, comparingInt(String::length));
```

더 나아가 자바 8 때 List 인터페이스에 추가된 sort 메서드를 이용하면 더욱 짧아진다.

```js
words.sort(comapringInt(String::length));
```

람다를 언어 차원에서 지원하면서 기존에는 적합하지 않았떤 곳에서도 함수 객체를 실용적으로 사용할 수 있게 되었다. item34의 Operation 열거 타입을 예로 들어보자.

```js
public enum Operation {
    PLUS("+"){
        public double apply(double x, double y) {
            return x + y;
        }
    },
    MINUS("-"){
        public double apply(double x, double y) {
            return x - y;
        }
    },
    TIMES("*+*"){
        public double apply(double x, double y) {
            return x * y;
        }
    },
    DIVIDE("/"){
        public double apply(double x, double y) {
            return x / y;
        }
    };

    private String symbol;
    Operation(String symbol) {
        this.symbol = symbol;
    }
    public abstract double apply(double x, double y);
}
```

`함수 객체(람다)를 인스턴스 필드에 저장해 상수별 동작을 구현한 열거 타입`

```js
public enum Operation {
    PLUS("+", (x,y) -> x + y),
    MINUS("-", (x,y) -> x - y),
    TIMES("*", (x,y) -> x * y),
    DIVIDE("/", (x,y) -> x / y);

    private final String symbol;
    private final DoubleBinaryOperation op;

    Operaiotn(String symbol, DoubleBinaryOperation op) {
        this.symbol = symbol;
        this.op = op;
    }

    public double apply(double x, double y) {
        return op.applyAsDouble(x,y);
    }
}
```

람다는 이름이 없고 문서화도 못한다. 따라서 코드 자체로 동작이 명확히 설명되지 않거나 코드 줄 수가 많아지면 람다를 쓰지 말아야 한다. 람다는 한줄일 때 가장 좋고 길어야 세 줄 안에 끝내는게 좋다. 람다가 길거나 읽기 어렵다면 더 간단히 줄여보거나 람다를 쓰지 않는 쪽으로 리팩터링하길 바란다.

<br>

## _람다의 제한_

1. 람다는 함수형 인터페이스에서만 쓰인다. 에컨대 추상 클래스의 인스턴스를 만들 때 람다를 쓸 수 없으니 익명 클래스를 써야 한다.

2. 추상 메서드가 여러 개인 인터페이스의 인스턴스를 만들 때도 익명 클래스를 쓸 수 있다.

3. 람다는 자신을 참조할 수 없다. 람다에서 this 키워드는 바깥 인스턴스를 가리킨다. 그래서 함수 객체가 자신을 참조해야 한다면 반드시 익명 클래스를 사용해야 한다.

4. 람다도 익명 클래스처럼 직렬화 형태가 구현별로 다를 수 있다. 따라서 람다를 직렬화하는 일은 극히 삼가야 한다. 직렬화해야만 하는 함수 객체가 있다면 private 중첩 클래스의 인스턴스를 사용하자.

<br>

## _Conclusion_

익명 클래스는 (함수형 인터페이스가 아닌) 타입의 인스턴스를 만들 때만 사용하라.
