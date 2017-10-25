import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class Config
{
    private final int numberOfPreferredNeighbors;
    private final int unchokingInterval;
    private final int optimisticUnchokingInterval;
    private final String fileName;
    private final int fileSize; // in bytes
    private final int pieceSize; // in bytes


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
    }

    ArrayList<Peer> initPeers(String peerInfoFile, int peerId) throws FileNotFoundException
    {
        ArrayList<Peer> peers = new ArrayList<Peer>();
        Scanner peerInfo = new Scanner(new FileReader(peerInfoFile));
        while (peerInfo.hasNextLine())
        {
            String[] line = peerInfo.nextLine().split(" ");
            int id = Integer.parseInt(line[0]);
            String hostname = line[1];
            int port = Integer.parseInt(line[2]);
            boolean hasFile = line[3].trim().equals("1");

            // don't add our client to the peer list (no need to send anything to ourselves)
            if (id == peerId)
                continue;
            peers.add(new Peer(id, hostname, port, hasFile));
        }
        peerInfo.close();
        return peers;
    }
}
