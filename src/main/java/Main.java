import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        new Main().run(args);
    }

    private DatagramSocket socketToServer;

    private void run(String[] args) {
        ConnectionData connectionData = createConnectionData(args);

        try {
            createConnection(connectionData.getHostname(), ConnectionData.PORT);
            System.out.println(socketToServer);
        } catch (SocketException se) {
            System.out.println("could not connect to server, please try again!");
        }
    }

    private void createConnection(String hostname, int port) throws SocketException {
        InetSocketAddress address = new InetSocketAddress(hostname, port);
        socketToServer = new DatagramSocket();
        socketToServer.connect(address);
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