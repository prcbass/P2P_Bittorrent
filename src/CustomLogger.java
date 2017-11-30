import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.*;


public class CustomLogger
{
    private final int id;
    private static FileHandler fileHandler;
	private static Logger logger;
    
	CustomLogger(int id) throws IOException, SecurityException
	{
		this.id = id;
		logger = Logger.getLogger("peer" + id);
		File dir = new File("peer_" + id);
		if(!dir.isDirectory())
		{
		    dir.mkdir();
		}
		fileHandler = new FileHandler(dir.getPath() + "/log_peer_" + id + ".log");
		fileHandler.setFormatter(new SimpleFormatter()
		{
		    private static final String format = "%1$tF %1$tT %3$s %n";
		
		    @Override
		    public synchronized String format(LogRecord lr)
		    {
		        return String.format(format,
		                new Date(lr.getMillis()),
		                lr.getLevel().getLocalizedName(),
		                lr.getMessage()
		        );
		    }
		});
		logger.addHandler(fileHandler);        
	}
	
	public void TCPMakeConnection(int id2)
	{
		logger.info("Peer " + id + " makes a connection to Peer " + id2 + "\n");
	}

	public void TCPIsConnected(int id2)
	{
		logger.info("Peer " + id + " is connected from Peer " + id2 + "\n");
	}
	
	public void changeOfPreferredNeighbors(ArrayList<Peer> neighbors)
	{
		String s = "Peer " + id + " has the preferred neighbors ";
		
		for (int i = 0; i < neighbors.size(); i++)
		{
			if (i != neighbors.size() - 1)
			{
				s += neighbors.get(i).GetId() + ", ";
			}
			else
			{
				s += neighbors.get(i).GetId()  + "\n";
			}
		}
		logger.info(s.toString());
	}
	
	public void changeOfOptimisticallyUnchokedNeighbor(int id2)
	{
		logger.info("Peer " + id + " has the optimistically unchoked neighbor " + id2 + "\n");
	}
	
	public void unchoking(int id2)
	{
		logger.info("Peer " + id + " is unchoked by " + id2 + "\n");
	}
	
	public void choking(int id2)
	{
		logger.info("Peer " + id + " is unchoked by " + id2 + "\n");
	}
	
	public void recievingHave(int id2, int pieceIndex)
	{
		logger.info("Peer " + id + " received the 'have' message from " + id2 + " for the piece " + pieceIndex + "\n");
	}
	
	public void recievingInterested(int id2)
	{
		logger.info("Peer " + id + " received the 'interested' message from " + id2 + "\n");
	}
	
	public void recievingNotInterested(int id2)
	{
		logger.info("Peer " + id + " received the 'not interested' message from " + id2 + "\n");
	}
	
	public void downloadingPiece(int id2, int pieceIndex, int numPieces)
	{
		logger.info("Peer " + id + " has downloaded the piece " + pieceIndex + " from " + id2 + ". Now the number of pieces it has is " + numPieces + "\n");
	}
	
	public void completionDownload()
	{
		logger.info("Peer " + id + " has downloaded the complete file\n");
	}
}
