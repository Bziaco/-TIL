# 이왕이면 제네릭 메서드로 만들라

## *로 타입 사용 불가*

매개변수화 타입을 받는 정적 유틸리티 메서드는 보통 제네릭이다. 제네릭 메서드 작성법은 제네릭 타입 작성법과 비슷하다. 다음은 두 집합의 합집합을 반환하는 문제가 있는 메서드다.

```js
public static Set union(Set s1, Set s2) {
    Set result = new HashSet(s1);
    result.addAll(s2);
    return result;
}
```

컴파일은 되지만 경고가 발생한다.

```js
Union.java:5: warning: [unchecked] unchecked call to
Hashset(Collection<? extends E>) as a member of raw type HashSet
    Set result = new HashSet(s1);
```
```js
Union.java:6: warning: [unchecked] unchecked call to
Hashset(Collection<? extends E>) as a member of raw type Set
    result.addAll(s2);
```

경고를 없애려면 원소 타입을 타입 매개변수로 명시하고 메서드 안에서도 이 타입 매개변수만 사용하게 수정하면 된다. 타입 매개변수 목록은 메서드의 제한자와 반환 타입 사이에 온다. 아래 코드는 제네릭 메서드로 변경한 코드이다.

```js
public static <E> Set<E> union(Set<E> s1, Set<E> s2) {
    Set<E> result = new HashSet<>(s1);
    result.addAll(s2);
    return result;
}
```

다음은 제네릭 메서드를 활용한 간단한 실행 프로그램이다.

```js
public static void main(String[] args) {
    Set<String> guys = Set.of("톰","딕","해리");
    Set<String> stooges = Set.of("래리","모에","컬리");
    Set<String> aflCio = union(guys, stooges);
    System.out.println(aflCio);
}
```

<br>

## *제네릭 싱글턴*

제네릭은 런타임에 타입 정보가 소거되므로 하나의 객체를 어떤 타입으로든 매개변수화할 수 있다. 하지만 이렇게 하려면 요청한 타입 매개변수에 맞게 매번 그 객체의 타입을 바꿔주는 정적 팩터리를 만들어야 한다. 이러한 패턴을 제네릭 싱글턴 팩터리라 한다.

항등함수(identity function)를 담은 클래스를 직접 만들어보자. 항등함수 객체는 상태가 없으니 요청할 때마다 새로 생성하는 것은 낭비이므로 제네릭 싱글턴 하나면 충분하다.

`제네릭 싱글턴 팩터리 패턴`

```js
private static UnaryOperator<Object> IDENTITY_FN = (t) -> t;

@SuppressWarings("unchecked")
public static <T> UnaryOperator<T> udentityFunction() {
    return (UnaryOperator<T>) IDENTITY_FN;
}
```

IDENTITY_FN를 UnaryOperator<T>로 형변환 하면 T가 어떤 타입이든 UnaryOperator<Object>가 UnaryOperator<T>가 아니기 때문에 비검사 형변환 경고가 발생한다. 하지만 항등함수 특성상 입력 값을 수정없이 그대로 반환하는 함수이므로 T가 어떤 타입이든 UnaryOperator<T>를 사용해도 타입 안전하다. 그래서 @SuppressWarings("unchecked")를 를 추가해 오류 경고를 발생하지 않게 했다.

`제네릭 싱글턴을 사용하는 예`

```js
public static void main(String[] args) {
    String[] strings = {"apple","banana",....};
    UnaryOperator<String> sameString = identityFunction();
    for (String s : sameString) {
        System.out.println(sameString.apply(s));
    }

    Number[] numbers = {1, 2.0L, 3L};
    UnaryOperator<Number> sameNumber = identityFunction();
    for (Number s : numbers) {
        System.out.println(sameNumber.apply(s));
    }
}
```

<br>

## *재귀적 타입 한정(recursive type bound)*

자기 자신이 들어간 표현식을 사용하여 타입 매개변수의 허용범위를 한정할 수 있는 방법이 재귀적 타입 한정이라는 개념이다. 재귀적 타입 한정은 주로 Comparable 인터페이스와 함께 쓰인다.

```js
public interface Comparable<T> {
    int compareTo(T o);
}
```

타입 매개변수 T는 Comparable<T>를 구현한 타입이 비교할 수 있는 원소의 타입을 정의한다. 거의 모든 타입은 자신과 같은 타입의 원소와만 비교할 수 있다. 따라서 String은 Comparable<String>을 구현하고 Integer는 Comparable<Integer>를 구현하는 식이다.

Comparable을 구현한 원소의 컬렉션을 입력받는 메서드들은 주로 그 원소들을 정렬 혹은 검색하거나 최솟값이나 최댓값을 구하는 식으로 사용된다. 이 기능을 수행하려면 컬렉션에 담긴 모든 원소가 상호 비교될 수 있어야 한다. 다음은 이 제약을 코드로 표현한 모습이다.

`재귀적 타입 한정을 이용해 상호 비교할 수 있음을 표현`

```js
public static <E extends Comparable<E>> E max(Collection<E> c);
```

타입 한정인 <E extends Comparable<E>>는 "모든 타입 E는 자신과 비교할 수 있다"라고 읽을 수 있다. 다음은 방금 선언한 메서드 구현이다. 컬렉션에 담긴 원소의 자연적 순서를 기준으로 최댓값을 계산한다.

`컬렉션에서 최댓값을 반환한다. - 재귀적 타입 한전 사용`

```js
public static <E extneds Comparable<E>> E max(Collection<E> c) {
    if (c.isEmpty()) {
        throw ....
    }

    E result = null;
    for (E e : c) {
        if (result == null || e.compareTo(result) > 0) {
            result = Objects.requireNonNull(e);
        }
    }
}
```

> 이 메서드에 빈 컬렉션을 건네면 IllegalArgumentException을 던지니, Optional<E>를 반환하도록 고치는 편이 나을 것이다.


<br>

## *Conclusion*

제네릭 타입과 마찬가지로 클라이언트에서 입력 매개변수와 반환값을 명시적으로 형변환해야 하는 메서드보다 제네릭 메서드가 더 안전하며 사용하기도 쉽다. 타입과 마찬가지로 메서드도 형변환 없이 사용할 수 있는 편이 좋으며 많은 경우 그렇게 하려면 제네릭 메서드가 되어야 한다. 타입과 마찬가지로 형변환을 해줘야 하는 기존 메서드는 제네릭하게 만들자.



