package Objects;

public class SaxTPRequest extends SaxTPMessage {
    private String filename;

    public SaxTPRequest(byte[] packetType, byte[] transferId, String filename) {
        super(packetType, transferId);
        this.filename = filename;
    }

    public SaxTPRequest(byte[] packetType, String filename) {
        super(packetType);
        this.filename = filename;
    }

    @Override
    public byte[] getBytes() {
        byte[] filenameBytes = filename.getBytes();
        byte[] message = super.getBytes();
        byte[] buf = new byte[message.length + filenameBytes.length];

        System.arraycopy(message, 0, buf, 0, message.length);
        System.arraycopy(filenameBytes, 0, buf, message.length, message.length + filenameBytes.length);
        return buf;
    }
}
