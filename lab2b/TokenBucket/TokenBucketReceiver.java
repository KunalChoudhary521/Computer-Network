package TokenBucket;


import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;


/**
 * Listens on specified port for incoming packets.
 * Packets are sent out immediately if possible, and stored in buffer if not.
 */
public class TokenBucketReceiver implements Runnable
{
    // TokenBucketSender used to send packets immediately.
    private TokenBucketSender sender;
    // buffer used to store incoming packets
    private Buffer buffer;
    // port on which packets are received
    private int port;
    // Bucket from which tokens are consumed when sending packets
    private Bucket bucket;
    // name of output file
    private String fileName;

    /**
     * Constructor.
     * @param buffer Buffer to which packets are stored.
     * @param port Port on which to lister for packets.
     * @param sender TokenBucketSender used to send packets.
     * @param bucket Bucket from which tokens are consumed when sending packets.
     * @param fileName Name of output file.
     */
    public TokenBucketReceiver(Buffer buffer, int port, TokenBucketSender sender, Bucket bucket, String fileName)
    {
        this.buffer = buffer;
        this.port = port;
        this.sender = sender;
        this.bucket = bucket;
        this.fileName = fileName;
    }

    /**
     * Listen on port and send out or store incoming packets to buffer.
     * This method is invoked when starting a thread for this class.
     */
    public void run()
    {
        int noDropped=0;
        DatagramSocket socket = null;
        PrintStream pOut = null;
        try
        {
            FileOutputStream fOut =  new FileOutputStream(fileName);
            pOut = new PrintStream (fOut);

            socket = new DatagramSocket(port);

            int received = 0;

            long previous = 0;
            long difference = 0;
            int amountOfTimeReceived = 0;

            // receive and put packets in buffer (or send immediately)
            while (true)
            {
                byte[] buf = new byte[Buffer.MAX_PACKET_SIZE];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                long currTime = System.nanoTime();

                if(received == 0 ){
                    received++;
                    difference = 0;
                } else {
                    difference = (currTime - previous)/1000; //microsec
                }

                int noTokens = bucket.getNoTokens();
                long bufferSize = buffer.getSizeInBytes();
                String line = String.format("%-10s %-10s %-10s %-10s %-10s", amountOfTimeReceived+1, difference, packet.getLength(), bufferSize, noTokens);
                pOut.println(line);
                previous = currTime;
                amountOfTimeReceived += 1;
				/*
				 * Process packet.
				 */

                // if buffer is empty, no packet is currently being sent,
                // and there are enough tokens received packet should be sent immediately
                if (bufferSize == 0
                        && !sender.sendingInProgress
                        && noTokens >= packet.getLength())
                {
                    bucket.removeTokens(packet.getLength());
                    sender.sendPacket(packet);
                }
                // else add packet to buffer if there is enough space
                else if (buffer.addPacket(new DatagramPacket(packet.getData(), packet.getLength())) < 0)
                {
                    System.err.println("Packet dropped, total: " + (++noDropped));
                }
            }
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}