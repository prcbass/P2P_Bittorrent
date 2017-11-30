import java.net.*;
import java.io.*;
import java.util.*;

public class Peer
{
    private final int id;
    private final String hostname;
    private final int port;
    private boolean hasFile;
    private Socket socket;
    private BitSet bitfield;

    Peer(int id, String hostname, int port, boolean hasFile, int bitfieldLength) throws IOException
    {
        this.id = id;
        this.hostname = hostname;
        this.port = port;
        this.hasFile = hasFile;
        this.bitfield = new BitSet(bitfieldLength);

        // bitfield should be all 1s if this peer has the file
        if (this.hasFile)
            this.bitfield.set(0, bitfieldLength, true);
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

    public void OpenSocket() throws IOException
    {
        this.socket = new Socket(hostname, port); //for sending requests
    }

    public String toString()
    {
        return String.format("Peer %d: %s:%d %s", id, hostname, port, hasFile ? "Has file" : "No file");
    }

    public byte[] getBitField()
    {
        return this.bitfield.toByteArray();
    }

    public String PrintBitset()
    {
        String res = "";
        for (int i = 0; i < bitfield.length(); i++)
            res += bitfield.get(i) ? "1" : "0";
        return res + bitfield.length();
    }
}
