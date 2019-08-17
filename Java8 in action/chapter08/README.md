<h1>리팩토링, 테스팅, 디버깅</h1>

- 람다표현식을 이용해서 가독성과 유연성을 높이려면 어떻게 리펙토링해야 해야 하는가?
- strategy, template method, observer, chain of responesibility, factory 등의 객체지향 디자인 패턴을 어떻게 간소화 할건인가?
- 람다표현식과 스트림 API를 사용하는 코드를 테스트하고 디버깅 하는 방법은?

<h2>8.1 가독성과 유연성을 개선하는 리팩토링</h2>

- 람다표현식은 동작 파라미터화의 형식을 지원하므로 코드의 더 큰 유연성을 갖출 수 있다. 즉 람다표현식을 이용한 코드는 다양한 요구사항 변화에 대을 할수 있도록 동작파라미터화한다.

<h3>8.1.1 코드 가독성 개선</h3>

- 자바 8에서는 코드 가독성에 도움을 주는 기능을 새롭게 제공
    - 코드의 장황함을 줄여 쉽게 이해 할 수 있는 코드를 구현
    - 메서드 레퍼런스와 스트림 API를 이용해서 코드의 의도를 쉽게 표현 가능
- 세가지 리텍토링 예제를 소개
    - 익명 클래스를 람다 표현식으로 리팩토링
    - 람다 표현식을 메서드 레퍼런스로 리팩토링
    - 명령형 데이터 처리를 스트림으로 리팩토링

<h3>8.1.2 익명 클래스를 람다 표현식으로 리팩토링하기</h3>

- 익명 클래스 코드는 코드를 장황하게 만들고 쉽게 에러를 일으킬 수 있기 때문에 람다표현식을 이용해 가독성이 좋은 코드를 구현할 수 있디.
    ```
    //익명 클래스를 사용한 이전 코드
    Runnable r1 = new Runnable() {
        public void run() {
            System.out.println("Hello");
        }
    };

    //람다 표현식 코드
    Runnable r2 = () -> System.out.println("Hello");
    ```

- 하지만 모든 익명 클래스를 람다 표현식으로 변환할 수 있는 것은 아니다.
    - 첫째, 익명 클래스에서 사용한 this와 super는 람다 표현식에서 다른 의미를 갖는다.
        - 익명 클래스에서 this는 익명 클래스 자신을 가리키지만 람다에서 this는 람다를 감싸는 클래스를 가리킨다.
    - 둘째, 익명 클래스는 감싸고 있는 클래스의 변수를 가릴 수 있다.
        - 람다 표현식으로는 변수를 가릴 수 없다.
            ```
            int a =10;
            Runnable r1 = () -> {
                int a = 2;
                System.out.println(a);
            }

            //컴파일 에러

            Runnable r2 = new Runnable() {
                public void run() {
                    int a = 2;
                    System.out.println(a);
                }
            }

            //컴파일 성공
            ```
    - 셋째, 익명 클래스를 람다 표현식으로 바꾸면 콘텍스트 오버로딩에 따른 모호함이 초래 될 수 있다. 

        - 익명클래스는 인스턴스화할 때 명시적으로 형식이 정해지는 반면 람다의 형식은 콘텍스트에 따라 달라지기 때문이다.
            ```
            interface Task {
                public void execute();
            }
            public static void doSomething(Runnable r){ r.run(); }
            public static void doSomething(Task a){ a.execute(); }

            doSomeThing(new Task) {
                public void execute() {
                    System.out.println("Danger danger!!");
                }
            }
            ```
            - 익명 클래스를 람다 표현식으로 바꾸면 메서드를 호출할 때 Runnable과 Task 모두 대상 형식이 될 수 있으므로 모호함이 발생한다.
            ```
            doSomeThing(() -> System.out.println("Danger!!"));
            ```
        - 명시적 형변환(Task)를 이용해서 모호함을 제거할 수 있다.
            ```
            doSomeThing((Task)() -> System.out.println("Danger!!"));
            ```
        - 대부분의 통합 개발환경에서 제공하는 리팩토링 기능을 이용하면 이와 같은 문제가 자동으로 해결된다.

<h3>8.1.3 람다 표현식을 메서드 레퍼런스로 리팩토링하기</h3>

- 메서드 레퍼런스의 메서드명으로 코드의 의도를 명확하게 알릴 수 있으므로 가독성이 좋아진다.

    ```
    Map<CaloricLevel, List<Dish> dishesByCaloricLevel = 
        menu.stream()
            .collect(
                groupingBy(Dish - > {
                    if(dish.getCalories() <= 400>)
                    return CaloricLevel.DIET;
                    else if(dish.getCaloires() <= 700)
                    return CaloricLevel.NORMAL; 
                    else return CaloricLevel.FAT;
            }));

    // 람다 표현식을 별도 메서드로 추출한 후 groupingBy에 인수로 전달
    Map<CaloricLevel, List<Dish>> dishesByCaloricLevel = 
        menu.stream()
            .collect(groupingBy(Dish::getCaloricLevel));
    
    public class Dish {
        ...
        public CaloricLevel getCaloricLevel() {
            if(this.getCaloris() <= 400)
            return CaloricLevel.DIET;
            else if(this.getCaloris() <= 700)
            return CaloricLevel.NORMAL;
            else return CaloricLevel.FAT;
        }
    }
    ```

- 또한 Comparing과 maxBy 같은 정적 헬퍼 메서드를 활용하는 것도 좋다.
    ```
    inventory.sort(
        (Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight();)
    )

    inventory.sort(comparing(Apple::getWeight));
    ```
- sum, maximum 등 자주 사용하는 리듀싱 연산은 메서드 레퍼런스와 함께 사용할 수 있는 내장 헬퍼 메서드를 제공한다. 

<h3>8.1.4 명령형 데이터 처리를 스트림으로 리팩토링하기</h3>

- 스트림은 쇼트서킷과 게으름이라는 강력한 최적화뿐 아니라 멀티코어 아키텍처를 활용할 수 있는 지름길을 제공한다.
    ```
    // 명령형 코드
    List<String> dishNames = new ArrayList<>();
    for(Dish dish : menu) {
        if(dish.getCalories() > 300){
            dishNames.add(dish.getName());
        }
    } 

    // 자바 8 스트림 API를 활용한 코드
    menu.stream()
        .filter(dish -> dish.getCalories() > 300)
        .map(Dish::getName)
        .collect(toList());
    ```

- 명령형 코드를 스트림 API로 바꾸는 것은 쉬운 일이 아니지만 도움을 주는 몇가지 도구가 있다.

<h3>8.1.5 코드 유연성 개선</h3>

- 동작 파라미터화를 통해 다양한 동작을 표현할 수 있다.

> 함수형 인터페이스 적용

- 람다표현식을 이용하려면 함수형 인터페이스가 필요하다.

- 조건부 연기 실행, 실행 어라운드 패턴으로 람다 표현식 리팩토링을 살펴본다.
    - 조건부 연기 실행
        - 다음은 내장 자바 Logger 클래스를 사용하는 예제다.
            ```
            if(logger.isLoggable(Log.FINER)) {
                logger.finer("Problem: " + generateDiagnostic());
            }
            ```
        - 위 코드는 다음과 같은 사항에 문제가 있다.
            - logger의 상태가 isLoggable이라는 메서드에 의해 클라이언트 코드로 노출된다.
            - 메시지를 로깅할 때마다 logger 객체의 상태를 매번 확인 해야 할까?
        - 다음처럼 메시지를 로깅하기 전에 logger 객체가 적절한 수준으로 설정되었는지 내부적으로 확인하는 log 메서드를 사용하는 것이 바람직하다.
            ```
            logger.log(Level.FINER, "probelm: " + generateDiagnostic());
            ```
        - 덕분에 불필요한 if 문을 제거할 수 있으며 logger의 상태를 노출할 필요도 없다.
        - 그러나 위 코드로 모든 문제가 해결된 것은 아니다. 즉, 인수로 전달된 메시지 수준에서 logger가 활성화되어 있지 않더라도 항상 로깅 메시지를 평가하게 된다.
        - 람다를 이용하면 이 문제를 쉽게 해결 할 수 있다. 특정 조건에서만 메시지가 생성될 수 있도록 메시지 생성 과정을 연기해야 한다. 자바 8 API에서 Supplier를 인수로 갖는 오버로드된 log메서드를 제공했다. 아래는 새로 추가된 log 메서드의 시그니처다.
            ```
            public void log(Level level, Supplier<String> msgSuppier)

            logger.log(Level.FINER, () -> "Prolem: " + generateDiagnostic();)

            // log 메서드의 내부 구현 코드
            public void log(Level level, Supplier<String> msgSupplier) {
                if(logger.isLoggable(level)) {
                    log(level, msgSupplier.get());
                }
            }
            ```
        - 클라이언트 코드에서 객체 상태를 자주 확인하거나 객체의 일부 메서드를 호출하는 상황이라면 내부적으로 객체의 상태를 확인한 다음에 메서드를 호출하도록 새로둔 메서드를 구현하는 것이 좋다.
    
    - 실행 어라운드
        - 매번 같은 준비, 종료 과정을 반복적으로 수행하는 코드가 있다면 이를 람다로 변환할 수 있다. 준비, 종료 과정을 처리하는 로직을 재사용함으로써 코드 중복을 줄일 수 있다.