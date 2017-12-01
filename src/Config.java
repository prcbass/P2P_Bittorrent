import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Config
{
    static private int numberOfPreferredNeighbors;
    static private int unchokingInterval;
    static private int optimisticUnchokingInterval;
    static private String fileName;
    static private int fileSizeInBytes;
    static private int pieceSizeInBytes;
    static private int optimisticNeighbor;

    static LinkedHashMap<Integer, Peer> peers;
    static private int serverListenPort;

    public static void initConfig(String commonFile) throws FileNotFoundException
    {
        Scanner cfg = new Scanner(new FileReader(commonFile));
        numberOfPreferredNeighbors = Integer.parseInt(cfg.nextLine().split(" ")[1].trim());
        unchokingInterval = Integer.parseInt(cfg.nextLine().split(" ")[1].trim());
        optimisticUnchokingInterval = Integer.parseInt(cfg.nextLine().split(" ")[1].trim());
        fileName = cfg.nextLine().split(" ")[1].trim();
        fileSizeInBytes = Integer.parseInt(cfg.nextLine().split(" ")[1].trim());
        pieceSizeInBytes = Integer.parseInt(cfg.nextLine().split(" ")[1].trim());
        optimisticNeighbor = 0;
        cfg.close();

        peers = new LinkedHashMap<Integer, Peer>();
    }

    public static int getOptimisticUnchokingInterval()
    {
        return optimisticUnchokingInterval;
    }

    public static int getUnchokingInterval()
    {
        return unchokingInterval;
    }
    
    public static int getOptimisticNeighbor()
    {
        return optimisticNeighbor;
    }

    public static void setOptimisticNeighbor(int myPeerId)
    {
        optimisticNeighbor = myPeerId;
    }


    public static int getNumberOfPreferredNeighbors()
    {
        return numberOfPreferredNeighbors;
    }

    public static void initPeers(String peerInfoFile, int myPeerId) throws IOException
    {
        Scanner peerInfo = new Scanner(new FileReader(peerInfoFile));
        while (peerInfo.hasNextLine())
        {
            String[] line = peerInfo.nextLine().split(" ");
            int id = Integer.parseInt(line[0]);

            String hostname = line[1];
            int port = Integer.parseInt(line[2]);
            boolean hasFile = line[3].trim().equals("1");

            if (id == myPeerId)
            {
                serverListenPort = port;
                //continue;
            }

            int bitfieldSize = Utility.calculateBitfieldSizeInBytes(pieceSizeInBytes, fileSizeInBytes);
            peers.put(id, new Peer(id, hostname, port, hasFile, bitfieldSize));
        }

        peerInfo.close();
        return;
    }

    public static int getServerListenPort()
    {
        return serverListenPort;
    }
}
