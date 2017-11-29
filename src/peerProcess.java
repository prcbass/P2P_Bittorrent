import java.net.*;
import java.io.*;
import java.util.*;

class peerProcess
{
    private static Config config;
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
            config = new Config("Common.cfg");
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
            config.initPeers("peerInfo.cfg", myPeerId);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Could not open peerInfo.cfg. Exiting...");
            return;
        }

        // open a socket for accepting requests
        ServerSocket listener = new ServerSocket(config.getServerListenPort());
        System.out.println("Listening on port " + listener.getLocalPort());

        // set up sockets for all peers and send handshakes to peers with lower peerIDs than us
        for (int peerId : config.peers.keySet())
        {
            config.peers.get(peerId).OpenSocket();

            // we need to make first contact with peers that have a smaller peerId
            if (peerId < myPeerId)
            {
                logger.TCPMakeConnection(peerId);

                // make first contact by sending a handshake message
                HandshakeMessage handshake = new HandshakeMessage(peerId);
                handshake.send(config.peers.get(peerId).GetSocket());
            }

            // we wait for first contact from peers with a bigger peerId
            else
            {
                logger.TCPIsConnected(peerId);
            }

            // spawn a thread that handles all messages received
            Thread messageReceiver = new Thread(new MessageReceiver(myPeerId, peerId, config.peers.get(peerId).GetSocket(), listener));
            messageReceiver.start();

        }


    }
}