<h1>전진하라</h1>
<h2>함수형 언어의 디자인패턴</h2>

- 함수형 프로그래밍에서는 전통적인 디자인 패턴들이 세가지로 분류된다.
    - 패턴이 언어에 흡수된다.
    - 패턴 해법이 함수형 패러다임에도 존재하지만 구체적인 구현 방식은 다르다.
    - 해법이 다른 언어나 패러다임에 없는 기능으로 구현된다.

<h2>함수 수준의 재사용</h2>

- 합성이란 주어진 매개변수와 일급 함수들의 형태로 이루어진 방식
- 합성은 함수형 프로그래밍 라이브러리에서 재사용의 방식으로 자주 사용
- 함수형 언어들은 객체지향 언어들보다 더 큰 단위로 재사용을 한다.
    - 매개변수로 커스터마이즈되는 공통된 작업들을 추출
- 기존 디자인 패턴을 통한 재사용은 궁극적으로 작은 단위의 재사용이다.
    - 패턴이 그 문제에만 적용되기 때문에 그 사용범위가 좁다.
- 함수형 프로그래밍은 구조물들간의 일련의 관계를 만들기 보다는 큰 단위의 재사용 매커니즘을 추출하려 한다.
- 궁극적으로 디자인 패턴의 존재 목적은 언어의 결함을 메꾸기 위함일 뿐이다.
    - 하지만 패턴이 필요한 경우도 존재한다. ex) 커맨드 디자인패턴의 실행취소

> 템플릿 메서드
- 일급함수를 사용하면 불필요한 구조물들을 없앨 수 있기 때문에 템플릿 메서드 패턴을 사용하기 쉬워진다.
- 템플릿 메서드는 하나의 알고리즘의 뼈대만 정의하고 세부절자는 오버라이딩 하도록 한다.
    ```
    //템플릿 메서드 표준구현
    abstract class Customer {
        def plan

        def Customer() {
            plan = []
        }

        def abstract checkCredit()
        def abstract checkInventory()
        def abstract ship()

        def process() {
            checkCredit()
            checkInventory()
            ship()
        }
    }
    ````
    ````
    //일급 함수를 사용한 템플릿 메서드
    class CustomerBlocks {
        def plan, checkCredit, checkInventory, ship

        def CustomerBlocks() {
            plan = []
        }

        def process() {
            checkCredit()
            checkInventory()
            ship()
        }
    }
    ````
    - 전통적인 템플릿 메서드는 하위 클래스가 추상 클래스에서 정해준 메서드를 구현해야 한다
    - 추상 메서드의 정의는 하위 클래스를 구현하는 개발자에게 알려주는 일종의 문서 역할
    - 좀 더 유동성이 요구되는 상황에서는 고정화된 메서드 선언이 적절하지 않을 수도 있다.

    ````
    //코드 블록을 호출 전에 보호하기
    class CustomerBlocksWithProtection {
        def plan, checkCredit, checkInventory, ship

        def CustomerBlocksWithProtection() {
            plan = []
        }
    // BEGIN groovy_customer_blocks
    def process() {
    checkCredit?.call()
    checkInventory?.call()
    ship?.call()
    }
    // END groovy_customer_blocks
    }
    ````
    - `?.`같은 문법적 설탕 덕분에 길게 열거된 if블록 등은 언어에 양도할 수 있고 위 구현해야 하는 함수들을 그냥 비워 둘 수도 있다.

    > 전략
    - 전략 패턴은 각자 캡슐화되어 서로 교환 가능한 알고리즘 군을 정의
    - 클라이언트에 상관없이 알고리즘을 바꿔서 사용할 수 있게 해주는 패턴
    ```
    // BEGIN groovy_calc
    interface Calc {
    def product(n, m)
    }

    class CalcMult implements Calc {
    def product(n, m) { n * m }
    }

    class CalcAdds implements Calc {
    def product(n, m) {
        def result = 0
        n.times {
        result += m
        }
        result
    }
    }
    ```
    - 두 수의 곱을 인터페이스로 정의
    - 곱셈과 덧셈을 클래스로 정의
    - 전략패턴 테스트
        ````
        def listOfStrategies = [new CalcMult(), new CalcAdds()]

        @Test
        public void product_verifier() {
            listOfStrategies.each { s ->
            assertEquals(10, s.product(5, 2))
            }
        }
        ````
        - 두 전략 모두 같은 값을 리턴
        - 코드블록을 일급함수로 사용하여 보일러플레이트 코드의 대부분을 제거
    - 전통적인 방법을 사용하면 제약이 따르지만 그런 제약을 더해서 안정성을 향샹할 수도 있다.
    
    > 플라이웨이트 디자인 패턴과 메모이제이션
    - 플라이웨이트 패턴은 많은 수의 조밀한 객체의 참조들을 공유하는 최적화 기법
    - 같은 자료형의 모든 객체를 대표하는 하나의 객체, 즉 표준 객체라는 아이디어 사용

    > 팩토리와 커링
    - 디자인 패턴 차원에서 보면 커링은 함수의 팩토리처럼 사용된다.
    - 함수형 프로그래밍 언어에서 보편적인 기능은 함수를 여느 자료구조처럼 사용할 수 있게 해주는 일급함수이며 이 덕분에 주어진 조건에 따라 다른 함수들을 리턴하는 함수를 만들수 있다. 이것이 팩토리의 본질이다.

    ```
    //함수 팩토리로 사용되는 커링
    def adder = { x, y -> x + y}
    def incrementer = adder.curry(1)

    println "increment 7: ${incrementer(7)}" // 8
    ```
    - 매개변수를 1로 커링하여 변수하나만 받는 함수를 리턴
    - 이는 실질적으로 함수 팩토리를 만든 셈이다.

```
object CurryTest extends App {

  def filter(xs: List[Int], p: Int => Boolean): List[Int] =
    if (xs.isEmpty) xs
    else if (p(xs.head)) xs.head :: filter(xs.tail, p)
    else filter(xs.tail, p)

  def dividesBy(n: Int)(x: Int) = ((x % n) == 0) // <1>

  val nums = List(1, 2, 3, 4, 5, 6, 7, 8)
  println(filter(nums, dividesBy(2)))  // <2>
  println(filter(nums, dividesBy(3)))
}
```
- <1> : 커링할 함수를 정의
- <2> : filter는 컬렉션과 일인수 함수를 매개변수로 받는다.
- 위 예제에서는 함수형 프로그래밍의 두가지 패턴형태를 보여준다.
    - 커링이 언어나 런타임에 내장되어 있기 때문에 함수 팩토리의 개념이 이미 녹아들어 있어 다른 구조물이 필요없다.
    - 다양한 구현 방법에 대한 중요성을 보여준다.
- 일반적 함수에서 특정한 함수를 만들 때는 커링을 사용하라.

<h2>구조형 재사용과 함수형 재사용</h2>

- 함수형 프로그래밍 : 구조물들을 연결하기 보다는 부분들로 구성하여 움직이는 부분을 최소화
- 객체지향 프로그래밍 : 캡슐화와 상태 조작을 쉽게 하는 것. 즉, 움직이는 부분인 클래스와 클래스간의 상호 관계를 주로 사용
