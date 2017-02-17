import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

class TrafficSinkVid
{

    //public static ArrayList<DataLine> recvList;

    public static void main (String[] args)
    {
        String outputFile = "TSinkVid.txt";

        PrintStream recvTraffic = null;
        int recvPort = 50001;
        byte[] recvBuf = new byte[70000];

        int currSeqNo = 0, recvLimit = 10000;

        try
        {
            DatagramSocket recvSocket = new DatagramSocket(recvPort);
            DatagramPacket recvPacket = new DatagramPacket(recvBuf,recvBuf.length);
            FileOutputStream fout = new FileOutputStream(outputFile);
            recvTraffic = new PrintStream(fout);

            int timeoutWindow = 10000;//in ms
            System.out.println("Traffic Sink Times out in " + timeoutWindow + "ms ...");

            double currTime, packetRecvTime, timeDiff;

            while(currSeqNo <= recvLimit)
            {
                recvSocket.setSoTimeout(timeoutWindow);//receiver closes (milliseconds)
                currTime = System.nanoTime();
                recvSocket.receive(recvPacket);

                packetRecvTime = System.nanoTime();

                if(currSeqNo != 0)
                {
                    timeDiff = (packetRecvTime - currTime) / 1000;//in ms
                }
                else
                {
                    timeDiff = 0;
                }
                //System.out.println("Packet " + currSeqNo + " received");
                recvTraffic.printf("%-7s %-10.3f %s\n", currSeqNo, timeDiff, recvPacket.getLength());
                //recvList.add(new DataLine(currSeqNo,timeDiff, data,null));
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