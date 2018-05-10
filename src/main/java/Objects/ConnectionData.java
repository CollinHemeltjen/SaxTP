package Objects;

public class ConnectionData {

  public static final int PORT = 29588;
  private String hostname;
  private String filename;

  public ConnectionData(final String hostname, final String filename) {
    this.hostname = hostname;
    this.filename = filename;
  }

  public String getHostname() {
    return hostname;
  }

  public String getFilename() {
    return filename;
  }
}