import java.util.ArrayList;
import java.util.Random;

public class refreshOptimisticNeighbor implements Runnable
{
	public void run()
    {
        System.out.println("refreshOptimisticNeighbor");
                
        ArrayList<Integer> chokedAndInterestedPeerIds= new ArrayList<Integer>();
        for(int i = 0; i < Config.peers.size(); i++){
            int id = (int)Config.peers.keySet().toArray()[i];
            
            if(Config.peers.get(id).choked &&
            		Config.peers.get(id).interested){
            	chokedAndInterestedPeerIds.add(Config.peers.get(id).GetId());
        	}
        }
        
        if(chokedAndInterestedPeerIds.size() > 0){
        	Random generator = new Random();
            int size = chokedAndInterestedPeerIds.size();
            int index = generator.nextInt(size);
            int optimisticNeighbor = (int)(chokedAndInterestedPeerIds.toArray()[index]);
            Config.setOptimisticNeighbor(optimisticNeighbor);
            System.out.println("PeerID " + optimisticNeighbor + " is the optimistic neighbor Id");

        }
        
    }
}
