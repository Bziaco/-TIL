# 상속보다는 컴포지션을 사용하라

## 1. 상속은 코드를 재사용하는 강력한 수단이지만 항상 최선은 아니다.

### 1.1 다른 패키지의 구체 클래스를 상속하는 일은 위험하다.

### 1.2 메서드 호출과 달리 상속은 캡슐화를 깨드린다.

- **상위 클래스가 어떻게 구현되냐에 따라 하위 클래스의 동작에 이상이 생길 수 있다.**

- HashSet의 에를 보자. HashSet이 처음 생성된 이후 원소가 몇 개 더해졌는지 알수 있어야 한다. 그래서 추가된 원소의 수를 저장하는 변수와 접근자 메서드를 추가했다. 그런 다음 HashSet에 원소를 추가하는 메서드인 add와 addAll을 재정의했다.

    ```js
    public class InstrumentedHashSet<E> extends HashSet<E> {
        // 추가된 원소의 수
        private int addCount = 0l

        public InstrumentedHashSet(int initCap, float loadFactor) {
            super(initCap, loadFactor);
        }

        @Override public boolean add(E e) {
            addCount++;
            return super.add(e);
        }

        @Ovveride public boolean addAll(Collection<? extends E> c) {
            addCount += c.size();
            return super.addAll(c);
        }

        public int getAddCount() {
            return addCount;
        }
    }
    ```

    ```js
    InstrumentedHashSet<String> s = new InstrumentedHashSet<>();
    s.addAll(List.of("a","aa","aaa"));
    ```

    - getCount메서드를 호출하면 3을 반환할 것이라 예정하겠지만 실제로는 6이 반환된다.

    - 원인은 **HashSet의 addAll 메서드가 add메서드를 사용해 구현**된데 있다. 그러므로 add 메서드의 호출이 원소 갯수만큼 호출이 이뤄져 최종값이 6으로 나오는 것이다.

<br>

## 2. 상속의 한계점에 대한 해법과 그에 따른 문제점

### 2.1 하위 클래스에서 addAll 메서드를 재정의 하지 않으면?

- 당장은 제대로 동작할지 모르나 결국 HashSet의 addAll이 add 메서드를 이용해 구현했다는 것은 달라지지 않는다.

- 이처럼 자신의 다른 부분을 사용하는 자기사용(*self-use*) 여부는 해당 클래스의 내부 구현 방식에 해당하며 자바 플랫폼 전반적인 정책인지 그래서 다음 릴리스에서도 유지될지는 알 수 없다.

- 따라서 가정에 기댄 InstrumentedHashSet도 깨지기 쉽다.

### 2.2 addAll 메서드를 다른식으로 재정의 한다면?

- 주어진 컬렉션을 순회하며 원소 하나당 add 메서드를 한번만 호출하는 것이다.

- 더이상 HashSet의 addAll을 호출하지 않으니 괜찮을지 모르겠다. 하지만 상위 클래스의 메서드 동작을 다시 구현하는 방식은 어렵고 시간도 더 들고 자칫 오류를 내거나 성능을 떨어뜨릴 수 있다. 

- 또한 **하위 클래스에서는 접근할 수 없는 private 필드를 써야 하는 상황이라면 이 방식으로는 구현 자체가 불가능**하다.

### 2.3 상위 클래스에 새로운 메서드를 추가 하는건?

- 다음 릴리스에서 상위 클래스에 또 다른 원소 추가 메서드가 만들어진다면 문제이다. 하위 클래스에서 재정의하지 못한 그 새로운 메서드를 사용해 허용되지 않은 원소를 추가 할 수 있게 된다.

- 실제로도 컬렉션프레임워크 이전부터 존재하던 Hashtable과 Vector를 컬렉션 프레임워크에 포함시키자 이와 관련한 보안 구멍들을 수정해야 하는 사태가 벌어졌다.

### 2.4 하위 클래스에 새로운 메서드를 추가 하는건?

- 위의 문제들 모두 메서드 재정의가 문제였다. 그래서 클래스를 확장하더라도 메서드를 재정의하는 대신 새로운 메서드를 추가하면 괜찮을 것이라 생각할 수도 있다. 

- 그러나 다음과 같은 상황이면 결국 똑같은 문제에 부닥칠 것이다. 다음 릴리스에서 상위 클래스에 새 메서드가 추가됐는데 하위 클래스에 추가한 메서드와 시그니처가 같고 반환 타입은 다르다면 컴파일 조차 되지 않을 것이다. 결국 상위 메서드를 재정의 한 셈이다.

- 더군다나 상위 클래스의 메서드는 존재하지도 않았으니 하위 클래스에서 새로 만든 메서드는 상위 클래스의 메서드가 요구하는 규약을 만족하지 못할 가능성이 크다.


<br>


## 3. 상속 대신 컴포지션을 사용하면 위의 모든 문제가 해결된다.

- **기존 클래스를 확장하는 대신 새로운 클래스를 만들고 private 필드로 기존 클래스의 인스턴스를 참조하게 하자**

- **기존 클래스가 새로운 클래스의 구성요소로 쓰인다는 뜻에서 이러한 설계를 컴포지션(composition; 구성)**이라 한다.

- 새 클래스의 인스턴스 메서드들은 기존 클래스의 대응하는 메서드를 호출해 그 결과를 반환한다. 이 방식을 전달(*forwarding*)이라 하며 새 클래스의 메서드들을 전달 메서드(*forwarding method*)라 부른다.

- 그 결과 새로운 클래스는 기존 클래스의 내부 구현 방식의 영향에서 벗어나며 기존 클래스에 새로운 메서드가 추가되더라도 전혀 영향을 받지 않는다.

- 구체적인 예시를 로서 InstrumentedHashSet을 컴포지션과 전달방식으로 다시 구현한 코드이다. 하나는 집합 클래스, 다른 하나는 전달 클래스이다.

    ```js
    // 집합 클래스

    public class InstrumentedHashSet<E> extends ForwardingSet<E> {
        // 추가된 원소의 수
        private int addCount = 0l

        public InstrumentedHashSet(int initCap, float loadFactor) {
            super(initCap, loadFactor);
        }

        @Override public boolean add(E e) {
            addCount++;
            return super.add(e);
        }

        @Ovveride public boolean addAll(Collection<? extends E> c) {
            addCount += c.size();
            return super.addAll(c);
        }

        public int getAddCount() {
            return addCount;
        }
    }
    ```

    ```js
    // 전달 클래스

    public class ForwardingSet<E> implement Set<E> {
        private final Set<E> s;
        public ForwardingSet(Set<E> s) { this.s = s};

        public void clear() {s.clear()}
        public boolean contatins(Object o) {return s.contatin(o);}
        public boolean isEmpty()    {return s.isEmpty();}
        public int size()   {return s.size();}
        public Iterator<E> iterator()   {return s.iterator();}
        public boolean add(E e) {return s.add(e);}
        public boolean remove(Object o) {return s.remove(o);}
        
        public boolean containsAll(Collection<?> c) [return s.contatinAll(c);]
        public boolean removeAll(Collection<? extends E> c) {return s.removeAll(c);}
        public boolean retainAll(Collection<?> c) {return s.retainAll(c);}
        public Object[] toArray() {return s.toArray();}
        public <T> t[] toArray(T[] a) {return s.toArray(a);}
        @Override public boolean equals(Object o) {return s.equals(o);}
        @OOveride public int hashcode() {return s.hashCode();}
        @Override public String toString() {return s.toString();}
    }
    ```

    - InstrumentedHashSet는 임의이 Set에 계측 기능을 덧씌워 새로운 Set으로 만드는 것이 이 클래스의 핵심이다.

    - 상속 방식은 구체 클래스를 각각을 따로 확장해야 하며 지원하고 싶은 상위 클래스의 생성자 각각에 대응하는 생성자를 별도로 정의해줘야 한다.

- InstrumentedHashSet을 이용하면 대상 Set 인스턴스를 특정 조건하에서만 임시로 계측할 수 있다.
    
    ```js
    static void walk(Set<Dog> dogs) {
        InstrumentedHashSet<Dog> iDogs = new InstrumentedHashSet<>(dogs);
    }
    ```

### 3.1 래퍼 클래스

- 다른 Set 인스턴스를 감싸고(wrap) 있다는 뜻에서 InstrumentedHashSet같은 클래스를 **래퍼 클래스**라하며 다른 Set에 계측 기능을 덧씌운다는 뜻에서 *데코레이더 패턴*이라고 한다.

- 컴포지션과 전달의 조합은 넓은 의미로 위임(delegation)이라고 부른다. 단 래퍼 객체가 내부 객체에 자기 자신의 참조를 넘기는 경우만 위임에 해당한다.

#### 3.1.1 래퍼 클래스 사용시 주의점

- 자기 자신의 참조를 다른 객체에 넘겨서 다음 호출(콜백) 때 사용하도록 한다. 내부 객체는 자신을 감싸고 있는 래퍼의 존재를 모르니 대신 자신의 참조를 넘기고 콜백 때는 래퍼가 아닌 내부 객체를 호출하게 된다. 이를 *SELF 문제*라고 한다. 

- 메모리 사용량에서도 큰 영향이 없으며 재사용 전달 클래스를 인터페이스당 하나씩만 만들어두면 원하는 기능을 덧씌우는 전달 클래스들을 아주 손쉽게 구현할 수 있다.

<br>

## 4. 상속은 언제 구현해야 하는가? 언제 상속을 쓰고 언제 컴포지션을 써야 하는가?

- 상속은 반드시 하위 클래스가 상위 클래스의 '진짜' 하위 타입인 상황에서만 써야 한다.**(클래스 B가 클래스 A와 is-a관계 일때)**

- 컴포지션을 써야 할 상황에서 상속을 사용하는 건 내부 구현을 불필요하게 노출하는 꼴이다.

- 또한 확장하려는 클래스의 API에 아무런 결함이 없는가? 결함이 있다면 이 결함이 우리의 클래스의 API까지 전파돼도 괜찮은가?를 자문한 뒤 컴포지션 대신 상속을 사용해라. **컴포지션으로는 이런 결함을 숨기는 새로운 API를 설계할 수 있지만 상속은 상위 클래스의 API까지도 그대로 승계한다.**


<br>


## 5. Conclusion

- 상속은 강력하지만 캡슐화를 해친다는 문제가 있다.

- 상속은 상위 클래스와 하위 클래스가 순수한 is-a 관계일 때만 써야 한다.

- is-a 관계 일지라도 패키지가 다른 상위 클래스라면 확장을 고려해 설계하지 않았다면 문제가 될 수 있다.

- 상속의 취약점을 피하려면 상속 대신 컴포지션과 전달을 사용하자. 특히 래퍼 클래스로 구현할 적당한 인터페이스가 있다면 더욱 그렇다.

