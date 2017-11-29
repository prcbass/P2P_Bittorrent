import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;

public class MessageReceiver implements Runnable
{
    private int myPeerId;
    private int peerId;

    DataOutputStream output;
    DataInputStream input;

    MessageReceiver(int myPeerId, int peerId, Socket socket) throws IOException
    {
        this.myPeerId = myPeerId;
        this.peerId = peerId;

        output = new DataOutputStream(socket.getOutputStream());
        input = new DataInputStream(socket.getInputStream());
    }

    public void run()
    {
        // handle different types of messages
        while (true)
        {
            try
            {
                byte[] lenBytes = new byte[4];
                input.readFully(lenBytes, 0, 4);
                int msgLength = Utility.byteArrayToInt(lenBytes);
                System.out.println("Got message of length: " + msgLength);
            }

            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
