package part1;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

public class ReferenceTrafficSink {

    private String host;
    private int port;
    private String outFileName;


    public ReferenceTrafficSink(String host, int port, String outFileName){
        this.host = host;
        this.port = port;
        this.outFileName = outFileName;
    }

    public void receiveTraffic(){
        BufferedReader bfReader = null;
        BufferedWriter bfWriter = null;

        String currentLine;
        StringTokenizer st = null;

        long cumulatedArrivals = 0;

        try
        {
            InetAddress sendIP = InetAddress.getByName(host);
            File fout = new File(outFileName);
            FileWriter fos = new FileWriter(fout);
            bfWriter = new BufferedWriter(fos);

            int amountOfTimesReceived = 0;
            long getCurrTime = System.nanoTime();

            byte constantAmount[];

            DatagramSocket serverSocket = new DatagramSocket(port);

            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            long currTime = System.nanoTime();

            Date startTime = cal.getTime();
            bfWriter.write(sdf.format(startTime));
            bfWriter.newLine();
            bfWriter.write(""+currTime);
            bfWriter.newLine();
            bfWriter.flush();

            byte[] buf = new byte[10000];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            while(true){
                serverSocket.receive(packet);
                long difference = (System.nanoTime() - currTime)/1000000;
                ByteBuffer buffer = ByteBuffer.wrap(packet.getData());
                int sequenceNumber = buffer.getInt();
                if(sequenceNumber == 0){
                    System.out.println("Waiting...");
                }
                int sizeOfPacket = buffer.getInt();
                String line = String.format("%-5s %-5s %-12s", sequenceNumber + 1, difference, sizeOfPacket);
                bfWriter.write(line);
                bfWriter.newLine();
                bfWriter.flush();
                currTime = System.nanoTime();
            }

        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        finally
        {
            if (bfWriter != null) {
                try {
                    bfWriter.close();
                } catch (IOException e) {
                    System.out.println("IOException: " +  e.getMessage());
                }
            }
        }
    }


    public static void main(String args[]){
        int port = Integer.parseInt(args[1]);
        String receiver = args[0];
        String outFileName = args[2];

        ReferenceTrafficSink generator = new ReferenceTrafficSink(receiver, port, outFileName);

        System.out.println("Waiting...");
        generator.receiveTraffic();
    }

}