import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.BitSet;

public class MessageReceiver implements Runnable
{
    private int myPeerId;
    private int peerId;

    DataOutputStream output;
    DataInputStream input;

    CustomLogger logger;

    boolean handshakeReceived;

    boolean hasSentBitfield = false;

    MessageReceiver(int myPeerId, int peerId, Socket socket, boolean handshakeReceived, CustomLogger logger) throws IOException
    {
        this.myPeerId = myPeerId;
        this.peerId = peerId;

        input = new DataInputStream(socket.getInputStream());

        output = new DataOutputStream(socket.getOutputStream());
        output.flush();

        this.logger = logger;

        this.handshakeReceived = handshakeReceived;
    }

    public void run()
    {
        // handle different types of messages
        while (true)
        {
            try
            {
                if (input.available() == 0)
                {
                    //System.out.println("Nothing to read");
                    continue;
                }

                byte[] lenBytes = new byte[4];
                input.readFully(lenBytes, 0, 4);

                byte[] msgType = new byte[1];
                input.readFully(msgType, 0, 1);

                // 5th byte in P2PFILESHARINGPROJ is 'I'
                // if the type is 'I' the message is probably a handshake
                if (msgType[0] == 'I')
                {
                    handleHandshakeMsg(lenBytes, msgType);
                }
                else
                {
                    int msgLength = Utility.byteArrayToInt(lenBytes);
                    byte[] payload = new byte[msgLength - 1];
                    input.readFully(payload, 0, payload.length);

                    switch (msgType[0])
                    {
                        case Message.CHOKE:
                            System.out.printf("%d received CHOKE from %d\n", myPeerId, peerId);
                            break;
                        case Message.UNCHOKE:
                            System.out.printf("%d received UNCHOKE from %d\n", myPeerId, peerId);
                            break;
                        case Message.INTERESTED:
                            System.out.printf("%d received INTERESTED from %d\n", myPeerId, peerId);
                            break;
                        case Message.NOT_INTERESTED:
                            System.out.printf("%d received NOT_INTERESTED from %d\n", myPeerId, peerId);
                            break;
                        case Message.HAVE:
                            System.out.printf("%d received HAVE from %d\n", myPeerId, peerId);
                            break;
                        case Message.BITFIELD:
                            System.out.printf("%d received BITFIELD from %d\n", myPeerId, peerId);
                            HandleBitFieldMsg(payload);
                            break;
                        case Message.REQUEST:
                            System.out.printf("%d received REQUEST from %d\n", myPeerId, peerId);
                            break;
                        case Message.PIECE:
                            System.out.printf("%d received PIECE from %d\n", myPeerId, peerId);
                            break;
                        default:
                            System.out.println("*ERROR*: Received unknown message type: " + msgType[0]);
                    }
                }
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
                if (Config.peers.get(myPeerId).getBitField().cardinality() > 0)
                {
                    System.out.println("Sending bitfield " + Config.peers.get(myPeerId).PrintBitset() + "(" + Config.peers.get(myPeerId).getBitField().toByteArray().length + ") to " + peerId);
                    sendMessage(Message.BITFIELD, Config.peers.get(myPeerId).getBitField().toByteArray());
                    hasSentBitfield = true;
                }
                else
                {
                    System.out.println("Didn't send bitfield to " + peerId + ". (Bitfield is all 0s)");
                }
            }
        }
        else
        {
            System.out.println("Invalid");
        }
    }

    public synchronized void HandleBitFieldMsg(byte[] payload) throws IOException
    {
        // reply to a bitfield msg with our own bitfield if we haven't sent it before
        if (!hasSentBitfield)
        {
            System.out.println("Sending bitfield " + Config.peers.get(myPeerId).PrintBitset() + "(" + Config.peers.get(myPeerId).getBitField().toByteArray().length + ") to " + peerId);
            sendMessage(Message.BITFIELD, Config.peers.get(myPeerId).getBitField().toByteArray());
            hasSentBitfield = true;
        }

        Config.peers.get(peerId).setBitfield(BitSet.valueOf(payload));

        if (Utility.shouldBeInterested(Config.peers.get(myPeerId).getBitField(), Config.peers.get(peerId).getBitField()))
        {
            System.out.println("Sending interested msg from " + myPeerId + " to " + peerId);
            sendMessage(Message.INTERESTED);
        }
        else
        {
            System.out.println("Sending NOT interested msg from " + myPeerId + " to " + peerId);
            sendMessage(Message.NOT_INTERESTED);
        }
    }

    public synchronized void sendMessage(int type, byte[] payload) throws IOException
    {
        output.writeInt(payload.length + 1);
        output.writeByte(type);
        output.write(payload);
        output.flush();
    }

    public synchronized void sendMessage(int type) throws IOException
    {
        output.writeInt(1);
        output.writeByte(type);
        output.flush();
    }
}
