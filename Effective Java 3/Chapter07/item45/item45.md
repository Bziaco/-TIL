# 스트림은 주의해서 사용하라

## _스트림 API_

스트릠 API는 다량의 데이터 처리 작업을 돕고자 자바 8에 추가 되었다. 이 API가 제공하는 추상 개념 중 핵심은 두가지이다.

- 스트림(stream) : 데이터 원소의 유한 혹은 무한 시퀀스(sequence)

- 스트림 파이프라인(stream pipeline) : 스트림 데이터 원소들로 연산 단계를 표현하는 개념

  - ex) 컬렉션, 배열, 파일, 정규표현식 패턴 매처, 난수 생성기

  - 스트림 안의 데이터 원소들은 객체 참조나 기본 타입 값이다. 기본 타입 값으로는 int, long, double 세가지를 지원한다.

<br>

## _스트림 파이프라인(Stream pipline)_

스트림 파이프라인은 소스 스트림에서 시작해 종단 연산(terminal operation)으로 끝나며 그 사이에 하나 이상의 중간 연산(intermediate operation)이 있을 수 있다. 각 중간 연산은 어떠한 방식으로 변환(transform)한다. 각 원소에 함수를 적용하거나 특정 조건을 만족 못하는 원소를 걸러낼 수 있다.

### 중간 연산

중간 연산들은 모두 한 스트림을 다른 스트림으로 변환하는데 변환된 스트림의 원소 타입은 변환 전 스트림의 원소 타입과 같을 수 있고 다를 수 있다.

### 종단 연산

종단 연산은 마지막 중간 연산이 내놓은 스트림에 최후의 연산을 가한다. 원소를 정렬해 컬렉션에 담거나 특정 원소 하나를 선택하거나 모든 원소를 출력하는 식이다.

### 지연 평가

스트림 파이프라인은 지연 평가(lazy evalutation)이다. 평가는 종단 연산이 호출될 때 이뤄지며 종단 연산에 쓰이지 않은 데이터 원소는 계산에 쓰이지 않는다. 이러한 지연 평가가 무한 스트림을 다룰 수 있게 해주는 열쇠이다.

### 순차적 수행

파이프라인을 병렬로 실행하려면 parallel 메서드를 호출해주기만 하면 되나 효과를 볼 수 있는 상황은 많지 않다.

<br>

## _스트림 API를 항상 사용하는 것만이 정답은 아니다_

스트림 API를 제대로 사용하면 프로그램이 짧고 깔끔해지지만 잘못 사용하면 읽기 어렵고 유지보수도 힘들어 진다. 스트림을 언제 써야 하는지를 규정하는 확고부동한 규칙은 없지만 참고할 만한 노하우는 있다.

### 아나그램

아나그램이란 철자를 구성하는 알파벳이 같고 순서만 다른 단어를 말한다. "staple"의 키는 "aelpst"가 되고 "petals"의 키도 "aelpst"가 된다. 따라서 이 두 단어는 아나그램이고 아나그램끼리는 같은 키를 공유한다. 맵의 값은 같은 키를 공유한 단어들을 담은 집합이다.

`사전 하나를 훓어 원소 수가 많은 아나그램 그룹들을 출력한다.`

```js
public static void main(String[] args) {
    File dectionary = new File(args[0]);
    int minGroupSize = Integer.parseInt(args[1]);

    Map<String, Set<String>> groups = new HashMap<>();
    try (Scanner s = new Scanner(dectionary)) {
        while(s.hasNext()) {
            String word = s.next();
            groups.computeIfAbsent(alphabetize(word),
                                   (unused) -> new TreeSet<>()).add(word);
        }
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    }

    for(Set<String> group : groups.values()) {
        if(group.size() >= minGroupSize) {
            System.out.println(group.size() + ": " + group);
        }
    }
}

private static String alphabetize(String s) {
    char[] a = s.toCharArray();
    Arrays.sort(a);
    return new String(a);
}
```

첫번째 단계를 보면 맵에 각 단어를 삽입할 때 자바 8에 추가된 computeIfAbsent 메서드를 사용했다. 이 메서드는 맵 안에 키가 있는지 찾은 다음 있으면 단순히 그 키에 매핑된 값을 반환한다. 없으면 건네진 함수 객체를 키에 적용하여 값을 계산해낸 다음 그 키와 값을 매핑해 놓고 계산된 값을 반환한다.

다음 예제는 위 코드와 같은 일을 하는 코드지만 스트림을 과하게 활용한다.

`스트림을 과하게 사용했다. - 따라하지 말 것!!!`

```js
public static void main(String[] args) throws IOException {
    File dectionary = new File(args[0]);
    int minGroupSize = Integer.parseInt(args[1]);

    try(Stream<String> words = Files.lines(dectionary.toPath())) {
        words.collect(
            groupingBy(word -> word.chars().sorted()
                       .collect(StringBuilder::new,
                                (sb, c) -> sb.append((char) c),
                                StringBuilder::append).toString()))
            .values().stream()
            .filter(group -> group.size() >= minGroupSize)
            .map(group -> group.size() + ": " + group)
            .forEach(System.out::println);
    }
}
```

스트림에 익숙한 프로그래머도 이해하기 어려운 코드이다. 이처럼 스트림을 과하게 사용하면 프로그램이 읽거나 유지보수하기 어려워진다. 다행히 절충 지점이 있다. 스트림을 적당히 사용한 예이다.

`스트림을 적절히 활용하면 깔끔하고 명료해진다`

```js
public static void main(String[] args) throws IOException {
    File dectionary = new File(args[0]);
    int minGroupSize = Integer.parseInt(args[1]);

    try(Stream<String> words = Files.lines(dectionary.toPath())) {
        words.collect(groupingBy(word -> alphabetize(word)))
            .values().stream()
            .filter(group -> group.size() >= minGroupSize)
            .forEach(group -> System.out.println(group.size() + ": " + group));
    }
}
```

try-with-resource 블록에서 사전 파일을 열고 파일의 모든 라인으로 구성된 스트림을 얻는다. 스트림 변수의 이름을 words로 지어 스트림 안의 각 원소가 단어임을 명확히 했다. 이 스트림의 파이프라인에는 중간 연산은 없으며 종단 연산에서는 모든 단어를 수집해 맵으로 모은다. 그 다음으로 이 맵의 values()가 반환하는 값으로부터 새로운 Stream<List< String >> 스트림을 연다. 이 스트림의 원소는 물론 아나그램 리스트다. 그 리스트들 중 원소가 minGroupSize보다 적은 것은 필터링돼 무시된다. 마지막으로 종단 연산인 forEach는 살아남은 리스트를 출력한다.

> 람다 매개변수의 이름은 주희해서 사용해서 한다. 람다에서는 타입이름을 자주 생략하므로 매개변수 이름을 잘 지어야 스트림 파이프라인의 가독성이 유지된다. 한편 단어의 철자를 알파벳순으로 정렬하는 일은 별도 메서드인 alphabetize에서 수행했다. 세부 구현을 주 프로그램 로직 밖으로 빼내 전체적인 가독성을 높인 것이다. 도우미 메서드를 적절히 활용하는 일의 중요성은 일반 반복코드에서보다는 스트림 파이프라인에서 훨씬 크다.

<br>

## _char용 스트림은 지원하지 않는다_

alphabetize도 스트림으로 지원할 수 있다. 그러나 명확성이 떨어지고 잘못 구현할 가능성이 커진다. 또한 느려질수도 있다. 이유는 자바가 char용 스트림은 지원하지 않기 때문이다. char 값을 출력하면 int 값이 반환되기 때문에 헷갈릴 수 있다. char 값들을 처리할 때는 스트림을 삼가하는 편이 낫다.

<br>

## _기존 코드는 더 나아 보일 때만 리팩토링하자_

기존 코드를 스트림으로 바꾸는게 가능할지라도 코드 가독성과 유지보수 측면에서는 손해를 볼 수 있다. 스트림과 반복문을 적절히 조합하는게 최선이다. 그러니 기존 코드는 스트림을 사용하도록 리팩터링하되 새 코드가 더 나아 보일 때만 반영하자.

<br>

## _코드 블록과 스트림을 구분하여 사용해야 하는 일들의 예_

### 코드 블록을 사용해야 하는 경우

- 코드 블록에서는 범위 안의 지역변수를 읽고 수정할 수 있다. 하지만 람다에서는 final이거나 사실상 final인 변수만 읽을 수 있고 지역 변수를 수정하는건 불가능하다.

- 코드 블록에서는 return 문을 사용해 메서드에서 빠져나가거나 break나 continue 문으로 블록 바깥의 반복문을 종료하거나 반복을 한번 건너뛸 수 있다. 또한 메서드 선언에 명시된 검사 예외를 던질 수 있다. 하지만 람다로 이 중 어떤 것도 할 수 없다.

계산 로직에서 이상들을 수행해야 한다면 스트림과는 맞지 않는 것이다.

### 스트림을 사용해야 하는 경우

- 원소들의 시퀀스를 일관되게 변환한다.

- 원소들의 시퀀스를 필터링한다.

- 원소들의 시퀀스를 하나의 연산을 사용해 결합한다(더하기, 연결하기, 최솟값 구하기 등)

- 원소들의 시퀀스를 컬렉션에 모은다(공통된 기준으로 묶어가며)

- 원소들의 시퀀스에서 특정 조건을 만족하는 원소를 찾는다.

<br>

## _스트림으로 처리하기 어려운 일_

한 데이터가 파리프라인의 여러 단계를 통과할 때 이 데이터의 각 단계에서의 값들에 동시에 접근하기는 어려운 경우다. 스트림 파이프라인은 일단 한 값을 다른 값에 매핑하고 나면 원래의 값은 잃는 구조이기 때문이다. 원래 값과 새로운 값의 쌍을 저장하는 객체를 사용해 매핑하는 우회 방법도 있지만 그리 만족스러운 해법은 아닐 것이다. 매핑 객체가 필여한 단계가 여러 곳이라면 특히 더 그렇다. 이런 방식은 코드 양도 많고 지저분하여 스트림을 쓰는 주목적에서 완전히 벗어난다.

예를 들어 20개의 메르센 소수(Mersenne prime)를 출력하는 프로그램을 작성해 보자. 메르센 수는 2의P승 -1 형태의 수다. 여기서 p가 소수이면 해당 메르센 수도 소수일 수 있는데 이때의 수를 메르센 소수라 한다. 다음 코드는 (무한) 스트림을 반환하는 메서드다. BigInteger의 정적 멤버들은 정적 임포트하여 사용한다고 가정한다.

```js
static Stream<BigInteger> primes() {
    return Stream.iterator(TWO, BigInteger::nextProbablePrime);
}
```

메서드 이름 primes는 스트림의 원소가 소수임을 말해준다. 스트림을 반환하는 메서드 이름은 이처럼 원소의 정체를 알려주는 복수 명사로 쓰기를 강력히 추천한다. 위 코드는 아직 종단 연산이 없기 때문에 실행하지 않는 메서드이다. 아래는 메르센 소수를 출력하는 프로그램이다.

```js
public static void main(String[] args) {
    primes().map(p -> TWO.pow(p.intValueExact().subtract(ONE)))
            .filter(mersenne -> mersenne.isProbablePrime(50))
            .limit(20)
            .forEach(System.out::println);
}
```

## _스트림과 반복 중 선택이 어려운 상황_

카드 덱을 초기화 하는 예를 들어보자. 카드는 숫자(rank)와 무늬(suit)를 묶는 불변 값 클래스이고 숫자와 무늬는 모두 열거 타입이라 하자. 이 작업은 두 집합의 원소들로 만들 수 있는 가능한 모든 조합을 계산하는 문제다. 이를 데카르트 곱이라고 부른다. 다음은 반복문 코드이다.

`데카르트 곱 계산을 반복 방식으로 구현`

```js
private static List<Card> newDeck() {
    List<Card> result = new ArrayList<>();
    for(Suit suit : Suit.values())
        for(Rank rank : Rank.values())
            result.add(new Card(suit, rank));
    return result;
}
```

다음은 스트림으로 구현한 코드이다

`데카르트 곱 걔산을 스트림 방식으로 구현`

```js
privte static List<Card> newDeck() {
    return Stream.of(Suit.values())
                 .flatmap(suit ->
                            Stream.of(rank.values()
                                .map(rank -> new Card(suit, rank))
                 .collect(toList());
}
```

어느 newDeck()이 더 종아보이는건 개인 취향과 프로그래밍 환경의 문제다. 이해하고 유지보수하기에는 처음 코드가 더 편한 프로그래머가 많겠지만 스트림 방식에 익숙한 프로그래머라면 두번째 방식이 더 편한 프로그래머도 있을 것이다. 스트림 방식이 더 나아보이고 동료들도 스트림 코드를 이해할 수 있고 선호한다면 스트림 방식을 사용하자.

<br>

## _Conclusion_

스트림을 사용해야 멋지게 처리할 수 있는 일이 있고 반복 방식이 더 알맞은 일도 있다. 그리고 수많은 작업이 이 둘을 조합했을 때 가장 멋지게 해결된다. 어느 쪽을 선택하는 확고부동한 규칙은 없지만 참고할 만한 지침 정도는 있다. 어느 쪽이 나은지가 확연히 드러나는 경우가 많겠지만 아니더라도 방법은 있다. 스트림과 반복 중 어느 쪽이 더 나은지 확신하기 어렵다면 둘 다 해보고 더 나은 쪽을 택하라.
