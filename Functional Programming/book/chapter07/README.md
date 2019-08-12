<h1>실용적 사고</h1>
<h2>자바 8</h2>

```
// BEGIN java8_process
public String cleanNames(List<String> names) {
    if (names == null) return "";
    return names
            .stream()
            .filter(name -> name.length() > 1)
            .map(name -> capitalize(name))
            .collect(Collectors.joining(","));
}

private String capitalize(String e) {
    return e.substring(0, 1).toUpperCase() + e.substring(1, e.length());
}
```
- collect(), forEach()와 같이 출력을 하는 함수를 최종연산 함수라고 부른다.
- 최종연산 함수를 호출할 때까지 다른 함수들을 호출하여 파이프라인을 구성할 수 있다.
- collect를 활용해 요소들을 결합하여 결과를 낸다. 물론 reduce()를 사용할 수도 있겠지만 StringBuilder와 같은 가변구조에 효과적으로 쓸 수 있는 collect()가 적합하다.

```
Predicate<String> p = (name) -> name.startWidth("Mr");
List<String> l = List.of("Mr Rogers", "Ms Robinson", "Mr Ed");
l.stream.filter(p).forEach(i -> System.out.println(i));
```
- 위 예제에서는 필터하는 람다 블록을 사용하여 술어를 생성
- 셋째줄에서 filter()를 호출할 때 이 술어를 매개변수로 주면 된다.

> 함수형 인터페이스
- Runnable, Callable 같이 메서드를 하나만 가지는 인터페이스를 단일 추상 메서드 인터페이스라고 한다.
- 하나의 함수형 인터페이스는 하나의 단일 추상 메서드를 포함하며 여러개의 디폴트 메서드도 함께 포함할 수 있다. 

```
List<String> n = List.of(1,4,45,5,6,9,101);
Comparator<Integer> c1 = (x, y) -> x - y;
Comnarator<Integer> c2 = c1.reversed();
System.out.println("Smallest = " + n.stream().min(c1).get());
System.out.println("Largest = " + n.stream().max(c2).get());
```
- 람다 블록을 감싸는 Comparator를 생성하고 reversed() 디폴드 메서드를 호출할 수 있다.
- 디폴트 메서드를 덧붙일 수 있는 기능은 흔히 사용되는 믹신과 유사하다.
- 믹신이란 다른 클래스에서 사용될 메서드를 정의하지만 그 클래스의 상속 체계에 포함되지 않은 클래스를 지칭. 언어마다 구현이나 사용법은 다르지만 공통적으로 코드의 재사용성을 권장하고 다중상속의 모호함을 해결해준다.

> 옵셔널
- Optional은 오류로서의 null과 리턴 값으로서의 null을 혼용하는 것을 방지
- ifPresent() 메서드를 사용하여 제대로 된 리턴 값에만 코드 블록을 실행
    ````
    n.stream()
      .min((x,y) -> x -y)
      .ifPresent(z -> System.out.println("Smallest is" + z));      
    ````
- 또 하나의 메서드 orElse()는 다른 조치를 추가로 취하고 싶을 때 사용

> 자바 8 스트림
- 스트림이란 추상화를 포함한다.
- 스트림과 컬렉션은 비슷하지만 다음과 같은 중요한 차이점이 있다.
    - 스트림은 값을 저장하지 않으며 종결 작업을 통해 입력에서 종착점까지 흐르는 파이프라인처럼 사용
    - 스트림은 상태를 유지하지 않는 함수형으로 설계되어있다.
    - 스트림 작업은 최대한 게으르게 한다.
    - 무한 스트림이 가능하다. 모든 정수를 리턴하는 스트림을 만들어 limit(), findfirst()같은 메서드를 사용하고 그 부분집합을 구할 수 있다.
    - Iterator 인스턴스처럼 스트림은 사용과 동시에 소멸되고 재사용 전에 다시 생성해야 한다.
- 스트림 작업은 중간 작업 또는 종결 작업이다. 중간 작업은 새 스트림을 리턴하고 항상 게으르다. 종결작업은 스트림을 순회하여 값이나 부수효과를 낳는다.

<h2>함수형 인프라스트럭처</h2>

> 아키텍처
- 함수형 아키텍처는 불변성이 그 중심에 있고 이를 최대한 사용하려 한다. 함수형 프로그래머처럼 사고하려면 불변성을 받아들이는 것이 중요하다.
- 변이가 많을 수록 테스트가 많이 필요하게 된다.
- 변이를 엄격하게 제한해서 변이점들을 고립시키면 오류가 발생할 장소가 적어지고 결국 테스트 할 곳이 줄어든다.
- 자바 클래스를 불변형으로 만들려면 반드시 다음과 같이 해야 한다.
    - 모든 필드를 final로 선언한다.
        - final로 선언된 필드들은 선언 시나 생성자 내부에서 초기화 해야한다.
    - 클래스를 final로 선언해서 오버라이드를 방지해라.
        - 클래스가 오버라이드 되면 그 메서드들도 오버라이드 될 수 있다. 가장 좋은 방법은 하위 클래스를 금지하는 것이다.
    - 인수가 없는 생성자를 제공하지 말라
        - 불변형 객체의 모든 상태는 생성자가 정해야 한다.
    - 적어도 하나의 생성자를 제공하라.
    - 생성자 외엔느 변이 메서드를 제공하지 말라.
        - 자바빈스 식으로 set 메서드를 제공하지 않아야 하는 것은 물론이고 가변 객체의 참조를 리턴하지 않게 조심해야 한다.
        - 객체 참조가 final이라고 해서 그 참조가 지정하는 것이 변이되지 말라는 보장은 없다.