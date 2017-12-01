import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Random;

public class MessageReceiver implements Runnable
{
    private int myPeerId;
    private int peerId;

    DataOutputStream output;
    DataInputStream input;

    CustomLogger logger;

    boolean handshakeSent = false;

    boolean hasSentBitfield = false;

    // number of peers (including us) with the complete file
    int finishedPeers = 0;

    MessageReceiver(int myPeerId, int peerId, boolean handshakeSent, CustomLogger logger) throws IOException
    {
        this.myPeerId = myPeerId;
        this.peerId = peerId;

        this.logger = logger;

        this.handshakeSent = handshakeSent;

        for (Peer p : Config.peers.values())
            if (p.HasFile())
                finishedPeers++;

        this.output = Config.peers.get(peerId).getOutputStream();
        this.input = Config.peers.get(peerId).getInputStream();
    }

    public void run()
    {
        boolean socketNotReady = true;
        System.out.print("Waiting for all peers to come online...");
        while (socketNotReady)
        {
            socketNotReady = false;
            for (Peer p : Config.peers.values())
            {
                if (p.getOutputStream() == null)
                    socketNotReady = true;
            }
        }
        System.out.println("Done");

        // handle different types of messages
        while (true)
        {
            try
            {
                // check if all peers have the complete file
                if (finishedPeers == Config.peers.size())
                {
                    System.out.println("Exiting! We're done.");
                    System.exit(0);
                }

                System.out.println("Finished peers: " + finishedPeers);

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
                            HandleChokeMsg();
                            break;
                        case Message.UNCHOKE:
                            System.out.printf("%d received UNCHOKE from %d\n", myPeerId, peerId);
                            HandleUnchokeMsg();
                            break;
                        case Message.INTERESTED:
                            System.out.printf("%d received INTERESTED from %d\n", myPeerId, peerId);
                            Config.peers.get(peerId).setInterested(true);
                            break;
                        case Message.NOT_INTERESTED:
                            System.out.printf("%d received NOT_INTERESTED from %d\n", myPeerId, peerId);
                            Config.peers.get(peerId).setInterested(false);
                            break;
                        case Message.HAVE:
                            System.out.printf("%d received HAVE from %d\n", myPeerId, peerId);
                            HandleHaveMsg(payload);
                            break;
                        case Message.BITFIELD:
                            System.out.printf("%d received BITFIELD from %d\n", myPeerId, peerId);
                            HandleBitFieldMsg(payload);
                            break;
                        case Message.REQUEST:
                            System.out.printf("%d received REQUEST from %d\n", myPeerId, peerId);
                            HandleRequestMsg(payload);
                            break;
                        case Message.PIECE:
                            System.out.printf("%d received PIECE from %d\n", myPeerId, peerId);
                            HandlePieceMsg(payload);
                            break;
                        default:
                            System.out.println("*ERROR*: Received unknown message type: " + msgType[0]);
                    }
                }
            }

            catch (Exception e) {
                e.printStackTrace();
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
            if (!handshakeSent)
            {
                handshakeSent = true;

                System.out.println("Sending response handshake");
                HandshakeMessage message = new HandshakeMessage(myPeerId);
                message.send(output);
            }
            else
            {
                //System.out.println("1002 bitfield: " + Config.peers.get(1002).PrintBitset());
                /*if (Config.peers.get(myPeerId).getBitField().cardinality() > 0)
                {*/
                    System.out.println("Sending bitfield " + Config.peers.get(myPeerId).PrintBitset() + "(" + Config.peers.get(myPeerId).getBitField().toByteArray().length + ") to " + peerId);
                    Utility.sendMessage(output, Message.BITFIELD, Config.peers.get(myPeerId).getBitField().toByteArray());
                    hasSentBitfield = true;
                //}
                /*else
                {
                    System.out.println("Didn't send bitfield to " + peerId + ". (Bitfield is all 0s)");
                }*/
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
        if (!hasSentBitfield/* && Config.peers.get(myPeerId).getBitField().cardinality() > 0*/)
        {
            System.out.println("Sending bitfield " + Config.peers.get(myPeerId).PrintBitset() + "(" + Config.peers.get(myPeerId).getBitField().toByteArray().length + ") to " + peerId);
            Utility.sendMessage(output, Message.BITFIELD, Config.peers.get(myPeerId).getBitField().toByteArray());
            hasSentBitfield = true;
        }

        if (payload.length > 0)
            Config.peers.get(peerId).setBitfield(payload);

        if (Utility.shouldBeInterested(Config.peers.get(myPeerId).getBitField(), Config.peers.get(peerId).getBitField()))
        {
            System.out.println("Sending interested msg from " + myPeerId + " to " + peerId);
            Utility.sendMessage(output, Message.INTERESTED);
        }
        else
        {
            System.out.println("Sending NOT interested msg from " + myPeerId + " to " + peerId);
            Utility.sendMessage(output, Message.NOT_INTERESTED);
        }

        /*if (Config.peers.get(peerId).HasFile())
            finishedPeers++;*/
    }

    public synchronized void HandleUnchokeMsg(/*no payload*/) throws IOException
    {
        Config.peers.get(myPeerId).setChoked(false);

        requestNewPiece();
    }

    public synchronized void HandleChokeMsg(/*no payload*/) throws IOException
    {
        Config.peers.get(myPeerId).setChoked(true);
    }

    public synchronized void HandleRequestMsg(byte[] payload) throws IOException
    {
        System.out.println("HandleReq: payload length: " + payload.length);
        int requestedPieceIndex = Utility.byteArrayToInt(payload);
        byte[] pieceInBytes = Utility.getBytesForPiece(requestedPieceIndex, myPeerId);
        Utility.sendMessage(output, Message.PIECE, requestedPieceIndex, pieceInBytes);
    }

    public synchronized void HandlePieceMsg(byte[] payload) throws IOException
    {
        int pieceIndex = Utility.byteArrayToInt(Arrays.copyOfRange(payload, 0, 4));
        byte[] piece = Arrays.copyOfRange(payload, 4, payload.length);
        Utility.writePieceToFile(pieceIndex, piece, myPeerId);

        Config.peers.get(myPeerId).setBitInBitField(pieceIndex, true);

        // after writing piece to file, send HAVE msg to everyone else
        for (int peerId : Config.peers.keySet())
        {
            if (peerId != myPeerId)
                Utility.sendMessage(Config.peers.get(peerId).getOutputStream(), Message.HAVE, pieceIndex);
        }

        requestNewPiece();
    }

    public synchronized void HandleHaveMsg(byte[] payload) throws IOException
    {
        int pieceIndex = Utility.byteArrayToInt(payload);
        Config.peers.get(peerId).setBitInBitField(pieceIndex, true);
    }

    public synchronized void requestNewPiece() throws IOException
    {
        ArrayList<Integer> possiblePieces = Utility.getRequestPieces(Config.peers.get(myPeerId).getBitField(), Config.peers.get(peerId).getBitField());
        if (possiblePieces.size() == 0)
        {
            System.out.println("NO MORE PIECES LEFT TO REQUEST");
            return;
        }
        Random generator = new Random();
        int size = possiblePieces.size();

        int requestedPiece = possiblePieces.get(generator.nextInt(size));
        System.out.println("Sending request to " + peerId + " for piece #" + requestedPiece);
        Utility.sendMessage(output, Message.REQUEST, requestedPiece);
    }
}
