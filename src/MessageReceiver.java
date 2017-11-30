import javax.rmi.CORBA.Util;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class MessageReceiver implements Runnable
{
    private int myPeerId;
    private int peerId;

    DataOutputStream output;
    DataInputStream input;

    CustomLogger logger;

    boolean handshakeReceived = false;

    MessageReceiver(int myPeerId, int peerId, Socket socket) throws IOException
    {
        this.myPeerId = myPeerId;
        this.peerId = peerId;

        input = new DataInputStream(socket.getInputStream());

        output = new DataOutputStream(socket.getOutputStream());
        output.flush();

        logger = new CustomLogger(myPeerId);
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

                byte[] msgType = new byte[1];
                input.readFully(msgType, 0, 1);

                if (msgType[0] == 'I')
                {
                    handleHandshakeMsg(lenBytes, msgType);
                }

                int msgLength = Utility.byteArrayToInt(lenBytes);
                System.out.println("Got message of length: " + msgLength + " from" + " PeerID: " + peerId);
            }

            catch (Exception e) {
                break;
            }
        }
    }

    public synchronized void handleHandshakeMsg(byte[] msgLen, byte[] msgType) throws IOException
    {
        System.out.printf("%d received handshake from %d...", myPeerId, peerId);

        // Handshake messages are always 32 bytes
        byte[] otherBytes = new byte[27];
        input.readFully(otherBytes, 0, 27);

        byte[] fullMsg = Utility.combine(msgLen, msgType);
        fullMsg = Utility.combine(fullMsg, otherBytes);

        String header = new String(Arrays.copyOfRange(fullMsg, 0, 18));

        int id = Utility.byteArrayToInt(Arrays.copyOfRange(fullMsg, 28, 32));

        if ((header.equals(HandshakeMessage.header) && id == peerId))
        {
            System.out.println("Valid");
            if (!handshakeReceived)
            {
                handshakeReceived = true;
                
                HandshakeMessage message = new HandshakeMessage(myPeerId);
                message.send(output);
            }
            else
            {
                // send bitfield
                System.out.printf("%d should send bitfield message to %d\n", myPeerId, peerId);
            }
        }
        else
        {
            System.out.println("Invalid");
        }
    }
}
