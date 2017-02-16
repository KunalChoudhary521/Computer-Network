import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

class TrafficSink
{
    public static void main (String[] args)
    {
        String outputFile = "TSinkEth.txt";

        PrintStream recvTraffic = null;
        int recvPort = 50001;
        byte[] recvBuf = new byte[1520];//no size is close to this in BC-pAug89-small.TL

        int currSeqNo = 0, recvLimit = 10000;

        try
        {
            DatagramSocket recvSocket = new DatagramSocket(recvPort);
            DatagramPacket recvPacket = new DatagramPacket(recvBuf,recvBuf.length);
            FileOutputStream fout = new FileOutputStream(outputFile);
            recvTraffic = new PrintStream(fout);

            System.out.println("Traffic Sink Waiting ...");

            double currTime, packetRecvTime, timeDiff;

            while(currSeqNo < recvLimit)
            {
                currTime = System.nanoTime();
                recvSocket.receive(recvPacket);

                packetRecvTime = System.nanoTime();
                if(currSeqNo != 1)
                {
                    timeDiff = (packetRecvTime - currTime) / 1000_000_000;//in seconds
                }
                else
                {
                    timeDiff = 0;
                }
                //currTime = packetRecvTime;
                //System.out.println("Packet " + currSeqNo + " received");

                recvTraffic.printf("%-7s %-10.3f %s\n",
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