---
title: "The Great Effectvie Java 3/E item02"
date: "2019-09-25"
---

<h1>생성자에 매개변수가 많다면 빌더를 고려하라</h1>

- 매개변수가 많을 경우 정적팩터리와 생성자는 적절히 대응하기 어렵다.

<h2>점층적 생성자 패턴</h2>

- 예를 들어 파라미터의 값이 많을 경우 다양한 매개변수를 받는 생성자를 여러개 만들어 사용하는 방식이다. 이와같은 방식을 점층적 생성자 패턴이라고 한다.
- 이는 매개 변수가 많아지면 클라이언트 코드를 작성하거나 읽기 어렵다.

<h2>자바빈즈 패턴</h2>

- 매개변수가 없는 생성자로 객체를 만든 후 세터 메서드들을 호출해 원하는 매개변수의 값을 설정하는 방식이다.
- 하지만 자바빈즈 패턴에서는 객체 하나를 만들려면 메서드를 여러개 호출해야 하고 객체가 완전히 생성되기 전까지는 일관성이 무너진 상태에 놓이게 된다.
- 클래스를 불변으로 만들 수 없다.

<h2>빌더 패턴</h2>

- 필요한 객체를 직접 만드는 대신 필수 매개변수만으로 생성자를 호출해 빌더 객체를 얻는다. 그리고 빌더 객체가 제공하는 일종의 세터 메서드들로 원하는 선택 매개변수들을 설정한다. 마지막으로 매개변수가 없는 build 메서드를 호출해서 사용한다.

- 빌더는 생성할 클래스 안에 정적 멤버 클래스로 만들어두는게 보통이다.

```
// 빌더 패턴 예제
public NF {
    private final int servingSize;
    private final int servings;
    private final int calories;
    private final int fat;
    private final int sodium;
    private final int carbohydrate;

    public static class Builder {
        // 필수 매개변수
        private final int servingSize;
        private final int servings;

        // 선택 매개변수 - 기본값으로 초기화한다.
        private int calories;
        private int fat;
        private int sodium;
        private int carbohydrate;

        public Builder(int servingSize, int servings) {
            this.servingSize = servingSize;
            this.servings = servings;
        }

        public Builder calories(int val) {
            calories = val;
            return this;
        }
        public Builder fat(int val) {
            fat = val;
            return this;
        }
        public Builder sodium(int val) {
            sodium = val;
            return this;
        }
        public Builder carbohydrate(int val) {
            carbohydrate = val;
            return this;
        }

        public NF build() {
            return new NF(this);
        }
    }

    private NF(Builder builder) {
        servingSize = builder.servingSize;
        servings = builder.servings;
        calories = builder.calories;
        fat = builder.fat;
        sodium = builder.sodium;
        carbohydrate = builder.carbohydrate;
    }
}

// 빌더 패턴을 사용한 클라이언트 코드 예제
NF cocaCola = new NF.Builder(240, 8)
    .calories(100).sodium(35).carbohydrate(27).build();
```

- 빌더 패턴은 명명된 선택적 매개변수를 흉내낸 것이다.
- 빌더 패턴은 계층적으로 설계된 클래스와 함께 쓰기에 좋다.

  ```
  public abstract class Pizza {
      pulbic enum Topping {HAM, MUSHROM, ONION, PEPPER, SAUSAGE}
      final Set<Topping> toppings;

      abstract static class Builder<T extends Builder<T>> {
          EnumSet<Topping> toppings = EnumSet.noneOf(Topping.class);
          public T addTopping(Topping topping) {
              toppings.add(Objects.requireNonNull(topping));
              return self();
          }

          abstract Pizza build();

          //하위 클래스는 이 메서드를 재정의 하여 'this'를 반환하도록 해야한다.
          protected abstract T self();
      }

      Pizza(Builder<?> builder) {
          toppings = builder.topping.clone();
      }
  }
  ```

  ```
  // 뉴욕피자
  public class NyPizza extends Pizza {
      public enum Size { SMALL, MEDIUM, LARGE}
      private final Size size;

      public static class Builder extends Pizza.Builder<Builder> {
          private final Size size;

          // size 매개변수를 필수로 받음
          public Builder(Size size) {
              this.size = Objects.requireNonNull(size);
          }

          @Override
          public NyPizza builder() {
              return new NyPizza(this);
          }

          @Override
          protected Builder self() {
              return this;
          }
      }

      private NyPizza(Builder builder) {
          super(builder);
          size = builder.size;
      }
  }
  ```

  ```
  // 칼초네 피자
  public class Calzone extends Pizza {
      private final boolean sauceInside;

      public static class Builder extends Pizza.Builder<Builder> {
          private boolean sauceInside = false;

          public Builder sauceInside() {
              sauceInside = true;
              return this;
          }

          @Override
          public Calzone build() {
              return new Calzone(this);
          }

          @Override
          protected Builder self() {
              return this;
          }
      }

      private Calzone(Builder builder) {
          super(builder);
          sauceInside = builder.sauceInside;
      }
  }
  ```

  ```
  // 클라이언트 코드
  NyPizza pizza = new NyPizza.Builder(SMALL)
      .addTopping(SAUAGE).addTopping(ONION).build();
  Calzone calzone = new Calzone.Builder()
      .addTopping(HAM).sauceInside().build();
  ```

- 빌더를 이용하면 가변인수 매개변수를 여러 개 사용할 수 있다.
- 메서드를 여러번 호출하도록 하고 각 호출 때 넘겨진 매개변수들을 하나의 필드로 모을 수도 있다.(예 addTopping메서드)
- 빌더 패턴은 매개변수가 4개 이상에서 효율이 좋다.
