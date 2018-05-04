package Objects;

import java.util.Random;

public abstract class SaxTPMessage {
    private static final byte[] protocolMarker = "SaxTP".getBytes();
    private byte[] packetType;
    private byte[] transferId;

    public SaxTPMessage(byte[] packetType, byte[] transferId) {
        this.packetType = packetType;
        this.transferId = transferId;
    }

    public SaxTPMessage(byte[] packetType) {
        this.packetType = packetType;
        transferId = new byte[4];
        new Random().nextBytes(transferId);
    }

    public byte[] getBytes(){
        byte[] buf = new byte[protocolMarker.length + packetType.length + transferId.length];
        System.arraycopy(protocolMarker, 0, buf, 0, protocolMarker.length);
        System.arraycopy(packetType, 0, buf, protocolMarker.length, packetType.length);
        System.arraycopy(transferId, 0, buf, protocolMarker.length + packetType.length, transferId.length);
        return buf;
    }
}
