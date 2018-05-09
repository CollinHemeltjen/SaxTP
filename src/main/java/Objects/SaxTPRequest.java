package Objects;

import java.nio.charset.Charset;

public class SaxTPRequest extends SaxTPMessage {

  private String filename;

  public SaxTPRequest(String filename) {
    super(new byte[]{0});
    this.filename = filename;
  }

  @Override
  public byte[] getBytes() {
    byte[] filenameBytes = filename.getBytes(Charset.forName("UTF-8"));
    byte[] message = super.getBytes();
    byte[] buf = new byte[message.length + filenameBytes.length];

    System.arraycopy(message, 0, buf, 0, message.length);
    System.arraycopy(filenameBytes, 0, buf, message.length, filenameBytes.length);
    return buf;
  }
}
