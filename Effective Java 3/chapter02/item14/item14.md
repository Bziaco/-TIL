# Comparable을 구현할지 고려하라

## 1. Comparable 인터페이스란 무엇인가?

### 1.1 두가지만 빼면 Object의 equals와 같다.

- Comparable의 compareTo() 메서드는 **단순 동치성 비교에 더해 순서까지 비교할 수 있으며 제네릭**하다.

- Comparable을 구현했다는 것은 그 클래스의 인스턴스들에는 자연적인 순서(natural order)가 있음을 뜻한다.

- 그래서 Comparable을 구현한 객체 들은 *Arrays.sort(a)* 과 같이 손쉽게 정렬이 가능하다.

- 자바 플랫폼 라이브러리의 모든 값 클래스와 결거타입이 Comparable을 구현했다.

- 알파벳, 숫자, 연대 같이 순서가 명확한 값 클래스를 작성한다면 반드시 Comparable 인터페이스를 구현하자.

<br>

## 2. compareTo 메서드의 정의 및 일반 규약

### 2.1 compareTo 메서드 정의

 ***이 객체와 주어진 객체의 순서를 비교한다. 이 객체가 주어진 객체보다 작으면 음의 정수를 같으면 0을 크면 양의 정수를 반환한다. 이 객체와 비교할 수 없는 타입의 객체가 주어지면 ClassCastException을 던진다.***

#### 2.2 compareTo 사용시 타입이 다른 객체를 비교한다면?

- equals 메서드와 달리 compareTo는 타입이 다른 객체를 신경쓰지 않아도 되며 타입이 다른 객체가 주어지면 간단히 *ClassCastException*을 던지면 된다.

- 물론, 이 규약에서는 다른 타입의 사이의 비교도 허용하는데 보통은 비교할 객체들이 구현한 공통 인터페이스를 매개로 이뤄진다.

- 비교를 활용하는 클래스의 예로는 정렬된 컬렉션인 *TreeSet과 TreeMap* , 검색과 정렬 알고리즘을 활용하는 유틸리티 클래스인 *Collections와  Arrays*가 있다.

### 2.3 compareTo 메서드 규약

#### 2.3.1 두 객체 참조의 순서를 바꿔 비교해도 예상한 결과가 나와야 한다.

- 첫번째 객체가 두번째 객체보다 작으면 두번째가 첫번째보다 커야한다.

- 첫번째가 두번째와 크기가 같아면 두번째는 첫번째와 같아야 한다.

- 첫번째가 두번째보다 크면 두번째는 첫번째보다 작아야 한다.

#### 2.3.2 첫번째가 두번째보다 크고 두번째가 세번째보다 크면 첫번째는 세번째보다 커야 한다.

#### 2.3.3 크기가 같은 객체들끼리는 어떤 객체와 비교하더라도 항상 같아야 한다.

#### 2.3.4 compareTo 메서드로 수행한 동치성 테스트의 결과가 equals와 같아야 한다는 것이다.(필수는 아님!)

- 이를 잘 지키면 compareTo로 줄지은 순서와 equals의 결과가 일관되게 된다.

- compareTo의 순서와 equals의 결과가 일관되지 않은 클래스도 여전히 동작은 한다. 단 컬렉션에 넣으면 해당 컬렉션이 구현한 인터페이스에 정의된 동작과 맞지 않을 것이다.

#### 2.3.5 compareTo 작성시 주의 사항

- equals 규약과 똑강이 반사성, 대칭성, 추이성을 충족해야 한다. 그래서 기존 클래스를 확장한 구체 클래스에서 새로운 값 컴포넌트를 추가 했다면 compareTo 규약을 지킬 방법이 없다. 

- 위 문제에 대한 우회법도 equals와 같다. 확장하는 대신 독립된 클래스를 만들고 이 클래스에 원래 클래스의 인스턴스를 가리키는 필드를 두자. 그런 다음 내부 인스턴스를 반환하는 뷰 메서드를 제공하면 된다.

#### 2.3.6 인터페이스의 정렬된 컬렉션들은 동치성을 비교할 때 equals 대신 compareTo를 사용한다.

- compareTo와 equals가 일관되지 않은 *BigDecimal* 클래스를 예로 보자.

    ```js
    public class ComparableTest {

        public static void main(String[] args) {
            Set<BigDecimal> hs = new HashSet<>();
            Set<BigDecimal> ts = new TreeSet<>();
            BigDecimal db1 = new BigDecimal("1.0");
            BigDecimal db2 = new BigDecimal("1.00");
            
            hs.add(db1);
            hs.add(db2);
            
            ts.add(db1);
            ts.add(db2);
            
            System.out.println("HashSet size : " + hs.size());
            System.out.println("TreeSet size : " + ts.size());

            //결과
            //HashSet size : 2
            //TreeSet size : 1
        }

    }
    ```

- 위 예제 코드에서 보듯 HashSet은 equals 메서드로 비교하면서 서로 다르기 때문에 원소를 2개 갖는다. 그러나 TreeSet은 compareTo 메서드로 비교하면 두 BigDecimal 인스턴스가 똑같기 때문에 원소를 1개를 갖는다.(자세한 설명은  BigDecimal 문서 참고)

<br>

## 3. compareTo 메서드 작성 요령

### 3.1 compareTo 메서드 입력 인수의 타입을 확인하거나 형변활할 필요가 없다.

- Comparable은 타입을 인수로 받는 제네릭 인터페이스이므로 compareTo 메서드의 인수 타입은 컴파일타임에 정해진다.

- 인수의 타입이 잘못되었다면 컴파일 자체가 되지 않으며 **null을 인수로 넣어 호출하면 *NullPointerException*을 던져야 한다**.


### 3.2 compareTo 메서드는 각 필드가 동치인지를 비교하는게 아니라 그 순서를 비교한다.

### 3.3 객체 참조 필드를 비교하려면 compareTo 메서드를 재귀적으로 호출한다.

### 3.4 Comparable을 구현하지 않은 필드나 표준이 아닌 순서로 비교해야 한다면 비교자(Comparator)를 대신 사용한다.

- 자바에서 제공하는 비교자를 사용한 CaseInsensitiveString용 compareTo 메서드
    ```js
    public final class CaseInsensitiveString implements Comparable<CaseInsensitiveString>{
        public int compareTo(CaseInsensitiveString cis) {
            return String.CASE_INSENSITIVE_ORDER.compare(s, cis.s);
        }
    }
    ```

    - CaseInsensitiveString이 Comparable<CaseInsensitiveString>을 구현한 것은 CaseInsensitiveString의 참조는 CaseInsensitiveString 참조와만 비교할 수 있다는 뜻이며 Comparable을 구현할 때 일반적으로 따르는 패턴이다.

> *| 참고*
>> compareTo 메서드에서 관계 연산자 <와 >를 사용하는 이전 방식은 거추장스럽고 오류를 유발하니 사용하지 말 것

### 3.5. 핵심 필드가 여러개 일 경우 어떤 것부터 비교 하는지?

- 가장 핵심적인 필드부터 비교해 나가자. 가장 핵심이 되는 필드가 똑같다면 똑같지 않은 필드를 찾을 때까지 그 다음으로 중요한 필드를 비교해 나가자.

- PhoneNumber 클래스용 compareTo 메서드 구현 예

    ```js
    private static final Comparator<PhoneNumber> COMPARATOR = 
        comparingInt((PhoneNumber pn) -> pn.areaCode)
            .thenComparingInt(pn -> pn.prefix)
            .thenComparingInt(pn -> pn.lineNum);
    
    public int compareTo(PhoneNumber pn) {
        return COMPARATOR.compare(this, pn);
    }
    ```
    
    - comparingInt는 객체 참조를 int 타입 키를 매핑하는 키 추출함수를 인수로 받아 그 키를 기준으로 순서를 정하는 비교자를 반환하는 정적메서드이다.

    - comparingInt는 람다를 인수로 받으며 PhoneNumber에서 추출한 지역 코드를 기준으로 전화번호의 순서를 정하는 Comparator<PhoneNumber>를 반환한다.

#### 3.5.1 두 전화번호의 지역 코드가 같은 경우

- 두 전화번호의 지역 코드가 같을 경우 두번째 비교자 생성 메서드인 thenComparingInt를 활용해 int 키 추출자 함수를 입력받아 다시 비교자를 반환한다.

- thenComparingInt는 원하는 만큼 연달아 호출할 수 있다.

### 3.6 객체 참조용 비교자 생성 메서드

- comparing이라는 정적 메서드 2개가 다중정의 되어 있다. 첫번째는 키 추출자를 받아서 그 키의 자연적 순서를 사용한다. 두번째는 키 추출자 하나와 추출된 키를 비교할 비교자까지 2개의 인수를 받는다. 또한 thenComparing이란 인스턴스 메서드가 3개 다중정의 되어 있다.

### 3.7 값의 차를 기준으로 음수,0,양수를 반환하는 compareTo 또는 compare 메서드

#### 3.7.1 해시코드 값의 차를 기준으로 하는 비교자

- 결론부터 말하자면 이는 추이성을 위배한다. 이 방식은 정수 오버플로를 일으키거나 IEEE 754 부동소수점 계산방식에 따른 오류를 낼 수 있다. 그렇다고 속도도 빠른 것도 아니다.

    ```js
    static Comparator<Object> hashCodeOrder = new Comparator<>() {
        public int compare(Object 01, Object 02) {
            return o1.hashCode() - o2.hashCode();
        }
    }
    ```

- 대신 아래 다음의 두 방식 중 하나를 사용하자.

#### 3.7.2 정적 compare 메서드를 활용한 비교자

```js
static Comparator<Object> hashCodeOrder = new Comparator<>() {
    public int compare(Object 01, Object 02) {
        return Integer.compare(o1.hashCode(), o2.hashCode());
    }
}
```

#### 3.7.3 비교자 생성 메서드를 활용한 비교자
```js
static Comparator<Object> hashCodeOrder = 
    Comparator.comparingInt(o -> o.hashCode());
```

<br>

## 4. Conclusion

- 순서를 고려해야 하는 값 클래스를 작성한다면 꼭 Comparable 인터페이스를 구현하여 그 인스턴스들을 쉽게 정렬하고 검색하고 비교 기능을 제공하는 컬렉션과 어우려지도록 해야한다.

- compareTo 메서드에서 필드의 값을 비교할 때 <와 > 연산자는 쓰지 말아야 한다.

- 그 대신 박싱된 기본 타입 클래스가 제공하는 정적 compare 메서드나 Comparator 인터페이스가 제공하는 비교자 생성 메서드를 사욯하자.
