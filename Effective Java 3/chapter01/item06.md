<h1>불필요한 객체 생성을 피하라</h1>

- 똑같은 기능의 객체를 매번 생성하기 보다는 객체 하나를 재사용하는 편이 나을 때가 많다.
    - `String s = new String("bikini")` // 절대 사용하지 말 것
    - 위 문장은 실행될 때마다 String 인스턴스를 새로 만든다
    - `String s = "bikini";` 처럼 하나의 String 인스턴스를 사용한다.

- 생성자 대신 정적 팩터리 메서드를 제공하는 불변 클래스에서는 정적 팩터리 메서드를 사용해 불필요한 객체 생성을 피할 수 있다.
    - Boolean(String) 대신 Boolean.valueOf(String) 팩터리 메서드를 사용하는 것디 좋다.
    - 생성자는 호출할 때마다 새로운 객체를 만들지만 팩터리 메서드는 전혀 그렇지 않다.

- String.matches는 정규표현식으로 문자열 형태를 확인하는 가장 쉬운 방법이지만 성능이 중요한 상황에서 반복해 사용하기엔 적합하지 않다.
    - 이 메서드가 내부에서 만드는 정규표현식용 Pattern 인스턴스는 한번 쓰고 버려져서 곧바로 가비지 컬렉션 대상이 된다.
    - Pattern은 입력받은 정규표현식에 해당하는 유한 상태 머신을 만들기 때문에 인스턴스 생성비용이 높다.
    - 성능을 개선하려면 필요한 정규표현식을 표현하는 Pattern 인스턴스를 클래스 초기화 과정에서 직접 생성해 캐싱해두고 나중에 isRomanNumeral 메서드가 호출될 때마다 이 인스턴스를 재사용한다.

        ```
        public class RomanNumerals {
            private static final Pattern ROMAN = Pattern.compile(
                ....// 정규식
            );

            static boolean isRomanNumeral(String s) {
                return ROMAN.matcher(s).matches();
            }
        }
        ```

- 불필요한 객체를 만들어내는 또 다른 예로는 오토박싱이 있다.
    - 오토박싱은 기본 타입과 그에 대응하는 박싱된 기본타입의 구분을 흐려주지만 완전히 없애주는 것은 아니다. 의미상으론 별다를 것이 없지만 성능에서는 그렇지 않다.

    - 예로서 모든 양의 정수의 총합을 구하는 메서드로 int는 충분히 크지 않으니 long으로 사용해 계산하고 있다.
        ```
        private static long sum() {
            Long sum = 0L;
            for(long i = 0; i <= Integer.MAX_VALUE; i++) {
                sum += i;
            }

            return sum;
        }
        ```
        - 이는 sum 변수를 long이 아닌 Long으로 선언해서 불필요한 인스턴스를 계속해서 만들어 낸다.
    - 박싱된 기본 타입보다는 기본타입을 사용하고 의도치 않은 오토박싱이 숨어들지 않도록 주의하자.