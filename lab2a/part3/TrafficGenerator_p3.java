package part3;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

public class TrafficGenerator_p3
{
    public static ArrayList<DataLine_p3> dataList;


    public static void makeUdpPackets(String inFile, String recvHost, int port)
    {
        BufferedReader bfReader = null;

        String currentLine;
        StringTokenizer st = null;

        long cumulatedArrivals = 0;

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

                cumulatedArrivals += frameSize;

                dataList.add(new DataLine_p3(SeqNo,delayinNs, frameSize,
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
                delayinNs = (nextTime - prevTime) * 1000;// in nanoseconds
                prevTime = nextTime;

                cumulatedArrivals += frameSize;

                //adds dataLine to the end of dataList
                dataList.add(new DataLine_p3(SeqNo, delayinNs, frameSize,
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
            long prevPktSendTime = System.nanoTime(), waitTime, sendTime, currTime,deltaT;
            File fout = new File("poisson3_cumulated_arrivals.data");
            FileWriter fos = new FileWriter(fout);
            BufferedWriter bfWriter = new BufferedWriter(fos);

            sendSocket.send(dataList.get(0).udpPacket);//1st packet without delay
            long cumulatedBytes = 0;
            int i;
            for (i = 1; i < dataList.size(); i++)
            {
                waitTime = dataList.get(i).delayInNs;
                sendTime = System.nanoTime() + waitTime;//prevPktSendTime

                currTime = System.nanoTime();
                while (currTime < sendTime)//busy wait to create artificial delay
                {
                    currTime = System.nanoTime();
                }
                deltaT = sendTime - currTime;
                /*if(Math.abs((float)(sendTime - currTime - waitTime)) > 20)//for debugging
                {
                    System.out.println(i + " " + Math.abs((float)(currTime - sendTime))
                                         + " " + waitTime);
                }*/
                //prevPktSendTime = currTime;
                sendSocket.send(dataList.get(i).udpPacket);
                cumulatedBytes+= dataList.get(i).packetSize;
                bfWriter.write(cumulatedBytes+"\t\t\t"+currTime);
                bfWriter.newLine();
            }
            System.out.println(i + " packets sent!");
            sendSocket.close();
            bfWriter.flush();
            bfWriter.close();
        }
        catch(IOException ex)
        {
            System.out.println("Datagramsocket exception in sendUdpPackets()\n"
                                + ex.getMessage());
        }
    }
    public static void main (String[] args)
    {
        String inputFile = "poisson3.data", debugFile = "TGenout.txt";
        dataList = new ArrayList<>();
        int sendPort = 50000;

        String sendhost = null;
        try {
            sendhost = args[0];
        }
        catch(Exception ex)
        {
            System.out.println("Provide recevier's IP as cmd-line arg");
        }
        makeUdpPackets(inputFile,sendhost,sendPort);

        //printToFile(debugFile);//output SeqNo & Time to file (for debugging only)

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
            debugOut.printf("%-7s %-7s %s\n",
                    dataList.get(i).SeqNo, dataList.get(i).delayInNs,
                    dataList.get(i).packetSize);
        }
    }
}