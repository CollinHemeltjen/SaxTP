package Objects;

import java.util.Arrays;

public class SaxTPResponseData extends SaxTPMessage {

  public static final int RESPONSE_PACK_TYPE_NUMBER = -128;
  public static final int TRANSFERID_POSITION_START = 6;
  public static final int TRANSFERID_POSITION_END = 10;
  public static final int SEQUENCEID_POSITION_START = 10;
  public static final int SEQUENCEID_POSITION_END = 14;
  public static final int DATA_POSITION_START = 14;

  private byte[] data;
  private byte[] sequenceId;

  public SaxTPResponseData(final byte[] response) {
    super(new byte[]{RESPONSE_PACK_TYPE_NUMBER}, Arrays.copyOfRange(
        response, TRANSFERID_POSITION_START, TRANSFERID_POSITION_END));
    sequenceId = Arrays.copyOfRange(response, SEQUENCEID_POSITION_START, SEQUENCEID_POSITION_END);
    data = Arrays.copyOfRange(response, DATA_POSITION_START, response.length);
  }

  public byte[] getData() {
    return Arrays.copyOf(data, data.length);
  }

  public byte[] getSequenceId() {
    return Arrays.copyOf(sequenceId, sequenceId.length);
  }

}
