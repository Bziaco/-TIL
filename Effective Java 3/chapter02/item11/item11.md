<h1>equals를 재정의할 때 hashCode도 재정의 하라</h1>

<h2>왜 equals를 재정의 하면 hashCode도 재정의 해야 하는가?</h2>

- equals를 재정의 한 후 hashCode를 재정의 하지 않으면 hashMap과 hashSet과 같은 컬렉션의 원소로 사용할 때 문제를 일으킬 것이다.

- Hash를 사용한 Collections는 key를 결정할 때 hashCode를 사용하기 때문이다.

<h2>HashCode 재정의</h2>

```
public final class PhoneNumber {
    private final short areaCode, prefix, lineNum;

    public PhoneNumber(int areaCode, int prefix, int lineNum) {
        this.areaCode = areaCode;
        this.prefix = prefix;
        this.lineNum = lineNum;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof PhoneNumber)) {
            return false;
        }

        if( o == this) {
            return true;
        }

        PhoneNumber pn = (PhoneNumber) o;
        return pn.areaCode == areaCode && pn.prefix = prefix
            && pn.lineNum = lineNum;
    }
}
```

- areaCoded, prefix, lineNum 3개의 변수를 받는 클래스이다. 3개의 변수가 모두 같은 때 같은 값으로 인식하도록 equals를 재정의 했다.

- equals만 재정의 한 경우 PhoneNumber를 참조하는 인스턴스는 equals로 비교하면 동치객체로 인식한다. 그러나 Hash를 사용한 Map, Set 등의 사용하여 같은 키값으로 인식하길 원한다면 원치 않은 결과 값이 나올 것이다.

- Map, Set과 같은 Hash를 사용한 Collections는 위에서도 언급했듯 key를 결정할때 hashCode를 이용하기 때문에이다.

- 그래서 같은 Key값으로 사용하길 원한다면 hashCode값을 재정의 해줘야 한다. 아래 코드는 PhoneNumber에 대한 hashCode를 재정의 하는 코드이다.

```
@Override
public int hashCode() {
    private final int minority = 31;
    int result = minority + Short.hashCode(areaCode);
    result = minority * result + Short.hashCode(prefix);
    result = minority * result + Short.hashCode(lineNum);
    return result;
}
```

<h3>31을 곱하는 이유는 무엇인가?</h3>

- String의 hashCode를 곱셈 없이 구현한다면 모든 아나그램(anagram, 구성하는 철자가 같고 그 순서만 다른 문자열)의 해시코드가 같아진다.

- 굳이 곱셈시 31을 사용하는 이유는 31이 홀수이면서 소수이기 때문이다. 소수를 곱하는 이유는 명확하지는 않지만 전통적으로 그렇게 해왔다고 한다.

<br>

<h2>hashCode를 재정의 할 때 주의사항</h2>

- equals에 재정의 되지 않는 필드는 hashCode를 재정의 할 때도 반드시 제외해야 한다. 그렇지 않으면 equals가 두객체를 같아고 판단했지만 hashCode는 다른 값을 반환하기 때문이다.

- 성능을 높이기 위해 해시코드를 계산할 때 핵심 필드를 생략해서는 안된다.

- 서로 다른 인스턴스라면 되도록 해시코드를 서로 다르게 구현해야 한다.

<br>

<h2>Objects 클래스의 hashCode 메서드</h2>

- Objects 클래스는 임의의 갯수만큼 객체를 받아 해시코드를 계산해주는 정적 메서드인 hashCode가 있다.

- 하지만 입력 인수를 담기 위한 배열이 만들어지고 입력 중 기본 타입이 있다면 박싱과 언박싱도 거쳐야 하기 때문에 성능이 살짝 아쉽다. 그렇기 때문에 성능에 민감하지 않은 경우에만 사용하도록 하자.

<br>

<h2>hashCode를 캐싱하여 사용하는 상황을 고려하자.</h2>

- 클래스가 불변이고 해시코드를 계산하는 비용이 크다면(타입의 객체가 주로 키로 사용되는 경우 등) 매번 새로 계산하기 보다는 캐싱하는 방식을 고려하자.

<h3>hashCode가 키로 사용되지 않는 경우에는?</h3>

- 지연 초기화(lazy initialization)을 고려하자

- 지연 초기화하려면 그 클래스를 스레드 안전하게 만들도록 신경써야 한다.

```
private int hashCode; //자동으로 0으로 초기화한다.

@override
public int hashCode() {
    int result = hashCode;

    if(result == 0) {
        private final int minority = 31;
        int result = minority + Short.hashCode(areaCode);
        result = minority * result + Short.hashCode(prefix);
        result = minority * result + Short.hashCode(lineNum);
    }
    return result;
}
```
