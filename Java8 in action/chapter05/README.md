<h1>스트림 활용</h1>
<h2>필터링과 슬라이싱</h2>

> 프레디케이트로 필터링
- filter메서드는 프레디케이트를 인수로 받아서 프레디케이트와 일치하는 모든 요소를 포함하는 스트림을 반환
> 고유 요소 필터링
- distinct라는 메서드는 고유요소로 이루어진 스트림을 반환하는 메서드
- 즉, 중복제거 메서드
> 스트림 축소
- limit(n) 메서드는 주어진 사이즈 이하의 크기를 갖는 새로운 스트림을 반환하는 메서드
- 스트림이 정렬되어 있으면 최대 n개의 요소를 반환할 수 있다.
> 요소 건너뛰기
- skip(n) 메서드는 처음 n개 요소를 제외한 스트림을 반환하는 메서드
- n개 이하의 요소를 포함하는 스트림에 skip(n)을 호출하면 빈 스트릠이 반환

<h2>매핑</h2>

>스트림의 각 요소에 함수 적용하기
- 스트림은 함수를 인수로 받는 map메서드를 지원한다. 인수로 제공된 함수는 각 요소에 적용되며 함수를 적용한 결과가 새로운 요소로 매핑된다.
- 각 단어가 포함하는 글자 수의 리스트를 반환하는 예제
    ```
    List<String> word = Arrays.asList("java8", "Lambdas", "In", "action");
    List<Integer> wordLength = word.stream()
                                    .map(String::length)
                                    .collect(toList()); 
    ```
> 스트림의 평면화
- 고유문자로 이루어진 리스트 반환 예제
    ```
    //["Hello","World"] 리스트 존재
    //["H","e","l","o","W","r","d"]가 반환되어야 함
    
    ```
- map과 Array.stream 활용
    - 우선 배열 스트림 대신 문자열 스트림이 필요하며 문자열을 받아 스트림을 만드는 Arrays.stream() 메서드가 존재

- flatMap 사용
    - flatMap은 각 배열을 스트림이 아니라 스트림의 콘텐츠로 매핑. 즉, 평면화된 스트림을 반환
    - 스트림의 각 값을 다른 스트림으로 만든 다음에 모든 스트림을 하나의 스트림으로 연결하는 기능 수행

<h2>검색과 매칭</h2>
> 프레디케이트가 적어도 한 요소와 일치하는지 확인

- 프레디케이트가 주어진 스트림에서 적어도 한 요소와 일치하는지 확인할 때 anyMatch 메서드를 이용
- anyMatch는 불린을 반환하므로 최종 연산이다.
    ```
    if(menu.stream().anyMatch(Dish::isVegetarian)) {
        System.out.println("The menu is (somewhat) vegetarian friendly!!");
    }
    ```
> 프레디케이트가 모든 요소와 일치하는지 검사
- allMatch 메서드는 anyMatch와 달리 스트림의 모든 요소가 주어진 프레디케이트와 일치하는지 검사
    ```
    boolean isHealthy = menu.stream()
                            .allMatch(d -> d.getCalories() < 1000);
    ```
- noneMatch
- noneMatch는 allMatch와 반대 연산을 수행. 즉 주어진 프레디케이트와 일치하는 요소가 없는지 확인
    ```
    boolean isHealthy = menu.stream()
                            .noneMatch(d -> d.getCalories() >= 1000);
    ```
- 위 세가지 anyMatch, allMatch, noneMatch는 스트림 쇼트서킷 기법, 즉 자바의 &&,||와 같은 연산을 활용

> 쇼트서킷 평가
- 표현식에서 하나라도 거짓이라는 결과가 나오면 나머지 표현식의 결과와 상관없이 전체 결과도 거짓이 된다. 이러한 상황을 쇼트서킷이라고 한다.
- allMatch, noneMatch, findFirst, findAny 등의 연산은 모든 스트림의 요소를 처리하지 않고도 결과를 반환할 수 있다. 원하는 요소를 찾으면 그 즉시 반환할 수 있다.
- limit도 쇼트서킷 연산이다.

> 요소 검색
- findAny 메서드는 현재 스트림에서 임의의 요소를 반환
    ```
    Otional<Dish> dish = menu.stream()
                             .filter(Dish::isVegetarian)
                             .findAny();
    ```
- Optional이란?
    - Optional<T> 클래스는 값의 존재나 부재 여부를 표현하는 컨테이너 클래스
    - 값이 존재하는지 확인하고 값이 없을 때 어떻게 처리 할 것인지 강제하는 기능 제공
    - isPresent() : true, false 리턴
    - ifPresent(Consumer<T> block) : 값이 있으면 주어진 블록 실행
    - T get() : 값이 존재하면 값 반환, 없으면 예외발생
    - T orElse(T other) : 값이 존재하면 값 반환, 없으면 기본값 반환
    ```
    menu.stream()
        .filter(Dish::isVegetarian)
        .findAny()
        .ifPresent(d -> System.out.println(d.getName()));
    ```
> 첫번째 요소 찾기
- 숫자리스트에서 3으로 나누어 떨어지는 첫번째 제곱값을 반환 하는 예제
    ```
    List<Integer> number = Arrays.asList(1,2,3,4,5);
    Optional<Integer> firstSquareDivisibleByThree = 
        number.stream()
              .filter(number -> number % 3 == 0)
              .map(w -> w*w)
              .findFirst
    ```
- findFirst와 findAny는 병렬성 때 사용. 요소의 반환 순서가 상관없다면 병렬 스트림에서는 제약이 적은 findAny를 사용

<h2>리듀싱</h2>

- 메뉴의 모든 칼로리의 합계를 구하시오 와 같은 질의는 반복적으로 처리해야 한다. 이런 질의를 리듀싱 연산이라고 한다.
- 함수형 프로그래밍 언어 용어로는 이 과정이 마치 종이를 작은 조각이 될 때까지 반복해서 접는 것과 비슷하다는 의미로 폴드라고 한다.

> 요소의 합
- reduce를 이용하면 반복된 패턴을 추상화 할 수 있다.
    ```
    int sum = numbers.stream().reduce(0, (a,b) -> a + b);
    ```
- reduce는 두개의 인수를 갖는다.
    - 초깃값 0
    - 두 요소를 조합해서 새로운 값을 만드는 BinaryOperation<T>.
- 자바 8에서는 Interger클래스에 두 숫자를 더하는 정적 sum메서드를 제공한다.
    ```
    int sum = numbers.stream().reduce(0, Integer::sum);
    ```
- 초깃값 없음
    - 초깃값을 받지 않도록 오버로드된 reduce도 있다. 그러나 이 reduce는 Optional 객체를 반환한다.
        ```
        Optional<Integer> sum = menu.stream().reduce((a,b)->(a+b));
        ```
    - 왜 Optional<Integer>를 반환하는 것일까? 스트림에 아무요소도 없는 상황이라면 초깃값이 없으므로 reduce는 합계를 반환할 수 없다. 따라서 합계가 없음을 가리킬 수 있도록 Optional 객체로 감싼 결과를 반환
> 최댓값과 최솟값
- 최댓값과 최솟값을 찾을 때도 reduce를 활용 가능
    ```
    Optional<Integer> max = menu.stream().reduce(Integer::max);
    Optional<Integer> min = menu.stream().reduce(Integer::min);
    ```
> reduce 메서드의 장점과 병렬화
- reduce를 이용하면 내부 반복이 추상화되면서 내부 구현에서 병렬로 reduce를 실행 할 수 있게 된다. 반복적인 합계에서는 sum 변수를 공유해야 하므로 쉽게 병렬화 하기 어렵다.
- 스트림의 모든 요소를 더하는 코드를 병렬로 만들려면 stream()을 parallelStream()으로 바꾸면 된다.
> 스트림 연산 : 상태 없음과 상태 있음
- 자바 8의 연산은 내부적인 상태를 고려해야 한다.
- map, filter 등의 입력 스트림에서 각 요소를 받아 0 또는 결과를 출력 스트림으로 보낸다. 따라서 이들은 보통상태가 없는 내부상태를 갖지 않는 연산이다.
- 하지만 reduce, sum, max 같은 연산은 결과를 누적할 내부상태가 필요하다.
- sorted나 distinct 같은 연산도 filter나 map처럼 상태없음으로 보일 수 있으나 다르다. 스트림의 요소를 정렬하거나 중복을 제거하려면 과거의 이력을 알고 있어야 하지 때문에 내부상태를 갖는 연산으로 간주 할 수 있다.

