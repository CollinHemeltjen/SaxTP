
import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        new Main().run(args);
    }

    private void run(String[] args) {
        ConnectionData connectionData = createConnectionData(args);

        try (DatagramSocket serverSocket = createConnection(connectionData)) {
            sendMessage(serverSocket);
            System.out.println(receiveMessage(serverSocket));

        } catch (SocketException se) {
            System.out.println("could not connect to server, please try again!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(DatagramSocket socket) throws IOException {
        System.out.println("Sending request to server");
        byte[] start = "SaxTP".getBytes();
        byte[] mid = new byte[]{0, (byte) 218, 109, 1, 82};
        byte[] end = "1.zip".getBytes();
        byte[] buf = new byte[start.length + mid.length + end.length];

        System.arraycopy(start, 0, buf, 0, start.length);
        System.arraycopy(mid, 0, buf, start.length, mid.length);
        System.arraycopy(end, 0, buf, mid.length+start.length, end.length);

        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.send(packet);
    }

    private String receiveMessage(DatagramSocket socket) throws IOException {
        System.out.println("Waiting for server response");
        byte[] buf = new byte[500];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        System.out.println("Formatting response");
        return new String(packet.getData(), 0, packet.getLength());
    }

    private DatagramSocket createConnection(ConnectionData connectionData) throws SocketException {
        System.out.println("Creating connection with " + connectionData.getHostname());
        InetSocketAddress address = new InetSocketAddress(connectionData.getHostname(), ConnectionData.PORT);
        DatagramSocket socketServer = new DatagramSocket();
        socketServer.connect(address);
        return socketServer;
    }

    /**
     * Checks the program arguments on input data
     * asks the user if it cannot find them
     * <p>
     *
     * @param args the program arguments
     * @return a connection data object
     */
    ConnectionData createConnectionData(String[] args) {
        String hostname;
        String filename;

        if (args.length < 2) {
            //no(t enough) input data)
            Scanner scanner = new Scanner(System.in);

            System.out.println("please specify a hostname");
            hostname = scanner.nextLine();

            System.out.println("please specify a filename");
            filename = scanner.nextLine();

        } else {
            //Input data was included in arguments
            hostname = args[0];
            filename = args[1];
        }

        return new ConnectionData(hostname, filename);
    }

}