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
    private final int fileSizeInBytes;
    private final int pieceSizeInBytes; 

    LinkedHashMap<Integer, Peer> peers;
    private int serverListenPort;


    Config(String commonFile) throws FileNotFoundException
    {
        Scanner cfg = new Scanner(new FileReader(commonFile));
        this.numberOfPreferredNeighbors = Integer.parseInt(cfg.nextLine().split(" ")[1].trim());
        this.unchokingInterval = Integer.parseInt(cfg.nextLine().split(" ")[1].trim());
        this.optimisticUnchokingInterval = Integer.parseInt(cfg.nextLine().split(" ")[1].trim());
        this.fileName = cfg.nextLine().split(" ")[1].trim();
        this.fileSizeInBytes = Integer.parseInt(cfg.nextLine().split(" ")[1].trim());
        this.pieceSizeInBytes = Integer.parseInt(cfg.nextLine().split(" ")[1].trim());
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
            {
                serverListenPort = port;
            }

            // create new peer object based on line from peerInfo.cfg
            Peer p = new Peer(id, hostname, port, hasFile);

            // important to ceil this to ensure that any partial pieces are still included
            int numPieces = (int)Math.ceil(this.fileSizeInBytes / this.pieceSizeInBytes);

            // initialize peer bitfield (all 1s if hasFile, all 0s otherwise)
            p.initBitField(numPieces, hasFile);

            // now that the peer is initialized, add it to the peers hashmap
            peers.put(id, p);
        }

        peerInfo.close();
        return;
    }

    public int getServerListenPort()
    {
        return this.serverListenPort;
    }
}
