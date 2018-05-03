
import java.io.IOException;
import java.net.*;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        new Main().run(args);
    }

    private void run(String[] args) {
        ConnectionData connectionData = createConnectionData(args);

        try (DatagramSocket serverSocket = createConnection(connectionData)) {
            sendMessage(serverSocket, connectionData);
            System.out.println(receiveMessage(serverSocket));

        } catch (SocketException se) {
            System.out.println("could not connect to server, please try again!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(DatagramSocket socket, ConnectionData connectionData) throws IOException {
        System.out.println("Sending request to server");

        byte[] buf = createRequestMessage(connectionData.getFilename());
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

    private byte[] createRequestMessage(String filename) {
        byte[] protocolMarker = "SaxTP".getBytes();
        byte[] packetType = new byte[]{0};

        byte[] transferIdentifier = new byte[4];
        new Random().nextBytes(transferIdentifier);

        byte[] file = filename.getBytes();
        byte[] buf = new byte[protocolMarker.length + packetType.length + transferIdentifier.length + file.length];

        System.arraycopy(protocolMarker, 0, buf, 0, protocolMarker.length);
        System.arraycopy(packetType, 0, buf, protocolMarker.length, packetType.length);
        System.arraycopy(transferIdentifier, 0, buf, protocolMarker.length + packetType.length, transferIdentifier.length);
        System.arraycopy(file, 0, buf, transferIdentifier.length + packetType.length + protocolMarker.length, file.length);

        return buf;
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