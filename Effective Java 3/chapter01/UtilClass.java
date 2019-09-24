public abstract class UtilClass {
    public static String getName() {
        return "jaehwan";
    }

    // 유틸 클래스라 인스턴스를 만들지 못하게 한다.
    // StringUtil class reference
    // abstract로 선언만 해도 인스턴스 생성으로 유틸 메서드를 사용하지 못한다.
    private UtilClass() {
        throw new AssertionError();
    }

    static class AnotherClass extends UtilClass {

    }

    public static void main(String[] args) {
        // AnotherClass anotherClass = new AnotherClass();

    }
}