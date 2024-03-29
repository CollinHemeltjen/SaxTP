import Objects.ConnectionData;
import Objects.SaxTPResponseData;
import java.io.File;
import java.util.ArrayList;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

public class MainTest {

  @BeforeClass
  public static void deleteZipFiles() {
    String[] testFiles = {"1.zip", "2.zip", "3.zip", "4.zip"};
    for (String testFile : testFiles) {
      File file = new File(testFile);
      if (file.delete()) {
        System.out.println(testFile + " is deleted");
      } else {
        System.out.println(testFile + " did not exist");
      }
    }
  }

  /**
   * test zip files
   */
  public void testZipDownload(String filename) throws Exception {
    Main.main(new String[]{"vanviegen.net", filename});
    File file = new File(filename);
    assert file.exists();
  }

  @Test
  public void testZip1Download() throws Exception {
    String filename = "1.zip";
    testZipDownload(filename);
    System.out.println("1.zip downloaded successfully");
  }

  @Test
  public void testZip2Download() throws Exception {
    String filename = "2.zip";
    testZipDownload(filename);
    System.out.println("2.zip downloaded successfully");
  }

  @Test
  public void testZip3Download() throws Exception {
    String filename = "3.zip";
    testZipDownload(filename);
    System.out.println("3.zip downloaded successfully");
  }

  @Test
  public void testZip4Download() throws Exception {
    String filename = "4.zip";
    testZipDownload(filename);
    System.out.println("4.zip downloaded successfully");
  }


  @Test
  public void testSaxTPResponseData() throws Exception {
    byte[] response = new byte[]{83, 97, 120, 84, 80, -128, -115, -55, -82, 3, 0, 0, 0, 0, 80, 75,
        3, 4, 20, 0, 0, 0, 8, 0, 124, -84, -101, 76, -85, -22, -13, 28, -117, -84, 71, 0, 91, 10,
        72, 0, 5, 0, 28, 0, 50, 46, 109, 112, 51, 85, 84, 9, 0, 3, -100, 123, -29, 90, -100, 123,
        -29, 90, 117, 120, 11, 0, 1, 4, -24, 3, 0, 0, 4, -24, 3, 0, 0, -20, -3, 101, 112, -35, 94,
        -16, 45, 10, 30, 99, -52, -52, -52, -52, -52, -52, -52, -52, -52, -52, 108, -57, -52, 76,
        -57, -52, -52, 76, -119, -103, -103, 57, 49, -77, 99, -90, -40, -98, -8, 119, 111, -51, -99,
        55, 95, -26, -45, 84, -67, 122, -11, 111, -105, -67, 117, -92, 35, 105, 91, 106, -83, 94,
        -85, 119, 75, -110, 22, 99, 5, 3, -4, 51, 68, 15, 53, 37, 113, -26, 127, 19, -72, 0, 0, -56,
        -25, -121, 10, -64, 26, 96, 10, -80, 5, 16, 1, -124, 1, -82, 0, 55, -128, 29, -64, 28, -32,
        -3, 111, -87, -88, -94, -68, -4, -65, 6, -19, -33, -105, -52, 29, 44, 63, 63, 0, -128, -49,
        15, 102, 0, 55, -128, 11, -64, -7, 111, -74, -102, -88, -94, -62, -41, -74, -2, -37, -124,
        26, 64, 17, -96, 4, 96, 3, 48, 125, 45, -112, 86, 99, -7, -41, -80, -2, -73, 64, -31, -33,
        -74, 60, -2, -3, -70, -4, -37, -70, 36, -64, 17, -32, -16, -17, -57, -8, -65, 105, -21, -1,
        -26, 19, 1, -76, -1, -51, 117, -1, -41, -86, 3, -100, -66, 86, 86, 17, -107, -3, -41, -64,
        -2, -73, 50, -45, -65, -19, 113, 124, -51, -44, 22, 87, -7, -41, -64, -1, 55, -13, -1, -12,
        64, 73, 69, 90, -29, 95, 67, 9, 0, 104, -54, 51, -54, -101, -101, 89, 27, -117, -38, 25,
        -69, -70, 42, -71, 88, -37, 27, -69, 120, 75, -117, 1, 122, -4, -115, -26, 73, -9, -9, 100,
        -61, 11, -91, 74, 104, -88, -60, 8, -2, -9, 10, -44, -1, 95, 43, -88, -102, -101, 58, 58,
        -104, -3, -81, 85, -2, -57, -2, -57, -2, -57, -2, -57, -2, -57, -2, -57, -2, 31, 103, -97,
        111, 73, -1, 63, -65, -93, 101, -19, 96, 9, -8, 47, -84, 19, -58, 1, -92, 0, 59, 0, 80,
        -120, 111, 48, 112, -120, -56, 104, 24, -40, -72, 4, 68, -92, 20, 84, -76, -12, 76, -84,
        -20, 92, 60, -4, -126, 34, -30, -110, 50, 114, -118, 42, 106, -102, -38, 122, 6, -58, 102,
        22, -42, -74, 14, -50, -82, 30, 94, -66, -2, 65, -33, -61, 34, -93, -29, 18, -110, -45, 50,
        -78, 115, -14, 11, 75, -54, 43, 107, -22, 26, -101, -37, 58, -70, -5, 126, 14, 14, -113, 77,
        76, -49, 45, 44, -81, 110, 108, -19, -18, 29, 28, -97, 94, 92, -35, -36, 61, 62};
    SaxTPResponseData responseData = new SaxTPResponseData(response);
    assert Arrays.equals(responseData.getPacketType(), new byte[]{-128});
    assert Arrays.equals(responseData.getTransferId(), new byte[]{-115, -55, -82, 3});
    assert Arrays.equals(responseData.getSequenceId(), new byte[]{0, 0, 0, 0});
    assert Arrays.equals(responseData.getData(),
        new byte[]{80, 75, 3, 4, 20, 0, 0, 0, 8, 0, 124, -84, -101, 76, -85, -22, -13, 28, -117,
            -84, 71, 0, 91, 10, 72, 0, 5, 0, 28, 0, 50, 46, 109, 112, 51, 85, 84, 9, 0, 3, -100,
            123, -29, 90, -100, 123, -29, 90, 117, 120, 11, 0, 1, 4, -24, 3, 0, 0, 4, -24, 3, 0, 0,
            -20, -3, 101, 112, -35, 94, -16, 45, 10, 30, 99, -52, -52, -52, -52, -52, -52, -52, -52,
            -52, -52, 108, -57, -52, 76, -57, -52, -52, 76, -119, -103, -103, 57, 49, -77, 99, -90,
            -40, -98, -8, 119, 111, -51, -99, 55, 95, -26, -45, 84, -67, 122, -11, 111, -105, -67,
            117, -92, 35, 105, 91, 106, -83, 94, -85, 119, 75, -110, 22, 99, 5, 3, -4, 51, 68, 15,
            53, 37, 113, -26, 127, 19, -72, 0, 0, -56, -25, -121, 10, -64, 26, 96, 10, -80, 5, 16,
            1, -124, 1, -82, 0, 55, -128, 29, -64, 28, -32, -3, 111, -87, -88, -94, -68, -4, -65, 6,
            -19, -33, -105, -52, 29, 44, 63, 63, 0, -128, -49, 15, 102, 0, 55, -128, 11, -64, -7,
            111, -74, -102, -88, -94, -62, -41, -74, -2, -37, -124, 26, 64, 17, -96, 4, 96, 3, 48,
            125, 45, -112, 86, 99, -7, -41, -80, -2, -73, 64, -31, -33, -74, 60, -2, -3, -70, -4,
            -37, -70, 36, -64, 17, -32, -16, -17, -57, -8, -65, 105, -21, -1, -26, 19, 1, -76, -1,
            -51, 117, -1, -41, -86, 3, -100, -66, 86, 86, 17, -107, -3, -41, -64, -2, -73, 50, -45,
            -65, -19, 113, 124, -51, -44, 22, 87, -7, -41, -64, -1, 55, -13, -1, -12, 64, 73, 69,
            90, -29, 95, 67, 9, 0, 104, -54, 51, -54, -101, -101, 89, 27, -117, -38, 25, -69, -70,
            42, -71, 88, -37, 27, -69, 120, 75, -117, 1, 122, -4, -115, -26, 73, -9, -9, 100, -61,
            11, -91, 74, 104, -88, -60, 8, -2, -9, 10, -44, -1, 95, 43, -88, -102, -101, 58, 58,
            -104, -3, -81, 85, -2, -57, -2, -57, -2, -57, -2, -57, -2, -57, -2, -57, -2, 31, 103,
            -97, 111, 73, -1, 63, -65, -93, 101, -19, 96, 9, -8, 47, -84, 19, -58, 1, -92, 0, 59, 0,
            80, -120, 111, 48, 112, -120, -56, 104, 24, -40, -72, 4, 68, -92, 20, 84, -76, -12, 76,
            -84, -20, 92, 60, -4, -126, 34, -30, -110, 50, 114, -118, 42, 106, -102, -38, 122, 6,
            -58, 102, 22, -42, -74, 14, -50, -82, 30, 94, -66, -2, 65, -33, -61, 34, -93, -29, 18,
            -110, -45, 50, -78, 115, -14, 11, 75, -54, 43, 107, -22, 26, -101, -37, 58, -70, -5,
            126, 14, 14, -113, 77, 76, -49, 45, 44, -81, 110, 108, -19, -18, 29, 28, -97, 94, 92,
            -35, -36, 61, 62});
  }


  /**
   * createConnectionData()
   */
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

    System.setIn(new ByteArrayInputStream("vanviegen.net\r\n1.zip".getBytes("UTF-8")) );

    // set stdout
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(baos);
    System.setOut(ps);

    ConnectionData connectionData = new Main().createConnectionData(args);

    assert (connectionData.getHostname().equals("vanviegen.net"));
    assert (connectionData.getFilename().equals("1.zip"));
  }
}