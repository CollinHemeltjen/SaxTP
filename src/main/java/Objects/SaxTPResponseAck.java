package Objects;

import java.util.Arrays;

public class SaxTPResponseAck extends SaxTPMessage {
  private byte[] sequenceId;

  public SaxTPResponseAck(final byte[] transferId, final byte[] sequenceId) {
    super(new byte[]{1}, transferId);
    this.sequenceId = Arrays.copyOf(sequenceId, sequenceId.length);
  }

  @Override
  public byte[] getBytes() {
    byte[] message = super.getBytes();
    byte[] buf = new byte[message.length + sequenceId.length];

    System.arraycopy(message, 0, buf, 0, message.length);
    System.arraycopy(sequenceId, 0, buf, message.length, sequenceId.length);
    return buf;
  }
}
