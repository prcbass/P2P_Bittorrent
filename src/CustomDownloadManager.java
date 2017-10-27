import java.net.Socket;

public class CustomDownloadManager 
{
	
	private CustomLogger logger;
	private CustomFileManager fileManager;
	private Peer peer;
	private Bitfield bitfield;
	private Socket socket; 
	
	public CustomDownloadManager(Peer peer, Bitfield bitfield, CustomFileManager fileManager, CustomLogger logger) 
	{
		this.peer = peer;
		this.bitfield = bitfield;
		this.fileManager = fileManager;
		this.logger = logger;
		this.socket = peer.GetSocket();
	}
	
	public void download()
	{
		//use message type and write pieces as descibed by bitfield
	}

}
