<h2>전환</h2>

> 명령형 처리
- 명령형 프로그래밍이란 상태를 변형하는 일련의 명령들로 구성된 프로그래밍 방식
- 전형적인 for루프가 대표적인 예
    ```
    public class TheCompanyProcess {
        public String cleanNames(List<String> listOfNames) {
            StringBuilder result = new StringBuilder();
            for(int i = 0; i < listOfNames.size(); i++) {
                if (listOfNames.get(i).length() > 1) {
                    result.append(capitalizeString(listOfNames.get(i))).append(",");
                }
            }
            return result.substring(0, result.length() - 1).toString();
        }

        public String capitalizeString(String s) {
            return s.substring(0, 1).toUpperCase() + s.substring(1, s.length());
        }
    }
    ```
    - 한글자 이름을 필터했고 목록에 남아있는 이름들을 대문자로 변형하고 이 목록을 하나의 문자열로 변환했다.

> 함수형 처리
- 앞에서 언급한 필터, 변형, 변환 등의 논리적 분류도 저수준의 변형을 구현하는 함수들이었다. 함수형 프로그래밍 언어는 명령형 언어와는 다르게 문제를 분류한다.
    ```
    public class Process {

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
    // END java8_process
    ```
    - 자바 8로 리펙트 한 코드를 보면 필터, 변형, 변환을 filter, map, collect인 고계함수를 활용했다.
    - 자바 런타임은 null체크와 길이 필터를 하나의 연산으로 묶어준다.
    - 함수형 사고로의 전환은 어떤 경우에 세부적인 구현에 뛰어들지 않고 이런 고수준 추상 개념을 적용할지를 배우는 것이다.
    - 고수준의 사고로 얻는 이점
        - 런타임이 최적화를 잘 할 수 있도록 도와 준다.
        - 문제의 공통점을 고려하여 다른 방식으로 분류하기를 권장
        - 개발자가 엔진 세부사항에 깊이 파묻힐 경우 불가능한 해답을 가능하게 한다.
    - 절차보다는 결과에 집중하라

> 사례연구 : 자연수의 분류
- 명령형 자연수 분류
    ```
    public class ImpNumberClassifierSimple {
        private int _number;                          //<1>
        private Map<Integer, Integer> _cache;         //<2>

        public ImpNumberClassifierSimple(int targetNumber) {
        _number = targetNumber;
        _cache = new HashMap<>();
        }

        public boolean isFactor(int potential) {
        return _number % potential == 0;
        }

        public Set<Integer> getFactors() {
            Set<Integer> factors = new HashSet<>();
            factors.add(1);
            factors.add(_number);
            for (int i = 2; i < _number; i++)
                if (isFactor(i))
                    factors.add(i);
            return factors;
        }

        public int aliquotSum() {                     // <3>
            if (_cache.get(_number) == null) {
                int sum = 0;
                for (int i : getFactors())
                    sum += i;
                _cache.put(_number, sum - _number);
            }
            return _cache.get(_number);
        }

        public boolean isPerfect() {
            return aliquotSum() == _number;
        }

        public boolean isAbundant() {
            return aliquotSum() > _number;
        }

        public boolean isDeficient() {
            return aliquotSum() < _number;
        }
    }
    ```
    - <1> : 대상이 되는 수를 보유한 내부상태
    - <2> : 합을 반복해서 계산하는 것을 피하기 위한 내부 캐시
    - <3> : aliquotSum(자신을 제외한 모든 약수의 합) 계산
    - cache는 계산 결과를 빨리 리턴하기 위해 각각의 진약수의 합을 유지하는데 Map을 사용
    - OOP의 캡슐화를 이점으로 상태를 분리해 놓음으로서 단위 테스팅 같은 엔지니어링이 수훨해진다.

- 조금 더 함수적인 자연수 분류기
    ```
    public class NumberClassifier {

        public static boolean isFactor(final int candidate, final int number) {   //<1>
            return number % candidate == 0;
        }

        public static Set<Integer> factors(final int number) {                    //<2>
            Set<Integer> factors = new HashSet<>();
            factors.add(1);
            factors.add(number);
            for (int i = 2; i < number; i++)
                if (isFactor(i, number))
                    factors.add(i);
            return factors;
        }

        public static int aliquotSum(final Collection<Integer> factors) {
            int sum = 0;
            int targetNumber = Collections.max(factors);
            for (int n : factors) {                                               //<3>
                sum += n;
            }
            return sum - targetNumber;
        }

        public static boolean isPerfect(final int number) {
            return aliquotSum(factors(number)) == number;
        }
                                                                                //<4>
        public static boolean isAbundant(final int number) {
            return aliquotSum(factors(number)) > number;
        }

        public static boolean isDeficient(final int number) {
            return aliquotSum(factors(number)) < number;
        }
    }
    ```
    - <1> 모든 메서드는 number를 매개변수로 받아야 한다. 그 값을 유지할 내부 상태는 없다.
    - <2> 모든 메서드는 순수함수이기 때문에 public static이다, 그렇기 때문에 자연수 분류 문제라는 범위 밖에서도 유용하다.
    - 일반적이고 합리적인 변수의 사용으로 함수 수준에서의 재사용이 쉬워졌다
    - 이 코드는 캐시가 없기 때문에 반복적으로 사용하기에 비능률적이다.

- 자바 8을 사용한 자연수 분류기
    ```
    public class NumberClassifier {

        // BEGIN java8_filter
        public static IntStream factorsOf(int number) {
            return range(1, number + 1)
                    .filter(potential -> number % potential == 0);
        }
        // END java8_filter

        public static int aliquotSum(int number) {
            return factorsOf(number).sum() - number;
        }

        public static boolean isPerfect(int number) {
            return aliquotSum(number) == number;
        }

        public static boolean isAbundant(int number) {
            return aliquotSum(number)> number;
        }

        public static boolean isDeficient(int number) {
            return aliquotSum(number) < number;
        }

    }
    ```
    - factorsOf() 메서드는 스트림을 종료한 뒤 값으로 만드는 것과 같은 다른 작업과 연계 할 수 있도록 IntStream을 리턴
    - 함수형 언어에서 스트림은 나중에 사용하기 위히 저장해두는 물리학의 위치 에너지와 같다. 스트림은 개발자가 종료작업을 통해 값을 요구할 때까지는 위치에너지를 운동에너지로 변환하지 않는다.

    > 필터
    - 주어진 조건에 맞는 컬렉션의 부분집합을 구하려면 filter를 사용해라.
        ```
        public static IntStream factorOf(int number) {
            return range(0, number+1)
                   .filter(potential -> number % potentail == 0);
        }
        ```
    > 맵
    - 컬렉셔의 각 요소에 같은 함수를 적용하여 새로운 컬렉션으로 만든다.

    >폴드/리듀스
    - reduce 함수는 주로 초기 값을 주어야 할 때 사용하고 fold는 초기 값이 없을 때 보통 사용한다.
    - 왼쪽 폴드란 다음과 같은 의미이다.
        - 이항 함수나 연산으로 목록의 첫째 요소와 누산기의 초기 값을 결합한다. 초기값이 없는 경우도 있다.
        - 앞의 단계를 목록이 끝날 때까지 계속하면 누산기가 폴드 연산의 결과를 갖게 된다.
        - 함수형 자바의 foldLeft() 메서드 이다.
        ```
        public int aliquotSum(List<Integer> factors) {
            return factors.foldLeft(fj.function.Integer.add, 0) - factors.last();
        }
        ```
        - 폴드 연산은 전체 목록의 각 요소를 다음 요소와 결합하여 하나의 결과를 구하는 연산을 뜻한다.
    - 덧셈은 교환법칙이 성립되므로 결과는 foldLeft()든 foldRight()든 상관없다. 하지만 뺄셈이나 나눗셈 같은 경우는 순서가 중요하므로 foldRight() 메서드가 필요하다. 