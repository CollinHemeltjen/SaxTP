import Objects.ConnectionData;
import Objects.SaxTPRequest;
import Objects.SaxTPResponseAck;
import Objects.SaxTPResponseData;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

  public static void main(final String[] args) {
    new Main().run(args);
  }

  /**
   * The main running loop of the program.
   *
   * @param args the program variables (hostname,filename)
   */
  private void run(final String[] args) {
    ConnectionData connectionData = createConnectionData(args);
    HashMap<BigInteger, SaxTPResponseData> responses = new HashMap<>();
    ArrayList<Integer> lostPackets;

    try {
      System.out.println("Creating connection with " + connectionData.getHostname());
      while (true) {
        try (DatagramSocket serverSocket = createConnection(connectionData)) {
          sendRequest(serverSocket, connectionData);

          System.out.println(
              "Retrieving file, this may take a while depending on the file you are trying to retrieve");
          responses = receiveResponses(serverSocket, responses);
          lostPackets = findLostPackets(responses);

          if (!lostPackets.isEmpty()) {
            System.out.println("We lost some bits of data while downloading, we are trying again");
            continue;
          }

          System.out.println("Building file");
          buildFile(responses, connectionData.getFilename());

          System.out.println("File build");
          break;
        } catch (SocketTimeoutException ste) {
          System.out.println("Server timed out, trying again");
        }
      }
    } catch (SocketException se) {
      System.out.println("could not connect to server, please try again!");
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println("done!");

  }

  /**
   * Sends a Request to the server.
   *
   * @param socket the socket to send to
   * @param connectionData the connection data for the server
   * @throws IOException when output goes wrong
   */
  private void sendRequest(final DatagramSocket socket, final ConnectionData connectionData)
      throws IOException {
    System.out.println("Sending request to server");
    SaxTPRequest request = new SaxTPRequest(connectionData.getFilename());

    byte[] buf = request.getBytes();
    DatagramPacket packet = new DatagramPacket(buf, buf.length);
    socket.send(packet);
  }

  /**
   * Searches the HashMap to check if any packets are missing.
   *
   * @param responses a HashMap with responses to check
   * @return a list of packets missing
   */
  private ArrayList<Integer> findLostPackets(
      final HashMap<BigInteger, SaxTPResponseData> responses) {
    ArrayList<Integer> lostIndexes = new ArrayList<>();
    for (int i = 0; i < responses.size(); i++) {
      if (!responses.containsKey(BigInteger.valueOf(i))) {
        lostIndexes.add(i);
      }
    }
    return lostIndexes;
  }

  private static final int TOTAL_MESSAGE_SIZE = 514;
  private static final int PACKET_TYPE_POSITION = 5;
  private static final int RESPONSE_PACK_TYPE_NUMBER = -128;
  private static final int TIMEOUT_DURATION = 2500;

  /**
   * Uses receiveMessage and sendResponseAck to perform a packet exchange.
   *
   * @param socket the socket to listen and send on
   * @param responses a HashMap with previous results to merge the two maps. If this is the first
   * time, the map can be empty
   * @return a HashMap of retrieved responses
   * @throws IOException when the retrieval goes wrong
   */
  private HashMap<BigInteger, SaxTPResponseData> receiveResponses(
      final DatagramSocket socket,
      final HashMap<BigInteger, SaxTPResponseData> responses)
      throws IOException {

    byte[] response = null;
    socket.setSoTimeout(TIMEOUT_DURATION);
    do {
      if (response != null) {
        socket.setSoTimeout(0);
      }
      response = receiveMessage(socket);
      if (response[PACKET_TYPE_POSITION] != RESPONSE_PACK_TYPE_NUMBER) {
        continue;
      }
      SaxTPResponseData responseData = new SaxTPResponseData(response);

      BigInteger index = new BigInteger(responseData.getSequenceId());
      responses.put(index, responseData);

      sendResponseAck(socket, responseData.getTransferId(),
          responseData.getSequenceId());
    } while (response.length == TOTAL_MESSAGE_SIZE);

    return responses;
  }

  /**
   * Waits for a new message from the server.
   *
   * @param socket the socket to listen on
   * @return a byte[] containing the message
   * @throws IOException when it sees a faulty input
   */
  private byte[] receiveMessage(
      final DatagramSocket socket) throws IOException {
    byte[] buf = new byte[TOTAL_MESSAGE_SIZE];
    DatagramPacket packet = new DatagramPacket(buf, buf.length);
    socket.receive(packet);
    byte[] response = new byte[packet.getLength()];
    System.arraycopy(packet.getData(), 0, response, 0, response.length);
    return response;
  }

  /**
   * Creates and sends a new response ack.
   *
   * @param socket to send response to
   * @param transferId transfer to reply to
   * @param sequenceId packet to acknowledge
   * @throws IOException when sending goes wrong
   */
  private void sendResponseAck(final DatagramSocket socket,
      final byte[] transferId, final byte[] sequenceId) throws IOException {
    byte[] buf = new SaxTPResponseAck(transferId, sequenceId).getBytes();
    DatagramPacket packet = new DatagramPacket(buf, buf.length);
    socket.send(packet);
  }


  /**
   * Uses list of responses to build the final file.
   *
   * File is saved in the current folder
   *
   * @param responses list of SaxTPResponseData to merge into one file
   * @param filename the name of the file
   * @throws IOException when the creating of the file goes wrong
   */
  private void buildFile(final HashMap<BigInteger, SaxTPResponseData> responses,
      final String filename) throws IOException {
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
    }
  }

  /**
   * Creates a connection to the server.
   *
   * @param connectionData contains the data for the connection
   * @return the DatagramSocket containing the connection to the server
   * @throws SocketException when the connecting goes wrong
   */
  private DatagramSocket createConnection(final ConnectionData connectionData)
      throws SocketException {
    InetSocketAddress address = new InetSocketAddress(
        connectionData.getHostname(), ConnectionData.PORT);
    DatagramSocket socketServer = new DatagramSocket();
    socketServer.connect(address);
    return socketServer;
  }

  /**
   * Checks the program arguments on input data, asks the user if non exist.
   *
   * @param args the program arguments
   * @return a connection data object
   */
  ConnectionData createConnectionData(final String[] args) {
    String hostname;
    String filename;

    if (args.length < 2) {
      //no(t enough) input data)
      Scanner scanner = new Scanner(System.in, "UTF-8");

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
