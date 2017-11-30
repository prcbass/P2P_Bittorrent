import java.net.*;
import java.io.*;

public class HandshakeMessage 
{
	public final static String header = "P2PFILESHARINGPROJ";
	public final static byte[] headerBytes = header.getBytes();

	public final static byte[] zeroBytes = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
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
	}
	
	public void send(OutputStream output) throws IOException {
		DataOutputStream out = new DataOutputStream(output);
		out.writeBytes(header);
		out.writeLong(0);
		out.writeShort(0);
		out.writeInt(ID);
		out.flush();
	}
	
	public int getID() 
	{
		return ID;
	}
	
	public void setID(int ID) {
		this.ID = ID;
	}
}
