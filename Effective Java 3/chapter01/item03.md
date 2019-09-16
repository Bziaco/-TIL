<h1>private 생성자나 열거 타입으로 싱글턴임을 보증하라</h1>

<h2>싱글턴을 만드는 방식은 보통 둘 중 하나이다.</h2>

- 두 방식 모두 생성자는 private로 감춰두고 유일한 인스턴스에 접근할 수 있는 수단으로 public static 멤버를 하나 마련한다.

<h3>public static 멤버가 final 필드인 방식을 살펴보자</h3>

```
public class Elvis {
    public static final Elvis INSTACE = new Elvis();
    private Elvis() {...}

    public void leaveTheBuilding() {...}
}
```
- private 생성자는 public static final 필드인 Elvis.INSTANCE를 초기화ㅏ 할 때 딱한번 호출된다. 
- public이나 protected 생성자가 없으므로 Elvis 클래스가 초기화될 때 만들어진 인스턴스가 하나뿐임을 보장된다.
- private 생성자를 호출하는 공격에 방어하려면 생성자를 수정하여 두번째 객체가 생성되려 할 때 예외를 던지게 하면 된다.
- 위 방법의 장점은 public static 필드가 final 이므로 해당 클래스가 싱글턴임이 API에 명백히 드러나며 간결하다는 장점이 있다.

<h3>정적 팩터리 메서드를 public static 멤버로 제공한다.</h3>

```
public class Elvis {
    private static final Elvis INSTACE = new Elvis();
    private Elvis() {...} //생성자
    public static Elvis getInstance() { return INSTACE; }

    public void leaveTheBuilding() {...}
}
```
- Elvis.getInstance는 항상 같은 객체의 참조를 반환하므로 제2의 Elvis 인스턴스는 절대 만들어 지지 않는다.

<h3>원소가 하나인 열거 타입을 선언</h3>

```
public enum Elvis {
    INSTANCE;

    public void leaveTheBuilding() {...}
}
```
- 대부분 상황에서는 원소가 하나뿐인 열거 타입이 싱글턴을 만드는 가장 좋은 방법이다.
- 단 만들려는 싱글턴이 Enum 외의 클래스를 상속해야 한다면 이 방법은 사용할 수 없다.