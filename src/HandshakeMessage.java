import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class HandshakeMessage 
{
	private final static String header = "P2PFILESHARINGPROJ";
	private int ID;
	
	public HandshakeMessage(int ID) 
	{
		this.ID = ID;
	}
	
	public void read() 
	{
		
	}
	
	// create and send a handshake message
	public void send(Socket sock) throws IOException
	{
		DataOutputStream data = new DataOutputStream(sock.getOutputStream());
		data.writeBytes(header);
		data.writeLong(0);
		data.writeShort(0);
		data.writeInt(ID);
		System.out.println("Created handshake message of size " + data.size());
		data.flush();
		data.close();
	}
	
	public int getID() 
	{
		return ID;
	}
}
