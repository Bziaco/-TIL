# 태그 달린 클래스보다는 계층구조를 활용하라

## 1. 태그 달린 클래스 - 클래스 계층구조보다 훨씬 나쁘다.

- 두 가지 이상의 의미를 표현할 수 있으며 그 중 현재 표현하는 의미를 태그 값으로 알려주는 클래스를 태그 클래스라고 한다.

```js
class Figure {
    eunum Shape {RECTANGLE, CIRCLE};

    // 태그 필드 - 현재 모양을 나타낸다.
    final Shape shape;

    // 다음 필드들은 모양이 사각형(RECTANGLE)일 때만 쓰인다.
     double length;
     double width;

     // 다음 필드는 모양이 원(CIRCLE)일 때만 쓰인다.
     double radius;

     //원용 생성자
     Figure(double radius) {
         shape = Shape.CIRCLE;
         radius = this.radius;
     }

     // 사각형용 생성자
     Figure(double length, double width) {
         shape = Shape.RECTANGLE;
         length = this.length;
         width = this.width;
     }

     double area() {
         switch(shape) {
            case RECTANGLE:
                return length * width;
            case CIRCLE:
                return Math.PI * (radius * radius);
            default:
                throw new AssertionError(shape);
         }
     }
}
```

<br>

## 2. 태그 달린 클래스에는 단점이 너무 많다.

1. 열거 타입 선언, 태그 필드, switch 문 등 쓸데없는 코드가 많다.

2. 여러 구현이 한 클래스에 혼합되어 있어 가독성이 나쁘다.

3. 다른 의미를 위한 코드도 언제나 함께 하니 메모리도 많이 사용한다.

4. 필드들을 final로 선언하려면 해당 의미에 쓰이지 않는 필드들까지 생성자에서 초기화 해야 한다.

5. 새로운 다른 의미를 추가하려면 switch 문을 수정해야 하며 하나라도 빠트리면 런타임에 문제가 불거져 나올 것이다.

6. 한마리도 태그 달린 클래스는 장황하고 오류를 내기 쉽고 비효율적이다.

<br>

## 3. 태그 달린 클래스를 클래스 계층구조로 바꾸는 방법

1. 루트(root)가 될 추상 클래스 정의

2. 태그 값에 따라 동작이 달라지는 메서드들을 루트 클래스의 추상 메서드로 선언(위 예제코드의 area()와 같은)

3. 태그 값에 상관업이 동작이 일정한 메서드들을 루트 클래스에 일반 메서드로 추가

4. Figure 클래스에서는 태그 값에 상관없는 메서드가 하나도 없는 상태이며 모든 하위 클래스에서 사용하는 공통 데이터 필드도 없다.

5. 그 결과 루트 클래스에는 추상 메서드인 area하나만 남게 된다.

6. 다음으로 루트 클래스를 확장한 구체 클래스를 의미별로 하나씩 정의한다.

7. 각 하위 클래스에는 각자의 의미에 해당하는 데이터 필드를을 넣는다.

8. 그런 다음 루트 클래스가 정의한 추상 메서드를 각자의 의미에 맞게 구현한다.

- 태그 달린 클래스를 클래스 계층구조로 변환
```js
abstract class Figure {
    abstract double area();
}

class Circle extends Figure {
    final double radius;

    Circle(double radius) { this.radius = radius; }

    @Override
    double area() {
        return Math.PI * (radius * radius);
    }
}

class Rectangle extends Figure {
    final double width;
    final double length;

    Rectangle(double width, double length) {
        this.width = width;
        this.length = length;
    }

    @Override
    double area() {
        return length * width;
    }
}
```

- 간결하고 명확해 졌으며 쓸데 없는 코드도 모두 사라졌다.

- 각 의미를 독립된 클래스에 담아 관련 없던 데이터 필드를 모두 제거했다.

- 남은 모든 필드들은 모두 final이기 때문에 각 클래스의 생성자가 모든 필드를 남김없이 초기호하고 추상 메서드를 모두 구현햇는지 컴파일러가 알려준다.

<br>

## 3. 클래스 계층구조는 타입 사이의 자연스러운 계층 관계를 반영할 수 있다.

- 유연성은 물론 컴파일 타임 타입 검사 능력을 높여준다는 장점이 있다.

- 위 계층 구조 Figure 클래스에서 정사각형도 지원하도록 수정하려면 아래와 같이 반영 할 수 있다.

    ```js
    class Square extends Rectangle {
        Square(double side) {
            super(side, side);
        }
    }
    ```

> | 참고
>> 이번  모든 예제에서 접근자 메서드 없이 필드를 직접 노출했다. 이는 단지 코드를 단순하게 하려는 의도였고 공개할 클래스라면 이렇게 설계하는 것은 좋지 않다.

<br>

## 4. Conclusion

- 태그 달린 클래스를 사용 중이라면 계층구조로 리팩터링하는 것을 고민해보자.