
public class HandshakeMessage {
	private final static String header = "P2PFILESHARINGPROJ";
	private int ID;
	
	public HandshakeMessage(int ID) {
		this.ID = ID;
	}
	
	public void read() {
		
	}
	
	public void send() {
		
	}
	
	public int getID() {
		return ID;
	}
}
