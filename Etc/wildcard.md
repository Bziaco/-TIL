# **타입 가변성(variance)과 wildcard**

## *extends type wildcard*

```js
List<? extends String> stringList1 = new ArrayList<String>();
String first = stringList1.get(0);
stringList1.add("abc"); //에러발생
```

`? extends String`은 String의 **하위 타입**으로 리루어진 리스트 객체를 받는다는 의미이다. stringList1 타입은 String의 하위 타입이기 때문에 
`stringList1.get(0)` 값을 가져와 String 변수에 값을 대입할 수 있다. 그러나 `stringList1.add("abc")`에서는 정확히 무슨 타입인지 모르는 리스트에 String 값을 추가 하려니 에러가 나는 것이다.

## *super type wildcard*

```js
List<? super String> stringList2 = new ArrayList<String>();
String second = stringList2.get(0); //에러발생
stringList2.add("abc");
```

`? super String`는 String의 **상위 타입**으로 이루어진 리스트 객체를 받는다는 의미이다. 그렇기 때문에 `stringList2.get(0)`를 가져와 String에 넣으려고 할 때 현재 stringList2의 타입이 어떤 타입인지는 알 수 없으나 확실한건 String의 상위 타입이라는 것은 확실하다. 결국 상위 타입값을 하위타입에 대입하려다 보니 에러가 발생하는 것이다. `stringList2.add("abc")`는 어떤 타입인지 모르는 stringList2 리스트에 String 객체를 넣을려는 건데 이 경우도 stringList2 타입이 무엇인지 알 수는 없으나 String의 상위 타입 인것은 확실하므로 String 값을 추가할 수 있다.

## *Reference*

<http://happinessoncode.com/2017/05/21/java-generic-and-variance-1/#%ED%83%80%EC%9E%85-%EA%B0%80%EB%B3%80%EC%84%B1-variance-%EA%B3%BC-%EC%99%80%EC%9D%BC%EB%93%9C-%EC%B9%B4%EB%93%9C>