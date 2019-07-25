<h1>열심히 보다는 현명하게</h1>
<h2>메모이제이션</h2>

- 메모이제이션이란 연속해서 사용되는 연산 값을 함수 레벨에서 캐시하는 것을 지칭
- 함수가 같은 매개변수로 호출되면 다시 연산하는 대신 캐시의 값을 리턴
- 캐싱이 제대로 작동하려면 함수가 순수해야 하며 순수함수란 가변 클래스 필드를 참고하지도 않고 리턴값 외에는 아무 값도 쓰지 않아야 하며 주어진 매개변수에만 의존

> 캐싱
- 메서드 레벨에서의 캐싱
    ```
    if (Classifier.isPerfect(n)) print "!"
    else if (Classifier.isAbundant(n)) print "+"
    else if (Classifier.isDeficient(n)) print "-"
    ```
    - 이렇게 구현한 경우 모든 분류 메서드를 호출할 때마다 매개변수의 합을 계산
    - 이것이 클래스 내부 캐싱의 예
    - 상당히 비효율적
- 합산 결과를 캐시하기
    ```
    class ClassifierCachedSum {
    private sumCache = [:]

    def sumOfFactors(number) {
        if (! sumCache.containsKey(number)) {
        sumCache[number] = factorsOf(number).sum()
        }
    return sumCache[number]
    ```
    - 클래스를 초기화 할때 sumCache란 해시를 만든다.
    - sumOfFactors() 메서드 내부에서는 캐시에 매개변수의 합이 들어 있으면 그 값을 바로 리턴

    ```
    @Test
    void mashup() {
    println "Test for range 1-${TEST_NUMBER_MAX}"
    print "Non-optimized:              "
    start = System.currentTimeMillis()
    (1..TEST_NUMBER_MAX).each {n ->
      if (Classifier.isPerfect(n)) print '!'
      else if (Classifier.isAbundant(n)) print '+'
      else if (Classifier.isDeficient(n)) print '-'
    }
    println "\n\t ${System.currentTimeMillis() - start} ms"
    print "Non-optimized (2nd):        "
    start = System.currentTimeMillis()
    (1..TEST_NUMBER_MAX).each {n ->
      if (Classifier.isPerfect(n)) print '!'
      else if (Classifier.isAbundant(n)) print '+'
      else if (Classifier.isDeficient(n)) print '-'
    }
    println "\n\t ${System.currentTimeMillis() - start} ms"
    ```
    - 결과는 합을 캐시한 경우가 하지 않은 경우보다 약 5배 가량 더 빠르다.
    - 이와 같은 캐싱이 외부 캐싱이다.
- 합을 캐시하면 성능이 엄청나게 좋아지지만 내부 캐시가 상태를 표시하기 때문에 이 캐시를 사용하는 모든 메서드를 인스턴스 메서드로 만들어야 한다. 이렇게 하면 싱글톤으로 만들어 볼수도 있지만 그러면 코드가 복잡해짐
- 캐싱이 성능은 향상 시켰지만 코드를 복잡하게 하고 유지보수를 어렵게 한다.
- 전부 다 캐시하기
    - 매개변수의 합과 자연수 매개변수들에 대한 캐시를 더했다.
    - 성능은 상당히 좋아졌지만 이 역시 코드를 복잡하게 한다.

> 메모이제이션의 첨가
- 함수형 프로그래밍은 런타임에 재사용 가능한 메커니즘을 만들어서 움직이는 부분을 최소화 하는데 주력
- 메모이제이션은 프로그래밍 언어에 내장되어 반복되는 함수의 리턴 값을 자동으로 캐싱해주는 기능
- 함수를 메모이제이션 하는 것은 메타함수를 적용하는 것. 즉 리턴 값이 아니라 함수에 어떤 것을 적용하는 것.
- 커링도 메타함수 기법
- 재사용할 만한 결과를 전부 메모아이즈 한다면 첫번째 실행에서는 속도가 줄지만 다음번 부터는 항상 더 빠르게 실행. 그러나 이는 적은 수에 한해서 만이다.
- 명령형 기법이 잘 작동하기 위해서는 안정장치와 함께 실행 조건을 조심스럽게 다뤄야 한다.
- 그루비에서는 메모이제이션에서 결과를 최적화 하기 위한 몇가지 메서드 들이 있다
    - memoise() : 캐싱 형태의 클로저를 만든다.
    - memoizeAtMost() : 최대 크기를 가지는 캐싱 형태의 클로저를 만든다.
    - memoizeAtLeast() : 최소 크기를 가지고 크기를 자동 조정하는 캐싱 형태의 클로저를 만든다.
    - memoizeBetween() : 캐시 크기를 최대치와 최소치 사이에서 자동 조정하는 캐싱 형태의 클로저를 만든다.
- 메모아이즈된 함수는 부수효과가 없어야 하고 외부 정보에 절대로 의존하지 말아야 한다.

<h2>게으름</h2>
- 게으른 컬렉션은 그 요소들을 한꺼번에 미리 연산하는 것이 아니라 필요에 따라 하나씩 전달
- 게으른 연산의 장점
    - 시간이 많이 걸리는 연산을 반드시 필요할때 까지 미룰 수 있다.
    - 요청이 계속되는 한 요소를 계속 전달하는 무한 컬렉션을 만들 수 있다.
    맵이나 필어같은 함수형 개념을 게으르게 사용하면 효율이 높은 코드를 만들 수 있다.