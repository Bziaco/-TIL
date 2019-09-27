import java.util.function.Supplier;

public class SingleTest {
    public static void main(String[] args) {
        Singleton1 singleton1 = Singleton1.instance;
        // Singleton1 singleton3 = new Singleton1();
        Singleton2 singleton2 = Singleton2.getInstance();

        System.out.println(singleton1);
        System.out.println(singleton2);

        Supplier<Singleton2> s2supplier = Singleton2::getInstance;

        String name = Singleton3.INSTANCE.getName();

    }
}