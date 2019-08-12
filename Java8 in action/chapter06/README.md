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

<h2>분할</h2>

- 분할은 분할함수라 불리는 프레디케이트를 분류 함수로 사용하는 특수한 그룹화 기능이다.
- 분할함수는 불린을 반환하므로 맵의 키 형식을 Boolean이다. 결과적으로 그룹화 맵은 최대(참 또는 거짓) 2개의 그룹으로 분류된다.
- 분할 함수 예이다.

    ```
    Map<Boolean, List<Dish>> partitionedMenu = 
        menu.stream().collect(partitioningBy(Dish::getVegetarian));
    
    //결과
    {false=[pork, beef, chicken, prawns, salmon],
    true=[french fries, rice, season fruit, pizza]}

    List<Dish> vegetarianDishes = partitionedMenu.get(true);
    ```
<h3>분할의 장점</h3>

- 분할함수가 반환하는 참, 거짓 두가지 요소의 스트림 리스트를 모두 유지한다는 것이 분할의 장점이다.
- 컬렉터를 두 번째 인수로 전달할 수 있는 오버로드된 버전도 있다.

    ```
    Map<Boolean, Map<Dish.Type, List<Dish>>> vegetarianDishesByType =
        menu.stream().collect(
            partitioningBy(Dish::isVegetarian,
                            groupingBy(Dish::getType)));
    
    //결과
    {false={FISH=[salmon],MEAT=[pork,beef]},
    true={OTHER=[french fries, rice, season fruit]}}
    ```
- 채식요리와 채식이 아닌 요리 각각의 그룹에서 가장 칼로리가 높은 요리를 찾아보자
    ```
    Map<Boolean, Map<Dish.Type, List<Dish>>> ex = menu.stream()
        .collect(
            partitioningBy(Dish::isVegetarian,
                collectingAndThen(
                    maxBy(comparingInt(Dish::getCalories)),
                    Optional::get)));
    
    //결과
    {false=pork, true=pizza}
    ```

<h2>Collector 인터페이스</h2>

- Collector 인터페이스는 리듀싱 연산(즉, 컬렉터)을 어떻게 구현할지 제공하는 메서드 집합으로 구성된다.
- 다음 코드는 Collector 인터페이스의 시그너처와 다섯 개의 메서드 정의를 보여준다.

    ```
    public interface Collector<T, A, R> {
        Supplier<A> supplier();
        BiConsumer<A, T> accumulator();
        Function<A, R> finisher();
        BinaryOperation<A> combine();
        Set<Characteristics> characteristics();
    }
    ```

    - T는 수집될 스트림 항목의 제네릭 형식이다.
    - A는 누적자, 즉 수집 과정에서 중간 결과를 누적하는 객체의 형식이다.
    - R은 수집 연산 결과 객체의 형식이다.
    - 예를 들어 Stream<T>의 모든 요소를 List<T>로 수집하는 ToListCollector<T>라는 클래스를 구현 할 수 있다.
        ```
        public class ToListCollector<T> implements 
                 Collector<T, List<T>, List<T>>
        ```

<h3>Collector 인터페이스의 메서드 살펴보기</h3>

> supplier 메서드 : 새로운 결과 컨테이너 만들기
- supplier 메서드는 빈 결과로 이루어진 Supplier를 반환해야 한다. 즉 파라미터가 없는 함수이다.

    ```
    public Supplier<List<T>> supplier() {
        return () -> new ArrayList<T>();
    }

    //생성자 레퍼런스로 전달하는 방법
    public Supplier<List<T>> supplier() {
        return ArrayList::new;
    }
    ```

> accumulator 메서드 : 결과 컨테이너에 요소 추가하기

- accumalator 메서드는 리듀싱 연산을 수행하는 함수를 반환한다. 스트림에서 n번째 요소를 탐색할 때 두 인수, 즉 누적자와 n번째 요소를 함수에 적용한다.
- 함수의 반환값은 void, 즉 요소를 탐색하면서 적용하는 함수에 의해 누적자 내부 상태가 바뀌므로 누적자가 어떤 값일지 단정할 수 없다.
    ```
    public BiConsumer<List<T>, T> accumulator() {
        return (list, item) -> list.add(item);
    }

    //메서드 레퍼런스
    public BiConsumer<List<T>, T> accumulator() {
        return List::add;
    }
    ```

> finisher 메서드 : 최종 변환값을 결과 컨테이너로 적용하기

- finisher 메서드는 스트림 탐색을 끝내고 누적자 객체를 최종 결과로 변환하면서 누적 과정을 끝낼 때 호출할 함수를 반환해야 한다.
- 누적객체가 이미 최종인 경우 항등함수를 반환한다.
    ```
    public Function<List<T>, List<T>> finisher() {
        return Function.identity();
    }
    ```

> combine 메서드 : 두 결과 컨테이너 병합

- combine는 스트림의 서로 다른 서브파트를 병렬로 처리할 때 누적자가 이 결과를 어떻게 처리할지 정의한다.
- 스트림의 두번째 서브파트에서 수집한 항목 리스트를 첫 번째 서브파트 결과 리스트의 뒤에 추가하면 된다.
    ```
    public BinaryOperation<List<T>> combine() {
        return (list1, list2) -> {
            list1.addAll(list2);
            return list1;
        }
    }
    ```

> Characteristics 메서드

- Characteristics 메서드는 컬렉터의 연산을 정의하는 Characteristics 형식의 불변 집합을 반환한다.
- Characteristics는 스트림을 병렬로 리듀스할 것인지 그리고 병렬로 리듀스한다면 어떤 최적화를 선택해야 할지 힌트를 제공한다.
- Characteristics는 다음 세 항목을 포함하는 열거형이다.
    - UNORDERED
        - 리듀싱 결과는 스트림의 요소의 방문 순서나 누적 순서에 영향을 받지 않는다.
    - CONCURRENT
        - 다중 스레드에서 accumulator 함수를 동시에 호출할 수 있으며 이 컬렉터는 스트림의 병렬 리듀싱을 수행할 수 있다.
    - IDENTITY_FINISH
        - finisher 메서드가 반환하는 함수는 단순히 identity를 적용할 뿐이므로 이를 생략할 수 있다. 따라서 리듀싱 과정의 최종 결과로 누적자 객체를 바로 사용할 수 있다.