import Objects.ConnectionData;
import Objects.SaxTPRequest;
import Objects.SaxTPResponseAck;
import Objects.SaxTPResponseData;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;

public class Main {
    public static int counter = 0;

    public static void main(String[] args) {
        new Main().run(args);
    }

    private void run(String[] args) {
        ConnectionData connectionData = createConnectionData(args);
        HashMap<BigInteger,SaxTPResponseData> responses;
        ArrayList<Integer> lostPackets;

        try (DatagramSocket serverSocket = createConnection(connectionData)) {
            boolean fileReceived = false;
            while (!fileReceived) {
                try {
                    sendRequest(serverSocket, connectionData);

                    responses = receiveResponses(serverSocket);
                    lostPackets = findLostPackets(responses);

                    while(!lostPackets.isEmpty()){
                        System.out.println(lostPackets);
                        DatagramSocket newServerSocket = createConnection(connectionData);
                        sendRequest(newServerSocket, connectionData);
                        responses = combineResponses(responses, receiveResponses(newServerSocket), lostPackets);
                        lostPackets = findLostPackets(responses);
                    }
                    for (SaxTPResponseData responseData : responses.values()) {
                        System.out.println(Arrays.toString(responseData.getSequenceId()));
                    }
                    buildFile(responses, connectionData.getFilename());
                    fileReceived = true;
                    System.out.println("done!");
                } catch (SocketTimeoutException ste) {
                    System.out.println("Server timed out, trying again");
                }
            }
        } catch (SocketException se) {
            System.out.println("could not connect to server, please try again!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendRequest(DatagramSocket socket, ConnectionData connectionData) throws IOException {
        System.out.println("Sending request to server");
        SaxTPRequest request = new SaxTPRequest(connectionData.getFilename());

        byte[] buf = request.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.send(packet);
    }

    private void retrieveFile(DatagramSocket socket, String filename) throws IOException {
        System.out.println("Retrieving file from server");
        HashMap<BigInteger,SaxTPResponseData> responses = receiveResponses(socket);
        ArrayList<Integer> lostPackets = findLostPackets(responses);

        while(!lostPackets.isEmpty()){
            System.out.println(lostPackets);
            responses = combineResponses(responses, receiveResponses(socket), lostPackets);
            lostPackets = findLostPackets(responses);
        }

        for (SaxTPResponseData responseData : responses.values()) {
            System.out.println(Arrays.toString(responseData.getSequenceId()));
        }
        buildFile(responses, filename);
    }

    private BigInteger sequenceIdToIndex(byte[] sequenceID) {
        return new BigInteger(sequenceID);
    }

    private ArrayList<Integer> findLostPackets(HashMap<BigInteger,SaxTPResponseData> responses) {
        ArrayList<Integer> lostIndexes = new ArrayList<>();
        for (int i = 0; i < responses.size(); i++) {
            if (!responses.containsKey(BigInteger.valueOf(i)) ) {
                lostIndexes.add(i);
            }
        }
        return lostIndexes;
    }

    private HashMap<BigInteger,SaxTPResponseData> combineResponses(HashMap<BigInteger,SaxTPResponseData> originalList, HashMap<BigInteger,SaxTPResponseData> extraList, ArrayList<Integer> lostIndexes) {
        for (Integer lostIndex : lostIndexes) {

            originalList.put(BigInteger.valueOf(lostIndex), extraList.get(BigInteger.valueOf(lostIndex)));
        }
        return originalList;
    }

    private HashMap<BigInteger, SaxTPResponseData> receiveResponses(DatagramSocket socket) throws IOException {
        HashMap<BigInteger,SaxTPResponseData> responses = new HashMap<>();
        byte[] response;
        do {
            if (responses.isEmpty()) {
                socket.setSoTimeout(2500);
            } else {
                socket.setSoTimeout(0);
            }
            response = receiveMessage(socket);
            if (response[5] != -128) {
                System.out.println(response[5]);
                System.out.println("-128");
                continue;
            }
            SaxTPResponseData responseData = new SaxTPResponseData(response);

            BigInteger index = new BigInteger(responseData.getSequenceId());
            responses.put(index, responseData);



            sendResponseAck(socket, responseData.getTransferId(), responseData.getSequenceId());
            System.out.println(Arrays.toString(responseData.getSequenceId()));
        } while (response.length == 514);

        return responses;
    }

    private byte[] receiveMessage(DatagramSocket socket) throws IOException {
        byte[] buf = new byte[514];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        byte[] response = new byte[packet.getLength()];
        System.arraycopy(packet.getData(), 0, response, 0, response.length);
        return response;
    }

    private void sendResponseAck(DatagramSocket socket, byte[] transferId, byte[] sequenceId) throws IOException {
        byte[] buf = new SaxTPResponseAck(transferId, sequenceId).getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.send(packet);
    }

    private int countSequenceNumber(byte[] sequenceId) {
        int total = 0;
        for (byte number : sequenceId) {
            total += (int) number;
        }
        return total;
    }


    /**
     * Uses list of responses to build the final file
     *
     * @param responses list of SaxTPResponseData to merge into one file
     * @param filename  the name of the file
     * @throws IOException when the creating of the file goes wrong
     */
    private void buildFile(HashMap<BigInteger,SaxTPResponseData> responses, String filename) throws IOException {
        System.out.println("Building file");
        int length = 0;
        for (SaxTPResponseData data : responses.values()) {
            length += data.getData().length;
        }

        byte[] bytes = new byte[length];

        ByteBuffer target = ByteBuffer.wrap(bytes);
        for (SaxTPResponseData data : responses.values()) {
            target.put(data.getData());
        }

        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(bytes);
            System.out.println("File build");

        }
    }


    /**
     * Creates a byte array with the following setup
     * ‘SaxTP’ uint8​(0) uint32​(transferId) byte[...]​(filename)
     *
     * @param filename the filename to request from the server
     * @return the byte array
     */
    @Deprecated
    byte[] createRequestMessage(String filename) {
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

    /**
     * Creates a connection to the server
     *
     * @param connectionData a connectionData object containing the data for the connection
     * @return the DatagramSocket containing the connection to the server
     * @throws SocketException when the connecting goes wrong
     */
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