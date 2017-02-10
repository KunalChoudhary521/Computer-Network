import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class TrafficSink
{
    public static void main (String[] args)
    {
        String outputFile = "TSinkout.txt";
        
        PrintStream recvTraffic = null;
        int recvPort = 50000;//use the same port as in TrafficGenerator.java
        byte[] recvBuf = new byte[1024];//no size is close to this in poisson3.data

        int currSeqNo = 1, recvLimit = 10000;

        try
        {
            System.out.println( "Running at: " + InetAddress.getByName("localhost"));
            DatagramSocket recvSocket = new DatagramSocket(recvPort);
            DatagramPacket recvPacket = new DatagramPacket(recvBuf,recvBuf.length);
            FileOutputStream fout = new FileOutputStream(outputFile);
            recvTraffic = new PrintStream(fout);

            System.out.println("Traffic Sink Waiting ...");

            long currTime = System.nanoTime(), packetRecvTime, timeDiff;

            while(currSeqNo < recvLimit)
            {
                recvSocket.receive(recvPacket);

				packetRecvTime = System.nanoTime();
                if(currSeqNo != 1)
                {
                    timeDiff = (packetRecvTime - currTime) / 1000;
                }
                else
                {
                    timeDiff = 0;
                }
                currTime = packetRecvTime;

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