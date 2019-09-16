<h1>자원을 직접 명시하지 말고 의존 객체 주입을 사용하라</h1>

<h2>사용하는 자원에 따라 동작이 달라지는 클래스에는 정적 유틸리티 클래스나 싱글턴 방식이 적합하지 않다.</h2>

- 대신 클래스가 여러 자원 인스턴스를 지원해야 하며 클라이언트가 원하는 자원을 사용해야 한다. 
- 이 조건을 만족하는 간단한 패턴이 인스턴스를 생성할 때 생성자에 필요한 자원을 넘겨주는 방식이다.
    ```
    public class SpellChecker {
        private final Lexicon dictionary;
        public SpellChecker(Lexicon dictionary) {
            this.dictionary = Objects.requireNonNull(dictionary);
        }

        public boolean isValid(String word) {...}
        public List<String> suggestions(String type) {...}
    }
    ```

- 호출할 때마다 특정 타입의 인스턴스를 반복해서 만들어주는 객체를 말하는 팩터리로 생성자에 자원 팩터리를 넘겨주는 방식이 있다. 즉 팩터리 메서드 패턴을 구현하는 것이다.

- Supplier<T>를 입력으로 받은 메서드는 일반적으로 한정적 와잍드카드 타입을 사용해 팩터리의 타입 매개변수를 제한해야 한다. 이 방식을 사용해 클라이언트는 자신이 명시한 타입의 하위타입이라면 무엇이든 생성할 수 있는 팩터리를 넘길 수 있다.

<h2>의존 객체 주입이 유연성과 테스트 용이성을 개선해주긴 하지만 의존성이 수천개나 되는 큰 프로젝트에서는 코드를 어지럽게 만들기도 한다.</h2>

- 대거, 주스, 스프링같은 의존 객체 주입 프레임워크를 사용하면 이러한 문제점을 해결 할 수 있다.

