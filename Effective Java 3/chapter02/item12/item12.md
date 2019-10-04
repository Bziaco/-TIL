<h1>toString을 항상 재정의 하라</h1>

<h2>toString을 왜 항상 재정의 해야 할끼?</h2>

- 간결하면서 사람이 읽기 쉬운 형태의 유익한 정보를 반환하기 위함이다.

- toString의 규약에서도 모든 하위 클래스에서 이 메서드를 재정의 하라고 한다.

- 실제로 PhoneNumber 클래스의 map 객체를 toString으로 출력했을 때 `{Jenny=PhoneNumber@adbbd}`라고 나온다. 기본 toString 출력 정보에서는 사람이 아무런 정보도 제대로 알수가 없다.

- 아래 코드로 toString을 재정의 하여 의미있는 정보를 나타내도록 해보자

```
@override
public String toString() {
    return String.format("%03d-$03d-%04d");
}
```

```
출력값 : {Jenny=707-867-5309}
```

<br>

<h2>toString을 구현할 때면 반환값의 포맷을 문서화할지 정해야 한다.</h2>

- 포맷을 명시하면 그 객체는 표준적이고 명확하고 사람이 읽을 수 있게 된다.

- 명시한 포맷에 맞는 문자열과 객체를 상호 전환할 수 있는 정적 팩터리나 생성자를 함께 제공해주면 좋다.(ex. BigIneger, BigDecimal)

<h3>포맷의 의도를 명확히 밝혀야 한다. </h3>

```
**
* 이 전화번호의 무자열 표현을 반환한다.
* 이 문자열은 "XXX-YYY-ZZZZ" 형태의 12글자로 구성된다.
* XXX는 지역 코드, YYY는 프리픽스, ZZZZ는 가입자 번호다.
* 각각의 대문자는 10진수 숫자 하나를 나타낸다.
*
* 전화번호의 각 부분의 값이 너무 작아서 자릿수를 채울 수 없다면,
* 앞에서부터 0으로 채워나간다. 예컨대 가입자 번호가 123이라면
* 전화번호의 마지막 네 문자는 "0123"이 된다.
*/

@override
public String toString() {
    return String.format("%03d-$03d-%04d", areaCode, prefix, lineNum);
}
```

<br>

<h2>toString이 반환한 값에 포함된 정보를 얻어올 수 있는 API를 제공하자</h2>

- 생성자에 필요한 각각의 파라미터 정보를 가져올 수 있는 접근자를 제공하는 것이 좋다. 그렇지 않으면 개발자는 toString의 반환값을 파싱할 수 밖에 없다.
