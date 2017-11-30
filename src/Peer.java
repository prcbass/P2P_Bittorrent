import java.net.*;
import java.io.*;
import java.util.*;

public class Peer
{
    private final int id;
    private final String hostname;
    private final int port;
    private Socket socket;
    private BitSet bitfield;

    Peer(int id, String hostname, int port, boolean hasFile, int bitfieldLength) throws IOException
    {
        this.id = id;
        this.hostname = hostname;
        this.port = port;
        this.bitfield = new BitSet(bitfieldLength);

        // bitfield should be all 1s if this peer has the file
        if (hasFile)
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
        if (this.bitfield.cardinality() == this.bitfield.length() && this.bitfield.length() > 0)
        {
            System.out.println("Peer " + id + " has file");
            return true;
        }
        return false;
    }

    public Socket GetSocket()
    {
        return socket;
    }

    public void OpenSocket() throws IOException
    {
        this.socket = new Socket(hostname, port); //for sending requests
    }

    public String toString()
    {
        return String.format("Peer %d: %s:%d %s", id, hostname, port, this.HasFile() ? "Has file" : "No file");
    }

    public BitSet getBitField()
    {
        return this.bitfield;
    }

    public void setBitfield(BitSet bitfield)
    {
        this.bitfield = bitfield;
    }

    public String PrintBitset()
    {
        String res = "";
        for (int i = 0; i < bitfield.length(); i++)
            res += bitfield.get(i) ? "1" : "0";
        return res + bitfield.length();
    }
}
