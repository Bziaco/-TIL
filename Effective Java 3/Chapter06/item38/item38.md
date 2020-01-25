# 명명 패턴보다 애너테이션을 사용하라

## _JUint(버전3 이전)의 단점_

1. 메서드 이름은 무조건 test로 해야 한다. 만약 다른 이름으로 명명한다면 테스트를 진행하지 않는다.

2. 올바른 프로그램 요소에서만 사용되리라는 보증할 방법이 없다.

3. 프로그램 요소를 매개변수로 전달할 마땅한 방법이 없다.

<br>

## _JUint 버전4 이후 단점 개선_

애너테이션은 버전3 이전 JUnit 단점을 해결해주는 개념으로 버전4부터 도입되었다. 이번 아이템에서는 애너테이션의 동작 방식을 보여주고자 직접 제작한 작은 테스트 프레임워크를 사용할 것이다. Test라는 이름의 애너테이션을 정의한다고 해보자.

`마커(marker) 애너테이션 타입 선언`

```js
import java.lang.annotation.*

/**
* 테스트 메서드임을 선언하는 애너테이션이다.
* 매개변수 없는 정적 메서드 전용이다.
*/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test { }
```

@Test 애너테이션 타입 선언 자체에는 @Retention과 @Target 두가지의 다른 애너테이션이 달려있다. 이처럼 애너테이션 선언에 다는 애너테이션을 메타애너테이션(meta-annotation)이라 한다. @Retention(RetentionPolicy.RUNTIME) 메타애너테이션은 @Test가 런타임에도 유지되어야 한다는 표시다. 이 애너테이션을 생략하면 테스트 도구는 @Test를 인식할 수 없다. @Target(ElementType.METHOD) 메타애너테이션은 @Test가 반드시 메서드 선언에서만 사용돼야 한다고 알려준다.

앞 코드 주석에서 '매개변수 없는 정적 메서드 전용이다.'라고 쓰여 있다. 이 제약을 컴파일러가 강제하려면 적절한 애너테이션 처리기를 직접 구현해야 한다(javax.annotation.proccessing API 참조). 적절한 애너테이션 처리기 없이 인스턴스 메서드나 매개변수가 있는 메서드에 달면 컴파일은 잘되겠지만 테스트 도구를 실행할 때 문제가 될 것이다.

<br>

## _마커 애너테이션(marker-annotaion)_

다음 코드는 @Test 애너테이션을 실제 적용한 모습이다. 이와 같은 애너테이션을 '아무 매개변수 없이 단순히 대상에 마킹(marking)한다'는 뜻에서 마커(marker) 애너테이션이라 한다. 이 애너테이션을 사용하면 프로그래머가 Test이름에 오타를 내거나 메서드 선언 외의 프로그램 요소에 달면 컴파일 오류를 내준다.

`마커 애너테이션을 사용한 프로그램 예`

```js
public class Sample {
    @Test public static void m1(); // 성공해야 한다.
    public static void m2();
    @Test public static void m3(){ // 실패해야 한다.
        throw new RuntimeException("실패");
    }
    public static void m4();
    @Test public void m5(); // 잘못 사용한 예: 정적 메서드가 아니다.
    public static void m6();
    @Test public static void m7() { // 실패해야 한다.
        throw new RuntimeException("실패");
    }
    public satic void m8();
}
```

@Test 애너테이션이 Sample 클래스의 의미에 직접적인 영향을 주지는 않는다. 단지 @Test 애너테이션이 달린 메서드에 한해서만 특별한 처리를 할 기회를 준다. 다음의 RunTimes가 바로 그런 도구의 예다.

```js
public class RunTests {
    public static void main(String[] args) {
        int tests = 0;
        int passed = 0;
        Class<?> testClass = Class.forName(args[0]);
        for (Method m : testClass.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Test.class)) {
                test++;
                try {
                    m.invoke(null);
                    passed++;
                } catch (InvocationTargetException wrappedExc) {
                    Throwable exc = wrappedExc.getCause();
                    System.out.println(m + " 실패: " + exc);
                } catch (Exception e) {
                    System.out.println("잘못 사용한 @Test: " + m);
                }
            }
        }

        System.out.printf("성공: %d, 실패: %d%n", passed, tests-passed);
    }
}
```

이 테스트 러너는 명령줄로부터 완전 정규화된 클래스 이름을 받아 그 클래스에서 @Test 애너테이션이 달린 메서드를 차례로 호출한다. isAnnotationPresent가 실행할 메서드를 찾아주는 메서드다. 테스트 메서드가 예외를 던지면 리플렉션 매커니즘이 InovacationTargetException으로 감싸서 다시 던진다. 그래서 이 프로그램은 InovocationTargetException을 잡아 원래 예외에 담긴 실패 정보를 추출해 (getCuase) 출력한다.

InovocationTargetException 외의 예외가 발생한다면 @Test 애너테이션을 잘못 사용했다는 뜻이다. 아마도 인스턴스 메서드, 매개변수가 있는 메서드, 호출할 수 없는 메서드 등에 달았을 것이다. 앞 코드에서 두 번째 catch블록은 이처럼 잘못 사용해서 발생한 예외를 붙잡아 적절한 오류 메세지를 출력한다. 다음은 RunTests로 Sample을 실행했을 때의 출력 메시지다.

```js
public static void Smaple.m3() failed: RuntimeException: Boom
Invalid @Test: public void Sample.m5();
public static void Sample.m7() failed: RuntimeException: Crash
성공: 1, 실패: 3
```

<br>

## _특정 예외를 던지도록 하는 애너테이션 타입_

이제 특정 예외를 던져야만 성공하는 테스트를 지원하도록 해보자. 새로운 애너테이션 타입이 필요하다.

`매개변수 하나를 받는 애너테이션 타입`

```js
/**
* 명시한 예외를 던져야만 성공하는 테스트 메서드용 애너테이션
*/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionTest {
    Class<? extends Throwable> value();
}
```

이 애너테이션의 매개변수 타입은 Class<? extends Throwable>이다. 여기서 와일드 카드 타입은 'Throwable을 확장한 클래스의 Class 객체'라는 뜻이며 따라서 모든 예외 타입을 다 수용한다. 다음은 이 애너테이션을 실제 활용하는 모습이다. class 리터널은 애너테이ㅕㄴ 매개변수의 값으로 사용됐다.

`매개변수 하나짜리 애너테이션을 사용한 프로그램`

```js
public class Sample2 {
    @ExceptionTest(ArithmeticException,class)
    public static void m1() { // 성공해야 한다.
        int i = 0;
        i = i / i;
    }
    @ExceptionTest(ArithmeticException.class)
    public static void m2() { // 실패해야 한다.(다른 예외 발생)
        int[] a = new int[0];
        int i = a[1];
    }
    @ExceptionTest(ArithmeticException.class)
    public static void m3() { // 실패해야 한다.(예외가 발생하지 않음)

    }
}
```

이제 이 애너테이션을 다룰 수 있도록 테스트 도구를 수정해보자.

```js
if(m.isAnnotaionPresent(ExceptionTest.class)) {
    test++;
    try {
        m.invoke(null);
        System.out.printf("테스트 %s 실패: 예외를 던지지 않음%n", m);
    } catch (InvocationTargetException wrappedExc) {
        Throwable exc = wrappedExc.getCause();
        Class<? extends Throwable> excType = m.getAnnotation(ExceptionTest.class).value();
        if(excType.isInstance(exc)) {
            passed++;
        } else {
            System.out.printf("테스트 %s 실패: 기대한 예외 %s, 발생한 예외 %s%n", m, excType.getName(), exc);
        }
    } catch (Exception e) {
        System.out.println("잘못 사용한 @ExceptionTest: " + m);
    }
}
```

@Test 매너테이션용 코드와 비솟해 보인다. 한 가지 차이점이라면 이 코드는 애너테이션 매개변수의 값을 추출하여 테스트 메서드가 올바른 예외를 던지는지 확인하는데 사용한다.

<br>

## _배열 매개변수를 받는 애너테이션 타입_

조금 더 내용을 들어가자면, 예외를 여러 개 명시하고 그중 하나가 발생하면 성공하게 만들 수도 있다. 애너테이션 메커니즘에는 이런 쓰임에 아주 유용한 기능이 기본으로 들어 있다. @ExceptionTest 애너테이션의 매개변수 타입을 Class 객체의 배열로 수정해보자.

`배열 매개변수를 받는 애너테이션 타입`

```js
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionTest {
    Class<? extends Throwable>[] value();
}
```

배열 매개변수를 받는 애너테이션용 문법은 아주 유용하다. 단일 원소 배열에 최적화했지만 앞서의 @ExceptionTest들도 모두 수정없이 수용한다. 원소가 여럿인 배열을 지정할 때는 다음과 같이 원소들을 중괄호로 감싸고 쉼표로 구분해주기만 하면 된다.

`배열 매개변수를 받는 애너테이션을 사용하는 코드`

```js
@ExceptionTest({ IndexOutofBoundException.class, NullPointerException.class })
public static void doubleBad() { // 성공해야 한다.
    List<String> list = new ArrayList<>();

    // 자바 API 명세에 따르면 다음 메서드는 IndexOutofBoundException이나
    // NullPointException을 던질 수 있다.
    list.addAll(5, null);
}
```

다음은 새로운 @ExceptionTest를 지원하도록 테스트 러너를 수정한 모습이다.

```js
if(m.isAnnotaionPresent(ExceptionTest.class)) {
    test++;
    try {
        m.invoke(null);
        System.out.printf("테스트 %s 실패: 예외를 던지지 않음%n", m);
    } catch (InvocationTargetException wrappedExc) {
        Throwable exc = wrappedExc.getCause();
        Class<? extends Throwable>[] excTypes = m.getAnnotation(ExceptionTest.class).value();

        int oldPassed = passed;
        for(Class<? extends Throwable> excType : excTypes) {
            if(excType.isInstance(exc)) {
            	passed++;
                break;
            }
        }

        if(passed == oldPassed) {
            System.out.println("테스트 %s 실패: %s %n", m, exc);
        }
    } catch (Exception e) {
        System.out.println("잘못 사용한 @ExceptionTest: " + m);
    }
}
```

자바 8에서는 여러 개의 값을 받는 애너테이션을 다른 방식으로도 만들 수 있다. 배열 매개변수를 사용하는 대신 애너테이션에 @Repeatable 메타애너테이션을 다는 방식이다. @Repeatable을 단 애너테이션은 하나의 프로그램 요소에 여러번 달 수 있다. 단 주의할 점이 있다. 첫 번째 @Repeatable을 단 애너테이션을 반환하는 '컨테이너 애너테이션'을 하나 더 정의하고 @Repeatable에 이 컨테이너 애너테이션의 class 객체를 매개변수로 전달해야 한다. 두번째 컨테이너 애너테이션은 내부 애너테이션 타입의 배열을 반환하는 values 메서드를 정의해야 한다. 마지막으로 컨테이너 애너테이션 타입에는 적절한 보존 정책(@Retention)과 적용 대상(Target)을 명시해야 한다.

`반복 가능한 애너테이션 타입`

```js
// 반복 가능한 애너테이션
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(ExceptionTestContainer.class)
public @interface ExceptionTest {
    Class<? extends Throwable> values();
}

// 컨테이너 애너테이션
@Retention(RetentionPolicy.RUNTIME
@Target(ElementType.METHOD)
pulbic @interface ExceptionTestContainer {
    ExceptionTest[] values();
}
```

앞서 배열 방힉 대신 반복 가능 애너테이션을 적용해보자

`반복 가능 애너테이션을 두번 단 코드`

```js
@ExceptionTest(IndexOutOfBoundException.class)
@ExceptionTest(NullPointException.class)
public static void doubleBad(){...}
```

반복 가능 애너테이션은 처리할 때도 주의를 요한다. 반복 가능 애너테이션을 여러 개 달면 하나만 달았을 때와 구분하기 위해 해당 '컨테이너' 애너테이션 타입이 적용된다. getAnnotaionsByType 메서드는 이 둘을 구분하지 않아서 반복 가능 애너테이션과 그 컨테이너 애너테이션을 모두 가져오지만 isAnnotationPresent 메서드는 둘을 명확히 구분한다. 따라서 반복 가능 애너테이션을 여러 번 단 다음 isAnnotationPresent로 반복 가능 애너테이션이 달렸는지 검사한다면 '그렇지 않다'라고 알려준다. 그 결과 애너테이션을 여러 번 단 메서드들을 모두 무시하고 지나친다. 같은 이유로 isAnnotationPresent로 컨테이너 애너테이션이 달렸는지 검사한다면 반복 가능 애너테이션을 한 번만 단 메서드를 무시하고 지나친다. 그래서 달려 있는 수화 상관없이 모두 검사하려면 둘을 따로따로 확인해야 한다. 다음은 RunTests 프로그램이 ExceptionTest의 반복 가능 버전을 사용하도록 수정한 모습이다.

`반복 가능 애너테이션 다루기`

```js
if(m.isAnnotationPresent(ExceptionTest.class) ||
    m.isAnnotationPresent(ExceptionTestContainer.class)) {
    tests++;

    try {
        m.invoke(null);
        System.out.printf("테스트 %s 실패: 예외를 던지지 않음%n", m);
    } catch (InvocationTargetException wrappedExc) {
        Throwable exc = wrappedExc.getCause();
        int oldPassed = passed;

        ExceptionTest[] excTests = m.getAnnotationByType(ExceptionTest.class);
        for(ExceptionTest excType : excTypes) {
            if(excType.isInstance(exc)) {
            	passed++;
                break;
            }
        }

        if(passed == oldPassed) {
            System.out.println("테스트 %s 실패: %s %n", m, exc);
        }
    }
}
```

<br>

## _Conclusion_

애너테이션을 활용하여 코드의 가독성을 높일 수 있으나 애너테인션을 선언하고 이를 처리하는 부분에서는 코드 양이 늘어나며 특히 처리 코드가 복잡해져 오류가 날 가능성이 커짐을 명심하자.

애너테이션으로 할 수 있는 일을 명명패턴으로 처리할 이유는 없다. 소스코드에 추가 정보를 제공할 수 있는 도구를 만드는 일을 한다면 적당한 애너테이션 타입도 함께 정의해 제공하자

도구 제작자를 제외하고는 일반 프로그래머가 애너테이션 타입을 직접 정의할 일은 거의 없다. 하지만 자바 프로그래머라면 예외 없이 자바가 제공하는 애너테이션 타입들을 사용해야 한다.
