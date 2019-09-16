<h1>인스턴스화를 막으려거든 private 생성자를 사용하라</h1>

- java.lang.Math, java.util.Arrays처럼 기본 타입 값이나 배열 관련 메서드들을 모아놓을 수 있다.
- 또한 java.util.Collections처럼 특정 인터페이스를 구현하는 객체를 생성해주는 정적 메서드를 모아 놓을 수도 있다.

<h2>정적 멤버만 담은 유틸리티 클래스는 인스턴스로 만들어 쓰려고 설계한 게 아니다.</h2>

- 생성자를 명시하지 않으면 컴파이러가 자동으로 public 기본 생성자를 만들며 사용자는 이 생성자가 자동 생성된 것인지 구분할 수 없다.

<h2>추상 클래스로 만드는 것으로는 인스턴스화를 막을 수 없다.</h2>

- 하위 클래스를 만들어 인스턴스화하면 그만이다.

<h2>private 생성자를 추가하면 클래스의 인스턴스화를 막을 수 있다.</h2>

```
public class UtilityClass {
    private UtilityClass() {
        throw new AssertionError();
    }
    ...
}
``` 

- 명시적 생성자가 private이니 클래스 바깥에서는 접근할 수 없다.