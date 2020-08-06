package fileSharing;

public class Config {
    private int port;
    private String ip;
    private String filename;
    private int packetCount;
    private int packetSize;
    private int lastPacketSize;

    public Config() {

    }

    public Config(int port, String ip, int packetCount, int packetSize, int lastPacketSize) {
        this.port = port;
        this.ip = ip;
        this.packetCount = packetCount;
        this.packetSize = packetSize;
        this.lastPacketSize = lastPacketSize;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getPacketCount() {
        return packetCount;
    }

    public void setPacketCount(int packetCount) {
        this.packetCount = packetCount;
    }

    public int getPacketSize() {
        return packetSize;
    }

    public void setPacketSize(int packetSize) {
        this.packetSize = packetSize;
    }

    public int getLastPacketSize() {
        return lastPacketSize;
    }

    public void setLastPacketSize(int lastPacketSize) {
        this.lastPacketSize = lastPacketSize;
    }

    @Override
    public String toString() {
        return ip + "*" + Integer.toString(port) + "*" + filename + "*" +
                Integer.toString(packetCount) + "*" + Integer.toString(packetSize) + "*" + Integer.toString(lastPacketSize);
    }

    public void convertFromString(String object) {
        String[] objectItems = object.split("\\*");
        ip = objectItems[0];
        port = Integer.parseInt(objectItems[1]);
        filename = objectItems[2];
        packetCount = Integer.parseInt(objectItems[3]);
        packetSize = Integer.parseInt(objectItems[4]);
        lastPacketSize = Integer.parseInt(objectItems[5]);
    }
}
