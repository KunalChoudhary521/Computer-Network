package part1;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;


public class TrafficGenerator
{
    public static ArrayList<Packet> pktList;


    public static void makeUdpPackets(String inFile, String recvHost, int port, int N)
    {
        BufferedReader bfReader = null;

        String currentLine;
        StringTokenizer st = null;
        try
        {
            InetAddress sendIP = InetAddress.getByName(recvHost);

            FileReader fis = new FileReader(new File(inFile));
            bfReader = new BufferedReader(fis);

            String col1,col2,col3;
            int SeqNo, frameSize, prevTime = 0, nextTime, delay;

            //first packet is transmitted without a delay
            if((currentLine = bfReader.readLine()) != null)
            {
                st = new StringTokenizer(currentLine);
                col1 = st.nextToken();//sequence number
                col2 = st.nextToken();//time(mu-sec)
                col3  = st.nextToken();//packet size (bytes)

                nextTime = Integer.parseInt(col2) * 1000;
                prevTime = nextTime;
                delay = 0;//1st packet transmitted without a delay

                pktList.add(new Packet(Integer.parseInt(col1),delay, Integer.parseInt(col3),
                        new DatagramPacket(new byte[Integer.parseInt(col3)],
                                Integer.parseInt(col3),sendIP,port)));
            }

            while((currentLine = bfReader.readLine()) != null)
            {
                st = new StringTokenizer(currentLine);
                col1 = st.nextToken();//sequence number
                col2 = st.nextToken();//time(mu-sec) from lab1
                col3  = st.nextToken();//packet size (bytes) from lab1

                SeqNo = Integer.parseInt(col1);
                nextTime = Integer.parseInt(col2) * 1000;//convert: mu-sec -> ns
                frameSize = Integer.parseInt(col3);

                delay = (nextTime - prevTime)/N;//re-scale values ex 1.1 (in ns)
                prevTime = nextTime;

                //adds dataLine to the end of dataList
                pktList.add(new Packet(SeqNo, delay, frameSize,
                        new DatagramPacket(new byte[frameSize],frameSize,sendIP,port)));
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
        }
    }
    public static void sendUdpPackets()
    {
        try {
            DatagramSocket sendSocket = new DatagramSocket();


            sendSocket.send(pktList.get(0).udpPacket);//send 1st packet without delay

            int i;
            long waitTime, sendTime, currTime, prevTime = System.nanoTime();
            for (i = 1; i < pktList.size(); i++)
            {
                waitTime = pktList.get(i).delayInNs;

                sendTime = prevTime + waitTime;
                do
                {
                    currTime = System.nanoTime();
                }while (currTime < sendTime);//busy wait to create fake delay

                prevTime = currTime;

                sendSocket.send(pktList.get(i).udpPacket);
            }
            System.out.println(i + " packets sent!");
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
        String inputFile = "poisson3.data", debugFile = "TGen.txt";
        pktList = new ArrayList<>();
        int sendPort = 5000;

        String sendhost = null;
        int avgTRate = 0;//N (from 1.1)
        try {
            sendhost = args[0];
            avgTRate = Integer.parseInt(args[1]);
        }
        catch(Exception ex)
        {
            System.out.println("Traffic Generator Usage:\n " +
                                "java TrafficGenerator [hostname] [Avg. Traffic Rate (N)]");
        }

        makeUdpPackets(inputFile,sendhost,sendPort, avgTRate);

        //sendUdpPackets();

        printToFile(debugFile);//output SeqNo & Time to file (for debugging only)
    }

    public static void printToFile(String dbgFile)
    {
        PrintStream debugOut = null;
        try
        {
            FileOutputStream fout = new FileOutputStream(dbgFile);
            debugOut = new PrintStream(fout);
        }
        catch(IOException ex)
        {
            System.out.println("File exception in printToFile()\n" + ex.getMessage());
        }
        for (int i = 0; i < pktList.size(); i++)
        {
            debugOut.printf("%-7s %-10s %s\n",
                    pktList.get(i).SeqNo, pktList.get(i).delayInNs/1000,//convert: ns -> mu-sec
                    pktList.get(i).packetSize);
        }
    }
}