import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class TrafficSinkPos
{
    public static void main (String[] args)
    {
        String outputFile = "TSinkPos.txt";

        PrintStream recvTraffic = null;
        int recvPort = 50001;//use the same port as in TrafficGenerator.java
        byte[] recvBuf = new byte[1500];//no size is close to this in poisson3.data

        int currSeqNo = 1, recvLimit = 10000;

        try
        {
            //System.out.println( "Running at: " + InetAddress.getByName("localhost"));
            DatagramSocket recvSocket = new DatagramSocket(recvPort);
            DatagramPacket recvPacket = new DatagramPacket(recvBuf,recvBuf.length);
            FileOutputStream fout = new FileOutputStream(outputFile);
            recvTraffic = new PrintStream(fout);

            int timeoutWindow = 10000;//in ms
            System.out.println("Traffic Sink Times out in " + timeoutWindow + "ms ...");

            long currTime, packetRecvTime, timeDiff;

            while(currSeqNo < recvLimit)
            {
                recvSocket.setSoTimeout(timeoutWindow);//receiver closes (milliseconds)
                currTime = System.nanoTime();
                recvSocket.receive(recvPacket);

                packetRecvTime = System.nanoTime();
                if(currSeqNo != 1)
                {
                    timeDiff = (packetRecvTime - currTime) / 1_000;
                }
                else
                {
                    timeDiff = 0;
                }
                //System.out.println("Packet " + currSeqNo + " received");

                recvTraffic.printf("%-7s %-7s %s\n",
                        currSeqNo, timeDiff, recvPacket.getLength());
                currSeqNo++;

            }

            System.out.println(currSeqNo + " packets received!");
            recvTraffic.close();
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
            if(recvTraffic != null)
            {
                recvTraffic.close();
            }
        }
        recvTraffic.close();
    }

}