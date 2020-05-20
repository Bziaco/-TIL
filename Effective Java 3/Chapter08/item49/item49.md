# 매개변수가 유효한지 검사하라.

## 매서드 몸체가 실행되기 전에 매개변수를 확인하라

메서드 몸체가 실행되기 전에 매개변수를 확인한다면 잘못된 값이 넘어왔을때 즉각적이고 깔끔한 방식으로 예외를 던질수 있다.

매개변수 검사를 제대로 하지 못하면 몇가지 문제가 발생할 수 있다.

- 메서드가 수행되는 중간에 모호한 예외를 던지며 실패
- 메서드가 정상 작동했지만 잘못된 결과값을 반환할 때
- 수없이 정상작동했지만 미래의 알 수 없는 시점에서 메서드와 관련없는 오류를 발생

</br>

## public과 protected 메서드는 매개변수 값이 잘못됐을 때 던지는 예외를 문서화해야 한다.

@throws 자바독 태그를 사용, 매개변수의 제약을 문서화한다면 그 제약을 어겼을 때 발생하는 예외도 함께 기술해야 한다.

예)
```js
* 항상 음이 아닌 BigInteger를 반환한다는 점에서 remainder 메서드와 다르다.
* 
* @param m 계수(양수여야 한다.)
* @return 현재 값 mod m
* @throws ArithmeticException m이 0보다 작거나 같으면 발생한다.
*/
public BigInteger mod(BigInteger m) {
    if(m.signum() <= 0) {
        throw new ArithmeticException("계수(m)는 양수여야 합니다." + m);
        //.....
    }
}
```

위 예에서 m이 null이면 nullPointException을 던지는데 이는 메서드 수준이 아닌 클래스 수준에서 기술했기 때문이다. 모든 메서드에 적용되는 주석의 예외 내용을 다 적기 보단 클래스 수준에서 적는 것이 훨씬 깔끔하기 때문이다.

</br>

## Object의 requireNonNull, checkFromIndexSize,checkFromToIndex, checkIndex 

java.util.Objects.requireNonNull 메서드는 null 체크를 자동으로 해주며 null이 아닐시 값을 그대로 반환하고 null이라면 예외를 던진다. 

checkFromIndexSize,checkFromToIndex, checkIndex라는 메서드는 null검사만큼 유용하지는 않지만 제약이 걸림돌이 되지 않는 상황에서라면 리스트와 배열 전용으로 범위 검사 기능으로 사용하면 좋다.

</br>

## public이 아닌 메서드라면 단언문(assert)을 사용해 매개변수 유효성을 검증할 수 있다.

오직 유효한 값만이 메서드에 넘겨지리라는 것을 보증할 수 있고 그렇게 해야 한다.

`재귀 정렬용 private 도우미 함수`

```js
private static void sort(long a[], int offset, int length) {
    assert a != null;
    assert offset >= 0 && offset <= a.length;
    assert length >= 0 && length <= a.length - offset;
    //....
}
```
위 단언문의 핵심은 자신이 단언한 조건이 무조건 참이라고 선언한다는 것이다. 단언문은 몇가지 면에서 일반적인 유효성 검사와 다르다.

- 실패하면 AssertionError를 던진다. 
- 런타임에 아무런 효과도 아무런 성능 저하도 없다.

</br>

## 메서드가 직접 사용하지는 않으나 나중에 쓰기 위해 저장하는 매개변수는 특히 더 신경써야 한다.

만약 리스트 또는 배열을 반환하는 메서드가 존재한다고 가정하자. 이 메서드를 Object.requireNonNull을 이용해 null검사를 수행하며 클라이언트가 null을 건내면 NullpointException이 발생한다. 만약 이 검사를 생략했다면 클라이언트가 돌려받은 List를 사용하려 할때 비로소 예외가 발생하며 디버깅이 어려워진다.

<br>

## 메서드 매개변수 유효성 검사 규칙 예외

유효성 검사 비용이 지나치게 높거나 실용적이지 않을 때, 혹은 계산 과정에서 암묵적으로 검사가 수행될 때다.
 
예를 들어 Collection.sort(List)처럼 객체 리스트를 정렬하는 메서드를 생각해보자.
리스트 안의 객체들은 모두 상호 비교될 수 있어야 하며 정렬 과정에서 이 비교가 이뤄진다. 만약 상호 비교 될수 없는 타입 객체가 들어 있다면 그 객체와 비교할 때 classCastException을 던질 것이다. 따라서 비교하기 앞서 리스트 안의 모든 객체가 상호 비교될 수 있는지 검사해봐야 별다른 실익이 없다.

</br>
## Conclusion

메서드나 생성자를 작성할 때면 그 매개변수들에 어떤 제약이 있을지 생각해야 한다. 그 제약들을 문서화하고 메서드 코드 시작 부분에서 명시적으로 검사해야 한다. 이런 습관을 반드시 기르도록 하자.