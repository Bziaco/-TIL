# clone 재정의는 주의해서 진행하라

## 1. Cloneable 인터페이스는 무엇인가?

- Object의 protected 메서드인 clone의 동작 방식을 결정한다.

- Cloneable을 구현한 클래스의 인스턴스에서 clone을 호출하면 그 객체의 필드들을 하나하나 복사한 객체를 반환하며 아니라면 `CloneNotSupportedExceoption`을 던진다.

    > | 참고
    >> Cloneable 인터페이스는 이례적으로 사용한 예이니 절대 따라해서는 안된다.

- Cloneable을 구현한 클래스는 clone 메서드를 public으로 제공한다.

## 2. clone 메서드 일반 규약

- 객체의 복사본을 생성해 반환(복사의 정확한 뜻은 그 객체를 구혀한 클래스에 따라 다를 수 있음)한다.

- 일반적인 의도는 아래와 같다. 어떤 객체 x에 대해 다음 식은 참이다.

    - x.clone(x)
    - x.clone().getClass == x.getClass()

        > | 참고
        >> x.clone().getClass == x.getClass() 는 필수는 아니다.
    - x.clone.equals(x)

        > | 참고
        >> super.clone을 호출해서 얻어야 한다. 
    
    - x.clone().getClass == x.getClass()
        > | 참고
        >> x.clone.equals(x) 식이 참이라면 위 식도 참이다.

- 일반적으로 반환된 객체와 원본 객체는 `독립적`이어야 한다.

- 강제성을 제외하면 생성자 연쇄와 비슷한 매커니즘이다. clone 메서드가 super.clone()이 아닌 생성자 호출로 얻은 인스턴스를 반환해도 문제가 없을 것이다.
    > | 참고
    >> 

- 하지만 하위 클래스에서 super.clone을 호출한다면 잘못된 클래스 객체가 만들어져 clone 메서드가 제대로 동작하지 않게 된다.

- `clone을 재정의한 클래스가 final`이라면 위와 같은 관례는 무시해도 안전하다. 사실 super.clone을 호출하지 않는다면 Cloneable을 구현할 이유도 없다.

## 3. Cloneable 구현

### 3.1 불변 객체를 clone

- 모든 필드가 primitive type 이거나 불변 객체를 참조한다면 super.clone은 완벽한 복제본이다. 그러나 쓸데없는 복사를 지양한다는 관점에서 본다면 불변 클래스는 굳이 clone 메서드를 제공하지 않는 것이 좋다.
    ```js
    public class UserInfo {
        int age;
        String name;

        @Override
        public Object clone() throws CloneNotSupportedException {
            return (UserInfo) super.clone();
        }
    }
    ```
    
- 위 clone 메서드를 동작하게 하려면 UserInfo 클래스 선언에 Cloneable을 implement 해야 한다.
    ```js
    public class UserInfo implement Cloneable{
        ...

        @Override
        public Object clone() throws CloneNotSupportedException {
            return (UserInfo) super.clone();
        }
    }
    ```

    > | 참고
    >> super.clone 호출을 예외를 던지는 이유?
    >> Object의 clone 메서드가 검사 예외인 CloneNotSupportedException을 던지도록 선언되었기 때문이다.

### 3.2 가변 객체를 clone

- clone은 복제된 객체가 원본 객체에 영향을 주지 않아야 하며 복제된 객체의 불벼식을 보장해야 한다.

- 가변 객체가 포함된 클래스를 clone 하는 경우에는 가변객체의 clone을 재귀적으로 호출해 주는 것이다.

    ```js 
    // AddrInfo.class
    public class AddrInfo implements Cloneable{
        String ipAddr;
        String macAddr;
        
        @Override
        public Object clone() throws CloneNotSupportedException{
            return super.clone();
        }
    }
    ```

    ```js
    //UserInfo.class

    public class UserInfo implements Cloneable{
	
        int height;
        int weight;
        
        AddrInfo addrInfo;
        
        @Override
        public Object clone() throws CloneNotSupportedException {
            UserInfo userInfo = (UserInfo) super.clone();
            userInfo.addrInfo = (AddrInfo) userInfo.addrInfo.clone();
            return userInfo;
        }
        
        @Override
        public String toString() {
            return "height: "+ this.height  + ", height: " + this.weight + ", addrInfo: " + this.addrInfo;
        }
    }
    ```

- 한편 addrInfo 객체가 final 이었다면 앞서 방식은 작동하지 않는다. 새로운 값을 할당 할 수 없기 때문이다. 복제할 수 있는 클래스를 만들기 위해 일부 필드에서 final 한정자를 제거해야 할 수도 있다.


### 3.3 HashTable clone

- 해시테이블 내부는 버킷들의 배열이고 각 버킷은 키-값 쌍을 담는 연결 리스트의 첫 번째 엔트리를 참조한다.

```js
public class HashTable implements Cloneable {
    private Entry[] buckets = ...;

    private static class Entry {
        final Object key;
        Object value;
        Entry next;

        Entry(Object key, Object value, Entry next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
}
```

#### 3.3.1 잘못된 clone 메서드 - 가변 상태를 공유한다!
```js
@Override
public HashTable clone() {
    try {
        HashTable result = (HashTable) super.clone();
        result.buckets = buckets.clone();
        return result;
    } catch(CloneNotSupportedException e) {
        throw new AssertionError();
    }
}
```

- 복제본은 자신만의 버킷 배열을 가져야 하지만 이 배열은 원본과 같은 연결 리스트를 참조하여 원본과 복제본 모두 예기치 않게 동작할 가능성이 생긴다. 

- 이를 해결하려면 각 버킷을 구성하는 연결 리스트를 복사해야 한다.

#### 3.3.2 복잡한 가변 상태를 갖는 클래스용 재귀적 clone 메서드

```js
public class HashTable implements Cloneable {
    private Entry[] buckets = ...;

    private static class Entry {
        final Object key;
        Object value;
        Entry next;

        Entry(Object key, Object value, Entry next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        Entry deepCopy() {
            return new Entry(key, value, next == null ? null : next.deepCopy());
        }
    }

    @Override
    public HashTable clone() {
        try {
            HashTable result = (HashTable) super.clone();
            result.buckets = new Entry[buckets.length];
            for(int i = 0; i < buckets.length; i++) {
                if(buckets[i] != null) {
                    result.buckets[i] = buckets[i].deepCopy();
                }
            }
            return result;
        } catch(CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
``` 

- HashTable의 clone 메서드는 먼저 적절한 크기의 새로운 버킷 배열을 할당한 다음 원래의 버킷 배열을 순회하며 비지 않은 각 버킷에 대해 깊은복사를 수행한다.

- 이때 Entry의 deepCopy 메서드는 자신이 가리키는 연결 리스트 전체를 복사하기 위해 자신을 재귀적으로 호출한다.

- 그러나 리스트가 길면 스택 오버플로를 일으킬 위험이 있기 때문에 연결 리스트를 복제하는 방법은 그다지 좋지 않다.

#### 3.3.3 엔트리 자신이 가리키는 연결 리스트를 반복적으로 복사한다.

```js
Entry deepCopy() {
    Entry result = new Entry(key, value, next);
    for(Entry p = result; p.next != null; p = p.next) {
        p.next = new Entry(p.next.key, p.next.value, p.netx.next);
    }
    return result;
}
```

### 3.4 복잡한 가변 객체 복제

#### 3.4.1 복제 순서

1. super.clone()을 호출하여 얻은 객체의 모든 필드를 초기상태로 설정

2. 원본 객체의 상태를 다시 생성하는 고수준 메서드들을 호출

3. HashTable이라면 buckets 필드를 새로운 버킷 배열로 초기화한 다음 원본 테이블에 담긴 모든 키-값 쌍 각각에 대해 본제본 테이블의 put(key, value) 메서드를 호출

> | 참고
>> 이처럼 고수준 API를 활용해 복제하면 간단하고 우아한 코드를 얻게 되지만 아무래도 저수준에서 바로 처리할 때보다는 느리다. 또한 Cloneable 아키텍처의 기초가 되는 필드 단위 객체 복사를 우회하기 때문에 전체 Cloneable 아키텍처와는 어울리지 않는 방식이기도 하다.

#### 3.4.2 생성자에서는 재정의 될 수 있는 메서드를 호출하지 않아야 한다.

- 만약 clone이 하위 클래스에서 재정의한 메서드를 호출하면 하위 클래스는 복제 과정에서 자신의 상태를 교정할 기회를 잃게 되어 원본과 복제본의 상태가 달라질 가능성이 크다.

- 따라서 복제에 사용되는 put(key,value) 메서드는 final이거나 private이어야 한다.

#### 3.4.3 CloneNotSupportedException

- public인 clone 메서드에서는 throws 절을 없애야 한다. 검사 예외를 던지지 않아야 그 메서드를 사용하기 편하기 때문이다.

#### 3.4.4 상속용 클래스는 Cloneable을 구현해서는 안된다.

- clone을 동작하지 않게 구현해 놓고 하위 클래스에서 재정의하지 못하게 할 수 있다.
```js
@Override
protected final Object clone() throws CloneNotSupportedException {
    throws new CloneNotSupportedException();
}
```

### 3.5 복사 생성자와 복사 팩터리

#### 3.5.1 복사 생성자

- 복사 생성자란 자신과 같은 클래스의 인스턴스를 인수로 받는 생성자를 말한다.

```js
public Yum(Yum yum) {...};
```

#### 3.5.2 복사 팩터리

- 복사 팩터리는 복사 생성자를 모방한 정적 팩터리다.

```js
public static Yum getInstance(Yum yum) {...};
```

#### 3.6 Shallow Copy vs Deep Copy

- [Shallow Copy vs Deep Copy](https://zealous-wozniak-a49a78.netlify.com/blog/compareOfShallowAndDeep)

#### 3.5.3 장점

- 언어 모순적이고 위험한 객체 생성 메커니즘을 사용하지 않는다. (super.clone())
- clone 규약에 기대지 않는다.
- 정상적인 final필드 용법과도 충돌하지 않는다.
- 불필요한 check exception 처리가 필요없다.
- 형변환도 필요없다.
- 복사 생성자와 복사 팩터리는 인터페이스 타입의 인스턴스를 인수로 받을 수 있다.

### 3.6 Conclusion

- Cloneable을 구현하는 모든 클래스는 clone을 재정의 해야 한다. 접근 제한자는 public으로 반환 타입은 클래스 자신으로 변경한다.

- 기본 타입 필드와 불변 객체 참조만 갖는 클래스라면 아무 필드도 수정할 필요가 없다. 단 고유 ID는 비록 기본 타입이나 불변일지라도 수정해줘야 한다.

- 가변 객체가 필드로 있을 경우에는 깊은 구조에 숨어 있는 모든 가변 객체를 복사하고 본제본이 가진 객체 참조 모두가 복사된 객체들을 가리키게 한다.