import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;

public class Config
{
    private final int numberOfPreferredNeighbors;
    private final int unchokingInterval;
    private final int optimisticUnchokingInterval;
    private final String fileName;
    private final int fileSize; // in bytes
    private final int pieceSize; // in bytes

    LinkedHashMap<Integer, Peer> peers;
    private int serverListenPort;


    Config(String commonFile) throws FileNotFoundException
    {
        Scanner cfg = new Scanner(new FileReader(commonFile));
        this.numberOfPreferredNeighbors = Integer.parseInt(cfg.nextLine().split(" ")[1].trim());
        this.unchokingInterval = Integer.parseInt(cfg.nextLine().split(" ")[1].trim());
        this.optimisticUnchokingInterval = Integer.parseInt(cfg.nextLine().split(" ")[1].trim());
        this.fileName = cfg.nextLine().split(" ")[1].trim();
        this.fileSize = Integer.parseInt(cfg.nextLine().split(" ")[1].trim());
        this.pieceSize = Integer.parseInt(cfg.nextLine().split(" ")[1].trim());
        cfg.close();

        peers = new LinkedHashMap<Integer, Peer>();
    }

    void initPeers(String peerInfoFile, int myPeerId) throws FileNotFoundException, UnknownHostException, IOException
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
                serverListenPort = port;

            peers.put(id, new Peer(id, hostname, port, hasFile));
        }

        peerInfo.close();
        return;
    }

    public int getServerListenPort()
    {
        return this.serverListenPort;
    }
}
