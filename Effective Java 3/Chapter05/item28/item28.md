# 배열보다는 리스트를 사용하라

## 1. 배열과 제네릭의 차이

- 배열은 공변, 리스트는 불공변이다.

- 배열에서는 그 실수를 런타임에 알게 되지만 리스트는 컴파일 단게에 알게 된다.

    ```js
    //문법상 허용하지만 런타임에 실패한다.
    Object[] objectArray = new Long[1];
    objectArray[0] = '타입이 달라 넣을 수 없다.'; //ArrayStoreException을 던진다.

    //문법상 허용되지 않으므로 컴파일 단계에 실패한다.
    List<Object> ol = new HashList<Long>(); //호환되지 않는 타입이다.
    ol.add("타입이 달라 넣을 수 없다.");
    ```

- 배열은 실체화(reify)된다. 즉, 배열은 런타임에도 자신이 담기로 한 원소의 타입을 인지하고 확인한다. 리스트는 컴파일 단계에만 타입을 검사하고 런타임에는 타입을 알 수 조차 없다.

## 2. 제네릭 배열을 만들지 못하게 막은 이유는 무엇일까?

- 타입이 안전하지 않기 때문이다. 런타임에 ClassCastException이 발생하는 일을 막아주겠다는 제네릭 타입 시스템의 취지에 어긋하는 일이 생기기 때문이다. 타입이 안전하지 않기 때문에 컴파일러가 자동 생성한 형변환 코드에서 런타임에 ClassCastException이 발생할 수 있다.

### 2.1 제네릭 배열 생성을 허용하지 않는 구체적인 상황

```js
List<String>[] stringLists = new List<String>[]; //(1)
List<Integer>[] intList = new List.of(42); //(2)
Object[] objects = stringLists;  //(3)
objects[0] = intList;  //(4)
String s = stringLists[0].get(0) //(5)
```

- (1)이 허용된다고 가정해보자. (2)는 원소가 하나인 List<Integer>를 생성한다. (3)은 (1)에서 생성한 리스트를 할당한다. 배열은 공변이니 아무 문제 없다. (4)는 (2)에서 생성한 리스트의 인스턴스를 Object배열의 첫번째 원소로 저장한다. 하지만 (5)에서 문제이다. List<String>[]만 담겠다고 선언한 stringLists 배열에는 List<Integer> 인스턴스가 저장돼 있다. 그리고 (5)는 이 배열의 처음 리스트에서 첫 원소를 꺼내려 한다. 컴파일러는 꺼낸 원소를 자동으로 String으로 형변환하는데 이 원소는 Integer이므로 런타임에 ClassCastException이 발생한다. 

## 3. 실체 불가화 타입(non-reifiable type)

- E, List<E>, List<String>와 같은 타입을 실체화 불가 타입이라고 한다. 즉, 실체화되지 않아서 런타임에는 컴파일타임보다 타입 정보를 적게 가지는 타입이다.

- 소거 메커니즘 때문에 매개변수화 타입 가운데 실체화될 수 있는 타입은 List<?>와 Map<?,?>같은 비한정적 와일드카드 타입 뿐이다.

## 4. 배열로 형변환 시 형변환 경고에 대한 대처 방법

- 제네릭 배열 생성 오류나 비검사 형변환 경고가 뜨는 경우 대부분은 배열인 E[] 대신 컬렉션인 List<E>를 사용하면 해결된다. 

- 코드가 조금 복잡해지고 성능이 살짝 나빠질 수도 있지만 그 대신 타입 안정성과 상호운용성은 좋아진다.

## 5. 제네릭 적용 예시

- 다음은 제네릭을 쓰지 않고 구현한 가장 간단한 버전이다.

    ```js
    public class Chooser {
        private final Object[] choiceArray;

        public Chooser(Collection choice) {
            choiceArray = choices.toArray();
        }

        public Object choose() {
            Random rnd = ThreadLocalRandom.current();
            return choiceArray[rnd.nextInt(choiceArray.length)];
        }
    }
    ```

    - 이 클래스를 사용하려면 choose 메서드를 호출할 때마다 Object를 원하는 타입으로 형변환 해야 한다. 혹시나 타입이 다른 원소가 들어 있었다면 런타임에 형변환 오류가 날 것이다.

- Chooser를 제네릭으로 만들기 위한 첫 시도 - **컴파일 되지 않음**

    ```js
    public class Chooser<T> {
        private final T[] choiceArray;

        public Chooser(Collection<T> choice) {
            choiceArray = choices.toArray();
        }

        public Object choose() {
            Random rnd = ThreadLocalRandom.current();
            return choiceArray[rnd.nextInt(choiceArray.length)];
        }
    }
    ```

    - 이 클래스를 컴파일 하면 다음의 오류 메시지가 출력될 것이다.

        ```js
        Chooser.java:9: error: imcompatible types: Object[] cannot be
        converted to T[]
                choiceArray = choices.toArray();

            where T is a type-variable:
                T extends Object declared in class Chooser
        ```

    - `choiceArray = (T[]) choices.toArray();` 처럼 Object 배열을 T 배열로 형변환하면 된다.

    - 그런데 이번엔 다른 경고가 뜬다.
        ```js
        Chooser.java:9: warning: [unchecked] unchecked cast
                choiceArray = (T[]) choices.toArray();
            
            required: T[], found: Object[]
            where T is a type-variable:
        T extends object declared in class Chooser
        ```

        - T가 무슨 타입인지 알수 없으니 컴파일러는 이 형변환이 런타임에도 안전한지 보장할 수 없다는 메세지이다. 제네릭에서는 원소의 타입정보가 소거되어 런타임에는 무슨 타입인지 알 수 없음을 기억하자!

    - 타입 안정성을 확신한다고 한다면 애너테이션을 달고 끝내고 되겠지만 애초에 경고의 원인을 없애는 쪽으로 하는 것이 더 좋다. 그래서 배열 대신 리스트로 바꾼다.


        ```js
        public class Chooser<T> {
            private final List<T> choiceList;

            public Chooser(Collection<T> choices) {
                choiceList = new ArrayList<>(choices);
            }

            public Object choose() {
                Random rnd = ThreadLocalRandom.current();
                return choiceArray.get(rnd.nextInt(choiceList.size()));
            }
        }
        ```

## 6. Conclusion

- 배열은 공변이고 실체화되는 반면 제네릭은 불공변이고 타입 정보가 소거된다. 그 결과 배열은 런타임에는 타입 안전하지만 컴파일타임에는 그렇지 않다. 제네릭은 반대이다.

- 배열과 제네릭을 섞어 쓰는 일은 쉽지 않다. 둘을 섞어 쓰다 컴파일 오류나 경고를 만나면 가장 먼저 배열을 리스트로 대체하는 방법을 적용해보자.