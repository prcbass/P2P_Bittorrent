import java.net.*;
import java.io.*;

public class HandshakeMessage 
{
	public final static String header = "P2PFILESHARINGPROJ";
	private int ID;
	
	public HandshakeMessage(int ID) 
	{
		this.ID = ID;
	}
	
	// create and send a handshake message
	public void send(Socket sock) throws IOException
	{
		DataOutputStream out = new DataOutputStream(sock.getOutputStream());
		out.writeBytes(header);
		out.writeLong(0);
		out.writeShort(0);
		out.writeInt(ID);
		out.flush();
		out.close();
	}
	
	public int getID() 
	{
		return ID;
	}
}
