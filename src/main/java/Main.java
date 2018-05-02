import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class Main {
    public static void main(String[] args) {
        new Main().run();
    }

    private DatagramSocket socketToServer;

    private void run() {
        try {
            createConnection("vanviegen.net", 29588);
        } catch (SocketException se) {
            System.err.println(se.getMessage());
        }
    }

    private void createConnection(String hostname, int port) throws SocketException {
        InetSocketAddress address = new InetSocketAddress(hostname, port);
        socketToServer = new DatagramSocket();
        socketToServer.connect(address);
    }
}
