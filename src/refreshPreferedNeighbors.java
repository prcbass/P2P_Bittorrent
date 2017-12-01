import java.util.LinkedHashMap;
import java.util.*;

public class refreshPreferedNeighbors implements Runnable
{
    private int neighborCount;
    private int myPeerId;
    private Map<Integer, Double> sortedMap;

    refreshPreferedNeighbors(int neighborCount, int myPeerId)
    {
        this.neighborCount = neighborCount;
        this.myPeerId = myPeerId;
    }

    /*
    * TODO: if we have the complete file, we should choose peers randomly instead of by download rate
    * */
    public void run()
    {
        System.out.println("refreshPreferedNeighbors: ");

        // create linked list of interested peerID/download rate pairs
        LinkedHashMap<Integer, Double> downloadRates = new LinkedHashMap<>();
        for (int peerId : Config.peers.keySet())
        {
            if (peerId != myPeerId)
                System.out.println(Config.peers.get(peerId).getOutputStream().size());

            if (Config.peers.get(peerId).isInterested() && peerId != myPeerId)
                downloadRates.put(peerId, Config.peers.get(peerId).getDownloadRateBytesPerMilisec());
        }
        System.out.print(downloadRates.size());

        // sort the linked list highest to low - https://stackoverflow.com/questions/12184378/sorting-linkedhashmap
        List<Map.Entry<Integer, Double>> entries = new ArrayList<Map.Entry<Integer, Double>>(downloadRates.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<Integer, Double>>() {
            public int compare(Map.Entry<Integer, Double> a, Map.Entry<Integer, Double> b){
                return -1 * a.getValue().compareTo(b.getValue());
            }
        });
        System.out.print(entries.size());
        sortedMap = new LinkedHashMap<Integer, Double>();
        for (Map.Entry<Integer, Double> entry : entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        System.out.println(sortedMap.size());
        int unchokeCount = 0;
        for (int peerId : sortedMap.keySet())
        {
            // send unchoke messages for the first N peers in the list
            if (unchokeCount < neighborCount)
            {
                if (Config.peers.get(peerId).isChoked())
                {
                    // send unchoke
                    try {
                        Utility.sendMessage(Config.peers.get(peerId).getOutputStream(), Message.UNCHOKE);
                        System.out.println("Sending UNCHOKE to " + peerId);
                        Config.peers.get(peerId).setChoked(false);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else
                {
                    System.out.println("Not unchoking " + peerId + " (already choked)");
                }
                unchokeCount++;
            }
            else
            {
                if (peerId != Config.getOptimisticNeighbor())
                {
                    // send choke
                    try {
                        Utility.sendMessage(Config.peers.get(peerId).getOutputStream(), Message.CHOKE);
                        System.out.println("Sending CHOKE to " + peerId);
                        Config.peers.get(peerId).setChoked(true);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    System.out.println("Not choking optimistic neighbor " + peerId);
                }
            }
        }
    }
}

