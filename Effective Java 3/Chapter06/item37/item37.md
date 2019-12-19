# ordinal 인덱싱 대신 EnumMap을 사용하라

## _ordinal()을 배열 인덱스로 사용하지 말라!_

이따금 배열이나 리스트에서 원소를 꺼낼 때 ordinal 메서드로 인덱스를 얻는 코드가 있다. 아래 코드로 예를 들어보자.

```js
class Plant {
    enum LifeCycle{ ANNUAL, PERENNIAL, BIENNIAL }

    final String name;
    final LifeCycle lifeCycle;

    Plant(String name, LifeCycle lifeCycle) {
        this.name = name;
        this.lifeCycle = lifeCycle;
    }

    @Override public String toString() {
        return name;
    }
```

위 코드를 활용하여 3개의 생애주기(한해, 여러해, 두해) 별로 묶어 보자.

`절대 따라하지 말것`

```js
Set<Plant>[] plantsByLifeCycle =
    (Set<Plant>[]) new set[Plant.LifeCycle.values().length];

int i = 0;
for(Plant p : garden) {
    plantsByLifeCycle[i++] = new HashSet<>();
    plantsByLifeCycle[p.lifeCycle.ordinal()].add(p);
}
```

동작은 하지만 문제가 아주 많다. 배열은 제네릭과 호환되지 않으니 비검사 형변환을 수행해야 하고 깔끔히 컴파일되지 않을 것이다. 가장 심각한 문제는 정확한 정숫값을 사용한다는 것을 직접 보증해야 한다. 정수는 열거 타입과 달리 타입 안전하지 않기 때문이다. 잘못된 값을 사용하면 잘못된 동작이 수행되거나 ArrayIndexOutofBoundExceptoin을 던질 것이다.

<br>

## _EnumMap을 사용해 데이터와 열거 타입을 매팽_

열거 타입을 키로 사용하도록 설계한 아주 빠른 Map 구현체가 바로 EnumMap이다. 위 orinal()로 설계한 코드를 EnumMap을 사용하여 수정했다.

`EnumMap을 사용해 데이터와 열거 타입을 매핑한다.`

```js
List < Plant > garden = List.of(
  new Plant("복숭아", Plant.LifeCycle.ANNUAL),
  new Plant("개나리", Plant.LifeCycle.PERENNIAL),
  new Plant("장미", Plant.LifeCycle.BIENNIAL)
);
```

```js
Map<Plant.LifeCycle, Set<Plant>> plantByLifeCycle =
    new EnumMap<>(Plant.LifeCycle.class);

for(Plant.LifeCycle lc : Plant.LifeCycle.values()) {
    plantByLifeCycle.put(lc, new HashSet<>());
}
for(Plant p : garden) {
    plantsByLifeCycle.get(p.lifeCycle).add(p);
}
System.out.println(plantByLifeCycle);

// 결과값 {ANNUAL=[복숭아], PERENNIAL=[개나리], BIENNIAL=[장미]}
```

더 짧고 명료하고 안전하고 성능도 원래 버전과 비등하다. 안전하지 않은 형변환은 쓰지 않고 맵의 키인 열거타입이 그 자체로 출력용 문자열을 제공하니 출력 결과에 직접 레이블을 달 일도 없다. EnumMap의 성능이 ordinal을 쓴 배열에 비견되는 이유는 그 내부에서 배열을 사용하기 때문이다. 내부 구현 방식을 안으로 숨겨서 Map의 타입 안전성과 배열의 성능을 모두 얻어낸 것이다.
여기서 EnumMap의 생성자가 받는 키 타입의 Class 객체는 한정적 타입 토큰으로 런타임 제네릭 타입 정보를 제공한다.

<br>

## _Stream을 활용한 값 매핑_

스트림을 사용해 맵을 관리하면 코드를 더 줄일 수 있다.

`스트림을 사용한 코드1 - EnumMap을 사용하지 않는다!`

```js
System.out.println(Arrays.Stream(garden)
                         .collect(groupingBy(p -> p.lifeCycle)));

// 결과값 {PERENNIAL=[개나리], ANNUAL=[복숭아], BIENNIAL=[장미]}
```

이 코드는 EnumMap이 아닌 고유한 맵 구현체를 사용했기 때문에 EnumMap을 써서 얻은 공간과 성능 이점이 사라진다는 문제가 있다. 이 문제를 조금 더 구체적으로 보자. 매개변수 3개 짜리 Collectors.groupingBy 메서드는 mapFactory 매개변수에 원하는 맵 구현체를 명시해 호출할 수 있다.

`스트림을 사용한 코드2 - EnumMap을 이용해 데이터와 열거 타입을 매핑했다.`

```js
System.out.println(garden.stream()
                         .collect(Collectors.groupingBy(
                                 p -> p.lifeCycle
                                ,() -> new EnumMap<>(LifeCycle.class)
                                , Collectors.toSet())));

// 결과값 {ANNUAL=[복숭아], PERENNIAL=[개나리], BIENNIAL=[장미]}
```

단순한 프로그램이라면 이와 같은 최적화가 필요 없겠지만 맵을 빈번히 사용하는 프로그램에서는 꼭 필요할 것이다.

스트림을 사용하면 EnumMap만 사용했을 때와는 살짝 다르게 동작한다. EnumMap 버전은 언제나 식물의 생애주기당 하나씩의 중첩 맵을 만들지만 스트림 버전에서는 해당 생애주기에 속하는 식물이 있을 때만 만든다.

<br>

## _다차원 관계 EnumMap_

다음 예는 두가지 상태(Phase)를 전이(Transition)와 매핑하도록 구현한 프로그램이다. 예컨대 액체(LIQUID)에서 고체(SOLID)로의 전이는 응고(FREEZE)가 되고 액체에서 기체(GAS)로의 전이는 기화(BOIL)가 된다.

`EmnumMap으로 데이터와 열거 타입 쌍을 연결했다.`

```js
public enum Phase {
    SOLID, LIQUID, GAS;

    public enum Transition {
         MELT(SOLID,LIQUID), FREEZE(LIQUID,SOLID)
        ,BOIL(LIQUID,GAS), CONDENSE(GAS,LIQUID)
        ,SUBLIME(SOLID,GAS), DEPOSIT(GAS,SOLID);

    	private final Phase from;
        private final Phase to;

        Transition(Phase from, Phase to) {
            this.from = from;
            this.to = to;
        }

        // 상전이 맵을 초기화 한다.
        private static final Map<Phase, Map<Phase, Transition>>
        	m = Stream.of(values())
        			  .collect(Collectors.groupingBy(
        					   t -> t.from
        					  ,() -> new EnumMap<>(Phase.class)
        					  ,Collectors.toMap(
                                     t -> t.to
                                    ,t -> t
                                    ,(x,y) -> y
                                    ,() ->  new EnumMap<>(Phase.class))));

        public static Transition from (Phase from, Phase to) {
        	return m.get(from).get(to);
        }
    }
}
```

이 맵의 타입인 Map<Phase, Map<Phase,Transition>>은 이전 상태에서 이후 상태에서 전이로의 맵에 대응시키는 맵이라는 뜻이다. 이러한 맵을 초기화하기 위해 수집기(Collectors, toMap)를 2번 사용했다. 첫번째 Collectors는 groupingBy를 이용해 전이를 이전 상태를 기준으로 묶었다. 두번째 toMap에서는 이후 상태를 전이에 대응시키는 EnumMap을 생성한다. (x,y) -> y는 선언만 하고 실제로는 쓰이지 않는데 이는 단지 EnumMap을 얻으려면 맵 팩터리가 필요하고 수집기들은 점층적 팩터리를 제공하기 때문이다.

<br>

## _다차원 관계 EnumMap에 새로운 상태 추가하기_

새로운 상태인 PLASMA를 추가해보자. 이 상태와 연결된 전이는 2개(IONIZE, DEIONIZE)이다. 전이 목록에 IONIZE(GAS,PLASMA), DEIONIZE(PLASMA,GAS)를 추가만 하면 끝이다.

`EnumMap 버전에 새로운 상태 추가하기`

```js
public enum Phase {
    SOLID, LIQUID, GAS, PLASMA;

    public enum Transition {
         MELT(SOLID,LIQUID), FREEZE(LIQUID,SOLID)
        ,BOIL(LIQUID,GAS), CONDENSE(GAS,LIQUID)
        ,SUBLIME(SOLID,GAS), DEPOSIT(GAS,SOLID)
        ,IONIZE(GAS,PLASMA), DEIONIZE(PLASMA,GAS);
    }

    ..이후 동일
}
```

나머지는 기존 로직에서 잘 처리해주어 잘못 수정할 가능성이 극히 적다. 실제 내부에서는 맵들의 맵이 배열들의 배열로 구현되니 낭비되는 공간과 시간도 거의 없이 명확하고 안전하고 유지보수하지 좋다.

<br>

## _Conclusion_

배열의 인덱스를 얻기 위해 ordinal을 쓰는 것은 일반적으로 좋지 않으니 대신 EnumMap을 사용하라. 다차원 관계는 EnumMap<..., EnumMap<...>>으로 표현하라. 웬만해선 Enum.orinal()을 사용하지 말라.

<br>

## _그렇다면 ordinal()은 언제 사용하는 것일까?_
