import java.util.LinkedHashMap;
import java.util.*;

public class refreshPreferedNeighbors implements Runnable
{
    private int neighborCount;
    private int myPeerId;
    private CustomLogger logger;

    refreshPreferedNeighbors(int neighborCount, int myPeerId, CustomLogger logger)
    {
        this.neighborCount = neighborCount;
        this.myPeerId = myPeerId;
        this.logger = logger;
    }

    private List<Integer> getNeighborList(boolean hasFile)
    {
        // create linked list of interested peerID/download rate pairs
        LinkedHashMap<Integer, Double> downloadRates = new LinkedHashMap<>();
        for (int peerId : Config.peers.keySet())
        {
            if (Config.peers.get(peerId).isInterested() && peerId != myPeerId)
                downloadRates.put(peerId, (double)Config.peers.get(peerId).getBytesDownloaded()/Config.getUnchokingInterval());
        }

        // we have the complete file, choose neighbors randomly
        if (hasFile)
        {
            List<Integer> peers = new ArrayList<Integer>(downloadRates.keySet());
            Collections.shuffle(peers);
            //System.out.println("Creating neighborlist for " + myPeerId + " RANDOMLY");
            //System.out.println(peers);
            while (peers.size() > neighborCount)
                peers.remove(0);
            return peers;
        }

        // we don't have the complete file, choose neighbors based on highest download rate
        else
        {
            //System.out.println("Creating neighborlist for " + myPeerId + " BY DOWNLOAD RATE");
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

            //System.out.println(sortedMap.keySet());
            return new ArrayList<Integer>(sortedMap.keySet());
        }
    }

    /*
    * TODO: if we have the complete file, we should choose peers randomly instead of by download rate
    * */
    public void run()
    {
        List<Integer> neighbors = getNeighborList(Config.peers.get(myPeerId).HasFile());
        int unchokeCount = 0;
        for (int peerId : neighbors)
        {
            // send unchoke messages for the first N peers in the list
            if (unchokeCount < neighborCount)
            {
                if (Config.peers.get(peerId).isChoked())
                {
                    // send unchoke
                    try {
                        Utility.sendMessage(Config.peers.get(peerId).getOutputStream(), Message.UNCHOKE);
                        //System.out.println("Sending UNCHOKE to " + peerId);
                        Config.peers.get(peerId).setChoked(false);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else
                {
                    //System.out.println("Not unchoking " + peerId + " (already unchoked)");
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
                        //System.out.println("Sending CHOKE to " + peerId);
                        Config.peers.get(peerId).setChoked(true);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    //System.out.println("Not choking optimistic neighbor " + peerId);
                }
            }

            // we're starting a new unchoke interval so reset all the bytes downloaded
            Config.peers.get(peerId).resetBytesDownloaded();
        }

        logger.changeOfPreferredNeighbors(neighbors);
    }
}

