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

    