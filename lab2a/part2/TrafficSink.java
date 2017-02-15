//package part2;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class TrafficSink
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

        int recvPort = 50000;//use the same port as in TrafficGenerator.java
        byte[] recvBuf = new byte[1500];//no size is close to this in poisson3.data

        int currSeqNo = 0, recvLimit = 10000;

        try
        {
            //System.out.println( "Running at: " + InetAddress.getByName("localhost"));
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
                recvSocket.setSoTimeout(10000);//receiver closes (milliseconds)
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

                sequenceNumbers[i] = ""+currSeqNo;
                arrivals[i] = ""+timeDiff;
                packetSize[i] = ""+recvPacket.getLength();

                System.out.println("Packet " + currSeqNo + " received");
                currSeqNo++;
                i++;
            }

            printPacketsToFile(currSeqNo,sequenceNumbers,packetSize,arrivals,recvTraffic,cumulatedTraffic);
            System.out.println(currSeqNo + " packets received!");
            recvTraffic.close();
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
            printPacketsToFile(currSeqNo,sequenceNumbers,packetSize,arrivals,recvTraffic,cumulatedTraffic);
            if(recvTraffic != null)
            {
                recvTraffic.close();
            }
        }
    }

    public static void printPacketsToFile(int packetsRecv, String[] seqNum,String[] pSize, String[] arrv,PrintStream recvT, PrintStream culTraffic )
    {
        long cumulatedPackets = 0;
        long cumulatedArrivals = 0;
        for(int i = 0; i< packetsRecv;i++){
            cumulatedPackets += Long.parseLong(pSize[i]);
            cumulatedArrivals += Long.parseLong(arrv[i]);
            recvT.printf("%-7s %-7s %s\n",
                    seqNum[i], arrv[i], pSize[i]);

            culTraffic.printf("%-7s %-7s %s\n",
                    seqNum[i], cumulatedArrivals, cumulatedPackets);
        }
    }
}