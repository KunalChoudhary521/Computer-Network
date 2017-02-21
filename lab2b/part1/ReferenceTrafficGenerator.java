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
            File fout = new File(outFileName);
            FileWriter fos = new FileWriter(fout);
            bfWriter = new BufferedWriter(fos);

            int amountOfTimesSent = 0;
            long getStartTime = System.nanoTime();
            long getNewTime = System.nanoTime();

            byte constantAmount[];

            DatagramSocket serverSocket = new DatagramSocket(port);

            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Date startTime = cal.getTime();
            bfWriter.write(sdf.format(startTime));
            bfWriter.newLine();
            bfWriter.write(""+getStartTime);
            bfWriter.newLine();
            bfWriter.flush();

            while(amountOfTimesSent < 10000){
                 long newTime = System.nanoTime();
                 long difference = (newTime - getNewTime)/1000000;
                 if(amountOfTimesSent == 0){
                     constantAmount = new byte[sizeOfPacket];
                     byte[] integer =  ByteBuffer.allocate(8).putInt(amountOfTimesSent).putInt(sizeOfPacket).array();
                     for(int j = 0; j < integer.length; j++){
                         constantAmount[j] = integer[j];
                     }
                     String line = String.format("%-10s %-10s %-10s", amountOfTimesSent + 1, 0, constantAmount.length);
                     DatagramPacket sendPacket = new DatagramPacket(constantAmount, constantAmount.length, sendIP, receiverPort);
                     serverSocket.send(sendPacket);
                     getNewTime = newTime;
                     bfWriter.write(line);
                     bfWriter.newLine();
                     bfWriter.flush();
                     amountOfTimesSent++;
                     System.out.println(amountOfTimesSent);
                 } else if(difference >= transmissionInterval){
                     int bytesToSend = (int) Math.floor(difference/transmissionInterval);
                     for(int i = 0; i < packets; i++) {
                         constantAmount = new byte[sizeOfPacket];
                         byte[] integer =  ByteBuffer.allocate(8).putInt(amountOfTimesSent + 1).putInt(sizeOfPacket).array();
                         for(int j = 0; j < integer.length; j++){
                                 constantAmount[j] = integer[j];
                         }
                         String line = String.format("%-10s %-10s %-10s", amountOfTimesSent + 1, difference, constantAmount.length);
                         DatagramPacket sendPacket = new DatagramPacket(constantAmount, constantAmount.length, sendIP, receiverPort);
                         serverSocket.send(sendPacket);
                         getNewTime = newTime;
                         difference = (newTime - getNewTime)/1000000;
                         bfWriter.write(line);
                         bfWriter.newLine();
                         bfWriter.flush();
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
        int transmissionInterval = 2;
        int packets = 1;
        int sizeOfPacket = 100;
        int port = 8000;
        String receiver = "localhost";
        int receiverPort = 8001;
        String outFileName = "traffic-gen.data";

        System.out.println("Traffic Rate: " + (double)((1000/transmissionInterval)*packets*sizeOfPacket*8)/1000000 + "Mbps");

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