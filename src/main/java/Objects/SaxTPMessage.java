package Objects;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Random;

public abstract class SaxTPMessage {

  public static final String PROTOCOL_MARKER_TEXT = "SaxTP";
  public static final int PACKETID_SIZE = 4;
  private static final byte[] protocolMarker = PROTOCOL_MARKER_TEXT
      .getBytes(Charset.forName("UTF-8"));
  private byte[] packetType;
  private byte[] transferId;

  public SaxTPMessage(final byte[] packetType, final byte[] transferId) {
    this.packetType = Arrays.copyOf(packetType, packetType.length);
    this.transferId = Arrays.copyOf(transferId, transferId.length);
  }

  public SaxTPMessage(final byte[] packetType) {
    this.packetType = Arrays.copyOf(packetType, packetType.length);
    transferId = new byte[PACKETID_SIZE];
    new Random().nextBytes(transferId);
  }

  public byte[] getBytes() {
    byte[] buf = new byte[protocolMarker.length + packetType.length + transferId.length];
    System.arraycopy(protocolMarker, 0, buf, 0, protocolMarker.length);
    System.arraycopy(packetType, 0, buf, protocolMarker.length, packetType.length);
    System.arraycopy(transferId, 0, buf, protocolMarker.length + packetType.length,
        transferId.length);
    return buf;
  }

  public byte[] getPacketType() {
    return Arrays.copyOf(packetType, packetType.length);
  }

  public byte[] getTransferId() {
    return Arrays.copyOf(transferId, transferId.length);
  }
}
