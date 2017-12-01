import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.*;

public class refreshPreferedNeighbors implements Runnable
{
    private int neighborCount;

    refreshPreferedNeighbors(int neighborCount) throws IOException
    {
        this.neighborCount = neighborCount;

        // create linked list of interested peerID/download rate pairs
        LinkedHashMap<Integer, Double> downloadRates = new LinkedHashMap<>();
        for (int peerId : Config.peers.keySet())
        {
            if (Config.peers.get(peerId).isInterested())
                downloadRates.put(peerId, Config.peers.get(peerId).getDownloadRateBytesPerMilisec());
        }

        // sort the linked list highest to low - https://stackoverflow.com/questions/12184378/sorting-linkedhashmap
        List<Map.Entry<Integer, Double>> entries = new ArrayList<Map.Entry<Integer, Double>>(downloadRates.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<Integer, Double>>() {
            public int compare(Map.Entry<Integer, Double> a, Map.Entry<Integer, Double> b){
                return -1 * a.getValue().compareTo(b.getValue());
            }
        });
        Map<Integer, Double> sortedMap = new LinkedHashMap<Integer, Double>();
        for (Map.Entry<Integer, Double> entry : entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        int unchokeCount = 0;
        for (int peerId : sortedMap.keySet())
        {
            // send unchoke messages for the first N peers in the list
            if (unchokeCount < neighborCount)
            {
                if (Config.peers.get(peerId).isChoked())
                {
                    // send unchoke
                    Utility.sendMessage(Config.peers.get(peerId).getOutputStream(), Message.UNCHOKE);
                    System.out.println("Sending UNCHOKE to " + peerId);
                }
                else
                {
                    System.out.println("Not unchoking " + peerId + " (already choked)");
                }
            }
            else
            {
                if (peerId != Config.getOptimisticNeighbor())
                {
                    // send choke
                    Utility.sendMessage(Config.peers.get(peerId).getOutputStream(), Message.CHOKE);
                    System.out.println("Sending CHOKE to " + peerId);
                }
                else
                {
                    System.out.println("Not choking optimistic neighbor " + peerId);
                }
            }
        }

    }

    public void run()
    {
        System.out.println("refreshPreferedNeighbors");

    }
}

