package part1;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class TrafficGeneratorVid {
    public static ArrayList<DataLine> dataList;

    public static void sendUdpPackets(String inFile, String recvHost, int port) {
        BufferedReader bfReader = null;

        String currentLine;
        StringTokenizer st;

        try {
            InetAddress sendIP = InetAddress.getByName(recvHost);

            File fin = new File(inFile);

            FileReader fis = new FileReader(fin);
            bfReader = new BufferedReader(fis);

            String col1, col2, col3, col4;
            int SeqNo = 0, frameSize;
            long delayinNs = 3 * 1000000; //every 3 ms
            int maxDGramSize = 65507;
            byte[] data;
            int dataSize = 4;

            DatagramSocket sendSocket = new DatagramSocket();
            long prevTime = 0, sendTime, currTime;

            int amountOfTimesSent = 0;
            String dbgFile = "3-movie-gen.txt";
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

            int count = 0;

            currentLine = bfReader.readLine();
            while (currentLine != null) {
                currTime = System.nanoTime();
                sendTime = currTime + delayinNs;
                long difference = (currTime - prevTime)/1000;
                st = new StringTokenizer(currentLine);
                col1 = st.nextToken();
                col2 = st.nextToken();
                col3 = st.nextToken();
                col4 = st.nextToken();

                SeqNo = Integer.parseInt(col1);
                frameSize = Integer.parseInt(col4);//in bytes

                //adds dataLine to the end of dataList (break up datagram if size over 65507 bytes)
                //System.out.println("Adding Packet " + SeqNo);
                while (frameSize > 0) {
                    if (frameSize <= maxDGramSize) {
                        data = new byte[frameSize];
                        //(ByteBuffer.allocate(dataSize).order(ByteOrder.LITTLE_ENDIAN).putInt(frameSize).array());
                       sendSocket.send(new DatagramPacket(data, frameSize, sendIP, port));
                    } else {
                        data = new byte[maxDGramSize];
                        //(ByteBuffer.allocate(dataSize).order(ByteOrder.LITTLE_ENDIAN).putInt(maxDGramSize).array());
                        sendSocket.send(new DatagramPacket(data, maxDGramSize, sendIP, port));
                    }
                    frameSize -= maxDGramSize;
                }
                if(count == 0){
                    debugOut.printf("%-7s %-7s %s\n",
                            count+1, 0, Math.abs(frameSize));
                } else {
                    debugOut.printf("%-7s %-7s %s\n",
                            count+1, difference, Math.abs(frameSize));
                }
                prevTime = currTime;
                count++;
                currentLine = bfReader.readLine();
                do{
                    currTime = System.nanoTime();
                } while(currTime < sendTime);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            // Close files
            if (bfReader != null) {
                try {
                    bfReader.close();
                } catch (IOException e) {
                    System.out.println("IOException: " + e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        String inputFile = "movietrace.data", debugFile = "3-movie-gen.txt";
        dataList = new ArrayList<>();
        int sendPort = 8001;

        String sendhost = null;
        try {
            sendhost = "localhost";
        } catch (Exception ex) {
            System.out.println("Provide receiver's IP as cmd-line arg (ex. localhost, or 192.168.0.12)");
        }
        sendUdpPackets(inputFile, sendhost, sendPort);
    }
}
/*
        byte[] a = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(40000).array();
        System.out.println("Integer: " + ByteBuffer.wrap(a).order(ByteOrder.LITTLE_ENDIAN).getInt());
*/