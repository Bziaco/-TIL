# 로 타입은 사용하지 말라

## 1. 제네릭 타입이란?

- 각각의 제네릭 타입은 일련의 매개변수화 타입(parameterized type)을 정의한다.

- 먼저 클래스(혹은 인터페이스) 이름이 나오고 이어서 꺽쇠괄호 안에 실제 타입 매개변수들을 나열한다. 예컨대 List<String>은 원소의 타입이 String인 리스트를 뜻하는 매개변수화 타입이다.

- 제네릭 타입을 하나 정의하면 그에 딸리 로 타입(raw type)도 함께 정의 된다.

## 2. 로 타입(raw type)이란?

- 로 타입이란 제네릭 타입에서 타입 매개변수를 전혀 사용하지 않을 때를 말한다. List<E>의 로 타입은 List다.

### 2.1 컬렉션의 로 타입 - 따라 하지 말 것!

```js
// Stamp 인스턴스만 취급한다.
private final Collection stamps = ...;

// 실수로 동전을 넣는다.
stamps.add(new Coin(...)); // 'unchecked call'경로를 내뱉는다.
```

- 실제로 위 코드는 Stamp 객체만 받기로 했는데 실수로 Coin 객체를 넣어도 경로만 보일 뿐 컴파일 에러가 나지 않는다. 컬렉션에서 이 동전을 다시 꺼내기 전까지는 오류를 알아채지 못한다.,

### 2.2 매개변수화된 컬렉션 타입 - 타입 안전성 확보!

```js
private final Collection<Stamp> stamps = ...;
```

- 이렇게 선언하면 컴파일러는 stamps에는 Stamp의 인스턴스만 넣어야 함을 컴파일러가 인지하게 된다. 다른 타입의 인스턴스를 넣으면 컴파일 오류를 뱉는다.
    ```js
    Test.java9: error: incompatible types: Coin cannot be converted to Stamp
    stamps.add(new Coiin());
    ```

### 2.3 로 타입을 쓰면 제네릭이 안겨주는 안전성과 표현력을 모두 잃게 된다.

- 절대 써서는 안되지만 이미 많은 기존 코드들이 로 타입을 사용하고 있었기 때문에 호환성 위해 남겨 놓았을 뿐이다.

### 2.4 List< Object >와 List의 차이 

- List< Object >처럼 임의 객체를 허용하는 매개변수 타입은 괜찮다. 둘의 차이는 로 타입은 제네릭 타입에서 완전이 발을 뺀 것이며 List< Object >는 모든 타입을 허용한다는 의사를 컴파일러에게 명확히 전달한 것이다.

- 매개변수로 List를 받는 메서드에 List<String>은 넘길 수 있지만 List< Object >는 넘길 수 없다. 하위 타입 규칙 때문인데 List<String>은 로 타입인 List의 하위 타입이지만 List< Object >는 아니기 때문이다. 결국 로 타입을 사용하면 타입 안전성을 잃게 된다.

```js
public static void main(String[] args) {
    List<String> strings = new ArrayList<>();

    unsafeAdd(Strings, Integer.valueOf(42));
    String s = strings.get(0);
}

private static void unsafeAdd(List list, Object o) {
    list.add(o);
}
```

- 위 코드는 컴파일은 되지만 로 타입인 List를 사용하여 경고가 발생한다.

- 로 타입인 List를 매개변수화 타입인 List<Object>로 바꾼다면 컴파일 조차 되지 않고 에러를 뱉는다.


## 3. 비한정적 와일드카드 타입(unbounded wildcard type)

- 제네릭 타입을 쓰고 싶지만 실제 타입 매개변수가 무엇인지 신경 쓰고 싶지 않을 때 물음표(?)를 사용하자. 

- 제네릭 타입인 Set<E>의 비한정적 와일드 타입은 Set<?>다. 이것이 어떤 타입이라도 담을 수 잇는 가장 범용적인 매개변수화 Set 타입이다.

```js
static int numElementsInCommon(Set<?) s1, Set<?> s2){...}
```

### 3.1 Set<?>과 Set의 차이는?

- 와일드카드 타입은 안전하고 로 타입은 안전하지 않다. 로 타입 컬렉션에는 아무 원소나 넣을 수 잇으니 타입 불변식을 훼손하기 쉽다. 반면 **Collection<?>에는 (null 외에는) 어떤 원소도 넣을 수 없다.** 넣게 된다면 오류 메세지를 보게 될 것이다.

## 4. 로 타입이 사용가능한 몇몇 예외

### 4.1 class 리터널에는 로 타입을 써야 한다.

- 자바 명세는 class 리터널에 매개변수화 타입을 사용하지 못하게 했다.(배열과 기본 타입은 허용한다.)

- List.class, String[].class, int.class는 허용하고 List<String>.class, List<?>.class는 허용하지 않는다.


### 4.2 instanceof 연산자는 비 한정적 와일드카드 타입 이외의 매개변수화 타입에는 적용할 수 없다.

- 로 타입이든 비한정적 와일드카드 타입이든 instanceof는 완전히 똑같이 동작한다.

- 아름은 제네릭 타입에 instanceof를 사용하는 올바른 예다.

```js
if (o instanceof Set) { // 로 타입
    Set<?> s = (Set<?>) o; //와일드카드 타입
}
```

## 5. Conclusion

- 로 타입을 사용하면 런타임에 예외가 일어날 수 있으니 사용하면 안 된다.

- Set< Object >는 어떤 타입의 객체도 저장할 수 잇?는 매개변수화 타입이고 Set<?>는 모종의 타입 객체만 저장할 수 있는 와일드카드 타입이다.

