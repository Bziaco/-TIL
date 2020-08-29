# null이 아닌, 빈 컬렉션이나 배열을 반환하라

## null이 아닌 빈 컬렉션을 반환하라

```js
/**
 * null을 반환하는 컬렉션 - 따라하지 말것 
 */
 public List<Cheese> getCheese() {
     return cheeeseInStock.isEmpty() ? null
        : new ArrayList<>(cheeseInStock);
 }
```
위 예와 같이 null을 반환해야 한다면 클라이언트는 이 null 상황을 처리하는 코드를 추가로 작성해야 하는 불편함이 있다. 때로는 빈 컨테이너를 할당하는데도 비용이 드니 null을 반환하는 쪽이 낫다는 주장도 있다. 그러나 아래와 같은 이유에서 틀린주장이다.

1. 성능 분석 결과 이 할당이 성능 저하의 주범이지 않는 이상 신경쓸 정도는 아니다.
2. 빈 켤렉션과 배열은 굳이 새로 할당하지 않고도 반환할 수 있다.

```js
/**
 * 빈 컬렉션을 반환하는 올바른 예 
 */
 public List<Cheese> getCheese() {
     return new ArrayList<>(cheeseInStock);
 }
```
가능성은 작지만 사용 패턴에 따라 빈 컬렉션 할당이 성능을 눈에 띄게 떨어 뜨릴 수도 있다. 이는 빈 `불변` 컬렉션을 반환하는 것이다. 불변 객체는 자유롭게 공유해도 안전하다(item17). 리스트가 필요하면 `Collections.emptyList`, 집합이 필요하면 `Collections.emptySet`, 맵이 필요하면 `Collections.emptyMap`을 사용하면 된다. 단 이 역시 최적화에 해당하니 꼭 필요할 때만 사용하고 성능 개선 여부를 확인하자.

```js
/**
 * 최적화 - 빈 컬렉션을 매번 새로 할당하지 않도록 했다. 
 */
 public List<Cheese> getCheese() {
     return cheeseInStock.isEmpty() ? Collections.emptyList
        : new ArrayList<>(cheeseInStock);
 }
``` 

</br>

## null이 아닌 길이가 0인 배열을 반환하라. 

```js
/**
 * 빈 배열을 매번 새로 할당하지 않고 길이가 0인 배열 반환하도록 했다.
 */
 private satic final Cheese[] EMPTY_CHEESE_ARRAY = new Cheese[0];

 public Cheese[] getCheese() {
     return cheesesInStock.toArray(EMPTY_CHEESE_ARRAY);
 }
```

항상 `EMPTY_CHEESE_ARRAY`를 인수로 넘겨 toArray를 호출한다. 따라서 cheesesInStock이 비었을 땐 언제나 `EMPTY_CHEESE_ARRAY`를 반환하게 된다. 단순히 성능을 개선할 목적이라면 toArray에 넘기는 배열을 미리 할당하는 건 추천하지 않는다.

```js
return cheeseInStock.toArray(new Cheese[cheesesInStock.size()]);
```

</br>

## Conclusion

null이 아닌, 빈 배열이나 컬렉션을 반환하라. null을 반환하는 API는 사용하기도 어렵고 오류 처리 코드도 늘어난다. 그렇다고 성능이 좋은 것도 아니다.