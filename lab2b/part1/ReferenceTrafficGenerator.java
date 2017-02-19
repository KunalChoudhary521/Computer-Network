package part1;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ReferenceTrafficGenerator{

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
        BufferedReader bfReader = null;
        BufferedWriter bfWriter = null;

        String currentLine;
        StringTokenizer st = null;

        long cumulatedArrivals = 0;

        try
        {
            InetAddress sendIP = InetAddress.getByName(receiver);
            File fout = new File(outFileName);
            FileWriter fos = new FileWriter(fout);
            bfWriter = new BufferedWriter(fos);

            int amountOfTimesSent = 0;
            long getCurrTime = System.nanoTime();

            byte constantAmount[];

            DatagramSocket serverSocket = new DatagramSocket(port);

            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Date startTime = cal.getTime();
            bfWriter.write(sdf.format(startTime));
            bfWriter.newLine();
            bfWriter.write(""+getCurrTime);
            bfWriter.newLine();
            bfWriter.flush();

            while(amountOfTimesSent < 10000){
                 long newTime = System.nanoTime();
                 long difference = (newTime - getCurrTime)/1000000;
                 if(amountOfTimesSent == 0){
                     getCurrTime = System.nanoTime();
                     constantAmount = new byte[sizeOfPacket];
                     byte[] integer =  ByteBuffer.allocate(8).putInt(amountOfTimesSent).putInt(sizeOfPacket).array();
                     for(int j = 0; j < integer.length; j++){
                         constantAmount[j] = integer[j];
                     }
                     String line = String.format("%-5s %-5s %-12s", amountOfTimesSent + 1, 0, constantAmount.length);
                     DatagramPacket sendPacket = new DatagramPacket(constantAmount, constantAmount.length, sendIP, receiverPort);
                     serverSocket.send(sendPacket);
                     bfWriter.write(line);
                     bfWriter.newLine();
                     bfWriter.flush();
                     amountOfTimesSent++;
                     System.out.println(amountOfTimesSent);
                 } else if(difference >= transmissionInterval){
                     int bytesToSend = (int) Math.floor(difference/transmissionInterval);
                     for(int i = 0; i < bytesToSend*packets; i++) {
                         getCurrTime = System.nanoTime();
                         constantAmount = new byte[sizeOfPacket];
                         byte[] integer =  ByteBuffer.allocate(8).putInt(amountOfTimesSent).putInt(sizeOfPacket).array();
                         for(int j = 0; j < integer.length; j++){
                                 constantAmount[j] = integer[j];
                         }
                         String line = String.format("%-5s %-5s %-12s", amountOfTimesSent + 1, difference, constantAmount.length);
                         DatagramPacket sendPacket = new DatagramPacket(constantAmount, constantAmount.length, sendIP, receiverPort);
                         serverSocket.send(sendPacket);
                         bfWriter.write(line);
                         bfWriter.newLine();
                         bfWriter.flush();
                         amountOfTimesSent++;
                         System.out.print("\033[H\033[2J");
                         System.out.flush();
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


    public static void main(String args[]){
        int transmissionInterval = Integer.parseInt(args[0]);
        int packets = Integer.parseInt(args[1]);
        int sizeOfPacket = Integer.parseInt(args[2]);
        int port = Integer.parseInt(args[3]);
        String receiver = args[4];
        int receiverPort = Integer.parseInt(args[5]);
        String outFileName = args[6];

        ReferenceTrafficGenerator generator = new ReferenceTrafficGenerator(transmissionInterval, packets, sizeOfPacket
        , port, receiver, receiverPort, outFileName);

        generator.GenerateTraffic();
    }

}