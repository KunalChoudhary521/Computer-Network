package part3;

import part1.Packet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    public int[] maxPacketSize;

    public WRRScheduler(int inPort,String inAddress,  String outAddress, int outPort, long linkCapacity,
                           int numQueues,
                           int[] maxPacketSize, long[] queueCapacities,
                           long[] queueWeights, long[] avgFlowRates,
                           String recvfileName, String[] sendFileName)
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

            int dropped = 0;

            // receive and process packets
            while (true)
            {
                int max = 0;
                for(int i = 0; i < scheduler.maxPacketSize.length; i++){
                    if(max < scheduler.maxPacketSize[i]){
                        max = scheduler.maxPacketSize[i];
                    }
                }
                byte[] buf = new byte[max];
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
                byte pVal = packet.getData()[0];
                int[] count = { 0, 0, 0};

                if (pVal == (byte) 1)//poisson traffic from source 1
                {
                    this.scheduler.queueLock.lock();
                        result = this.scheduler.queues.get(0).addToQueue(packet);
                    this.scheduler.queueLock.unlock();
                    count[0]++;
                }
                else if (pVal == (byte) 2)//poisson traffic from source 2
                {
                    this.scheduler.queueLock.lock();
                        result = this.scheduler.queues.get(1).addToQueue(packet);
                    this.scheduler.queueLock.unlock();
                    count[1]++;
                } else if (pVal == (byte) 3)//poisson traffic from source 3
                {
                    this.scheduler.queueLock.lock();
                        result = this.scheduler.queues.get(2).addToQueue(packet);
                    this.scheduler.queueLock.unlock();
                    count[2]++;
                }

                if(!result){
                    dropped++;
                    //System.out.println("Receiver: # dropped = "+ dropped);
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

    private String[] fileNames;

    private double[] xi;
    private double x;
    private double[] packetsPerRound;
    private double roundsPerSecond;

    public WRRSender(int port, String address, WRRScheduler scheduler,
                     long[] avgFlowRates, long[] weights, long linkCapacity, String[] fileNames){
        this.port = port;
        this.sendAddress = address;

        this.scheduler = scheduler;
        this.avgRates = avgFlowRates;
        this.weights = weights;
        this.linkCapacity = linkCapacity;

        this.xi = new double[this.scheduler.queues.size()];
        this.packetsPerRound = new double[this.scheduler.queues.size()];
        this.fileNames = fileNames;
        this.roundsPerSecond = 0;
    }

    public void run(){

        PrintStream[] debugOut = new PrintStream[3];
        FileOutputStream[] fout = new FileOutputStream[3];

        try
        {
            for(int i = 0; i < scheduler.queues.size(); i++){
                fout[i] = new FileOutputStream(fileNames[i]);
                debugOut[i] = new PrintStream(fout[i]);
            }
        }
        catch(IOException ex)
        {
            System.out.println("Sender: \n" + ex.getMessage());
        }

        for(int i  = 0; i < scheduler.queues.size(); i++){
            xi[i] = (double)weights[i]/scheduler.maxPacketSize[i];
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
            roundsPerSecond += packetsPerRound[i];
        }

        double[] decimalHandling = new double[packetsPerRound.length];
        for(int i = 0; i < decimalHandling.length; i++){
            decimalHandling[i] = packetsPerRound[i] - (long)packetsPerRound[i];
            if(decimalHandling[i] > 0.0) {
                decimalHandling[i] = Math.ceil(1 / decimalHandling[i]);
            }
            System.out.println("Sender: for queue " + i + " send " + 1  + " extra packet every " + decimalHandling[i]
            + " rounds.");
        }

        long[] roundsSinceLastExtraPacket = { 0, 0, 0};

        roundsPerSecond = linkCapacity / (roundsPerSecond*100*8);

        System.out.println("Sender: rounds per second = " + roundsPerSecond);

        long timeBetweenRounds = (long)((1/roundsPerSecond) * 1000000000);

        System.out.println("Sender: Time between rounds = " + timeBetweenRounds);

        DatagramSocket socket = null;
        //set up socket
        try {
            socket = new DatagramSocket();
        } catch(Exception ex){

        }

        //begin rounds
        long prevTime = 0;
        long difference = 0;
        long currTime = 0;
        long nextRoundTime = 0;
        long departures = 0;

        while(true){
            if(departures == 0){
                currTime = 0;
                prevTime = 0;
                difference = 0;
            } else {
                currTime = System.nanoTime();
                difference = (currTime - prevTime)/1000;
            }
            nextRoundTime = System.nanoTime() + timeBetweenRounds;
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
                            if(sent == 0){
                                debugOut[i].printf("%-15s %-10s %s\n",
                                        departures, difference,//convert: ns -> mu-sec
                                        p.getLength());
                            } else {
                                debugOut[i].printf("%-15s %-10s %s\n",
                                        departures, 0,//convert: ns -> mu-sec
                                        p.getLength());
                            }
                            sent++;
                            prevTime = currTime;
                        } catch(Exception ex){

                        }
                    }
                    if(scheduler.queues.get(i).getSize() != 0 && sent == (long)(packetsPerRound[i])){
                        if(roundsSinceLastExtraPacket[i] >= (long)decimalHandling[i]){
                            if((long)decimalHandling[i] > 0.0) {
                                DatagramPacket p = scheduler.queues.get(i).removeFromQueue();
                                try {
                                    p.setAddress(InetAddress.getByName(sendAddress));
                                    p.setPort(port);
                                    socket.send(p);
                                    sizeTransmitted += p.getLength() * 8;
                                    departures++;
                                    sent++;
                                    prevTime = currTime;
                                    debugOut[i].printf("%-7s %-10s %s\n",
                                            departures, 0,//convert: ns -> mu-sec
                                            p.getLength());
                                } catch (Exception ex) {

                                }
                                roundsSinceLastExtraPacket[i] = 0;
                            }
                        }
                    }
                }
            this.scheduler.queueLock.unlock();

            while(System.nanoTime() < nextRoundTime){
                currTime = System.nanoTime();
            }
            roundsSinceLastExtraPacket[0]++;roundsSinceLastExtraPacket[1]++;roundsSinceLastExtraPacket[2]++;
        }
    }
}
