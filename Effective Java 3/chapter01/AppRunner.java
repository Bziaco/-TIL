public class AppRunner {
    public static void main(String[] args) {
        try (MyResource myResource = new MyResource(); MyResource myResource2 = new MyResource()) {
            myResource.doSomething();
        }
        // try {
        // myResource.doSomething();
        // } finally {
        // myResource.close();
        // }
    }
}