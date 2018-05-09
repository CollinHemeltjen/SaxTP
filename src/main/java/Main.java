import Objects.ConnectionData;
import Objects.SaxTPRequest;
import Objects.SaxTPResponseAck;
import Objects.SaxTPResponseData;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Random;

public class Main {

  public static void main(final String[] args) {
    new Main().run(args);
  }

  private void run(final String[] args) {
    ConnectionData connectionData = createConnectionData(args);
    HashMap<BigInteger, SaxTPResponseData> responses = new HashMap<>();
    ArrayList<Integer> lostPackets;

    try {
      boolean fileReceived = false;
      while (!fileReceived) {
        try (DatagramSocket serverSocket = createConnection(connectionData)) {
          sendRequest(serverSocket, connectionData);

          responses = receiveResponses(serverSocket, responses);
          lostPackets = findLostPackets(responses);

          if (!lostPackets.isEmpty()) {
            System.out.println("Lost packets: " + lostPackets);
            continue;
          }
//          while (!lostPackets.isEmpty()) {
//            System.out.println("Lost packets: " + lostPackets);
//            DatagramSocket newServerSocket = createConnection(connectionData);
//            sendRequest(newServerSocket, connectionData);
//            responses = combineResponses(responses, receiveResponses(newServerSocket), lostPackets);
//            lostPackets = findLostPackets(responses);
//          }

          System.out.println("responses:");
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

  private void sendRequest(final DatagramSocket socket, final ConnectionData connectionData)
      throws IOException {
    System.out.println("Sending request to server");
    SaxTPRequest request = new SaxTPRequest(connectionData.getFilename());

    byte[] buf = request.getBytes();
    DatagramPacket packet = new DatagramPacket(buf, buf.length);
    socket.send(packet);
  }

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

  private HashMap<BigInteger, SaxTPResponseData> combineResponses(
      final HashMap<BigInteger, SaxTPResponseData> originalList,
      final HashMap<BigInteger, SaxTPResponseData> extraList,
      final ArrayList<Integer> lostIndexes) {
    for (Integer lostIndex : lostIndexes) {

      originalList.put(BigInteger.valueOf(lostIndex), extraList.get(BigInteger.valueOf(lostIndex)));
    }
    return originalList;
  }

  private HashMap<BigInteger, SaxTPResponseData> receiveResponses(final DatagramSocket socket,
      final HashMap<BigInteger, SaxTPResponseData> responses)
      throws IOException {

    byte[] response = null;
    do {
      if (response == null) {
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

  private void sendResponseAck(final DatagramSocket socket, final byte[] transferId,
      final byte[] sequenceId) throws IOException {
    byte[] buf = new SaxTPResponseAck(transferId, sequenceId).getBytes();
    DatagramPacket packet = new DatagramPacket(buf, buf.length);
    socket.send(packet);
  }

  private int countSequenceNumber(final byte[] sequenceId) {
    int total = 0;
    for (byte number : sequenceId) {
      total += (int) number;
    }
    return total;
  }


  /**
   * Uses list of responses to build the final file.
   *
   * @param responses list of SaxTPResponseData to merge into one file
   * @param filename the name of the file
   * @throws IOException when the creating of the file goes wrong
   */
  private void buildFile(final HashMap<BigInteger, SaxTPResponseData> responses,
      final String filename) throws IOException {
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
   * Creates a byte array with the following setup. ‘SaxTP’ uint8​(0) uint32​(transferId)
   * byte[...]​(filename)
   *
   * @param filename the filename to request from the server
   * @return the byte array
   */
  @Deprecated
  byte[] createRequestMessage(final String filename) {
    byte[] protocolMarker = "SaxTP".getBytes();
    byte[] packetType = new byte[]{0};

    byte[] transferIdentifier = new byte[4];
    new Random().nextBytes(transferIdentifier);

    byte[] file = filename.getBytes();
    byte[] buf = new byte[protocolMarker.length + packetType.length + transferIdentifier.length
        + file.length];

    System.arraycopy(protocolMarker, 0, buf, 0, protocolMarker.length);
    System.arraycopy(packetType, 0, buf, protocolMarker.length, packetType.length);
    System.arraycopy(transferIdentifier, 0, buf, protocolMarker.length + packetType.length,
        transferIdentifier.length);
    System.arraycopy(file, 0, buf,
        transferIdentifier.length + packetType.length + protocolMarker.length, file.length);

    return buf;
  }

  /**
   * Creates a connection to the server.
   *
   * @param connectionData a connectionData object containing the data for the connection
   * @return the DatagramSocket containing the connection to the server
   * @throws SocketException when the connecting goes wrong
   */
  private DatagramSocket createConnection(final ConnectionData connectionData)
      throws SocketException {
    System.out.println("Creating connection with " + connectionData.getHostname());
    InetSocketAddress address = new InetSocketAddress(connectionData.getHostname(),
        ConnectionData.PORT);
    DatagramSocket socketServer = new DatagramSocket();
    socketServer.connect(address);
    return socketServer;
  }

  /**
   * Checks the program arguments on input data asks the user if it cannot find them.
   *
   * @param args the program arguments
   * @return a connection data object
   */
  ConnectionData createConnectionData(final String[] args) {
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
