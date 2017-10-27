import java.io.File;
import java.io.RandomAccessFile;

public class CustomFileManager 
{
	
	private RandomAccessFile file;
	
	public CustomFileManager(int peerID) 
	{	
		//create directory to store incoming file
		File dir = new File("peer_" + peerID);
		if (!dir.exists()) 
		{
			dir.mkdirs();
		}
	}
	
	//TODO: Finalize signature of this method. What do we need to take in?
	public Piece readPiece(int pieceIndex)
	{
		return null;
	}
	
	public void writePiece(Piece piece) 
	{
		//determine offset and other relevant information to write piece into this.file
		
	}

}
