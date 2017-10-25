import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

class peerProcess
{

    private static Config config;
    private static ArrayList<Peer> peers;

    static int getPeerIndexFromId(int peerId)
    {
        for (int i = 0; i < peers.size(); i++)
            if (peers.get(i).GetId() == peerId)
                return i;
        return -1;
    }
    public static void main(String[] args) throws IOException, ClassNotFoundException
    {
        if (args.length < 1)
        {
            System.out.println("No peer id specified!\nCorrect syntax is: java peerProcess %peer ID%");
            return;
        }

        int peerId = Integer.parseInt(args[0]);

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

        // create peer objects and open a socket for each one
        try
        {
            peers = config.initPeers("peerInfo.cfg", peerId);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Could not open peerInfo.cfg. Exiting...");
            return;
        }

        ServerSocket listener = new ServerSocket(2000);
        System.out.println("Listening on port " + listener.getLocalPort());

        // send a handshake message to all peers before us
        for (int i = 0; i < peers.size(); i++)
        {
            if (peers.get(i).GetId() == peerId)
            {
                System.out.println("breaking cause peer" + peerId + " is us");
                break;
            }

            HandshakeMessage handshake = new HandshakeMessage(peers.get(i).GetId());
            handshake.send(peers.get(i).GetSocket());
            peers.get(i).SetSentHandshake(true);
        }

        // wait for responses and react accordingly
        while (true)
        {
            InputStream response = new DataInputStream(listener.accept().getInputStream());
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
                if (ByteBuffer.wrap(Arrays.copyOfRange(bytes, 28, 32)).getInt() == peerId)
                {
                    int peerIndex = getPeerIndexFromId(peerId);
                    // if we sent a handshake already and just received one, send a bitfield message
                    if (peers.get(peerIndex).HasSentHandshake())
                    {
                        System.out.println("We should senda bitfield msg here.");
                    }
                    else
                    {
                        peers.get(peerIndex).OpenSocket();
                        HandshakeMessage handshake = new HandshakeMessage(peerId);
                        handshake.send(peers.get(peerIndex).GetSocket());
                        peers.get(peerIndex).SetSentHandshake(true);
                    }

                }
            }
            else {

            }
            /*byte[] bytes = new byte[32];
            in.read(bytes, 0, 32);
            byte[] text = Arrays.copyOfRange(bytes, 0, 27);
            System.out.print(new String(text) + " ");
            byte[] id = Arrays.copyOfRange(bytes, 28, 32);
            //System.out.println(byteArrayToInt(id));
            System.out.println(ByteBuffer.wrap(id).getInt());*/
        }
    }
}