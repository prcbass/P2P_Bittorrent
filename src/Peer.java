import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Peer
{
    private final int id;
    private final String hostname;
    private final int port;
    private boolean hasFile;
    private Socket socket;
    private boolean sentHandshake; //true if this instance has sent a handshake to the peer
    private Bitfield bitfield;

    Peer(int id, String hostname, int port, boolean hasFile) throws UnknownHostException, IOException
    {
        this.id = id;
        this.hostname = hostname;
        this.port = port;
        this.hasFile = hasFile;
        this.sentHandshake = false;
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

    public Socket GetSocket()
    {
        return socket;
    }

    public void SetHasFile(boolean hasFile)
    {
        this.hasFile = hasFile;
        return;
    }

    public boolean HasSentHandshake()
    {
        return this.sentHandshake;
    }

    public void SetSentHandshake(boolean sent)
    {
        this.sentHandshake = sent;
    }

    public void OpenSocket() throws IOException
    {
        this.socket = new Socket(hostname, port); //for sending requests
    }

    public String toString()
    {
        return String.format("Peer %d: %s:%d %s", id, hostname, port, hasFile ? "Has file" : "No file");
    }
}
