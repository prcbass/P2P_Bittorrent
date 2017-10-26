import java.net.*;
import java.io.*;
import java.nio.*;
import java.util.*;

class peerProcess
{

    private static Config config;

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

            HandshakeMessage handshake = new HandshakeMessage(peerId);
            handshake.send(config.peers.get(peerId).GetSocket());
            config.peers.get(peerId).SetSentHandshake(true);
        }

        // wait for responses and react accordingly
        while (true)
        {
            // contains Data field of the most recently received TCP packet
            InputStream response = new DataInputStream(listener.accept().getInputStream());

            // convert inputstream to byte array
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int read = 0;
            while ((read = response.read(buff, 0, buff.length)) != -1)
                byteStream.write(buff, 0, read);
            byteStream.flush();
            byte[] bytes = byteStream.toByteArray();

            // see if response is a message or a handshake
            System.out.println(new String(Arrays.copyOfRange(bytes, 0, 18)));
            if (bytes.length == 32 && 
                new String(Arrays.copyOfRange(bytes, 0, 18)).equals("P2PFILESHARINGPROJ"))
            {
                System.out.println("Got handshake");

                // this is a handshake meant for us
                int peerId = ByteBuffer.wrap(Arrays.copyOfRange(bytes, 28, 32)).getInt();
                if (peerId == myPeerId)
                {
                    // if we sent a handshake already and just received one, send a bitfield message
                    if (config.peers.get(peerId).HasSentHandshake())
                    {
                        System.out.println("We should send a bitfield msg here.");
                    }
                    else
                    {
                        config.peers.get(peerId).OpenSocket();
                        HandshakeMessage handshake = new HandshakeMessage(myPeerId);
                        handshake.send(config.peers.get(peerId).GetSocket());
                        config.peers.get(peerId).SetSentHandshake(true);
                    }

                }
            }
            else {

            }
        }
    }
}