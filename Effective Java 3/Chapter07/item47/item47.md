# 반환 타입으로는 스트림보다 컬렉션이 낫다

## 스트림은 반복(Iterator)을 지원하지 않는다

스트림은 반복을 지원하지 않는다. 그렇기 때문에 스트림과 반복을 알맞게 조합해야 좋은 코드가 나온다.

사실 Stream 인터페이스는 Iterable 인터페이스가 정의한 추상 메서드를 전부 포함할 뿐만 아니라 Iterable 인터페이스가 정의한 방식대로 동작한다. 그럼에도 for-each로 스트림을 반복할 수 없는 이유는 Stream이 Iterable을 확장(extend)하지 않아서 이다. 그러나 이러한 문제를 해결할 좋지 않은? 후회는 있다. 코드가 좀 지저분하고 직관성이 떨어지는 단점이 있다.

`스트림을 반복하기 위한 끔찍한 우회 방법`

```js
for(ProcessHandle ph : (Iterable<ProcessHandle>) ProcessHandle.allProcess()::iterator) {
    // 프로세스 처리
}
```

작동은 하지만 실전에서 쓰기엔 너무 난잡하다. 다행히 어댑터 메서드를 사용하면 상황이 나아진다.

`Stream<E>를 Iterable<E>로 중개해주는 어댑터`

```js
public static <E> Iterable<E> iterableOf(Stream<E> stream) {
    return stream::iterator;
}
```

어댑터를 사용하면 어떤 스트림도 for-each문으로 반복할 수 있다.

```js
for(ProcessHandle p : iterableOf(ProcessHandle::allProcesses())) {
    // 프로세스를 처리한다.
}
```

## Iterable를 반환하는 경우

스트림으로 반환받고자 원하는 프로그래머일 경우 iterable로 반환되는 경우를 반기지 않을 것이다. 자바는 이를 위한 어댑터도 제공하지 않지만 손쉽게 구현할 수 있다.

`Iterable<E>를 Stream<E>로 중개해주는 어댑터`

```js
public static <E> Stream<E> streamof(Iterable<E> iterable) {
    return StreamSupport.stream(iterable.spliterator(), false);
}
```

반대로 반환된 객체들이 반복문에서만 쓰일 걸 안다면 Iterable을 반환하자. 하지만 공개 API를 작성할 때는 스트림 파이프라인을 사용하는 사람과 반복문에서 쓰려는 사람을 모두 배려해야 한다.

<br>

## 공개 API의 경우 반환 타입에는 Collection이나 그 하위 타입을 쓰는게 일반적이다.

Collection 인터페이스는 Iterable의 하위 타입이고 stream 메서드도 제공하니 반복과 스트림을 동시에 지원한다. 따라서 원소 시퀀스를 반환하는 공개 API의 반환 타입에는 Collection이나 그 하위 타입을 쓰는게 일반적으로 최선이다. Arrays 역시 Arrays.asList와 Stream.of 메서드로 손쉽게 반복과 스트림을 지원할 수 있다. 반환하는 시퀀스의 크기가 메모리에 올려도 안전할 만큼 작다면 ArrayList나 HashSet 같은 표준 컬렉션 구현체를 반환하는 게 최선일 수도 있다. 하지만 단지 컬렉션을 반환한다는 이유로 덩치 큰 시퀀스를 메모리에 올려서는 안된다.

<br>

## 컬렉션 내의 시퀀스가 크면 전용 컬렉션을 구현하라

반환할 시퀀스가 크지만 표현을 간결하게 할 수 있다면 전용 컬렉션을 구현하는 방안을 검토해보자. 주어진 집합의 멱집합(한 집합의 모든 부분집합을 원소로 하는 집합)을 반환하는 상황이다. 원소의 개수가 n개면 멱집합의 원소 개수는 2에 n승개가 된다. 그러니 멱집합을 표준 컬렉션 구현체에 저장하려는 생각은 위험하다. 하지만 AbstractList를 이용하면 휼륭한 전용 컬렉션을 손쉽게 구현할 수 있다.

비결은 멱집합을 구성하는 각 원소의 인덱스를 비트 벡터로 사용하는 것이다. 인덱스의 n번째 비트 값은 멱집합의 해당 원소가 원래 집합의 n번째 원소를 포함하는지 여부를 알려준다. 따라서 0부터 2의 n승 -1 까지의 이진수와 원소 n개인 집합의 멱집합과 자연스럽게 매핑된다. 다음 코드를 보자

`입력 집합의 멱집합을 전용 컬렉션에 담아 반환한다`

```js
public class PowerSet {
    public static final <E> Collection<Set<E>> of(Set<E> s) {
       List<E> src = new ArrayList<>(s);
       if(src.size() > 30) {
           throw new IllegalArgumentException("집합에 원소가 너무 많습니다(최대 30개).: " + s);
       }

       return new AbstractList<Set<E>>() {
           @Override
           public int size() {
               return 1 << src.size();
           }

           @Override
           public boolean contains(Object o) {
               return o instanceof Set && src.containsAll((Set) o);
           }

           @Override
           public Set<E> get(int index) {
               Set<E> result = new HashSet<>();
               for (int i = 0; index != 0; i++, index >>=1) {
                   if((index & 1) == 1) {
                       result.add(src.get(i));
                   }
               }
               return result;
           }
       };
    }
}
```

입력집합의 원소수가 30을 넘으면 PowerSet.of가 예외를 던진다. 이는 Stream이나 Iterable이 아닌 Collection을 반환타입으로 쓸때의 단점을 보여준다. 다시말해 Collection의 size 메서드가 int 값을 반환하므로 PowerSet.of가 반환되는 시퀀스의 최대 길이는 Integer.MAX_VALUE 혹은 2에 31승 -1로 제한된다. 완전히 만족스러운 해법은 아니다.

<br>

## Stream이 나을 때도 있다.

AbsctractCollection을 활용해서 Collection 구현체를 작성할 때는 Iterable용 메서드 외에 2개만 더 구현하면 된다. 바로 contain과 size다. contains와 size를 구현하는게 불가능할 때는 컬렉션보다는 스트림이나 Iterable을 반환하는 것이 낫다.

<br>

## 때로는 구현이 쉬운 쪽을 선택하기도 한다.

입력 리스트의 부분리스트를 반환하는 메서드를 작성한다고 가정해보자. 표준 컬렉션에 담는 리스트를 만드는 코드는 단 3줄이면 충분하다. 하지만 이 컬렉션은 입력 리스트 크기의 거듭제곱만큼 메모리를 차지한다. 멱집합 보다는 낫지만 역시나 좋은 방법은 아니다.

하지만 입력 리스트의 모든 부분리스트를 스트림으로 구현하기는 어렵지 않다. 첫번째 원소를 포함하는 부분리스트를 그 리스트의 프리픽스(prefix)라 해보자. 같은 식으로 마지막 원소를 포함하는 부분 리스트를 그 리스트의 서픽스(suffix)라 하자. 어떤 리스트의 부분리스트는 단순히 그 리스트의 프리픽스의 서픽스에 빈 리스트 하나만 추가하면 된다.

`입력 리스트의 모든 부분리스트를 스트림으로 반환한다`

```js
public class SubList {

    public static <E> Stream<List<E>> of(List<E> list) {
        return Stream.concat(Stream.of(Collections.emptyList()),
                             prefixes(list).flatMap(SubList::suffixes));
    }

    public static <E> Stream<List<E>> prefixes(List<E> list) {
        return IntStream.rangeClosed(1, list.size())
                        .mapToObj(end -> list.subList(0, end));
    }

    public static <E> Stream<List<E>> suffixes(List<E> list) {
        return IntStream.rangeClosed(0, list.size())
                        .mapToObj(start -> list.subList(start, list.size()));
    }
}
```

Stream.concat 메서드는 반환되는 스트림에 빈 리스트를 추가하며 flatMap 메서드는 모든 프리픽스의 모든 서픽스로 구성된 하나의 스트릠을 만든다. 마지막으로 프리픽스들과 서픽스들의 스트림은 IntStream.range와 IntStream.rangeClosed가 반환되는 연속된 정숫값들을 매핑해 만들었다.

```js
for (int start = 0; start < src.size(); start++) {
    for (int end = start + 1; end <= src.size(); end++) {
        System.out.println(src.subList(start, end));
    }
}
```

이 반복문은 그대로 스트림으로 변환할 수 있다. 그렇게 하면 앞서의 구현보다 간결해지지만 아마도 읽기에는 더 안 좋을 것이다.

`입력 리스트의 모든 부분리스트를 스트림으로 반환한다.`

```js
public static <E> Stream<List<E>> of(List<E> list) {
    return IntStream.range(0, list.size())
        .mapToObj(start ->
                  IntStream.rangeClosed(start + 1, list.size())
                           .mapToObj(end -> list.subList(start, end)))
        .flatMap(x -> x);
}
```

바로 앞의 for 반복문처럼 이 코드도 빈 리스트는 반환하지 않는다. 이부분을 고치려면 앞에서처럼 concat를 사용하거나 rangeClosed 호출 코드의 1을 (int)Math.signum(start)로 고쳐주면 된다.

<br>

## Conclusion

원소 시퀀스를 반환하는 메서드를 작성할 때는 이를 스트림으로 처리하기를 원하는 사용자와 반복으로 처리하길 원하는 사용자가 모두 있을 수 있음을 떠올리고 양쪽을 다 만족시키려 노력하자. 컬렉션을 반환할 수 있다면 그렇게 하라. 반환 전부터 이미 원소들을 컬렉션에 담아 관리하고 있거나 컬렉션을 하나 더 만들어도 될 정도로 원소 개수가 적다면 ArrayList 같은 표준 컬렉션에 담아 반환하라. 그렇지 않으면 앞서의 멱집합 예처럼 전용 컬렉션을 구현할지 고민하라. 컬렉션을 반환하는 게 불가능함녀 스트림과 Iterable 중 더 자연스러운 것을 반환하라. 만약 나중에 Stream 인터페이스가 Iterable을 지원하도록 자바가 수정된다면 그때는 안심하고 스트림을 반환하면 될 것이다.
