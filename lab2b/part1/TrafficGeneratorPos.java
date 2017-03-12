package part1;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;


public class TrafficGeneratorPos
{
    public static ArrayList<DataLine> dataList;


    public static void makeUdpPackets(String inFile, String recvHost, int port)
    {
        BufferedReader bfReader = null;

        String currentLine;
        StringTokenizer st = null;
        try
        {
            InetAddress sendIP = InetAddress.getByName(recvHost);

            File fin = new File(inFile);
            FileReader fis = new FileReader(fin);
            bfReader = new BufferedReader(fis);

            String col1,col2,col3;
            int SeqNo, frameSize, prevTime = 0, nextTime, delayinNs;

            //first packet is transmitted without a delay
            if((currentLine = bfReader.readLine()) != null)
            {
                st = new StringTokenizer(currentLine);
                col1 = st.nextToken();
                col2 = st.nextToken();
                col3  = st.nextToken();

                SeqNo = Integer.parseInt(col1);
                frameSize = Integer.parseInt(col3);
                prevTime = Integer.parseInt(col2);
                delayinNs = 0;//first packet is transmitted without a delay

                dataList.add(new DataLine(SeqNo,delayinNs, frameSize,
                        new DatagramPacket(new byte[frameSize],frameSize,sendIP,port)));
            }

            while((currentLine = bfReader.readLine()) != null)
            {
                st = new StringTokenizer(currentLine);
                col1 = st.nextToken();
                col2 = st.nextToken();
                col3  = st.nextToken();

                SeqNo = Integer.parseInt(col1);
                nextTime = Integer.parseInt(col2);//in microseconds
                frameSize = Integer.parseInt(col3);//in bytes

                //re-scale values
                delayinNs = (nextTime - prevTime);
                prevTime = nextTime;

                //adds dataLine to the end of dataList
                dataList.add(new DataLine(SeqNo, delayinNs, frameSize,
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
            double waitTime, sendTime, currTime;

            sendSocket.send(dataList.get(0).udpPacket);//1st packet without delay

            int i;
            for (i = 1; i < dataList.size(); i++)
            {
                waitTime = dataList.get(i).delayInNs;
                currTime = System.nanoTime()/1000;

                sendTime = currTime + waitTime;
                while (currTime < sendTime)//busy wait to create artificial delay
                {
                    currTime = System.nanoTime()/1000;
                }
                //prevPktSendTime = currTime;
                sendSocket.send(dataList.get(i).udpPacket);
            }
            System.out.println(i + " packets sent!");
            sendSocket.close();
        }
        catch(IOException ex)
        {
            System.out.println("Datagramsocket exception in sendUdpPackets()\n"
                    + ex.getMessage());
        }
    }
    public static void main (String[] args)
    {
        String inputFile = "poisson3.data", debugFile = "3-poisson-gen.txt";
        dataList = new ArrayList<>();
        int sendPort = 8001;

        String sendhost = null;
        try {
            sendhost = "localhost";
        }
        catch(Exception ex)
        {
            System.out.println("Provide receiver's IP as cmd-line arg (ex. localhost or 192.168.0.12)");
        }
        makeUdpPackets(inputFile,sendhost,sendPort);

        printToFile(debugFile);//output SeqNo & Time to file (for debugging only)

        sendUdpPackets();
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
        for (int i = 0; i < dataList.size(); i++)
        {
            debugOut.printf("%-7s %-10s %s\n",
                    dataList.get(i).SeqNo, dataList.get(i).delayInNs,
                    dataList.get(i).packetSize);
        }
    }
}