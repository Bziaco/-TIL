<h2>람다 표현식</h2>

> 3.5.3 형식추론
- 대상 형식을 이용해서 함수 디스크립터를 알 수있고 컴파일러는 람다의 시그너처도 추론할 수 있다.
- 형식추론을 활용해 아래와 같이 코드를 간결화 할 수 있다.

    `Comparator<Apple> c = (Apple a1, Apple a2) -> a1.getWeight().compareTo (a2.getWeight());`(형식추론을 하지 않음)</br> 
    `Comparator<Apple> c = (a1, a2) -> a1.getWeight().compareTo (a2.getWeight());`(형식추론을 함)

>3.5.4 지역 변수 사용
- 람다 표현식에서는 익명 함수가 하는 것처럼 자유변수(파라미터로 넘겨진 변수가 아닌 외부에서 정의된 변수)를 활용할 수 있다. 이와 같은 동작을 람다 캡처링이라 부른다.
    ```
    int portNum = 19293;
    Runnable r = () -> System.out.println(portNum);
    ```
- 하지만 자유변수에도 약간의 제약이 있다. 지역 변수는 명시적으로 final로 선언 및 final로 선언된 변수처럼 사용 되어야 한다.
- 인스턴스 변수는 힙에 저장되는 반면 지역변수는 스택에 위치한다. 그러므로 자바구현에서 원래 변수에 접근을 허용하는 것이 아니라 자유 변수의 복사본을 제공한다. 따라서 값이 바뀌지 않아야 하므로 지역변수에는 한번만 값을 할당해야 하는 제약이 생긴 것이다.
- 클로저란 함수의 비지역 변수를 자유롭게 참조할 수 있는 함수의 인스턴스를 가리킨다. 다만 람다와 익명클래스는 람다가 정의된 메서드의 지역 변수의 값은 바꿀 수 없다. 

> 메서드 레퍼런스
- 메서드 레퍼런스를 사용하면 기존의 메서드 정의를 재활용해서 람다처럼 전달 할 수 있다.
    ```
    //일반 람다 코드
    inventory.sort((Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight()));
    ```
    ```
    //메서드 레퍼런스를 활용한 코드
    inventory.sort(compareing(Apple::getWeight);
    ```
>메서드 레퍼런스를 만드는 방법
- 메서드 레퍼런스는 3가지 유형으로 구분 할 수 있다.
    - 정적 메서드 레퍼런스 : Integer:parseInt
    - 다양한 형식의 인스턴스 메서드 레퍼런스 : String::length
    - 기존 객체의 인스턴스 메서드 레퍼런스 : Transaction을 할당 받은 a 지역변수가 있고 Transaction 객체에는 getValue 메소드가 있다면 a::getValue로 표현가능
- 세가지  종류의 람다표현식을 메서드 레퍼런스로 바꾸는 방법
    ```
    //람다
    (args) -> ClassName.staticMethod(args)
    //메서드 레퍼런스
    ClassName::staticMethod
    ```
    ```
    //람다
    (args0, rest) -> arg0.instanceMethod(rest);
    //메서드 레퍼런스
    ClassName::instanceMethod
    ```
    ```
    //람다
    (args) -> expr.instanceMethod(args);
    //메서드 레퍼런스
    expr::instanceMethod
    ```

>생성자 레퍼런스
- ClassName:new 처럼 클래스명과 new 키워드를 이용해 기존 생성자의 레퍼런스를 만들 수 있다.
    ```
    Supplier의 () -> Apple과 같은 시그니처를 갖는 생성자

    //변경 전
    Supplier<Apple> c1 = () -> newApple();
    Apple a1 = c1.get();

    //생성자 레퍼런스를 활용하여 변경한 코드
    Supplier<Apple> c1 = Apple::new;
    Apple a1 = c1.get();
    ```
    ```
    Function의 () -> apply과 같은 시그니처를 갖는 생성자

    //변경 전
    Funtion<Integer, Apple> c2 = (weight) -> newApple(weight);
    Apple a2 = c2.apply(110);

    //생성자 레퍼런스를 활용하여 변경한 코드
    Funtion<Integer, Apple> c2 = Apple::new;
    Apple a2 = c2.apply(110);
    ```
    ```
    BiFunction의 (String, Integer, Apple)  -> newApple(String,Integer)와 같은 시그니처를 갖는 생성자

    //변경 전
    BiFunction의<String, Integer,Apple> c3 = (color,weigth) -> newApple(color,weight);
    Apple a3 = c3.apply("green",110);

    //생성자 레퍼런스를 활용하여 변경한 코드
    BiFunction의<String, Integer,Apple> c3 = Apple::new;
    Apple a3 = c3.apply("green",110);
    ```
- 인수가 세개 이상인 생성자의 생성자 레퍼런스를 만들기 위해서는 직접 함수형 인터페이스를 만들어야 한다.
    ```
    public interface TriFunction<T,U,V,R> {
        R apply(T t, U u, V v);
    } 
    ```
>람다 표현식을 조합 할 수 있는 유용한 메서드
- 자바 8 API의 몇몇 함수형 인터페이스는 다양한 유틸리티 메서드를 포함한다.
- Comparator 조합
    ```
    Comparator<Apple> c = Comparator.comparing(Apple::getWeigt);
    ```
    - 역정렬은 내림차순으로 정렬하고 싶을때 사용할 수 있다.
    ```
    Inventory.sort(comparing(Apple::getWeight).reversed());
    ```
- Comparator 연결
    - 만약 무게가 같은 사과가 존재한다면 원산지 국가별로 사과를 정렬하도록 해야 한다.
    - thenComparing 메서드로 두번째 비교자를 만들 수 있다.
    ```
    inventory.sort(comparing(Apple::getWeight).thenComparing(Apple::getCountry));
    ```
- Predecate조합
    - Predecate 인터페이스는 복잡한 프레디케이트를 만들 수 있도록 negate,and,or 세가지 메서드를 제공한다.
    ```
    //빨간색이 아닌 사과
    Predicate<Apple> notRedApple = redApple.negate();
    ```
    ```
    //빨간색이면서 무거운 사과
    //두 프레디케이트를 연결해서 새로운 프레디케이트 객체를 만든다.
    Predicate<Apple> redAndHeavyApple = redApple.and(a -> a.getweight() > 150);
    ```
    ```
    //빨간색이면서 무거운 사과 또는 그냥 녹색사과
    Predicate<Apple> redAndHeavyOrGreenApple = redApple.and(a -> a.getweight() > 150).or(a -> "green".equals(a.getColor()));
    ```
- Function 조합
    - Function 인터페이스는 andThen,compose 두가지 디폴트 메서드를 제공한다.
    - andThen메서드는 g(f(x))
    - compose메서드는 f(g(x))
    ```
    //andThen 메서드 
    Function<Integer, Integer> f = x -> x + 1
    Function<Integer, Integer> g = y -> y + 1
    Function<Integer, Integer> h = f.andThen(g);
    int result = h.apply(1); // 4를 반환
    ```
    ```
    //compose 메서드 
    Function<Integer, Integer> f = x -> x + 1
    Function<Integer, Integer> g = y -> y + 1
    Function<Integer, Integer> h = f.compose(g);
    int result = h.apply(1); // 3를 반환
    ```
    - 문자열로 구성된 편지 내용을 변환하는 예시를 보자
        ```
        public class Letter {
            public static String addHeader(String text) {
                return "From Raoul, Mario and Alen: " + text;
            }

            public static String addFooter(String text) {
                return text + " Kind regards";
            }

            public static String checkSpelling(String text) {
                return text.replaceAll("ladma","lamba");
            }
        }
        ```
        ```
        //헤더를 추가한 다음에 철자검사를 하고 마지막에 푸터를 추가
        Function<String, String> addHeader = Letter::addHeader;
        Function<String, String> transfomationPipeline = 
            addHeader.andThen(Letter::checkSpelling)
                     .adnThen(Letter::addFooter);
        ```
        ```
        //철자검사를 빼고 헤더와 푸터만 추가
        Function<String, String> addHeader = Letter::addHeader;
        Function<String, String> transfomationPipeline = 
            addHeader.adnThen(Letter::addFooter);
        ```


