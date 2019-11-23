# 이왕이면 제네릭 타입으로 만들라.

## 1. Object 기반 Stack -> Generic 기반 Stack

- Object 기반 Stack

     ```js
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

        public boolean imEmpty() {
            return size == 0;
        }

        private void ensureCapacity() {
            if(elements.length == size) {
                elements = Arrays.copyOf(elements, 2 * size +1);
            }
        }
    }
    ```
    - 일반 클래스를 제네릭 클래스로 만드는 첫 단계는 클래스 언언에 타입 매개 변수를 추가하는 일이다. 이때 타입 이름으로는 보통 E를 사용한다.

- Generic 기반 Stack

    ```js
    public class Stack {
        private Object[] elements;
        private int size = 0;
        private static final int DEFAULT_INITAIL_CAPACITY = 16;

        @SuppressWarnings("unchecked")
        public GenericStack() {
            elements = (E[]) new Object[DEFAULT_INITAIL_CAPACITY];
        }

        public void push(E e) {
            ensureCapacity();
            elements[size++] = e;
        }

        public E pop() {
            if(size == 0) { throw new EmptyStackException(); }
            @SuppressWarnings("unchecked")
            E result = (E) elements[--size];
            elements[size] = null;
            return result;
        }

        public boolean imEmpty() {
                return size == 0;
            }

        private void ensureCapacity() {
            if(elements.length == size) {
                elements = Arrays.copyOf(elements, 2 * size +1);
            }
        }
    }
    ```

    - 이 단계에서 하나의 경고 에러가 방생한다.
        ```js
        Stack.java:8: generic array creartion
                elements = new E[DEFAULT_INITAIL_CAPACITY];
        ```
    
    - E와 같은 실체화 불가 타입으로는 배열을 만들수 없다. 적절한 해결책은 두 가지이다. 첫번째는 제네릭 배열 생성을 금지하는 제약을 대놓고 우회하는 방법이다. 두번째는 elements 필드의 타입을 E[]에서 Object[]로 바꾸는 것이다.


### 1.1 제네릭 배열 생성을 금지하는 제약을 우회하는 방법

- Object 배열을 생성한 다음 제네릭 배열로 형변환해보자. 이제 컴파일러는 오류 대신 경고를 내보낼 것이다.
    
    ```js
    Stack.java:8: warning: [unchecked] unchecked cast
    found: Object[], required: E[]
        elements = (E[]) new Object[DEFAULT_INITAIL_CAPACITY];
    ```

- 비검사 형변환이 프로그램의 타입 안전성을 해치지 않음을 스스로 확인해야 한다. 문제의 배열 elements는 private 필드에 저장되고 클라이언트로 반환되거나 다른 메서드에 전달되는 일이 전혀 없다. push 메서드를 통해 배열에 저장되는 원소의 타입은 항상 E다. 따라서 이 비검사 형변환은 확실히 안전하다.

- 비검사 형변환이 안전함을 직접 증명했다면 범위를 최소로 좁혀 어노테이션으로 해당 경고를 숨긴다. 생성자가 비검사 배열 생성말고는 하는 일이 없으니 생성자 전체에 경고를 숨겨도 좋다.

```js
// 배열 elements는 push(E)로 넘어온 E 인스턴스만 담는다.
// 따라서 타입 안전성을 보장하지만 
// 이 배열의 런타임 타입은 E[]가 아닌 Object[]다!
@SuppressWarnings("unchecked")
public GenericStack() {
    elements = (E[]) new Object[DEFAULT_INITAIL_CAPACITY];
}
```

### 1.2 elements 필드의 타입을 E[]에서 Object[]로 바꾼다.

- 이렇게 하면 다른 오류가 발생한다.

    ```js
    Stack.java:19: incompatible types
    found: Object, required: E
        E result = elemnets[--size];
    ```

- 배열이 반환한 원소를 E로 형변환하면 오류 대신 경고가 뜬다.

    ```js
    Stack.java:19: warning: [unchecked] unchecked cast
    found: Object, required: E
        E result = (E) elemnets[--size];
    ```

- 이번에도 스스로 직접 증명하고 경고를 숨길 수 있다.
    ```js
    public E pop() {
        if(size == 0) { throw new EmptyStackException(); }
        @SuppressWarnings("unchecked")
        E result = (E) elements[--size];
        elements[size] = null;
        return result;
    }
    ```

### 1.3 위 두가지 방식 중 첫번째 방식을 현업에서 더 선호한다.

- 첫번째 방법은 배열을 생성할 때 형변환을 한번만 해주지만 두번째 방법은 배열에서 원소를 읽을 때 마다 해줘야 한다.

- 하지만 배열의 런타임 타입이 컴파일타임 타입과 달라 힙 오염(heap pollution)을 일으킨다. 힙 오염이 맘에 걸리는 프로그래머는 두 번째 방식을 고수하기도 한다.

- 제네릭 Statck을 사용한느 맛보기 프로그램이다.

    ```js
    public static void main(String[] args) {
        Stack<String> statck = new Stack<>();
        for(String arg : args) {
            statck.push(arg);
        }
        while(!stack.isEmpty())
            System.out.println(statck.pop().toUpperCase());
    }
    ```

## 2. '배열보다 리스트를 우선하라'는 아이템28과 모순돼 보인다??

- 제네릭 타입 안에서 리스트를 사용하는 게 항상 가능하지도 꼭 더 좋은 것도 아니다. 자바가 리스트를 기본 타입으로 제공하지 않으므로 ArrayList 같은 제네릭 타입도 결국은 기본 타입인 배열을 사용해 구현해야 한다. 또한 HashMap 같은 제네릭 타입은 성능을 높일 목적으로 배열을 사용하기도 한다.

## 3. Conclusion

- 클라이언트에서 직접 형변환해야 하는 타입보다 제네릭 타입이 더 안전하고 쓰기 편하다. 그러니 새로운 타입을 설계할 때는 형변환 없이도 사용할 수 있도록 하라. 그렇게 하려면 제네릭 타입으로 만들어야 할 경우가 많다.