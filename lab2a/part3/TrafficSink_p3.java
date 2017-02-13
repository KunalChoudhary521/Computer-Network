package part3;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;

public class TrafficSink_p3
{
    public static void main (String[] args)
    {
        String outputFile = "TSinkout.txt";
        String outputFile2 = "TSinkout_cumulated.txt";

        String sequenceNumbers[] = new String[10000];
        String arrivals[] = new String[10000];
        String packetSize[] = new String[10000];
        
        PrintStream recvTraffic = null;
        PrintStream cumulatedTraffic = null;

        int recvPort = 50001;//use the same port as in TrafficGenerator.java
        byte[] recvBuf = new byte[1024];//no size is close to this in poisson3.data

        int currSeqNo = 1, recvLimit = 10000;

        try
        {
            System.out.println( "Running at: " + InetAddress.getByName("localhost"));
            DatagramSocket recvSocket = new DatagramSocket(recvPort);
            DatagramPacket recvPacket = new DatagramPacket(recvBuf,recvBuf.length);
            FileOutputStream fout = new FileOutputStream(outputFile);
            recvTraffic = new PrintStream(fout);
            FileOutputStream fout2 = new FileOutputStream(outputFile2);
            cumulatedTraffic = new PrintStream(fout2);

            System.out.println("Traffic Sink Waiting ...");

            int i = 0;
            long currTime = System.nanoTime(), packetRecvTime, timeDiff;

            while(currSeqNo <= recvLimit)
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
                    recvTraffic.printf(currTime+"\n");
                }
                currTime = packetRecvTime;

                sequenceNumbers[i] = ""+currSeqNo;
                arrivals[i] = ""+new Date().getTime();
                packetSize[i] = ""+recvPacket.getLength();

                currSeqNo++;
                i++;
            }
            long cumulatedPackets = 0;
            long cumulatedArrivals = 0;

            for(i = 0; i< 10000;i++){
                cumulatedPackets += Long.parseLong(packetSize[i]);
                cumulatedArrivals += Long.parseLong(arrivals[i]);
                recvTraffic.printf(cumulatedPackets+"\t\t\t"+System.nanoTime()+"\n");

                //cumulatedTraffic.printf("%-7s %-7s %s\n",
                        //sequenceNumbers[i], cumulatedArrivals, cumulatedPackets);
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