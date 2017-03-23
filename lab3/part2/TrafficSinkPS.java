package part2;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

class TrafficSinkPS
{
    /*
        To test traffic generator, run TrafficSink --> TrafficGen (in that order)
        To test scheduler, run TrafficSink --> scheduler --> TrafficGen (in that order)
     */

    public static void main (String[] args)
    {
        String possionFile = "TSinkPos.txt";
        String vidFile = "TSinkVid.txt";

        PrintStream posTraffic = null;
        PrintStream vidTraffic = null;

        int recvPort = 5000;//use same port as in TrafficGenerator.java, unless using PacketScheduler
        byte[] recvBuf = new byte[1500];

        int posSeqNo = 0, vidSeqNo = 0;

        try
        {
            DatagramSocket recvSocket = new DatagramSocket(recvPort);
            DatagramPacket recvPacket = new DatagramPacket(recvBuf,recvBuf.length);
            posTraffic = new PrintStream(new FileOutputStream(possionFile));
            vidTraffic = new PrintStream(new FileOutputStream(vidFile));

            int timeoutWindow = 7000;//in ms
            System.out.println("Traffic Sink Times out in " + timeoutWindow + "ms ...");


            //receive the 1st packet without delay
            recvSocket.setSoTimeout(timeoutWindow);//receiver closes (milliseconds)
            recvSocket.receive(recvPacket);

            //check priority
            if(recvPacket.getData()[0] == (byte)1)
            {
                posTraffic.printf("%-7s %-10s %s\n",
                        ++posSeqNo, 0, recvPacket.getLength());
            }
            else if (recvPacket.getData()[0] == (byte)2)
            {
                vidTraffic.printf("%-7s %-10s %s\n",
                        ++vidSeqNo, 0, recvPacket.getLength());
            }


            long currTime, timeDiff, prevTime = System.nanoTime();
            while(true)//keep accepting packets until TrafficSink times-out
            {
                recvSocket.setSoTimeout(timeoutWindow);//receiver closes (milliseconds)
                recvSocket.receive(recvPacket);

                currTime = System.nanoTime();//time after getting the packet

                timeDiff = (currTime - prevTime);


                //check priority
                if(recvPacket.getData()[0] == (byte)1)//poisson packet
                {
                    if(posSeqNo == 0) { timeDiff = 0; }
                    posTraffic.printf("%-7s %-10s %s\n",
                            ++posSeqNo, timeDiff/1_000,      //convert: ns -> mu-sec
                            recvPacket.getLength());
                }
                else if (recvPacket.getData()[0] == (byte)2)//video packet
                {
                    if(vidSeqNo == 0) { timeDiff = 0; }
                    vidTraffic.printf("%-7s %-10s %s\n",
                            ++vidSeqNo, timeDiff/1_000_000,  //convert: ns -> ms
                            recvPacket.getLength());
                }

                prevTime = currTime;
            }

        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
            if(posTraffic != null)
            {
                posTraffic.close();
            }
            if(posTraffic != null)
            {
                vidTraffic.close();
            }
            System.out.println(posSeqNo + " poisson packets received!");
            System.out.println(vidSeqNo + " video packets received!");
        }
        posTraffic.close();
        vidTraffic.close();
    }

}