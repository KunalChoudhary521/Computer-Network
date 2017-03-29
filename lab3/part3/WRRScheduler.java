package part3;

import part1.Packet;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by rahmanz5 on 3/28/2017.
 */
public class WRRScheduler {

    public ArrayList<Queue> queues;
    public ReentrantLock queueLock;
    public int maxPacketSize;

    public WRRScheduler(int inPort,String inAddress,  String outAddress, int outPort, long linkCapacity,
                           int numQueues,
                           int maxPacketSize, long[] queueCapacities,
                           long[] queueWeights, long[] avgFlowRates,
                           String recvfileName, String sendFileName)
    {
        queues = new ArrayList<>();
        for(int i = 0; i < numQueues; i++){
            queues.add(new Queue(queueCapacities[i], queueWeights[i]));
        }

        this.maxPacketSize = maxPacketSize;
        this.queueLock = new ReentrantLock();

        new Thread(new WRRReceiver(inPort, inAddress,
                this, recvfileName)).start();
        new Thread(new WRRSender(outPort, outAddress,
                this, avgFlowRates, queueWeights, linkCapacity, sendFileName)).start();
    }
}

class WRRReceiver implements Runnable{

    private int port;
    private String address;
    private WRRScheduler scheduler;
    private String fileName;

    public WRRReceiver(int port, String address, WRRScheduler scheduler, String fileName){
        this.port = port;
        this.address = address;
        this.scheduler = scheduler;
        this.fileName = fileName;
    }

    public void run(){
        DatagramSocket socket = null;
        PrintStream pOut = null;

        try
        {
            FileOutputStream fOut =  new FileOutputStream(fileName);
            pOut = new PrintStream (fOut);
            long previsuTime = 0;

            socket = new DatagramSocket(port);
            System.out.println("Started receiver on " + address + ":" + port);

            // receive and process packets
            while (true)
            {
                byte[] buf = new byte[scheduler.maxPacketSize];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);

                // wait for packet, when arrives receive and recored arrival time
                socket.receive(packet);
                long startTime=System.nanoTime();

				/*
				 * Record arrival to file in following format:
				 * elapsed time (microseconds), packet size (bytes), backlog in buffers ordered by index in array (bytes).
				 */
                // to put zero for elapsed time in first line
                if(previsuTime == 0)
                {
                    previsuTime = startTime;
                    //System.out.println(startTime);
                }
                pOut.print((startTime-previsuTime)/1000 + "\t" + packet.getLength() + "\t");

                this.scheduler.queueLock.lock();
                for (int i = 0; i<this.scheduler.queues.size(); i++)
                {
                    long bufferSize = this.scheduler.queues.get(i).getSize();
                    pOut.print(bufferSize + "\t");
                }
                this.scheduler.queueLock.unlock();

                pOut.println();
                previsuTime = startTime;

				/*
				 * Process packet.
				 */

				boolean result = false;

                // add packet to a queue if there is enough space
                if (packet.getData() [0] == (byte) 1)//poisson traffic (low priority)
                {
                    this.scheduler.queueLock.lock();
                        result = this.scheduler.queues.get(0).addToQueue(packet);
                    this.scheduler.queueLock.unlock();
                }
                else if (packet.getData() [0] == (byte) 2)//video traffic (high priority)
                {
                    this.scheduler.queueLock.lock();
                        result = this.scheduler.queues.get(1).addToQueue(packet);
                    this.scheduler.queueLock.unlock();
                } else if (packet.getData() [0] == (byte) 3)//video traffic (high priority)
                {
                    this.scheduler.queueLock.lock();
                        result = this.scheduler.queues.get(2).addToQueue(packet);
                    this.scheduler.queueLock.unlock();
                }

                if(result){
                    System.out.println("There was not enough space in the queue for this packet, dropped.");
                }
            }
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        socket.disconnect();
        socket.close();
    }

}

class WRRSender implements Runnable{

    private int port;
    private String sendAddress;

    private WRRScheduler scheduler;
    private long[] avgRates;
    private long[] weights;
    private long linkCapacity;

    private String fileName;

    private double[] xi;
    private double x;
    private double[] packetsPerRound;

    public WRRSender(int port, String address, WRRScheduler scheduler,
                     long[] avgFlowRates, long[] weights, long linkCapacity, String fileName){
        this.port = port;
        this.sendAddress = address;

        this.scheduler = scheduler;
        this.avgRates = avgFlowRates;
        this.weights = weights;
        this.linkCapacity = linkCapacity;

        this.xi = new double[this.scheduler.queues.size()];
        this.fileName = fileName;
    }

    public void run(){
        for(int i  = 0; i < scheduler.queues.size(); i++){
            xi[i] = weights[i]/avgRates[i];
            System.out.println("Sender: xi[" + i + "] = " + xi[i]);
        }

        this.x = Integer.MAX_VALUE;
        for(int i =0; i< xi.length; i++){
            if(xi[i] < x){
                x = xi[i];
            }
        }

        System.out.println("Sender: x = " + x);

        for(int i = 0; i < xi.length; i++){
            packetsPerRound[i] = xi[i]/x;
            System.out.println("Sender: packets per round for source " + i + " = " + packetsPerRound[i]);
        }

        DatagramSocket socket = null;
        //set up socket
        try {
            socket = new DatagramSocket();
        } catch(Exception ex){

        }

        //begin rounds

        long prevTime = 0;
        long currTime = 0;
        long nextRoundTime = 0;
        long difference = 0;
        long departures = 0;
        while(true){
            if(departures == 0){
                currTime = 0;
                prevTime = 0;
                difference = 0;
            } else {
                currTime = System.nanoTime();
                difference = currTime - prevTime;
            }
            nextRoundTime = System.nanoTime() + 1000000000;
            double sizeTransmitted = 0;

            this.scheduler.queueLock.lock();
                for(int i = 0; i < scheduler.queues.size(); i++){
                    int sent = 0;
                    while(scheduler.queues.get(i).getSize() != 0 && sent < packetsPerRound[i]){
                        DatagramPacket p = scheduler.queues.get(i).removeFromQueue();
                        try {
                            p.setAddress(InetAddress.getByName(sendAddress));
                            p.setPort(port);
                            socket.send(p);
                            sizeTransmitted += p.getLength() * 8;
                            departures++;
                            sent++;
                            prevTime = currTime;
                            System.out.println("Sent packet, " + p.getLength() + " bytes in size");
                        } catch(Exception ex){

                        }
                    }
                }
            this.scheduler.queueLock.unlock();

            while(System.nanoTime() < nextRoundTime){
                currTime = System.nanoTime();
            }
        }
    }
}
