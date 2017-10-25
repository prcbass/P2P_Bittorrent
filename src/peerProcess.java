import java.util.*;

class peerProcess
{
    public static void main(String[] args)
    {
        if (args.length < 1)
        {
            System.out.println("No peer id specified!\nCorrect syntax is: java peerProcess %peer ID%");
            return;
        }

        int peerId = Integer.parseInt(args[0]);

        // load Common.cfg
        Config config;
        ArrayList<Peer> peers;
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

        for (Peer p : peers)
        {
            System.out.println(p);
        }

        // send out initial requests

        // wait for responses and react accordingly
        while (true)
        {

        }
    }
}