import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class Message {

	public final static int CHOKE = 0;
	public final static int UNCHOKE = 1;
	public final static int INTERESTED = 2;
	public final static int NOT_INTERESTED = 3;
	public final static int HAVE = 4;
	public final static int BITFIELD = 5;
	public final static int REQUEST = 6;
	public final static int PIECE = 7;
	
	private int length;
	private byte[] payload;
	private int type;
	
	public Message() {
		length = 0;
		type = 0;
		payload = null;
	}
	
	public Message(int type, int length, byte[] payload) {
		this.type = type;
		this.length = length;
		this.payload = payload;
	}
	
	//reads an incoming packet from Socket and sets length, type, and payload for this object
	public void read(Socket sock) throws IOException {
		//TODO: See how this is used with the main listening code we have in PeerProcess(while true loop)
		DataInputStream in = new DataInputStream(sock.getInputStream());
		byte[] packetBytes = Utility.inputStreamToByteArray(in);
		length = Utility.byteArrayToInt(Arrays.copyOfRange(packetBytes, 0, 4));
		type = Utility.byteArrayToInt(Arrays.copyOfRange(packetBytes, 4, 5));
		
		if(packetBytes.length > 5) {
			payload = Arrays.copyOfRange(packetBytes, 5, packetBytes.length);
		} else {
			payload = null;
		}
	}
	
	public void send(Socket sock) throws IOException {
		if(payload == null) {
			length = 1;
		} else {
			length = payload.length + 1;
		}
		
		DataOutputStream out = new DataOutputStream(sock.getOutputStream());
		out.writeInt(length);
		out.writeByte(type);
		
		if(payload != null) {
			out.write(payload);
		}
		out.flush();
		out.close();
	}
	
	public int getType() {
		return type;
	}
	
	public int getLength() {
		return length;
	}
	
	public byte[] getPayload() {
		return payload;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public void setLength(int length) {
		this.length = length;
	}
}
