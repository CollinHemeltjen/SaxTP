import Objects.ConnectionData;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class MainTest {
    @Test
    public void testCreateConnectionData() throws Exception {
        String[] args = {"vanviegen.net", "1.zip"};
        ConnectionData connectionData = new Main().createConnectionData(args);
        assert (connectionData.getHostname().equals(args[0]));
        assert (connectionData.getFilename().equals(args[1]));
    }

    @Test
    public void testCreateConnectionDataNoArgs() throws Exception {
        String[] args = new String[0];

        System.setIn(new ByteArrayInputStream("vanviegen.net\r\n1.zip".getBytes()));

        // set stdout
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setOut(ps);

        ConnectionData connectionData = new Main().createConnectionData(args);

        assert (connectionData.getHostname().equals("vanviegen.net"));
        assert (connectionData.getFilename().equals("1.zip"));
    }

    @Test
    public void testCreateRequestMessageProtocolMarker() throws Exception {
        byte[] buf = new Main().createRequestMessage("1.zip");

        byte[] protocolMarker = "SaxTP".getBytes();
        for (int i = 0; i < protocolMarker.length; i++) {
            assert buf[i] == protocolMarker[i];
        }

    }

    @Test
    public void testCreateRequestMessagePacketType() throws Exception {
        byte[] buf = new Main().createRequestMessage("1.zip");
        assert buf[5] == (byte) 0;
    }

    @Test
    public void testCreateRequestMessageFileName() throws Exception {
        byte[] buf = new Main().createRequestMessage("1.zip");

        byte[] protocolMarker = "1.zip".getBytes();
        for (int i = 10; i < protocolMarker.length; i++) {
            assert buf[i] == protocolMarker[i];
        }
    }

}