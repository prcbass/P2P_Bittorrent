public class Peer
{
    private final int id;
    private final String hostname;
    private final int port;
    private boolean hasFile;

    Peer(int id, String hostname, int port, boolean hasFile)
    {
        this.id = id;
        this.hostname = hostname;
        this.port = port;
        this.hasFile = hasFile;
    }

    public int GetId()
    {
        return this.id;
    }

    public String GetHostname()
    {
        return this.hostname;
    }

    public int GetPort()
    {
        return this.port;
    }

    public boolean HasFile()
    {
        return hasFile;
    }

    public void SetHasFile(boolean hasFile)
    {
        this.hasFile = hasFile;
        return;
    }

    public String toString()
    {
        return String.format("Peer %d: %s:%d %s", id, hostname, port, hasFile ? "Has file" : "No file");
    }
}
