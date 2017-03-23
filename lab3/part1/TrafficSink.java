package part1;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

class TrafficSink
{
    /*
        For 1.2: Run traffic sink before running traffic generator
        For 1.3: Run TrafficSink --> FIFOSch --> TrafficGenerator (in that order)
     */

    public static void main (String[] args)
    {
        String outputFile = "TSinkOut.txt";

        PrintStream recvTraffic = null;
        int recvPort = 5001;//use same port as in TrafficGenerator.java, unless using PacketScheduler
        byte[] recvBuf = new byte[1500];

        int currSeqNo = 0;

        try
        {
            DatagramSocket recvSocket = new DatagramSocket(recvPort);
            DatagramPacket recvPacket = new DatagramPacket(recvBuf,recvBuf.length);
            FileOutputStream fout = new FileOutputStream(outputFile);
            recvTraffic = new PrintStream(fout);

            int timeoutWindow = 7000;//in ms
            System.out.println("Traffic Sink Times out in " + timeoutWindow + "ms ...");


            //receive the 1st packet without delay
            recvSocket.receive(recvPacket);
            recvTraffic.printf("%-7s %-10s %s\n",
                    ++currSeqNo, 0, recvPacket.getLength());

            long currTime, timeDiff, prevTime = System.nanoTime();
            while(true)//keep accepting packets until TrafficSink times-out
            {
                recvSocket.setSoTimeout(timeoutWindow);//receiver closes (milliseconds)
                recvSocket.receive(recvPacket);

                currTime = System.nanoTime();//time after getting the packet

                timeDiff = (currTime - prevTime) / 1000;//convert: ns -> mu-sec
                prevTime = currTime;

                recvTraffic.printf("%-7s %-10s %s\n",
                        ++currSeqNo, timeDiff, recvPacket.getLength());
            }

        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
            if(recvTraffic != null)
            {
                recvTraffic.close();
            }
            System.out.println(currSeqNo + " packets received!");
        }
        recvTraffic.close();
    }

}