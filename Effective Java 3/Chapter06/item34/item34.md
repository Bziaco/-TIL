# int 상수 대신 열거 타입을 사용하라.

## _정수 열거 패턴(int enum pattern)에는 단점이 많다._

열거 타입은 일정 개수의 상수 값을 정의한 다음, 그 외의 값은 허용하지 않는 타입이다. 자바에서 열거 타입을 지원하기 전에는 다음 코드처럼 정수 상수를 한 묶음 선언해서 사용하곤 했다.

```js
public static final APPLE_FUJI = 0;
public static final APPLE_PIPPIN = 1;
public static final APPLE_GRANNY_SMITH = 2;

public static final ORANGE_NAVEL = 0;
public static final ORANGE_TEMPLE = 1;
public static final ORANGE_BLOOD = 2;
```

정수 열거 패턴(int enum pattern)은 타입 안전을 보장할 방법이 없으며 표현력도 좋지 않다. 오렌지를 건네야 할 메서드에 사과를 보내고 동등 연산자(==)로 비교하더라도 컴파일러는 아무런 경고 메세지를 출력하지 않는다.

```js
int i = (APPLE_FUJI - ORANGE_TEMPLE) / APPLE_PIPPIN;
```

정수 열거 패턴을 사용한 프로그램은 깨지기 쉽다. 평범한 상수를 나열한 것뿐만 아니라 컴파일하면 그 값이 클라이언트 파일에 그대로 새겨진다. 따라서 상수의 값이 바뀌면 클라이언트도 반드시 다시 컴파일해야 한다. 정수대신 문자열 상수를 사용하는 문자열 열거 패턴(string enum pattern)이라 하는 이 변형은 더 나쁘다.

<br>

## _열거 타입(enum type)이란?_

`가장 단순한 열거 타입`

```js
public enum Apple { FUJI, PIPIN, GRANNY_SMITH };
public enum Orange{ NAVEL, TEMPLE, BLOOD };
```

열거 타입은 완전한 형태의 클래스이다. **열거 타입 자체는 클래스이며 상수 하나당 자신의 인스턴스를 하나씩 만들어 public static final 필드로 공개한다.** 열거 타입은 밖에서 접근할 수 있는 생성자를 제공하지 않으므로 사실상 final이다. 열거 타입 선언으로 만들어진 인스턴스들은 확장이 안되므로 딱 하나만 존재함이 보장된다. 다시 말해 열거 타입은 인스턴스 통제된다. 싱글턴은 원소가 하나뿐인 열거 타입이라고 할 수 있고 열거 타입은 싱글턴을 일반화한 형태라고 볼 수 있다.

<br>

## _열거 타입 장점_

1. 열거 타입은 컴파일타임 타입 안전성을 제공한다. Apple 열거 타입을 매개변수로 받는 메서드를 선언햇다면 건네받은 참조는 (null이 아니라면) Apple의 세 가지 값 중 하나임이 확실하다.

2. 열거 타입에는 각자의 이름공간이 있어서 이름이 같은 상수도 공존한다. 열거 타입에 새로운 상수를 추가하거나 순서를 바꿔도 다시 컴파일하지 않아도 된다. 공개되는 것은 오직 필드의 이름뿐이라 정수 열거 패턴과 달리 상수 값이 클라이언트로 컴파일되어 각인되지 않기 때문이다.

3. 열거 타입의 toString 메서드는 출력하기에 적합한 문자열을 내어준다.

4. 열거 타입에는 임의의 메서드나 필드를 추가할 수 있고 임의의 인터페이스를 구현하게 할 수도 있다. Object 메서드들을 구현해 놨고 Comparable과 Serializable을 구현했으며 그 직렬화 형태도 웬만큼 변형을 가해도 문제없이 동작하게끔 구현해 놨다.

<br>

## _열거 타입에서 메서드나 필드 추가를 언제 할까?_

태양계의 여덟 행성을 가지고 열거 타입에 대한 예를 들어보겠다. 각 행성에는 질량과 반지름이 있고 이 두 속성을 이용해 표면중력을 계산할 수 있다. 이 열거 타입의 모습은 다음과 같다.

`데이터와 메서드를 갖는 열거 타입`

```js
public enum Planet {

    MERCURY(3.302e+23, 2.439e6),
    VENUS(4.869e+24, 6.052e6),
    EARTH(5.975e+24, 6.378e6),
    MARS(6.419e+23, 3.393e6),
    JUPITER(1.899e+27, 7.149e7),
    SATURN(5.685e+26, 6.027e7),
    URANUS(8.683e+25, 2.556e7),
    NEPTUNE(1.024e+26, 2.447e7);

    private final double mass;
    private final double radius;
    private final double surfaceGrabity;

    //중력 상
    private static final double G = 6.67300E-11;

    //생성
    Planet(double mass, double radius) {
    	this.mass = mass;
    	this.radius = radius;
    	surfaceGrabity = G * mass / (radius * radius);
    }

    public double mass() {return mass;}
    public double radius() {return radius;}
    public double surfaceGravity() {return surfaceGrabity;}

    public double surfaceWeight(double mass) {
    	return mass * surfaceGrabity;
    }
}
```

열거 타입 상수 각각을 특정 데이터와 연결지으려면 생성자에서 데이터를 받아 인스턴스 필드에 저장하면 된다. 열거 타입은 근본적으로 불변이라 모든 필드는 final이어야 한다. Planet 열거 타입을 활용해 여덟 행성에서의 무게를 출력할 수 있다.

```js
public class WeightTable {
    public static void main(String[] args) {
        double earthWeight = Double.parseDouble(args[0]);
        double mass = earthWeight / Planet.EARTH.surfaceGravity();
        for(Planet p : Planet.values()) {
            System.out.printf("%s에서의 무게는 %f이다.%n", p, p.surfaceWeight(mass));
        }
    }
}
```

열거 타입은 자신 안에 정의된 상수들의 값을 배열에 담아 반환하는 정적 메서드인 values를 제공한다. 값들은 선언된 순서로 저장된다.

<br>

## _열거 타입에서 상수를 하나 제거한다면?_

제거한 상수를 참조하지 않는 클라이언트에는 아무 영향이 없다. 그렇다면 제거된 상수를 참조하는 클라이언트에서는 어떻게 될까? 컴파일 오류가 발생할 것이다. 그러나 정수 열거 패턴과 다르게 아주 유용한 예외 및 오류 메세지와 함께 발생 한다는 것이다.

<br>

## _열거 타입의 상수마다 동작이 달라져야 하는 경우_

사칙연산 계산기의 연산 종류를 열거 타입으로 선언하고 실제 연산까지 열거 타입 상수가 직접 수행하는 경우를 switch문을 이용해 상수의 값에 따라 분기하는 방법을 시도해 보자.

```js
public enum Operation {
    PLUS, MINUS, TIMES, DIVIDE;

    public double apply(double x, double y) {
        switch(this) {
            case PLUS:      return x + y;
            case MINUS:     return x - y;
            case TIMES:     return x * y;
            case DIVIDE:    return x / y;
        }
        throw new AssertionError("알 수 없는 연산: " + this);
    }
}
```

위 코드의 나쁜 점은 깨지기 쉬운 코드라는 사실이다. 새로운 상수가 추가 되거나 혹시라도 깜빡한다면 컴파일은 되지만 해당 연산을 수행하려 할때 런타임 오류를 내며 프로그램이 종료된다.

다행히도 열거타입은 상수별로 다르게 동작하는 코드를 구현하는 더 나은 수단을 제공한다. 열거 타입에 apply라는 추상 메서드를 선언하고 각 상수별 클래스 몸체, 즉 각 상수에서 자신에 맞게 재정의하는 방법이다. 이를 상수별 메서드 구현이라 한다.

`상수별 메서드 구현을 활용한 열거 타입`

```js
public enum Operation {
    PLUS{
        public double apply(double x, double y) {
            return x + y;
        }
    },
    MINUS{
        public double apply(double x, double y) {
            return x - y;
        }
    },
    TIMES{
        public double apply(double x, double y) {
            return x * y;
        }
    },
    DIVIDE{
        public double apply(double x, double y) {
            return x / y;
        }
    };

    public abstract double apply(double x, double y);
}
```

보다시피 apply 메서드가 상수 선언 바로 옆에 붙어 있으니 새로운 상수를 추가할 때 apply도 재정의해야 한다는 사실을 깜빡하기 어려울 것이다. 뿐만 아니라 재정의 하지 않았다면 컴파일 오류를 알려준다. 상수별 메서드 구현을 상수별 데이터와 결합할 수도 있다. 다음은 Operation의 toString을 재정의해 해당 연산을 뜻하는 기호를 반환하도록 한 예다.

`상수별 클래스 몸체와 데이터를 사용한 열거 타입`

```js
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
    TIMES("*"){
        public double apply(double x, double y) {
            return x * y;
        }
    },
    DIVIDE("/"){
        public double apply(double x, double y) {
            return x / y;
        }
    };

    private final String symbol;

    Operation(String symbol) {this.symbol = symbol;}

    @Override public String toString() { return symbol; }
    public abstract double apply(double x, double y);
```

toString을 재정의하여 계산식 출력을 얼마나 편하게 해주는지 보여준다.

```js
public static void main(String[] args) {
   double x = Double.parseDouble(args[0]);
   double y = Double.parseDouble(args[1]);

   for(Operation op : Operation.values()) {
       System.out.printf("%f %s %f = %f%n", x, op, y, op.apply(x,y));
   }
}
```

열거 타입에는 상수 이름을 입력받아 그 이름에 해당하는 상수를 반환해주는 valueOf(String) 메서드가 자동 생성된다. 한편 열거 타입의 toString 메서드를 재정의하려거든 toString이 반환하는 문자열을 해당 열거 타입 상수로 변환해주는 fromString 메서드도 함께 제공하는 걸 고려해보자. 다음은 모든 열거 타입에서 사용할 수 있도록 구현한 fromString이다.

`열거 타입용 fromString 메서드 구현하기`

```js
private static final Map<String, Operation> stringToEnum =
    Stream.of(values()).collect(
        toMap(Object::toString, e -> e)
    );

// 지정한 문자열에 해당하는 Operation을 반환한다.
public static Optional<Operation> fromString(String symbol) {
    return Optional.ofNullable(stringToEnum.get(symbol));
}
```

Operation 상수가 StringToEnum 맵에 추가되는 시점은 열거 타입 상수 생성 후 정적 필드가 초기화될 때다. 열거 타입의 정적 필드 중 열거 타입의 생성자에서 접근할 수 있는 것은 상수 뿐이다. 열거 타입 생성자가 실행되는 시점에는 정적 필드들이 아직 초기화되기 전이라 자기 자신을 추가하지 못하게 하는 제약이 꼭 필요하다. 이 제약의 특수한 예로 열거 타입 생성자에서 같은 열거 타입의 다른 상수에도 접근할 수 없다.

한편 상수별 메서드 구현에는 열거 타입 상수끼리 코드를 공유허기 어렵다는 단점이 있다. 급여 명세서에서 쓸 요일을 표현하는 열거 타입을 예로 생각해보자. 이 열거 타입은 직원의 기본 임금과 그날 일한 시간이 주어지면 일당을 계산해주는 메서드를 갖고 있다. 주중에 오버타임이 발생하면 잔업수당이 주어지고 주말에는 무조건 잔업수당이 주어진다. switch 문을 이용하면 case문을 날짜별로 두어 이 계산을 쉽게 수행할 수 있다.

분명 코드는 간결하지만 관리 관점에서는 위험한 코드다. 휴가와 같은 새로운 값을 열거 타입에 추가하려면 그 값을 처리하는 case 문을 잊지 말고 쌍으로 넣어줘야 하는 것이다.

상수별 메서드 구현으로 급여를 정확히 계산하는 방법은 잔업 수당을 계산하는 코드를 모든 상수에 중복해서 넣을 수도 있고 계산 코드를 평일용과 주말용으로 나눠 각각을 도우미 메서드로 작성한 다음 각 상수가 자신에게 필요한 메서드를 적절히 호출하면 된다. 그러나 두 방식 모두 코드가 장황해져 가독성이 크게 떨어지고 오류 발생 가능성이 높아진다. PayrollDay에 평일 잔업수당 계산용 메서드인 overtimePay를 구현해 놓고 주말 상수에서만 재정의해 쓰면 장황한 부분은 줄일 수 있다. 하지만 switch문을 썼을 때와 같이 overtimePay 메서드를 재정의하지 않으면 평일용 코드를 그대로 물려받게 되는 것이다.

가장 깔끔한 방법은 새로운 상수를 추가할 때 잔업수당 전략을 선택하도록 하는 것이다. 잔업수당 계산을 private 중첩 열거 타입으로 옮기고 PayrollDay 열거 타입의 생성자에서 이 중 적당한 것을 선택한다. 그러면 PayrollDay 열거 타입은 잔업수당 계산을 그 전략 열거 타입에 위임하게 된다.

```js
enum PayrollDay {
    MONDAY(WEEKDAY),
    TUESDAY(WEEKDAY),
    WEDNESDAY(WEEKDAY),
    THURSDAY(WEEKDAY),
    FRIDAY(WEEKDAY),
    SATURDAY(WEEKEND),
    SUNDAY(WEEKEND);

    private final PayType payType;

    PayrollDay(PayType payType) {
        this.payType = payType;
    }

    int pay(int minutesWorked, int payRate) {
        return payType.pay(minutesWorked, payRate);
    }

    // 전략 열거 타입
    enum PayType {
        WEEDAY {
            int overtimePay(int minsWorked, int payRate) {
                return minsWorked <= MINS_PER_SHIFT ? 0 :
                    (minsWorked - MINS_PER_SHIFT) * payRate / 2;
            }
        },
        WEEKEND {
            int overtimePay(int minsWorked, int payRate) {
                return minsWorked * payRate / 2;
            }
        };

        abstract int overtimePay(int mins, int payRate);
        private static final int MINS_PER_SHIFT = 8 * 60;

        int pay(int minsWorked, int payRate) {
            int basePay = minsWorked * payRate;
            return basePay + overtimePay(minsWorked, payRate);
        }
    }
}
```

보다시피 switch 문은 열거 타입의 상수별 동작을 구현하는데 적합하지 않다. 하지만 기존 열거 타입에 상수별 동작을 혼합해 넣을 때는 switch문이 좋은 선택이 될 수 있다.

`switch 문을 이용해 원래 열거 타입에 없는 기능을 수행한다.`

```js
public static Operation inverse(Operation op){

    switch(op) {
        case PLUS:      return Operation.MINUS;
        case MINUS:     return Operation.PLUS;
        case TIMES:     return Operation.DIVIDE;
        case DIVIDE:    return Operation.TIMES;

        default: throw new AssertionError("알 수 없는 연산:" + op);
    }
}
```

추가하려는 메서드가 의미상 열거 타입에 속하지 않는다면 직접 만든 열거 타입이라도 이 방식을 적용하는게 좋다. 대두분의 경우 열거 타입의 성능은 정수 상수와 별반 다르지 않다. 열거 타입을 메모리에 올리는 공간과 초기화하는 시간이 들긴 하지만 체감될 정도는 아니다.

<br>

## _열거 타입은 언제 쓰는 것인가?_

필요한 원소를 컴파일타임에 다 알 수 있는 상수 집합이라면 항상 열거 타입을 사용하자. 열거 타입에 정의된 상수 개수가 영원히 고정불변일 필요도 없다. 열거 타입은 나중에 상수가 추가돼도 바이너리 수준에서 호환되도록 설계되었다.

<br>

## _Conclusion_

열거 타입은 확실히 정수 상수보다 뛰어나다. 더 읽기 쉽고 안전하고 강력하다. 대다수 열거 타입이 명시적 생성자나 메서드 없이 쓰이지만 각 상수를 특정 데이터와 연결짓거나 상수마다 다르게 동작하게 할 때는 필요하다. 드물게 하나의 메서드가 상수별로 다르게 동작해야 할 때도 있따. 이런 열거 타입에서는 switch문 대신 상수별 메서드 구현을 사용하자. 열거 타입 상수 일부가 같은 동작을 공유한다면 전략 열거 타입 패턴을 사용하자
