package part3;

import part2.Packet;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.StringTokenizer;

/*
    Time in (microseconds) and packet size in bytes (from Lab1_Part_1.m)
*/
public class TGenPosCustom implements Runnable
{
    public static ArrayList<Packet> pktList;
    public static final int priority = 1;

    String inFile;
    String recvHost;
    int port;
    int N;
    String outFile;

    public TGenPosCustom(String inFile, String recvHost, int port, int N, String outFile){
        this.inFile = inFile;
        this.recvHost = recvHost;
        this.port = port;
        this.N = N;
        this.outFile = outFile;
    }


    public void makeUdpPackets()
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

            byte[] payload = null;

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

                payload = new byte[Integer.parseInt(col3)];
                payload[0] = (byte)priority;//poisson priority

                pktList.add(new Packet(Integer.parseInt(col1),delay, Integer.parseInt(col3),
                        new DatagramPacket(payload, Integer.parseInt(col3),sendIP,port)));
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

                payload = new byte[frameSize];
                payload[0] = (byte)priority;//poisson priority

                //adds dataLine to the end of dataList
                pktList.add(new Packet(SeqNo, delay, frameSize,
                        new DatagramPacket(payload,frameSize,sendIP,port)));
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
    public void sendUdpPackets()
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
            System.out.println(i + " packets sent from Poisson Generator");
            sendSocket.close();
        }
        catch(IOException ex)
        {
            System.out.println("DatagramSocket exception in sendUdpPackets()\n"
                    + ex.getMessage());
        }
    }
    public void run ()
    {
        pktList = new ArrayList<>();

        makeUdpPackets();

        sendUdpPackets();

        printToFile(outFile);//output SeqNo & Time to file (for debugging only)
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