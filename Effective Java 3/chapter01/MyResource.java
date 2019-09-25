public class MyResource implements AutoCloseable {
    public void doSomething() {
        System.out.println("Do Something");
        throw new FirstError();
    }

    @Override
    public void close() throws Exception {
        System.out.println("close My Resource");
        throw new SecondError();
    }
}