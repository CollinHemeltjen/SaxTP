package Objects;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;

public class SaxTPResponseData extends SaxTPMessage {
    private byte[] data;
    private byte[] sequenceId;


    public SaxTPResponseData(byte[] transferId, byte[] sequenceId, byte[] data) {
        super(new byte[]{-128}, transferId);
        this.sequenceId = sequenceId;
        this.data = data;
    }

    public SaxTPResponseData(byte[] sequenceId, byte[] data) {
        super(new byte[]{-128});
        this.sequenceId = sequenceId;
        this.data = data;
    }

    public SaxTPResponseData(byte[] response){
        super(new byte[]{-128}, Arrays.copyOfRange(response,6,10));
        sequenceId = Arrays.copyOfRange(response,10,14);
        data = Arrays.copyOfRange(response,14,response.length);
    }

    @Override
    @Deprecated
    public byte[] getBytes() {
        byte[] message = super.getBytes();
        byte[] buf = new byte[message.length + sequenceId.length + data.length];

        System.arraycopy(message, 0, buf, 0, message.length);
        System.arraycopy(sequenceId, 0, buf, message.length, sequenceId.length);
        System.arraycopy(data, 0, buf, message.length + sequenceId.length, data.length);
        return buf;
    }

    public byte[] getData() {
        return data;
    }

    public byte[] getSequenceId() {
        return sequenceId;
    }

    public boolean isNext(SaxTPResponseData o){
        BigInteger thisSequenceID = new BigInteger(sequenceId);
        BigInteger otherSequenceID = new BigInteger(o.sequenceId);
        return Objects.equals(thisSequenceID.add(BigInteger.valueOf(1)), otherSequenceID);
    }



}
