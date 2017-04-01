import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Receiver extends Thread
{
    public String fileOutput;
    int receiverPort, maxpktSize;
    public Timestamp[] rcvTS;

    public Receiver(Timestamp[] ts ,int recvPort, int maxpktSize)
    {
        this.rcvTS = ts;
        this.receiverPort = recvPort;
        this.maxpktSize = maxpktSize;

        this.fileOutput = "EstimatedTime.txt";
    }

    public void run()
    {
        receiveUDPPackets();
    }

    public void receiveUDPPackets()
    {
        try
        {
            DatagramSocket recvSocket = new DatagramSocket(this.receiverPort);
            PrintStream printPacket = new PrintStream(new FileOutputStream(fileOutput));

            byte[] recvBuf = new byte[this.maxpktSize];
            DatagramPacket recvdPacket = new DatagramPacket(recvBuf,recvBuf.length);

            int timeoutWindow = 3000;//in ms
            System.out.println("Receiver times out in " + timeoutWindow + "ms ...");

            long currTime, timeDiff, prevTime = System.nanoTime() / 1000;
            while(true)//keep accepting packets until TrafficSink times-out
            {
                recvSocket.setSoTimeout(timeoutWindow);//receiver closes (milliseconds)
                recvSocket.receive(recvdPacket);

                currTime = System.nanoTime() / 1000;//time after getting the packet

                timeDiff = (currTime - prevTime);
                prevTime = currTime;

                //extract port & sequence numbers
                int portNum = fromByteArray(recvdPacket.getData(),0,2);
                int currSeqNo = fromByteArray(recvdPacket.getData(),2,4);


                this.rcvTS[currSeqNo-1].setRecvTime(currSeqNo,currTime);

                printPacket.printf("%-7s %-10s %s\n",
                        rcvTS[currSeqNo-1].seqNo, rcvTS[currSeqNo-1].sendTime, rcvTS[currSeqNo-1].recvTime);
            }

        }
        catch (Exception ex)
        {
            //ex.printStackTrace();
            System.out.println("Receiver has timed out...");
        }

    }

    /**
     * Converts a byte array to an integer.
     * @param value a byte array
     * @param start start position in the byte array
     * @param length number of bytes to consider
     * @return the integer value
     */
    public int fromByteArray(byte [] value, int start, int length)
    {
        int Return = 0;
        for (int i=start; i< start+length; i++)
        {
            Return = (Return << 8) + (value[i] & 0xff);
        }
        return Return;
    }
}
