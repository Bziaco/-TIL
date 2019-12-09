# 제네릭과 가변인수를 함께 쓸 때는 신중하라

## _제네릭과 가변인수는 잘 어울리지 않는다!!_

가변인수(varargs)는 메서드에 넘기는 인수의 개수를 클라이언트가 조절할 수 있게 해주는데 구현 방식에 허점이 있다. 가변인수 메서드를 호출하면 가변인수를 담기 위한 배열이 자동으로 하나 만들어진다. 그런데 내부로 감춰야 했을 이 배열을 그만 클라이언트에 노출하는 문제가 생겼다. 그 결과 varargs 매개변수에 제네릭이나 매개변수화 타입이 포함되면 알기 어려운 컴파일 경고가 발생한다. 경고 형태는 대략 다음과 같다

```js
warning: [unchecked] Possible heap pollution from
    parameterized vararg type List<String>
```

매개변수화 타입의 변수가 타입이 다른 객체를 참조하면 힙 오염이 발생한다. 다음 메서드를 예로 생각해보자

`제네릭과 varargs를 혼용하면 타입 안전성이 깨진다!`

```js
static void dangerous(List<String>... stringLists) {
    List<Integer> intList = List.of(42);
    Object[] objects = stringLists;
    objects[0] = intList;             // 힙 오염 발생
    String s = StringLists[0].get(0); // ClassCastException
}
```

마지막 줄에서 보이지 않는 형변환이 숨어있기 때문에 예외 에러를 던진다. 이처럼 타입 안전성이 깨지기 때문에 제네릭 varargs 배열 매개변수에 값을 저장하는 것은 안전하지 않다.

<br>

## _varargs 매개변수를 받는 메서드를 선언할 수 있게 한 이유는 무엇일까?_

제네릭 배열을 프로그래머가 직접 생성하는 건 허용하지 않으면서 제네릭 varargs 개배션수를 받는 메서드를 선언할 수 있게 한 이유는 무엇인가? 이유는 제네릭이나 매개변수화 타입의 varargs 매개변수를 받는 메서드가 실무에서 매우 유용하기 때문이다. `Arrays.asList(T... a), Collections.addAll(Collection<? extends T> c, T... elements), EnumSet.of(E first, E...rest)`가 대표적이다. 이들은 타입 안전하다.

자바 7 이전에는 제네릭 가변인수 메서드의 작성자가 호출자 쪽에서 발생하는 경고에 대해 해줄 수 있는 일이 없었다. @SuppressWarnings("unchecked") 애너테이션ㅇ르 달아 경고를 숨겨야 했다. 지루함과 가독성을 떨어뜨릴 뿐만 아니라 진짜 문제마저 숨기는 안좋은 결과로 이뤄졌다.

그러나 자바 8 부터 @SafeVarargs 애너테이션이 추가되어 메서드 작성자가 그 메서드가 타입 안전함을 보장하는 장치로 사용할 수 있게 되었다. 매서드가 안전한게 확실하지 않다면 절대 @SafeVarargs 애너테이션을 달아서는 안된다.

<br>

## _가변인수 메서드가 안전한지는 어떻게 확신할 수 있을까?_

가변인수 메서드를 호출할 때 varargs 매개변수를 담는 제네릭 배열이 만들어진다. 메서드가 이 배열에 아무것도 저장하지 않고(그 매개변수들을 덮어쓰지 않고) 그 배열의 참조가 밖으로 노출되지 않는다면(신뢰할 수 없는 코드가 배열에 접근할 수 없다면) 타입 안전하다. 즉, varargs 매개변수 배열이 호출자로부터 그 메서드로 순수하게 인수들을 전달하는 일만 한다면 그 메서드는 안전하다.

이때, varargs 매개변수 배열에 아무것도 저장하지 않고도 타입 안전성을 깰 수도 있으니 조심해야 한다. 다음 코드는 가변인수로 넘어온 매개변수들을 배열에 담아 반환하는 제네릭 메서드다. 보기와 달리 위험하다!

`자신의 제네릭 매개변수 배열의 참조를 노출한다. - 안전하지 않다!`

```js
static <T> T[] toArray(T... args) {
    return args;
}
```

이 메서드가 반환하는 배열의 타입은 이 메서드에 인수를 넘기는 컴파일타임에 결정되는데 그 시점엔느 컴파일러에게 충분한 정보가 주어지지 않아 타입을 잘못 판단할 수 있다. 따라서 자신의 varargs 매개변수 배열을 그대로 반환하면 힙 오염을 이 메서드를 호출한 쪽의 콜스택으로까지 전이하는 결과를 낳을 수 있다. 구체적인 예를 보자 다음 메서드는 T 타입 인수 3개를 받아 그중 2개를 무작위로 골라 담은 배열을 반환한다.

```js
static <T> T[] pickTwo(T a, T b, T c) {
    switch(ThreadLocalRandom.current().nextInt(3)) {
        case 0: return toArray(a,b);
        case 1: return toArray(a,c);
        case 2: return toArray(b,c);
    }
    throw new AssertionError();
}
```

이 메서드는 제네릭 가변 인수를 받는 toArray 메서드를 호출한다. 컴파일러는 toArray에 넘길 T 인스턴스 2개를 담을 varargs 매개변수 배열을 만드는 코드를 생성한다. 이 코드가 만드는 배열의 타입은 Object[]인데 pickTwo에 어떤 타입의 객체를 넘기더라도 담을 수 있는 가장 구체적인 타입이기 때문이다. 그리고 toArray 메서드가 돌려준 이 배열이 그대로 pickTwo를 호출한 클라이언트까지 전달된다. 즉, pickTwo는 항상 Object[] 타입 배열을 반환한다. pickTwo를 사용하는 main 메서드를 보자.

```js
public static void main(String[] args) {
    String[] attributes = pickTwo("좋은", "빠른", "저렴한");
}
```

위 코드를 실행하면 ClassCastException을 던진다. 이유가 무엇일까? 이유는 위에서 말했듯이 pickTwo() 리턴타입이 Object[] 타입으로 반환된다. Object[]는 String[]의 하위타입이 아니므로 이 형변환은 실패한다. 이 예는 제네릭 varargs 매개변수 배열에 다른 메서드가 접근하도록 허용하면 안전하지 않다는 점을 다시 한번 상기시킨다.

단 예외가 두가지 있다.

- 첫 번째 @SafeVarargs로 제대로 애노테이트된 또 다른 varargs 메서드에 넘기는 것은 안전하다.

- 두 번째 그저 이 배열 내용의 일부 함수를 호출만 하는 (varargs를 받지 않는) 일반 메서드에 넘기는 것도 안전하다.

<br>

## _@SafeVarargs 애너테이션을 사용해야 할 때를 정하는 규칙_

제네릭이나 매개변수화 타입의 varargs 매개변수를 받는 모든 메서드에 @SafeVarargs를 달라. 안전하지 않은 varargs 메서드는 절대 작성해서는 안된다.

`제네릭 varargs 매개변수를 안전하게 사용하는 메서드`

```js
@SafeVarargs
static <T> List<T> flatten(List<? extends T>... lists) {
    List<T> result = new ArrayList<>();
    for(List<? extends T> list : lists) {
        result.addAll(list);
    }
    return result;
}
```

다음 두 조건을 모두 만족하는 제네릭 varargs 메서드는 안전하다. 둘 중 하나라도 어겼다면 수정하라!

- vararags 매개변수 배열에 아무것도 저장하지 않는다.

- 그 배열(혹은 복제본)을 신뢰할 수 없는 코드에 노출하지 않는다.

> SafeVarargs 애너테이션은 재정의할 수 없는 메서드에만 달아야 한다. 자바 8에서 이 애너테이션은 오직 정적 메서드와 final 인스턴스 메서드에만 붙일 수 있고 자바 9부터는 private 인스턴스 메서드에도 허용된다.

<br>

## _varargs 매개변수를 List 매개변수로 바꿀 수도 있다._

varargs 매개변수를 List 매개별수로 바꿀 수도 있다. 이 방식을 앞서의 flatten 메서드에 적용하면 다음처럼 된다. 매개변수 선언만 수정햇음에 주목하자.

```js
@SafeVarargs
static <T> List<T> flatten(List<List<? extends T>> lists) {
    List<T> result = new ArrayList<>();
    for(List<? extends T> list : lists) {
        result.addAll(list);
    }
    return result;
}
```

정적 팩터리 메서드인 list.of를 활용하면 다음 코드와 같이 이 메서드에 임의개수의 인수를 넘길 수 있다. 이렇게 사용하는게 가능한 이유는 List.of에도 @SafeVarargs 애너테이션이 달려 있기 때문이다.

```js
audience = flatten(List.of(friends, romans, countrymen));
```

이 방식의 장점은 컴파일러가 이 메서드의 타입 안전성을 검증할 수 있다는 데 있다. 애노테이션을 직접 달지 않아도 되며 실수로 안전하다고 판단할 걱정도 없다. 단점이라면 코드가 살짝 지져분해지고 속도가 조금 느려질 수 있다는 정도다.

pickTwo 메서드를 수정하면 아래와 같다.

```js
static <T> T[] pickTwo(T a, T b, T c) {
    switch(ThreadLocalRandom.current().nextInt(3)) {
        case 0: return List.of(a,b);
        case 1: return List.of(a,c);
        case 2: return List.of(b,c);
    }
    throw new AssertionError();
}
```

그리고 main 메서드는 다음처럼 변한다.

```js
public static void main(String[] args) {
    List<String> attributes = pickTwo("좋은","빠른","저렴한");
}
```

결과 코드는 배열 없이 제네릭만 사용하므로 타입 안전하다.

<br>

## _Conclusion_

가변인수와 제네릭은 궁합이 좋지 않다. 가변인수 기능은 배열을 노출하여 추상화가 완벽하지 못하고, 배열과 제네릭의 타입 규칙이 서로 다르기 때문이다. 제네릭 varargs 매개변수는 타입 안전하지는 않지만 허용된다. 메서드에 제네릭 varargs 매개변수를 사용하고자 한다면 먼저 그 메서드가 타입 안전한지 확인한 다음 @SafeVarargs 애너테이션을 달아 사용하는 데 불편함이 없게끔 하자.
