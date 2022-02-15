public class App {
    public static void main(String[] args) throws Exception {
        Store store = new StoreDecorator();
        store.RunSimulation(30);
    }
}
