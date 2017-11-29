import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;

public class MessageReceiver implements Runnable
{
    private int myPeerId;
    private int peerId;

    DataOutputStream output;
    ServerSocket listener;

    MessageReceiver(int myPeerId, int peerId, Socket socket, ServerSocket listener) throws IOException
    {
        this.myPeerId = myPeerId;
        this.peerId = peerId;

        this.listener = listener;
        output = new DataOutputStream(socket.getOutputStream());
    }

    public void run()
    {
        // handle different types of messages
        while (true)
        {
            try
            {
                DataInputStream input = new DataInputStream(listener.accept().getInputStream());

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
