<h1>다 쓴 객체 참조를 해제하라</h1>

- 자바에는 다 쓴 객체를 알아서 회수해 가는 가비지 컬렉터가 있다. 
- 그래서 메모리 관리에 신경을 쓰지 않아도 된다고 오해할 수 있는데 절대 그렇지 않다.

```
public class Stack {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITAIL_CAPACITY = 16;

    public Stack() {
        elements = new Object[DEFAULT_INITAIL_CAPACITY];
    }

    public void push(Object e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public Object pop() {
        if(size = 0) { throw new EmptyStackException(); }
        return element[--size];
    }

    private void ensureCapacity() {
        ...생략
    }
}
```
- 이 코드는 스택이 커졌다가 줄어들었을 때 스택에서 꺼내진 객체들을 가비지 컬렉터가 회수하지 않는다.
- 가바지 컬렉션 언어에서는 메모리 누수를 찾기가 아주 까다롭다. 
- 그 객체가 참조하는 모든 객체를 회수해가지 못한다.
- 위 문제의 해법은 해당 참조를 다 썼을 경우 null(참조해제) 처리를 하면 된다.
    ```
    public Object pop() {
        if(size = 0) { throw new EmptyStackException(); }
        Object result = element[--size];
        element[size] = null;
        return result;
    }
    ```
    -  다 쓴 참조를 null 처리하면 실수로 null 처리한 참조를 실수로 사용하려 할 경우 프로그램은 즉시 NullPointerException을 던지며 종료한다.
    - 하지만 모든 경우를 null 처리하는 것은 좋지 못하며 객체 참조를 null 처리하는 일은 예외적인 경우여야 한다.
    - 다쓴 참조를 해제하는 가장 좋은 방법은 그 참조를 담은 변수를 유효범위 박으로 밀어내는 것이다.

<h2>null 처리는 언제 해야 할까?</h2>

- stack자체는 자기 메모리를 직접 관리하기 때문에 메모리 누수에 취약하다.
- 배열의 활성영역에 원소들이 사용되고 비활성 영역이 되버리면 이는 프로그래머는 알지만 가비지 컬렉터에게는 똑같이 유효한 객체이기 때문에 비활성 영역이 되는 순간 가비지 컬렉터에게 해당 객체를 더이상 쓰지 않을 것이라고 알려야 한다.

> 캐시 역시 메모리 누수를 일으키는 주범이다.
- 캐시 외부에서 키를 참조하는 동안만 엔트리가 살아 있는 캐시가 필요한 상황이라면 WeakHashMap을 사용해 캐쉬를 만들자. 다 쓴 엔트리는 그 즉시 자동으로 제거될 것이다.(WeakHashMap는 이러한 상황에서만 유용하다.)
- 캐시를 만들 때 유효기간을 정확히 정의하기 어렵다면 시간이 지날수록 엔트리의 가치를 떨어 뜨리는 방식인 LinkedHashMap을 사용하자. LinkedHashMap은 removeEldestEntry 메서드를 써서 위와 같은 방식으로 처리한다.

> 리스너 혹은 콜백
- 클라이언트가 콜백을 등록만 하고 명확히 해지하지 않는다면 한 콜백은 계속 쌓일 것이다. 이 때 콜백을 약한 참조로 저장하면 가비지 컬렉터가 즉시 수거해 간다. 에를 들어 WeakHashMap에 키로 저장하면 된다.