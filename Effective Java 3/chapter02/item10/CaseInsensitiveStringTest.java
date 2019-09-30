
public class CaseInsensitiveStringTest {

    public static void main(String[] args) {
        CaseInsensitiveString cis = new CaseInsensitiveString("javaIsFun");
        String text = "javaIsFun";
        System.out.println(cis.equals(text));
    }

}