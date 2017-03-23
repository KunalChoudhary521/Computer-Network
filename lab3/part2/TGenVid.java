package part2;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

/*
    Time in (milliseconds) and packet size in bytes (from Lab1_Part_2.m)
*/

public class TGenVid
{
    public static final int priority = 2;
    public static final int maxFSize = 1480;//any packet more than this is fragmented
    public static int packetsSent = 0;

    public static void readPackets(String inFile, String recvHost, int port, String dbgFile)
    {
        BufferedReader bfReader = null;
        ArrayList<DatagramPacket> packetList;

        String currentLine;
        StringTokenizer st;
        try
        {
            InetAddress sendIP = InetAddress.getByName(recvHost);

            FileReader fis = new FileReader(new File(inFile));
            bfReader = new BufferedReader(fis);

            PrintStream debugOut = new PrintStream(new FileOutputStream(dbgFile));//print traffic on generator side

            String col1,col2,col3,col4;
            int frameSize;

            byte[] payload;

            while((currentLine = bfReader.readLine()) != null)
            {
                st = new StringTokenizer(currentLine);
                col1 = st.nextToken();//sequence number
                col2 = st.nextToken();//time(ms) from lab1
                col3  = st.nextToken();//irrelevant columns
                col4  = st.nextToken();//packet size (bytes) from lab1

                frameSize = Integer.parseInt(col4);

                //Assumption: the first packet doesn't need to be fragmented
                if(packetsSent == 0)
                {
                    DatagramSocket sendSocket = new DatagramSocket();
                    payload = new byte[frameSize];
                    payload[0] = (byte)priority;//video priority
                    sendSocket.send(new DatagramPacket(payload,frameSize,sendIP,port));

                    debugOut.printf("%-7s %-10.3f %s\n", 0,0.0, payload.length);
                    packetsSent++;
                    continue;
                }

                if(frameSize > maxFSize)
                {
                    packetList = fragmentPacket(frameSize,sendIP, port);
                    sendUdpPacket(packetList,(100.0/3.0));
                }
                else//no fragmentation needed
                {
                    payload = new byte[frameSize];
                    payload[0] = (byte)priority;//video priority

                    packetList = new ArrayList<>();
                    packetList.add(new DatagramPacket(payload,frameSize,sendIP,port));
                    sendUdpPacket(packetList,(100.0/3.0));
                }

                for (int i = 0; i < packetList.size(); i++)
                {
                    debugOut.printf("%-7s %-10.3f %s\n", packetsSent,(100.0/3.0),
                            packetList.get(i).getLength());
                    packetsSent++;
                }
                //packetsSent += packetList.size();
            }
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        finally
        {
            // Close files
            if (bfReader != null) {
                try {
                    bfReader.close();

                } catch (IOException e) {
                    System.out.println("IOException: " +  e.getMessage());
                }
            }

            System.out.println(packetsSent + " were sent from Generator");
        }
    }
    public static ArrayList<DatagramPacket> fragmentPacket(int frameSize, InetAddress sinkIP, int sinkPort)
    {
        ArrayList<DatagramPacket> fragments = new ArrayList<>();
        byte[] payload;

        while(frameSize > maxFSize)
        {
            payload = new byte[maxFSize];
            payload[0] = (byte)priority;
            fragments.add(new DatagramPacket(payload,maxFSize,sinkIP,sinkPort));

            frameSize -= maxFSize;
        }

        //leftover portion of the packet
        payload = new byte[frameSize];
        payload[0] = (byte)priority;
        fragments.add(new DatagramPacket(payload,frameSize,sinkIP,sinkPort));

        return fragments;
    }
    public static void sendUdpPacket(ArrayList<DatagramPacket> pkt, double waitTime)
    {
        double sendTime, currTime;

        try {
            DatagramSocket sendSocket = new DatagramSocket();

            for(int i = 0; i < pkt.size(); i++)
            {
                sendTime = System.currentTimeMillis() + waitTime;
                do
                {
                    currTime = System.currentTimeMillis();
                }while (currTime < sendTime);//busy wait to create fake delay

                sendSocket.send(pkt.get(i));
            }
            sendSocket.close();
        }
        catch(IOException ex)
        {
            System.out.println("DatagramSocket exception in sendUdpPackets()\n"
                    + ex.getMessage());
        }
    }
    public static void main (String[] args)
    {
        String inputFile = "movietrace.data", debugFile = "TGenVid.txt";

        int sendPort = 5000;

        String sendhost = null;
        //int avgTRate = 0;//no need to re-scale from video traffic
        try {
            sendhost = args[0];
        }
        catch(Exception ex)
        {
            System.out.println("Traffic Generator Usage:\n " +
                                "java TrafficGenerator [hostname]");
        }

        readPackets(inputFile,sendhost,sendPort,debugFile);
    }
}