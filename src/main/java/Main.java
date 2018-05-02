import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class Main {
    public static void main(String[] args) {
        new Main().run();
    }

    private void run(){
        try {
            InetSocketAddress address = new InetSocketAddress("vanviegen.net", 29588);

            DatagramSocket datagramSocket = new DatagramSocket();
            datagramSocket.connect(address);
            datagramSocket.close();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}
