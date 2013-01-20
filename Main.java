public class Main {
    public static void main(String[] args) {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 54321;
        }
        new Acceptor(port).run();
    }
}
