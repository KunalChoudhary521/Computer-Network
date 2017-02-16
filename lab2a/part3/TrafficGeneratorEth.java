//package part3;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class TrafficGeneratorEth
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

            String col1,col2;
            int SeqNo = 0, frameSize;
            double prevTime = 0, nextTime, delayinNs;

            //first packet is transmitted without a delay
            if((currentLine = bfReader.readLine()) != null)
            {
                st = new StringTokenizer(currentLine);
                col1 = st.nextToken();
                col2 = st.nextToken();

                SeqNo++;
                prevTime = Double.parseDouble(col1);
                frameSize = Integer.parseInt(col2);
                delayinNs = 0;//first packet is transmitted without a delay;

                dataList.add(new DataLine(SeqNo,delayinNs, frameSize,
                        new DatagramPacket(new byte[frameSize],frameSize,sendIP,port)));
            }

            while((currentLine = bfReader.readLine()) != null)
            {
                st = new StringTokenizer(currentLine);
                col1 = st.nextToken();
                col2 = st.nextToken();

                SeqNo++;
                nextTime = Double.parseDouble(col1);//in microseconds
                frameSize = Integer.parseInt(col2);//in bytes

                //re-scale values
                delayinNs = (nextTime - prevTime) * 1000_000_000;// in nanoseconds
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
            double  waitTime, sendTime, currTime;

            int seqNo = 0;

            sendSocket.send(dataList.get(0).udpPacket);//1st packet without delay
            seqNo++;

            for (seqNo = 1; seqNo < dataList.size(); seqNo++)
            {
                waitTime = dataList.get(seqNo).delayInNs;
                sendTime = System.nanoTime() + waitTime;

                currTime = System.nanoTime();
                while (currTime < sendTime)//busy wait to create artificial delay
                {
                    currTime = System.nanoTime();
                }
                //System.out.println("Packet " + seqNo + " received");
                sendSocket.send(dataList.get(seqNo).udpPacket);
            }
            System.out.println(seqNo + " packets sent!");
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
        String inputFile = "BC-pAug89-small.TL", debugFile = "TGenEth.txt";
        dataList = new ArrayList<>();
        int sendPort = 50000;

        String sendhost = null;
        try {
            sendhost = args[0];
        }
        catch(Exception ex)
        {
            System.out.println("Provide recevier's IP as cmd-line arg (ex. localhost or 192.168.0.12)");
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
                    dataList.get(i).SeqNo, dataList.get(i).delayInNs/1000_000_000,
                    dataList.get(i).packetSize);
        }
    }
}