# 표준 함수혀 인터페이스를 사용하라

## _java.util.function_

java.util.function 패키지를 보면 다양한 용도의 표준 함수형 인터페이스가 담겨 있다. 필요한 용도에 맞는게 있다면 직접 구현하지 말고 표준 함수형 인터페이스를 활용하라. 또한 표준 함수형 인터페이스들은 유용한 디폴트 메서드를 많이 제공하므로 다른 코드와의 상호 운용성도 크게 좋아질 것이다.

java.util.function 패키지에는 총 43개의 인터페이스가 담겨 있다. 이 기본 인터페이스들은 모두 참조 타입용이다.

- Opertation : 반환값과 인수의 타입이 같은 함수

- Predicate : 인수와 반환 타입이 다른 함수

- Supplier : 인수를 받지 않고 값을 반환(혹은 제공)하는 함수

- Consumer : 인수를 하나 받고 반환값은 없는 함수

다음은 기본 함수형 인터페이스들을 정리한 표다.

| 인터페이스         | 함수 시그니처       | 예                  |
| ------------------ | ------------------- | ------------------- |
| UnaryOpertaion<T>  | T apply(T t)        | String::toLowerCase |
| BinaryOpertaion<T> | T apply(T t1, T t2) | BigInteger::add     |
| Predicate<T>       | boolean test(T t)   | Collection::isEmpty |
| Function<T,R>      | R apply(T t)        | Arrays::asList      |
| Supplier<T>        | T get()             | Instant::now        |
| Consumer<T>        | void accept(T t)    | System.out::println |

기본 인터페이스는 기본 타입인 int, long, double용으로 각 3개씩 변형이 생겨난다. 그 이름도 기본 인터페이스의 이름 앞에 해당 기본 타입 이름을 붙여 지었다. 예컨대 int를 받는 Predicate는 IntPredicate가 되고 long을 받아 long을 반환하는 BinaryOperator는 LongBinaryOperator가 되는 식이다.

<br>

## _Function 인터페이스의 기본 타입 변형_

인수와 같은 타입을 반환하는 함수는 UnaryOperator이므로 Function 인터페이스의 변형은 입력과 결과의 타입이 항상 다르다. 입력과 결과 타입이 모두 기본 타입이면 접두어로 SrcToResult를 사용한다. 예컨대 long을 받아 int를 반환하면 LongToIntFunction이 되는 식이다. 나머지 입력이 객체 참조이고 결과가 int, long, double인 변형들로 앞서와 달리 입력을 매개변수화하고 접두어로 ToResult를 사용한다. 즉, ToLongFunction<int[]>은 int[] 인수를 받아 long을 반환한다.

기본 함수형 인터페이스 중 3개에는 인수를 2개씩 받는 변형이 있다. BiPredicate<T,U>, BiFunction<T,U,R>, BiConsumer<T,U>다. BiFunction에는 다시 기본 타입을 반환하는 세 변형 ToIntBiFunction<T,U>, ToLongBiFunction<T,U>, ToDoubleBiFunction<T,U>가 존재한다. Consumer에도 ObjDoubleConsumer< T >, ObjIntComsumer< T >, ObjLongConsumer< T >가 존재한다.

마지막으로 BooleanSupplier 인터페이스는 boolean을 반환하도록 한 Supplier의 변형이다. 이것이 표준 함수형 인터페이스 중 boolean을 이름에 명시한 유일한 인터페이스지만 Predicate와 그 변경 4개도 boolean 값을 반환할 수 있다.

<br>

## _기본 함수형 인터페이스에 박싱된 기본 타입을 넣어 사용하지 말자_

동작은 하지만 계산량이 많을 때는 성능이 처참히 느려질 수 있다.

<br>

## 표준 함수형 인터페이스를 직접 작성해야 하는 경우는?

표준 인터페이스 중 필요한 용도에 맞는게 없다면 직접 작성해야 한다. 예를 들어 매개변수 3개를 받는 Predicate라든가 검사 예외를 던지는 경우가 있을 수 있다. 그런데 구조적으로 똑같은 표준 함수형 인터페이스가 있더라도 직접 작성해야 하는 경우가 있다.

예를 들어 Comparator< T > 인터페이스를 보자. 구조적으로는 ToIntBiFunction<T,U>와 동일하다. 심어어 자바라이브러리에 Comparator< T >를 추가할 당시 ToIntBiFunction<T,U>가 이미 존재 했더라도 ToIntBiFunction<T,U>를 사용하면 안됐다. Comparator가 독자적인 인터페이스로 살아남아야 하는 이유가 몇개 있다.

1. API에서 굉장히 자주 사용되며 지금의 이름이 그 용도를 아주 휼륭하게 설명해준다.

2. 구현하는 쪽에서 반드시 지켜야 할 규약을 담고 있다.

3. 비교자들을 변환하고 조합해주는 유용한 디폴트 메서드들을 듬뿍 담고 있다.

이상의 Comparator 특성을 정리하면 다음의 세 가지인데 이 중 하나 이상을 만족한다면 전용 함수형 인터페이스를 구현해야 하는 건 아닌지 진중히 고민해야 한다.

<br>

## _@FunctionalInterface_

전용 함수형 인터페이스를 작성하기로 했다면 그것은 인터페이스임을 명심해야 한다. 아주 주의해서 설계해야 한다는 뜻히다.

@Functional Interface는 프로그래머의 의도를 명시하는 것으로 @Override를 사용하는 이유와 비슷하다.

1. 해당 클래스의 코드나 설명 문서를 읽을 이에게 그 인터페이스가 람다용으로 설계된 것임을 알려준다.

2. 해당 인터페이스가 추상 메서드를 오직 하나만 가지고 있어야 컴파일되게 해준다.

3. 그 결과 유지보수 과정에서 누군가 실수로 메서드를 추가하지 못하게 막아준다.

그래서 직접 만든 함수형 인터페이스에는 항상 @FunctionalInterface 애너테이션을 사용하라.

<br>

## _API 사용시 함수형 인터페이스 사용 주의사항_

서로 다른 함수형 인터페이스를 같은 위치의 인수로 받는 메서드들을 다중정의해서는 안된다. 클라이언트에게 불필요한 모호함만 안겨줄 뿐이며 이 모호함으로 인해 실제로 문제가 일어나기도 한다. ExecutorService의 submit 메서드는 Callable< T >를 받는 것돠 Runnable을 받는 것을 다중정의 했다. 그래서 용바른 메서드를 알려주기 위해 형변환해야 할 때가 있다. 이런 문제를 피하는 가장 쉬운 방법은 서로 다른 함수형 인터페이스를 같은 위치의 인수로 사용하는 다중정의를 피하는 것이다.

<br>

## _Conclusion_

입력값과 반환값에 함수형 인터페이스 타입을 활용하라. 보통은 java.util.function 패키지의 표준 함수형 인터페이스를 사용하는 것이 가장 좋은 선택이다. 단, 흔지는 않지만 함수형 인터페어스를 만들어 쓰는 편이 나을 수도 있음을 잊지 말자.
