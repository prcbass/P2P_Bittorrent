import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

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
		System.out.println("Created handshake message of size " + out.size());
		out.flush();
		out.close();
	}
	
	public int getID() 
	{
		return ID;
	}
}
