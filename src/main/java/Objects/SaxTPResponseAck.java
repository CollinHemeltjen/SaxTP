package Objects;

public class SaxTPResponseAck extends SaxTPMessage {
    private byte[] sequenceId;

    public SaxTPResponseAck(byte[] packetType, byte[] transferId, byte[] sequenceId) {
        super(packetType, transferId);
        this.sequenceId = sequenceId;
    }

    public SaxTPResponseAck(byte[] packetType, byte[] sequenceId) {
        super(packetType);
        this.sequenceId = sequenceId;
    }

    @Override
    public byte[] getBytes() {
        byte[] message = super.getBytes();
        byte[] buf = new byte[message.length + sequenceId.length];

        System.arraycopy(message, 0, buf, 0, message.length);
        System.arraycopy(sequenceId, 0, buf, message.length, message.length + sequenceId.length);
        return buf;
    }
}
