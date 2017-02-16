//package part3;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;

public class TrafficGeneratorVid
{
    public static ArrayList<DataLine> dataList;


    public static void makeUdpPackets(String inFile, String recvHost, int port)
    {
        BufferedReader bfReader = null;

        String currentLine;
        StringTokenizer st;

        try
        {
            InetAddress sendIP = InetAddress.getByName(recvHost);

            File fin = new File(inFile);

            FileReader fis = new FileReader(fin);
            bfReader = new BufferedReader(fis);

            String col1,col2,col3,col4;
            int SeqNo = 0, frameSize;
            double delayinNs;
            int maxDGramSize = 65507;
            byte[] data; int dataSize = 4;
            //first packet is transmitted without a delay
            if((currentLine = bfReader.readLine()) != null)
            {
                st = new StringTokenizer(currentLine);
                col1 = st.nextToken();
                col2 = st.nextToken();
                col3 = st.nextToken();
                col4 = st.nextToken();

                SeqNo = Integer.parseInt(col1);
                //prevTime = Double.parseDouble(col2);
                frameSize = Integer.parseInt(col4);
                delayinNs = 0;//first packet is transmitted without a delay;

                data = new byte[frameSize];
                //(ByteBuffer.allocate(dataSize).order(ByteOrder.LITTLE_ENDIAN).putInt(frameSize).array());
                dataList.add(new DataLine(SeqNo,delayinNs, frameSize,
                        new DatagramPacket(data,frameSize,sendIP,port)));
            }

            while((currentLine = bfReader.readLine()) != null)
            {
                st = new StringTokenizer(currentLine);
                col1 = st.nextToken();
                col2 = st.nextToken();
                col3 = st.nextToken();
                col4 = st.nextToken();

                SeqNo = Integer.parseInt(col1);
                //nextTime = Double.parseDouble(col2);//in microseconds
                frameSize = Integer.parseInt(col4);//in bytes

                //re-scale values
                delayinNs = (100.0/3) * 1000;// in nanoseconds

                //adds dataLine to the end of dataList (break up datagram if size over 65507 bytes)
                //System.out.println("Adding Packet " + SeqNo);
                while(frameSize > 0)
                {
                    if(frameSize <= maxDGramSize)
                    {
                        data = new byte[frameSize];
                        //(ByteBuffer.allocate(dataSize).order(ByteOrder.LITTLE_ENDIAN).putInt(frameSize).array());
                        dataList.add(new DataLine(SeqNo, delayinNs, frameSize,
                                new DatagramPacket(data,frameSize,sendIP,port)));
                    }
                    else
                    {
                        data = new byte[maxDGramSize];
                        //(ByteBuffer.allocate(dataSize).order(ByteOrder.LITTLE_ENDIAN).putInt(maxDGramSize).array());
                        dataList.add(new DataLine(SeqNo, delayinNs, maxDGramSize,
                                new DatagramPacket(data,maxDGramSize,sendIP,port)));
                    }

                    frameSize -= maxDGramSize;
                }
            }

            //order packets by SeqNo
            Collections.sort(dataList, new Comparator<DataLine>()
            {
                @Override
                public int compare(DataLine o1, DataLine o2)
                {
                    Integer n1 = new Integer(o1.SeqNo);
                    Integer n2 = new Integer(o2.SeqNo);
                    return  n1.compareTo(n2);
                }
            });

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
            double  waitTime, sendTime, currTime;

            int idx = 0;

            sendSocket.send(dataList.get(0).udpPacket);//1st packet without delay
            idx++;

            for (idx = 1; idx < dataList.size(); idx++)
            {
                waitTime = (100.0/3) * 1000;//always 33ns for movietrace.data
                sendTime = System.nanoTime() + waitTime;

                currTime = System.nanoTime();
                while (currTime < sendTime)//busy wait to create artificial delay
                {
                    currTime = System.nanoTime();
                }
                //System.out.println("Packet " + idx + " received");
                sendSocket.send(dataList.get(idx).udpPacket);
            }
            System.out.println(idx + " datagram packets (after fragmentation) sent!");
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
        String inputFile = "movietrace.data", debugFile = "TGenVid.txt";
        dataList = new ArrayList<>();
        int sendPort = 50000;

        String sendhost = null;
        try {
            sendhost = args[0];
        }
        catch(Exception ex)
        {
            System.out.println("Provide receiver's IP as cmd-line arg (ex. localhost, or 192.168.0.12)");
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
            debugOut.printf("%-7s %-10.3f %s\n",
                    i, dataList.get(i).delayInNs/1000, dataList.get(i).udpPacket.getLength());

        }
        return;
    }
}

/*
        byte[] a = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(40000).array();
        System.out.println("Integer: " + ByteBuffer.wrap(a).order(ByteOrder.LITTLE_ENDIAN).getInt());
*/