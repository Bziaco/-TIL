# 스트림에서는 부작용 없는 함수를 사용하라

## 스트림 패러다임의 핵심

스트림 패러다임의 핵심은 계싼을 일련의 변환(transformation)으로 재구성하는 부분이다. 이때 각 변환 단계는 가능한 한 이전 단계의 결과를 받아 처리하는 순수 함수여야 한다. 순수 함수란 오직 입력만이 결과에 영향을 주는 함수를 말한다. 다른 가변 상태를 참조하지 않고 함수 스스로도 다른 상태를 변경하지 않는다. 이렇게 하려면 스트림 연산에 건네는 함수 객체는 모두 부작용(side effect)이 없어야 한다. 다음은 텍스트 파일에서 단어별 수를 세어 빈도표를 만드는 일을 하는 코드다.

`스트림 패러다임을 이해하지 못한 채 API만 사용했다. - 따라 하지 말 것!`

```js
Map<String, Long> freq = new HashMap<>();
try(Stream<String> words = new Scanner(file).tokens())
    words.forEach(word -> {
        freq.merge(word.toLowerCase(), 1L, Long::sum);
    });
}
```

위 코드의 문제는 절대 스트림 코드라 할 수 없다. 스트림 코드를 가장한 반복적 코드다. 스트림 API의 이점을 살리지 못하여 같은 기능의 반복적 코드보다 길고 읽기 어렵고 유지보수에도 좋지 않다. 이번엔 제대로된 스트림 API를 사용한 코드이다.

```js
Map<String, Long> freq;
try (Stream<String> words = new Scanner(file).tokens()) {
    freq = words.collect(groupingBy(String::toLowerCase, counting()));
}
```

이번엔 스트림 API를 제대로 사용했다. forEach 연산은 스트림 계산 결과를 보고할 때만 사용하고 계산하는 데는 쓰지 말자.

<br>

## 코드 수집기(collector)

코드 수집기(collector)는 스트림을 사용하려면 꼭 배워야하는 새로운 개념이다. java.util.stream.Collectors 클래스는 메서드를 무려 43개를 가지고 있다. 일단 Collectors 인터페이스는 축소(reduction) 전략을 캡슐화한 블랙박스 객체라고 생각하자. 여기서 축소는 스트림의 원소들을 객체 하나에 취합한다는 뜻이다. 수집기가 생성하는 객체는 일반적으로 컬렉션이며 그래서 "collector"라는 이름을 쓴다. 수집기는 총 세가지로 toList(), toSet(), toCollection(collectionFactory)가 있다.

### _toList()_

스트림 파이프라인을 이용해 간단한 코드 예를 보자

`빈도표에서 가장 흔한 단어 10개를 뽑아내는 파이프라인`

```js
List<String> topTen = freq.keySet().stream()
    .sorted(comparing(freq::get).reversed())
    .limit(10)
    .collect(toList());
}
```

> 마지막 toList는 Collectors의 메서드다. 이처럼 Collectors의 멤버를 정적 임포트하여 쓰면 스트림 파이프라인 가독성이 좋아진다.

이 코드의 어려운 부분은 sorted에 넘긴 비교자, 즉 comparing(freq::get).reversed() 뿐이다. comparing 메서드는 키 추출 함수를 받는 비교자 생성 메서드다. 그리고 한정적 메서드 참조이자 여기서 키 추출함수로 쓰인 freq::get은 입력 받은 단어(키)를 빈도표에서 찾아(추출) 그 빈도를 반환한다. 그런 다음 가장 흔한 단어가 위로 오도록 비교자(comparing)을 역순(reversed)으로 정렬한다(sorted).

### _toMap_

가장 간단한 수집기는 toMap(keyMapper, valueMapper)로 보다시피 스트림 원소를 키에 매핑하는 함수와 값에 매핑하는 함수를 인수로 받는다. 아래 예제를 보자

`toMap 수집기를 사용하여 문자열을 열거 타입 상수에 매핑한다.`

```js
private static final Map<String, Operation> stringToeEnum =
    Stream.of(values()).collect(
        toMap(Object::toString, e -> e);
    )
```

이 간단한 toMap 형태는 스트림의 각 원소가 고유한 키에 매핑되어 있는 때 적합하다. 스트림 원소 다수가 같은 키를 사용한다면 파이프라인이 IllegalStateException을 던지며 종료될 것이다.

더 복잡한 형태의 toMap이나 groupingBy는 이런 충돌을 다루는 다양한 전략을 제공한다. 예컨대 toMap에 키 매퍼와 값 매퍼는 물론 병합(merge) 함수까지 제공할 수 있다. 병합 함수의 형태는 BinaryOpertion< U >이며 여기서 U는 해당 맵의 값 타입이다. 같은 키를 공유하는 값들은 이 병합 함수를 사용해 기존 값에 합쳐진다. 예컨대 병합 함수가 곱셈이라면 키가 같은 모든 값(키/값 매퍼가 정한다)을 곱한 결과를 얻는다.

인수 3개를 받는 toMap은 어떤 키와 그 키에 연관된 원소들 중 하나를 골라 연관 짓는 맵을 만들 때 유용하다. 예컨대 다양한 음악가의 앨범들을 담은 스트림을 가지고 음악가와 그 음악가의 베스트 앨범을 연관 짓고 싶다고 해보자

`각 키와 해당 키의 특정 원소를 연관 짓는 맵을 생성하는 수집기`

```js
Map<Artist, Album> topHits = albums.collect(
    toMao(Album::artist, a -> a, maxBy(comparing(Album::sales)))
);
```

여기서 비교자로는 BinaryOperation에서 정적 임포트한 maxBy라는 정적 팩터리 메서드를 사용했다. maxBy는 Comparator< T >를 입력받아 BinaryOperator< T >를 돌려준다. 이 경우 비교자 생성 메서드인 comparing이 maxBy에 넘겨줄 비교자를 반환하는데 자신의 키 추출 함수로는 Album::sales를 받았다.

인수가 3개인 toMap은 충돌이 나면 마지막 값을 취하는 수집기를 만들 수 있다.

`마지막에 쓴 값을 취하는 수집기`

```js
toMap(keyMapper, valueMapper, (oldVal, newVal) -> newVal)
```

세번째이자 마지막 toMap은 네번째 인수로 맵 팩터리를 받는다. 이 인수로는 EnumMap이나 TreeMap처럼 원하는 특정 맵 구현체를 직접 지정할 수 있다.

### _groupingBy_

이 메서드는 입력으로 분류 함수(classifier)를 받고 출력으로는 원소들을 카테고리별로 모아 놓은 맵을 담은 수집기를 반환한다. 분류 함수는 입력받는 입력받는 원소가 속하는 카테고리를 반환한다. 그리고 이 카테고리가 해당 원소의 맵 키로 쓰인다. 다중정의된 groupingBy 중 형태가 가장 간단한 것은 분류 함수 하나를 인수로 받아 맵을 반환한다. 반환된 맵에 담긴 각각의 값은 해당 카테고리에 속하는 원소들을 모두 담은 리스트다. 이는 아나그램 프로그램에서 사용한 수집기로 알파벳화한 단어를 알파벳화 결과가 같은 단어들의 리스트로 매핑하는 맵을 생성했다.

```js
words.collect(groupingBy(word -> alphabetize(word)))
```

groupingBy가 반환하는 수집기가 리스트 외의 값을 갖는 맵을 생성하게 하려면 분류 함수와 함께 다운스트림(downstream) 수집기도 명시해야 한다. 다운 스트림 수집기의 역할은 해당 카테고리의 모든 원소를 담은 스트림으로부터 값을 생성하는 것이다. 이 매개변수를 사용하는 가장 간단한 방법은 toSet()을 넘기는 것이다. 그러면 groupingBy는 원소들의 리스트가 아닌 집합(set)을 값으로 갖는 맵을 만들어 낸다.

toSet() 대신 toCollection(collectionFactory)를 건내는 방법도 있다. 예상할 수 있듯이 이렇게 하면 리스트나 집합 대신 컬렉션을 값으로 갖는 맵을 생성한다. 다운스트림 수집기로 counting()을 건네는 방법도 있다. 이렇게 하면 각 카테고리(키)를 해당 카테고리에 속하는 원소의 개수와 매핑한 맵을 얻는다

```js
Map < String, (Long > freq = words.collect(groupingBy(String::toLowerCase, counting())));
```

groupingBy의 세번째 버전은 다운스트림 수집기에 더해 맵 팩터리도 지정할 수 있게 해준다. 참고로 이 메서드는 점층적 인수 목록 패턴(telescoping argument list pattern)에 어긋난다. 즉 mapFactory 매개변수가 downStream 매개변수보다 앞에 놓인다. 이 버전의 groupingBy를 사용하면 맵과 그 안에 담긴 컬렉션의 타입을 모두 지정할 수 있다. 예컨대 값이 TreeSet인 TreeMap을 반환하는 수집기를 만들 수 있다.

groupingBy의 사촌격인 partitioningBy는 분류 함수 자리에 프레디키드를 받고 키가 Boolean인 맵을 반환한다.

### _joining_

이 메서드는 CharSequence 인스턴스의 스트림에만 적용할 수 있다. 이 중 매개변수가 없는 joining은 단순히 원소들을 연결(concatenate)하는 수집기를 반환한다.

### _나머지_

Collections에는 이런 속성 메서드가 16개나 더 있다. 그 중 9개는 이름이 summing, averaging, summarizing으로 시작하며 각각 int, long, double 스트림용으로 하나씩 존재한다. 그리고 다중정의된 reducing 메서드들 filtering, mapping, flatMapping, collecting AndThen 메서드가 있다.

이외에도 3개의 Collectors 메서드를 보자면 특이하게도 Collectors에 정의는 되어 있지만 '수집'과는 관련이 없다. 그중 minBy, maxBy는 인수로 받은 비교자를 이용해 스트림에서 값이 가장 작은 혹은 가장 큰 원소를 찾아 반환한다.

<br>

## Conclusion

스트림 파이프라인 프로그래밍의 핵심은 부작용없는 함수 객체에 있다. 스트림뿐 아니라 스트림 관련 객체에 건네지는 모든 함수 객체가 부작용이 없어야 한다. 종단 연산 중 forEach는 스트림이 수행한 계산 결과를 보고할 때만 이용해야 한다. 계산 자체에는 이용하지 말자. 스트림을 올바로 사용하려면 수집기를 잘 알아둬야 한다. 가장 중요한 수집기 팩터리는 toList, toSet, toMao, groupingBy, joining이다.
