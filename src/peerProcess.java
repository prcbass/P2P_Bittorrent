import java.net.*;
import java.io.*;
import java.util.*;

class peerProcess
{
    private static Config config;

    // this is a hack caused by the fact that handshake MSGs only contain 1 peerID - the peer we want to connect to.
    private static ArrayList<String> handshakeHosts = new ArrayList<String>();

    public static void main(String[] args) throws IOException, ClassNotFoundException
    {
        if (args.length < 1)
        {
            System.out.println("No peer id specified!\nCorrect syntax is: java peerProcess %peer ID%");
            return;
        }

        int myPeerId = Integer.parseInt(args[0]);

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

        //
        ServerSocket listener = new ServerSocket(config.getServerListenPort());
        System.out.println("Listening on port " + listener.getLocalPort());

        // send a handshake message to all peers listed before us in peerInfo.cfg
        for (int peerId : config.peers.keySet())
        {
            if (peerId == myPeerId)
            {
                System.out.println("breaking cause peer" + peerId + " is us");
                break;
            }

            config.peers.get(peerId).OpenSocket();
            HandshakeMessage handshake = new HandshakeMessage(peerId);
            handshake.send(config.peers.get(peerId).GetSocket());

            // keep track of who we have already sent a handshake message to
            //config.peers.get(peerId).SetSentHandshake(true);
            handshakeHosts.add(config.peers.get(peerId).GetHostname());
        }

        // wait for responses and react accordingly
        while (true)
        {
            Socket acceptedSocket = listener.accept();
            // contains Data field of the most recently received TCP packet as a stream
            InputStream response = new DataInputStream(acceptedSocket.getInputStream());
            // convert to byte array
            byte[] packetBytes = Utility.inputStreamToByteArray(response);

            // see if response is a message or a handshake
            System.out.println(new String(Arrays.copyOfRange(packetBytes, 0, 18)));
            byte[] packetHeader = Arrays.copyOfRange(packetBytes, 0, 18);
            if (packetBytes.length == 32 && new String(packetHeader).equals(HandshakeMessage.header))
            {
                System.out.println("Got handshake");
                int peerId = Utility.byteArrayToInt(Arrays.copyOfRange(packetBytes, 28, 32));

                // this is a handshake meant for us
                if (peerId == myPeerId)
                {
                    // if we sent a handshake already and just received one, send a bitfield message
                    if (handshakeHosts.contains(acceptedSocket.getInetAddress().getHostName()))
                    {
                        System.out.println("We should send a bitfield msg here.");
                    }
                    else
                    {
                        config.peers.get(peerId).OpenSocket();
                        HandshakeMessage handshake = new HandshakeMessage(peerId);
                        handshake.send(config.peers.get(peerId).GetSocket());
                        // keep track of who we have already sent a handshake message to
                        //config.peers.get(peerId).SetSentHandshake(true);
                        handshakeHosts.add(config.peers.get(peerId).GetHostname());

                    }

                }
            }
            else 
            {
            	//this is a non-handshake packet and needs to be handled accordingly

            }
        }
    }
}