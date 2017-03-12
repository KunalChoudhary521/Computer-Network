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

public class ReferenceTrafficSink implements Runnable {

    private String host;
    private int port;
    private String outFileName;
    private PrintStream writer;
    private FileOutputStream fout;


    public ReferenceTrafficSink(String host, int port, String outFileName){
        this.host = host;
        this.port = port;
        this.outFileName = outFileName;
    }

    public void receiveTraffic(){
        long cumulatedArrivals = 0;

        try
        {
            InetAddress sendIP = InetAddress.getByName(host);

            fout = new FileOutputStream(outFileName);
            writer = new PrintStream(fout);

            int amountOfTimesReceived = 0;

            DatagramSocket serverSocket = new DatagramSocket(port);

            long previous = 0;

            byte[] buf = new byte[10000];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            long difference = 0;
            while(true){
                serverSocket.receive(packet);
                long currTime = System.nanoTime();

                if(amountOfTimesReceived == 0){
                    difference = 0;
                } else{
                    difference = (currTime - previous)/1000; //microsec
                }
                amountOfTimesReceived++;
                previous = currTime;
                String line = String.format("%-10s %-10s %-10s", amountOfTimesReceived + 1, difference, packet.getLength());
                writer.println(line);
            }
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        finally
        {
            try {
                writer.close();
                fout.close();
            } catch (Exception ex){

            }
        }
    }


    public static void main(String args[]){
        int port = 8002;
        String receiver = "localhost";
        String outFileName = args[0];

        ReferenceTrafficSink generator = new ReferenceTrafficSink(receiver, port, outFileName);

        System.out.println("Waiting...");
        generator.receiveTraffic();
    }

    @Override
    public void run() {
        this.receiveTraffic();
    }
}