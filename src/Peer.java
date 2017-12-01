import java.net.*;
import java.io.*;
import java.util.*;

public class Peer
{
    private final int id;
    private final String hostname;
    private final int port;
    //private Socket socket;
    private Bitfield bitfield;
    boolean choked;
    boolean interested;

    // bytes downloaded during the current unchoke session
    int bytesDownloaded;

    DataOutputStream outputStream;
    DataInputStream inputStream;

    Peer(int id, String hostname, int port, boolean hasFile, int bitfieldLength) throws IOException
    {
        this.id = id;
        this.hostname = hostname;
        this.port = port;
        this.bitfield = new Bitfield(bitfieldLength);

        // bitfield should be all 1s if this peer has the file
        if (hasFile)
            this.bitfield.set(0, bitfieldLength, true);

        this.choked = true;
        this.interested = false;

        this.bytesDownloaded = 0;
    }

    public void initStreams(Socket socket) throws IOException
    {
        this.outputStream = new DataOutputStream(socket.getOutputStream());
        this.outputStream.flush();

        this.inputStream = new DataInputStream(socket.getInputStream());
    }

    public DataOutputStream getOutputStream()
    {
        return outputStream;
    }

    public DataInputStream getInputStream()
    {
        return inputStream;
    }

    public int getBytesDownloaded()
    {
        return bytesDownloaded;
    }

    public void increaseBytesDownloaded(int bytes)
    {
        this.bytesDownloaded += bytes;
    }

    public void resetBytesDownloaded()
    {
        this.bytesDownloaded = 0;
    }

    public boolean isChoked()
    {
        return choked;
    }

    public void setChoked(boolean choked)
    {
        System.out.printf("%d is now%s choked\n", id, choked ? "" : " not");
        this.choked = choked;
    }

    public boolean isInterested()
    {
        return interested;
    }

    public void setInterested(boolean interested)
    {
        System.out.printf("%d is now%s interested\n", id, interested ? "" : " not");
        this.interested = interested;
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
        if (this.bitfield.cardinality() == this.bitfield.length())
        {
            System.out.println("Peer " + id + " has file");
            return true;
        }
        return false;
    }

    public String toString()
    {
        return String.format("Peer %d: %s:%d %s", id, hostname, port, this.HasFile() ? "Has file" : "No file");
    }

    public Bitfield getBitField()
    {
        return this.bitfield;
    }

    public void setBitfield(byte[] bytes)
    {
        BitSet bits = BitSet.valueOf(bytes);
        boolean[] bools = new boolean[bytes.length * 8];
        for (int i = bits.nextSetBit(0); i != -1; i = bits.nextSetBit(i+1)) {
            bools[i] = true;
        }

        this.bitfield = new Bitfield(bools);
    }

    public void setBitInBitField(int index, boolean value)
    {
        this.bitfield.set(index, value);
    }

    public String PrintBitset()
    {
        String res = "";
        for (int i = 0; i < bitfield.length(); i++)
            res += bitfield.get(i) ? "1" : "0";
        return res + " " + bitfield.length();
    }
}
