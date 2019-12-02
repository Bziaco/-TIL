# 한정적 와일드카드를 사용해 API 유연성을 높이라

## *리스트 스트링과 리스트 오브젝트의 관계*

List<Object>에는 어떤 객체든 넣을 수 있지만 List<String>에는 문자열만 넣을 수 있다. 매개변수화 타입이 불공변이기 때문이다.즉, List<String>은 List<Object>가 하는 일을 제대로 수행하지 못하니 하위 타입이 될 수 없다. 리스코프 치환 원칙에 어긋난다.

<br>

## *한정적 와일드 카드 타입*

`Stack의 pulbic API`

```js
public class Stack<E> {
    public Stack();
    public void push(E e);
    public E pop();
    public boolean isEmpty();
}
```

여기에 일련의 원소를 스택에 넣는 메서드를 추가한다고 가정해보자

`와일드카드 타입을 사용하지 않은 pushAll 메서드 - 결함이 있다!`

```js
public void pushAll(Iterable<E> src) {
    for(E e : src) {
        push(e);
    }
}
```

이 메서드는 깨긋이 잘 실행되지만 완벽하진 않다. 만약 Stack 클래스의 매개변수화 타입을 Stack<Number>로 선언한 후 pushAll(intVal)을 (Integer타입인 intVal) 호출하면 다음과 같은 오류 메세지가 뜬다. **매개변수화 타입이 불공변이기 때문이다.**

```js
StackTest.java:7: error: incompatible types: Iterable<Integer>
cannot be converted to Iterable<Number>
    numberStack.pushAll(integers);
```

**이러한 상황에서의 해결책은 한정적 와일드카드 타입이라는 특별한 매개변수화 타입을 통해 해결할 수 있다.** pushAll의 입력 매개변수 타입은 'E의 Iterable'이 아니라 'E의 하위 타입의 Iterable'이어야 하며 와일드 카드 타입
Iterable<? extend E>가 정확히 이런 뜻이다. 와일드 카드 타입을 사용하여 pushAll 메서드를 수정하면 아래와 같다.

`E 생산자(producer) 매개변수에 와일드카드 타입 적용`

```js
public void pushAll(Iterable<? extends E> src) {
    for(E e : src) {
        push(e);
    }
}
```

이제 pushAll과 짝을 이루는 popAll 메서드를 작성할 차례이다. popAll 메서드는 Stack 안의 모든 원소를 주어진 컬렉션으로 옮겨 담는다. 다음처럼 작성했다고 해보자.

`와일드카드 타입을 사용하지 않은 popAll 메서드 - 결함이 있다!`

```js
public void popAll(Collection<E> dst) {
    while(!isEmpty()) {
        dst.add(pop());
    }
}
```

위 코드도 앞선 pushAll()처럼 Stack 클래스의 매개변수화 타입을 Number로 바꿔(Stack< Number>) 컴파일과 동적을 하면 pushAll()을 사용했을 때와 비슷한 에러가 발생한다. 이번에도 와일드 카드 타입으로 해결할 수 있다. 이번에는 popAll의 입력 매개변수의 타입이 'E의 Collection'이 아니라 'E의 상위 타입의 Collection'이어야 한다. 와일드카드 타입을 사용한 Collection<? super E>'가 정확히 이런 의미다. 이를 popAll에 적용해보자.

```js
public void popAll(Collection<? super E> dst) {
    while(!isEmpty()) {
        dst.add(pop());
    }
}
```

위 두 예제에서의 전달하고자 하는 의미는 분명하다. **유연성을 극대화하려면 원소의 생산자나 소비자용 입력 매개변수에 와일드카드 타입을 사용하라.** 한편 입력 매개벼수가 생산자나 소비자 역할을 동시에 한다면 와일드카드 타입을 써도 좋을게 없다. 타입을 정확히 지정해야 하는 상황으로 이때는 와일드카드 타입을 쓰지 말아야 한다. 다음 공식을 외워두면 어떤 와일드 카드 타입을 써야 하는지 기억하는데 도움이 될 것이다.

***`펙스(PECS) : producer-extends, consumer-super`***

즉, 매개변수화 타입 T가 생산자라면 <? extends T>를 사용하고 소비자라면 <? super T> 를 사용하라. Stack 예에서 pushAll의 src 매개변수는 Stack이 사용할 E 인스턴스를 생산하므로 src의 적절한 타입은 Iterable<? extends E>이다. 한편, popAll의 dst 매개변수는 Stack으로부터 E 인스턴스를 소비하므로 dst의 적절한 타입은 Collection<? super E>이다.

<br>

## 클래스 사용자가 와일드카드 타입을 신경써야 한다면 잘못된 API일 가능성이 크다

```js
public Chooser(Collection<T> choices) 
```

이 생성자로 넘겨지는 choices 컬렉션은 T 타입의 값을 생산하기만 하니 T를 확장하는 와일드카드 타입을 사용해 선언해야 한다.

```js
public Chooser(Collection<? extends T> choices);
```

이렇게 수정하면 Chooser<Number>의 생성자에 List<Integer>를 넘기고 싶다고 가정해보자. 수정 전에는 컴파일조차 되지 않지만 수정 후에는 한정적 와일드카드 타입으로 선언한 수정 후 생성자에서는 문제가 사라진다. union 메서드를 예로 하나 더 들어보자.

```js
public static <E> Set<E> union<Set<E> s1, Set<E> s2)
```

s1과 s2 모두 E의 생산자이니 PECS 공식에 따라 다음처럼 선언해야 한다.

```js
public static <E> Set<E> union(Set<? extends E> s1, Set<? extends E> s2)
```

> *반환 타입은 여전히 Set< E>로 해애 한다. 반환 타입에는 한정적 와일드카드 타입을 사용하면 안 된다. 유연성을 높여주기는 커녕 클라이언트 코드에서도 와일드카드 타입을 써야 하기 때문이다.*

<br>

## 매개변수(parameter)와 인수(argument)의 차이

매개변수는 메서드 선언에 정의한 변수이고 인수는 메서드 호출시 넘기는 '실젯값'이다.

```js
void add(int value) {...}
add(19)
```

이 코드에서 value는 매개변수이고 10은 인수이다. 이 정의를 제네릭까지 확장하면 다음과 같다.

 ```js
 class Set<T> {...}
 Set<Integer> = ...;
 ```

 여기서 T는 타입 매개변수가 되고 Integer는 타입 인수가 된다.


 <br>

 ## 복수 와일드카드 타입 사용

```js
// 변경 전
public static <E extends Comparable<E>> E max(List<E> list);

// 변경 후
public static <E extends Comparable<? super E>> E max(List<? extends E> list);
```

이번에는 PECS 공식을 두 번 적용했다. 둘 중 더 쉬운 입력 매개변수 목록부터 살펴보자. 입력 매개변수에서는 E 인스턴스를 생산하므로 원래의 List<E>를 List<? extends E>로 수정했다. 다음은 더 난해한 쪽인 타입 매개변수 E 이다. 원래 선언에서는 E가 Comparable<E>를 확장한다고 정의했는데 이때 Comparable<E>는 E 인스턴스를 소비한다. 그래서 매개변수화 타입 Comparable<E>를 한정적 와일드카드 타입인 Comparable<? super E>로 대체 했다. Comparable은 언제나 소비자 이므로 일반적으로 Comparable<E>보다는 Comparable<? super E>를 사용하는 편이 낫다. Comparator도 마찬가지이다. Comparator<E>보다는 Comparator<? super E>를 사용하는 편이 낫다.

```js
List<ScheduledFuture<?>> scheduledFutures = ...;
```

수정 전 max가 이 리스트를 처리할 수 없는 이유는 ScheduledFuture가 Comparable<ScheduledFuture>를 구현하지 않았기 때문이다. ScheduledFuture는 Delay의 하위 타입 인터페이스 이고 Delayed는 Comparable<Delayed>를 확장했다. 다시 말해 ScheduledFuture의 인스턴스는 다른 ScheduledFuture 인스턴스뿐 아니라 Delay 인스턴스와도 비교할 수 있어서 수정 전 max가 이 리스트를 거부하는 것이다. 더 일반화 해서 말하자면 Comparable을 직접 구현하지 않고 직접 구현한 다른 타입을 확장한 타입을 지원하기 위해 와일드카드가 필요하다.(아래와 같은 구조)

```js
public interface Comparable<E>>
public interface Delayed extends Comparable<Delayed>
public interface ScheduledFuture<V> extends Delayed, Future<V>
```

<br>

## 메서드 선언에 타입 매개변수가 한 번만 나오면 와일드 카드로 대체하라.

`swap 메서드의 두 가지 선언`

```js
public static <E> void swap(List<E> list, int i, int j);
public static void swap(List<?> list, int i, int j);
```

둘 중 어떤 선언이 더 나을까? public API라면 간단한 두번째가 낫다. 메서드 선언에 타입 매개변수가 한 번만 나오면 와일드 카드로 대체하는게 좋다. 이때 비한정적 타입 매개변수라면 비한정적 와일드카드로 바꾸고 한정적 타입 매개변수라면 한정적 와일드카드로 바꾸면 된다.

하지만 두 번째 swap선언에는 문제가 하나 있다. 아래 코드가 컴파일되지 않는다는 것이다.

```js
public static void swap(List<?> list, int i, int j) {
    list.set(i, list.set(j, list.get(i)));
}
```

이유는 **List<?> 타입에는 null 외에는 어떤 값도 넣을 수 없다는데 있다.** 이러한 문제에 대한 해결책은 와일드카드 타입의 실제타입을 알려주는 메서드를 private 도우미 메서드로 따로 작성하여 활용하는 방법이다. 실제 타입을 알아내려면 이 도우미 메서드는 제네릭 메서드여야 한다. 다음 코드를 보자.

```js
public static void swap(List<?> list, int i, int j) {
    swapHelper(list, i, j);
}

// 와일드카드 타입을 실제 타입으로 바꿔주는 private 도우미 메서드
private static <E> void swapHelper(list<E> list, int i, int j) {
    list.set(i, list.set(j, list.get(i)));
}
```

swapHelper 메서드는 리스트가 List<E>임을 알고 있다. 즉, 이 리스트에서 꺼낸 값의 타입은 항상 E이고 E 타입의 값이라면 이 리스트에 넣어도 안전함을 알고 있다.

<br>

## Conclusion

조금 복잡하더라도 와일드카드 타입을 적용하면 API가 훨씬 유연해 진다. PECS 공식을 기억하자. 생산자는 extends를 소비자는 super를 사용한다. Comparable과 Comparator는 모두 소비자라는 사실도 잊지 말자.