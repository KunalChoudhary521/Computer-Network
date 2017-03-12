package part1;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ReferenceTrafficGenerator implements Runnable{

    private int transmissionInterval;
    private int packets;
    private int port;
    private int sizeOfPacket;
    private String receiver;
    private String outFileName;
    private int receiverPort;
    private PrintStream writer;
    private FileOutputStream fout;

    public ReferenceTrafficGenerator(int transmissionInterval, int packets, int sizeOfPacket, int port, String receiver,
                                     int receiverPort, String outFileName){
        this.transmissionInterval = transmissionInterval;
        this.packets = packets;
        this.port = port;
        this.sizeOfPacket = sizeOfPacket;
        this.receiver = receiver;
        this.receiverPort = receiverPort;
        this.outFileName = outFileName;
    }

    public void GenerateTraffic(){
        BufferedWriter bfWriter = null;
        try
        {
            InetAddress sendIP = InetAddress.getByName(receiver);
            fout = new FileOutputStream(outFileName);
            writer = new PrintStream(fout);

            int amountOfTimesSent = 0;

            DatagramSocket serverSocket = new DatagramSocket(port);


            long absTime = System.nanoTime();
            long currTime = System.nanoTime();
            long prevTime = 0;

            while(amountOfTimesSent < 10000){
                 absTime = System.nanoTime();
                 long difference = (absTime - prevTime)/1000; //microsec
                 if(difference >= transmissionInterval) {
                     byte[] data = new byte[sizeOfPacket];
                     for (int i = 0; i < packets; i++) {
                         currTime = System.nanoTime();
                         if(amountOfTimesSent != 0) {
                             difference = (currTime - prevTime) / 1000;
                         } else {
                             difference = 0;
                         }
                         DatagramPacket p = new DatagramPacket(data, data.length, sendIP, receiverPort);
                         serverSocket.send(p);
                         String line = String.format("%-10s %-10s %-10s", amountOfTimesSent + 1, difference, sizeOfPacket);
                         writer.println(line);
                         prevTime = currTime;
                         amountOfTimesSent++;
                         System.out.println(amountOfTimesSent);
                     }
                 }
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


    public static void main(String args[]) throws InterruptedException {
        int transmissionInterval = 160; //microsec
        int packets = 1;
        int sizeOfPacket = 100;
        int port = 8000;
        String receiver = "localhost";
        int receiverPort = 8001;
        String outFileName = args[0];

        System.out.println("Traffic Rate: " + (double)((1000000/transmissionInterval)*packets*sizeOfPacket*8)/1000000 + "Mbps");

        Thread.sleep(1000);

        ReferenceTrafficGenerator generator = new ReferenceTrafficGenerator(transmissionInterval, packets, sizeOfPacket
        , port, receiver, receiverPort, outFileName);

        new Thread(generator).start();
    }

    @Override
    public void run() {
        this.GenerateTraffic();
    }
}