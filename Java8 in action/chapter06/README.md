<h1>스트림으로 데이터 수집</h1>

<h2>6.1 컬렉터란 무엇인가?</h2>

> 6.1.1 고급 리듀싱 기능을 수행하는 컬렉터
- collect에서는 리듀싱 연산을 이용해서 스트림의 각 요소를 방문하며 컬렉터가 작업을 처리
- Collectors 유틸리티 클래스는 자주 사용하는 컬렉터 인스턴스를 손쉽게 생성할 수 있는 정적 팩토리 메서드를 제공한다. 예를 들어 toList()는 스트림의 모든 요소를 리스트로 수집한다.

> 6.1.2 미리 정의된 컬렉터
- Collectors에서 제공하는 메서드의 기능은 크게 세가지로 구분 할 수 있다.
    - 스트림 요소를 하나의 값으로 리듀스하고 요약
    - 요소 그룹화
    - 요소 분할

<h2>6.2 리듀싱과 요약</h2>

- 컬렉터로 스트림의 모든 항목을 하나의 결과로 합칠 수 있다.
```
long howManyDishes = menu.stream().collect(Collectors.counting());
// 아래와 같이 불필요 과정을 생략 할 수 있다.
long howManyDishes = menu.stream.counting();
```
- import static java.util.stream.Collectors.*;
- 위 처럼 콜렉터가 임포트 되어 있다는 가정하에 Collectors.counting()을 간단하게 count()으로 표현할 수 있다.

> 6.2.1 스트림값에서 최댓값과 최솟값 검색
- Collectors.maxBy, Collectors.minBy 두 개의 메서드를 이용해서 스트림의 최댓값과 최솟값을 계산할 수 있다.
- 아래 코드는 Comparator를 구현한 다음에 최댓값을 구하는 코드이다.
    ```
    Comparator<Dish> comparator = Comparator.comparing(Dish::getCalories);
    Optional<Dish> mostCalories = menu.stream.collect(maxBy(comparator));
    ```
- Optional은 값을 포함하거나 포함하지 않을 수 있는 컨테이너이다. 즉 반환되는 요리가 있을 수도 있고 없을 수도 있다.

> 6.2.2 요약 연산
- Collectors.summingInt는 객체를 int로 매핑하는 함수를 인수로 받는다.
    ```
    int totalCalories = menu.stream().collect(summingInt(Dish::getCalories));
    ```
- summingInt 외에 summingLong, summginDouble도 제공
- Collectors.averagingInt, averagingLong, averagingDouble 제공
    ```
    double avgCaloires = menu.stream().collect(averagingInt(Dish::getCalories));
    ```
- 위 연산 중 두개 이상의 연산을 한번에 수행해야 할 경우 summarizingInt가 있디. 요소 수, 합계, 평균, 최댓값, 최솟값 등을 한번에 계산한다.
    ```
    IntSummaryStatistics menuStatistics = 
        menu.stream().collect(summarizingInt(Dish::getCalories));
    
    //실행결과
    IntSummaryStatistics {count=9, sum=4300, min=120, max=800, average=477}
    ```
- summarizingLong summarizingDouble도 제공

> 6.2.3 문자열 연결
- 컬렉터에 joining 팩토리 메서드를 이용하면 스트림의 각 객체에 toString 메서드를 호출해서 추출한 모든 문자열을 하나의 문자열로 연결해서 반환한다.
    ```
    String shortMenu = menu.stream()
            .map(Dish::getName)
            .collect(joining(", "));
    
    //실행결과
    a, b, c, d, e
    ```

> 6.2.4 범용 리듀싱 요약 연산
- 지금까지 살펴본 모든 컬렉터는 reducing 팩토리 메서드로도 정의할 수 있다. 특화된 컬렉터들을 사용한 이유는 가독성과 편의성 때문이다.
- 아래코드는 리듀싱 예제이다.
    ```
    int totalCalories = menu.stream().collect(reducing(
                        0, Dish:getCalories, (i,j) -> j+j))
    ```
    - 첫번째 인수는 리듀싱 연산의 시작값이거나 스트림에 인수가 없을 경우 반환값
    - 변환 함수
    - 같은 종류의 두 항목을 하나의 값으로 더하는 BinaryOperator

- 다음 처럼 한개의 인수를 가진 reducing 연산도 가능하다.
    ```
    Optional<Dish> mostCaloriesDish = menu.stream()
        .collect(reducing(
            (d1,d2) -> d1.getCalories() > d2.getCalories() ? d1 : d2
        ));
    ```
    - 첫번째 요소를 시작값으로 받으며 자신을 그대로 반환하는 항등 함수를 두번째 인수로 받는 상황에 해당한다.
    - 시작값이 없어 빈값이 넘겨질 수 도 있기 때문에 Optional로 객체를 반환한다.

> 컬렉션 프레임워크 유연성 : 같은 연산도 다양한 방식으로 수행할 수 있다.
- reducing 컬렉터를 사용한 이전 예제에서 람다 표현식 대신 Integer 클래스의 sum 메서드 레퍼런스를 이용하여 코드를 더 단순화 할 수 있다.
    ```
    int totalCalories = menu.stream().collect(reducing(
        0, Dish::getCalories, Integer::sum
    ));
    ```
- 위 코드보다 IntStream으로 매핑하여 더 간단하게 할 수 있다.
    ```
    int totalCalories = menu.stream()
        .mapToInt(Dish::getCalories).sum()
    ```

<h2>6.3 그룹화</h2>

- Collectors.groupingBy를 이용해서 쉽게 그룹핑 할 수 있다.
    ```
    Map<Dish.Type, List<Dish>> dishsByType = 
        menu.stream().collect(groupingBy(Dish:getType));
    
    //결과
    {FISH=[prawns, salmon], OTHER=[french fries, rice, season fruit, 
    pizza], MEAT=[pork, beef, chicken]}
    ```
    - 스트림의 각 요리에서 Dish.Type과 일치하는 모든 요리를 추출하는 함수를 groupingBy 메서드로 전할했다.
    - 이 함수를 기준으로 스트림이 그룹화되므로 이를 분류함수라고 한다.
- 단순한 속성 접근자 대신 더 복잡한 분류 기준이 필요한 경우엔 메서드 레퍼런스로는 불가능하며 람다 표현식으로 필요한 로직을 구현 해야 한다.
    ```
    public enum CaloriesLevel {DIET, NORMAL, FAT}

    Map<CaloriesLevel, List<Dish>> dish = menu.stream()
        .collect(groupingBy(
            dish -> {
                if(dish.getCalories() <= 400) {
                    return CaloriesLevel.DIET;
                } else if(dish.getCalories <= 700) {
                    return CaloriesLevel.NORMAL;
                } else {
                    return CaloriesLevel.FAT;
                }
            }
        ));
    ```

> 6.3.1 다수준 그룹화
- 두 인수를 받는 팩토리 메서드 Collectors.groupingBy를 이용해서 항목을 다수준으로 그룹화할 수 있다. Collectors.groupingBy는 일반적인 분류함수와 컬렉터를 인수로 받는다. 즉 바깥족 groupingBy 메서드에 스트림의 항목을 분류할 두 번째 기준을 정의하는 내부 groupingBy를 전달해서 두 수준으로 스트림의 항목을 그룹화할 수 있다.
    ```
    Map<Dish.Type, Map<CaloriesLevel, List<Dish>>> dish = 
        menu.stream().collect(
            groupingBy(Dish:getType,
                groupingBy(dish -> {
                    if(dish.getCalories() <= 400) {
                        return CaloriesLevel.DIET;
                    } else if(dish.getCalories <= 700) {
                        return CaloriesLevel.NORMAL;
                    } else {
                        return CaloriesLevel.FAT;
                    }
                })
            )
        )
    
    //결과
    {MEAT=[DIET=[chicken]m NORMAL=[beef], FAT=[port]],
    FISH=[DIET=[prawns], NORMAL=[salmon]....]
    }
    ```
- 다수준 그룹화 연산은 다양한 수준으로 확장할 수 있다. 즉 n수준 그룹화의 결과는 n수준 트리 구조로 표현되는 n수준 맵이 된다.

> 6.3.2 서브그룹으로 데이터 수집
- 첫번째 groupginBy로 넘겨주는 컬렉터의 형식은 제한이 없다.
    ```
    Map<Dish.Type, Long> typeCount = menu.stream()
        .collect(groupingBy(Dish::getType), counting());

    //결과
    {MEAT=2, FISH=2, OTHER=4}
    ```
- 분류 함수 한개의 인수를 갖는 groupingBy(f)는 사실 groupingBy(f, toList())의 축약형이다.

> 컬렉터 결과를 다른 형식에 적용하기
- 팩토리 메서드 Collectors.collectingAndThen으로 컬렉터가 반환한 결과를 다른 형식으로 활용할 수 있다.
    ```
    Map<Dish.Type, Dish> mostCaloriesByType = menu.stream()
        .collect(groupingBy(Dish::getType,
            collectingAndThen(
                maxBy(comparingInt(Dish::getCalories)), Optional::get
            )
        ));
    
    //결과
    {FISH=salmon, OTHER=pizza, MEAT=pork}
    ```
    - 팩토리 메서드 collectingAndThen은 적용할 컬렉터와 변환 함수를 인수로 받아 다른 컬렉터를 반환한다.
    - 반환되는 컬렉터는 기존 컬렉터의 래퍼 역할을 하며 collect의 마지막 과정에서 변환 함수로 자신이 반환하는 값을 매핑한다.
    - 이미 언급했듯이 리듀싱 컬렉터는 절대 Optional.empty()를 반환하지 않으므로 안전한 코드다.