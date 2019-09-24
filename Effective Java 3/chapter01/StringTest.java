public class StringTest {

    public static void main(String[] args) {
        String string1 = new String("jaehwan");
        String string2 = new String("jaehwan");

        System.out.println(string1 == string2); // false;

        String string3 = "jaehwan";
        String string4 = "jaehwan";

        System.out.println(string3 == string4); // ture

        Boolean true1 = Boolean.valueOf(true);
        Boolean true2 = Boolean.valueOf(true);

        System.out.println(true1 == true2); // ture
        // boolean의 상수 TRUE를 리턴하기 때문에 같은 값이 나온다.
        System.out.println(true1 == Boolean.TRUE); // true

    }

}
