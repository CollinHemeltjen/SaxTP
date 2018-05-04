package Objects;

public class SaxTPResponseData extends SaxTPResponseAck {
    private byte[] data;

    public SaxTPResponseData(byte[] packetType, byte[] transferId, byte[] sequenceId, byte[] data) {
        super(packetType, transferId, sequenceId);
        this.data = data;
    }

    public SaxTPResponseData(byte[] packetType, byte[] sequenceId, byte[] data) {
        super(packetType, sequenceId);
        this.data = data;
    }

    @Override
    public byte[] getBytes() {
        byte[] message = super.getBytes();
        byte[] buf = new byte[message.length + data.length];

        System.arraycopy(message, 0, buf, 0, message.length);
        System.arraycopy(data, 0, buf, message.length, message.length + data.length);
        return buf;
    }
}
