<h1>진화하라</h1>

<h2>적은 수의 자료구조, 많은 연산자</h2>

- 함수형 프로그래밍 언어에서는 주요 자료구조(lsit,set,map)와 거기에 따른 최적화된 연산을 선호
- 이러한 연산에 자료구조와 함수를 끼워서 특정한 목적에 맞게 커스터마이즈 하는것
- 함수 수준에서 캡슐화하면 커스텀 클래스 구조를 만드는 것보다 좀더 세밀하고 기본적인 수준에서 재사용이 가능

<h2>문제를 향하여 언어를 구부리기</h2>
- 문제를 프로그램에 맞추지 말고 프로그램을 문제에 맞게끔 조정해가라.

<h2>디스패치 다시 생각하기</h2>
- 디스패치란 넓은 의미로 언어가 작동 방식을 동적으로 선택하는 것

- 그루비로 디스패치 개선하기
    - 자바에서 조건부 실행은 특별한 경우의 switch문을 제외하고는 if문을 사용하게 되는데 if문이 길어지면 가독성이 떨어지기 때문에 추상팩토리 패턴 등을 사용한다.
    - 그루비에서는 자바의 switch문과 유사한 문법을 사용하지만 다르게 실행되는 강력한 switch문이 있다.
    ```
    class LetterGrade {
        def gradeFromScore(score) {
            switch (score) {
            case 90..100 : return "A"
            case 80..<90 : return "B"
            case 70..<80 : return "C"
            case 60..<70 : return "D"
            case 0..<60  : return "F"
            case ~"[ABCDFabcdf]" : return score.toUpperCase()
            default: throw new IllegalArgumentException("Invalid score: ${score}")
            }
        }
    }
    ```
    - 자바와 달리 그루비의 switch 문은 여러가지 동적 자료형을 받을 수 있다.
    - 매개변수에 열린범위 및 정규식, 디폴드 조건 모두 사용할 수 있다. 즉, 동적자료형을 사용하므로 매개변수로 다른 자료형을 넣어서 각각 그게 맞게 반응하게 하는 것이 가능

- 클로저 언어 구부리기
    - 자바나 자바계열 언어들에는 키워드가 있는데 이는 문법의 기반을 이루며 개발자들이 이러한 키워드를 만들수 없다.
    - 자바에서는 함수나 클래스를 만들수는 있지만 기초적인 빌딩블록을 만드는 것은 불가능하다. 따라서 개발자는 문제를 프로그래밍 언어로 번역해야 한다.
        ```
        //클로저로 만든 학점 프로그램
        ns lettergrades)

        (defn in [score low high]
        (and (number? score) (<= low score high)))

        (defn letter-grade [score]
        (cond
            (in score 90 100) "A"
            (in score 80 90)  "B"
            (in score 70 80)  "C"
            (in score 60 70)  "D"
            (in score 0 60)   "F"
            (re-find #"[ABCDFabcdf]" score) (.toUpperCase score)))
        ```
        ```
        //이 학점 프로그램을 클로저로 테스트
        (ns lettergradestest
            (:use clojure.test)
            (:use lettergrades))

            (deftest numeric-letter-grades
            (dorun (map #(is (= "A" (letter-grade %))) (range 90 100)))
            (dorun (map #(is (= "B" (letter-grade %))) (range 80 89)))
            (dorun (map #(is (= "C" (letter-grade %))) (range 70 79)))
            (dorun (map #(is (= "D" (letter-grade %))) (range 60 69)))
            (dorun (map #(is (= "F" (letter-grade %))) (range 0 59))))

            (deftest string-letter-grades
            (dorun (map #(is (= (.toUpperCase %)
                    (letter-grade %))) ["A" "B" "C" "D" "F" "a" "b" "c" "d" "f"])))

            (run-all-tests)
        ```
        - #(is (= "A" (letter-grade %))) 코드 블록은 매개변수 하나를 받아서 학점이 제대로 주어지면 true를 리턴하는 익명함수
        - map함수는 이 익명함수를 둘째 매개변수인 컬렉션의 모든 요소들에 적용
        - (dorun )함수안에서 매핑함수를 호출하면 이 부수효과가 정확하게 일어나 모든 테스트를 실행
- 클로저의 멀티메서드와 맞춤식 다형성
    - 계속되는 if문은 읽기도 어렵고 디버그도 어렵다. 하지만 자바 언어 수준에서는 적당히 대체할 만한 것이 없기 때문에 추상 팩터리 패턴을 사용하여 해결한다. 팩토리 패턴 클래스를 사용한 다형성이므로 자바에서 사용할 만하다.
    - 이 패턴을 사용하면 상위클래스나 인터페이스에 일반적인 메서드 시그니처를 정해놓고 동적으로 실행되게끔 구현하면 된다.
    - 클로저는 개발자가 원하는 대로 디스패치가 결정되는 다형성 멀티메서드를 지원
        ```
        (defstruct color :red :green :blue)

        (defn red [v]
        (struct color v 0 0))

        (defn green [v]
        (struct color 0 v 0))

        (defn blue [v]
        (struct color 0 0 v))
        ```
        - 클로저의 멀티메서드는 디스패치 결정 조건을 리턴하는 디스패치 함수를 받아들이는 메서드를 말한다.
        ```
        (defn basic-colors-in [color]
        (for [[k v] color :when (not= v 0)] k))

        (defmulti color-string basic-colors-in)

        (defmethod color-string [:red] [color]
        (str "Red: " (:red color)))

        (defmethod color-string [:green] [color]
        (str "Green: " (:green color)))

        (defmethod color-string [:blue] [color]
        (str "Blue: " (:blue color)))

        (defmethod color-string :default [color]
        (str "Red:" (:red color) ", Green: " (:green color) ", Blue: " (:blue color)))
        ```
        - basic-colors-inㅇ란 디스패치 함수를 정의한다. 이 함수는 정해진 모든 색깔들을 벡터 형태로 리턴한다.
        - 마지막 경우는 모든 다른 경우를 처리하는 :default 키워드를 포함한다. 이 경우에는 색깔을 한가지만 받는다고 장담할 수 없기 때문에 모든 색깔의 목록을 리턴한다.

    <h2>연산자 오버로딩</h2>

    - 함수형 언어의 공통적인 기능은 연산자 오버로딩이다. 이것은 +,-,*와 같은 연산자를 새로 정의하여 새로운 자료형에 적용하고 새로운 행동을 하게 하는 기능이다.
    - 자바가 처음 만들어질 때는 의도적으로 연산자 오버로딩이 제외 되었지만 자바의 후계 언어들을 비롯해 모든 현애 언어들에는 이 기능이 들어 있다.
    > 그루비
    
    - 그루비는 연산자들을 메서드 이름에 자동으로 매핑하는 연산자 오버로딩을 허용한다.

        연산자 | 메서드
        ----| ----
        x + y | x.plus(x)
        x * y | x.multiply(y)
        x / y | x.div(y)
        x ** y | x.power(y)
    - 새로운 언어를 만들지말고 연산자 오버로딩을 통해 문제 도메인을 향하여 언어를 구부리자.

<h2>함수형 자료구조</h2>

- 대부분 함수형 언어들은 예외 패러다임을 지원하지 않기 때문에 개발자는 다른 방법으로 오류조건을 표현해야 한다.
- 예외는 많은 함수형 언어가 준수하는 전체 몇 가지를 깨드린다.
    - 함수형 언어는 부수효과가 없는 순수함수를 선호한다. 함수형 언어들은 주로 값을 처리하기 때문에 프로그램의 흐름을 막기보다는 오류를 나타내는 리턴 값에 반응하는 것을 선호한다.
    - 함수형 프로그래밍이 선호하는 또 하나의 특성은 참조 투명성이다. 호출하는 입장에서는 단순한 값 하나를 사용하든 하나의 값을 리턴하는 함수를 사용하든 다를 바가 없어야 한다. 만약 호출된 함수에서 예외가 발생할 수 있다면 호출하는 입장에서는 안전하게 값을 함수로 대체 할 수 없을 것이다.

    > 함수형 오류 처리

    - 자바에서 예외를 사용하지 않고 오류를 처리하기 위해 해결해야 할 근본적인 문제는 메서드가 하나의 값만 리턴할 수 있다는 제약이다.
    - Map을 사용하여 다수의 리턴 값을 지원하게 할 수 있다.
        ```
        public static Map<String, Object> divide(int x, int y) {
            Map<String, Object> result = new HashMap<>();
            if(y == 0) {
                result.put("exception", new Exception("div by zero"));
            } else {
                result.put("answer", (double) x/y);
            }
            return result;
        }
        ```
        - 위 코드를 테스트 해보자
        ```
        @Test
        public void maps_success() {
            Map<String, Object> result = RomanNumeralParser.divide(4, 2);
            assertEquals(2.0, (Double) result.get("answer"), 0.1);
        }

        @Test
        public void maps_failure() {
            Map<String, Object> result = RomanNumeralParser.divide(4, 0);
            assertEquals("div by zero", ((Exception) result.get("exception")).getMessage());
        }
        ```
        - 이 접근 방법에는 문제점이 있다. 첫째 Map에 들어가는 값은 타입 세이프하지 않기 때문에 컴파일러가 오류를 잡아낼 수 없다.
        - 둘째 메서드 호출자는 리턴 값을 가능한 결과들과 비교해보기 전에는 성패를 알 수 없다.
        - 셋째, 두가지 결과가 모두 리턴 Map에 존재할 수가 있으므로 그 경우에는 결과가 모호해진다.
        - 여기서 필요한 것은 타입 세이프하게 둘 또는 더 많은 값을 리턴할 수 있게 해주는 메커니즘이다.

    <h3>Either 클래스</h3>

    - 함수형 언어에서는 다른 두 값을 리턴해야 하는 경우가 종종 있는데 그런 행동을 모델링하는 자료구조가 Either 클래스이다.
    - Either는 왼쪽 또는 오른쪽 값 중 하나만 가질 수 있게 설계되었다. 이런 자료구조를 분리집합이라고 한다. 
    - Either는 오류처리에 주로 사용된다.
    - 자바에 내장되지는 않았지만 제네릭을 사용하면 Either 클래스를 만들 수 있다.
        ```
        public class Either<A,B> {
            private A left = null;
            private B right = null;

            private Either(A a,B b) {
                left = a;
                right = b;
            }

            public static <A,B> Either<A,B> left(A a) {
                return new Either<A,B>(a,null);
            }

            public A left() {
                return left;
            }

            public boolean isLeft() {
                return left != null;
            }

            public boolean isRight() {
                return right != null;
            }

            public B right() {
                return right;
            }

            public static <A,B> Either<A,B> right(B b) {
                return new Either<A,B>(null,b);
            }

            public void fold(F<A> leftOption, F<B> rightOption) {
                if(right == null)
                    leftOption.f(left);
                else
                    rightOption.f(right);
            }
        }
        ```
        - 예제에서 Either클래스는 생성자가 private이기 때문에 실제 생성은 정적 메서드인 left(a,b)와 right(a,b)가 담당한다. 
        - Either를 사용하면 타입 세이프티를 유지하면서 예외 또는 제대로된 결과 값을 리턴하는 코드를 만들 수 있다.
    
    <h3>게으른 파싱과 함수형 자바</h3>

    - Either클래스는 함수형 알고리즘에 자주 사용되며 다른 함수형 자바 구조물과 같이 사용될 수 있게 만들어져 있다.
    - 따라서 Either와 함수형 자바의 P1 클래스를 조합해서 사용하면 게으른 오류 평가를 구현할 수 있다.
    - 함수형 자바에서 P1클래스는 매개변수가 없는 _1()란 간단한 메서드의 단순한 래퍼이다.
    - P1은 함수형 자바에서 코드 블록을 실행하지 않고 여기저기 전해주어서 원하는 컨텍스트에서 실행하게 해주는 일종의 고계함수이다.
    - 자바에서 예외를 던지는 순간 그 객체가 만들어 진다. 게으른 평가 메서드를 리턴하면 예외의 생성을 지연할 수 있다.

    <h3>디폴트 값을 제공하기</h3>

    - Either를 예외 처리에 사용하여 얻는 이점은 게으름만이 아니다. 이폴트 값을 제공한다는 것이 다른 이점이다.

    <h3>옵션 클래스</h3>

    - Either와 유사한 Option이란 클래스가 있는데 이는 적당한 값이 존재하지 않을 경우를 의미하는 none, 성공적인 리턴을 의미하는 some을 사용하여 예외 조건을 더 쉽게 표현한다. 
    - Either는 어떤 값이든 저장할 수 있는 반면 Option은 주로 성공고 실패의 값을 저장하는데 쓰인다.

    <h3>Either 트리</h3>

    - 아래 표처럼 세가지 추상 개념을 가지고 트리 자료구조를 모델링할 수 있다.

        추상화된 트리 | 설명
        ---- | ----
        empty | 셀에 아무 값도 없음
        leaf | 셀에 특정 자료형의 값이 들어 있음
        node | 다른 leaf나 node를 가리킴

    ```
    public abstract class Tree {
        private Tree() {}

        public abstract Either<Empty, Either<Leaf, Node>> toEither();

        public static final class Empty extends Tree {
            public Either<Empty, Either<Leaf, Node>> toEither() {
                return left(this);
            }

            public Empty() {}
        }

        public static final class Leaf extends Tree {
            public final int n;

            @Override
            public Either<Empty, Either<Leaf, Node>> toEither() {
                return right(Either.<Leaf, Node>left(this));
            }

            public Leaf(int n) { this.n = n; }
        }

        public static final class Node extends Tree {
            public final Tree left;
            public final Tree right;

            public Either<Empty, Either<Leaf, Node>> toEither() {
                return right(Either.<Leaf, Node>right(this));
            }

            public Node(Tree left, Tree right) {
                this.left = left;
                this.right = right;
            }
        }

    }
    ```
    - Empty, Leaf, Node가 Tree 추상 클래스 내부에 세개의 final 구상 클래스를 정의한다.
    - 여기서 Either는 제일 왼쪽에 Empty, 가운데는 Leaf, 제일 오른쪽에는 Node를 가지는 관례를 따른다.
    - 이 트리 구조는 내부적으로 <Either,<Left,Node>>를 바탕으로 하므로 패턴 매칭을 흉내 내서 모든 요소를 순회 할 수 있다.

    <h3>패턴 매칭으로 트리 순회하기</h3>

    - 함수형 자바에서 구현된 Either의 left()와 right() 메서드는 모두 Iterable 인터페이스를 구현한다.





