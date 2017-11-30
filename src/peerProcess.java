import java.net.*;
import java.io.*;

class peerProcess
{
    private static CustomLogger logger;

    public static void main(String[] args) throws IOException, ClassNotFoundException
    {
        if (args.length < 1)
        {
            System.out.println("No peer id specified!\nCorrect syntax is: java peerProcess %peer ID%");
            return;
        }

        int myPeerId = Integer.parseInt(args[0]);
        logger = new CustomLogger(myPeerId);

        // load Common.cfg
        try
        {
            Config.initConfig("Common.cfg");
        }
        catch (Exception e)
        {
            System.out.println(e.getClass() + " " + e.getMessage());
            System.out.println("Could not open Common.cfg. Exiting...");
            return;
        }

        // load peerInfo.cfg
        // create peer objects and open a socket for each one
        try
        {
            Config.initPeers("peerInfo.cfg", myPeerId);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Could not open peerInfo.cfg. Exiting...");
            return;
        }

        // open a socket for accepting requests
        ServerSocket listener = new ServerSocket(Config.getServerListenPort());
        System.out.println("Listening on port " + listener.getLocalPort());

        // set up sockets for all peers and send handshakes to peers with lower peerIDs than us
        for (int peerId : Config.peers.keySet())
        {
            Thread messageReceiver;

            // we need to make first contact with peers that have a smaller peerId
            if (peerId < myPeerId)
            {
                Config.peers.get(peerId).OpenSocket();
                logger.TCPMakeConnection(peerId);

                // make first contact by sending a handshake message
                HandshakeMessage handshake = new HandshakeMessage(myPeerId);
                handshake.send(Config.peers.get(peerId).GetSocket());
                messageReceiver = new Thread(new MessageReceiver(myPeerId, peerId, Config.peers.get(peerId).GetSocket()));
            }

            // we wait for first contact from peers with a bigger peerId
            else
            {
                logger.TCPIsConnected(peerId);
                messageReceiver = new Thread(new MessageReceiver(myPeerId, peerId, listener.accept()));
            }

            // spawn a thread that handles all messages received
            messageReceiver.start();
        }
    }
}