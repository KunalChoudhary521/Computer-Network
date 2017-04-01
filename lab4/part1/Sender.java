import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.StringTokenizer;

/**
    Sender (not the Estimator) decides the # of packets,
    bytes per packet and rate at which those packets will be sent.

    SeqNum is b/w 0 & 125k, so only (17 -> 24 bits) are needed to
    store it after receiverPort

    Assumption: receiver port number can fit in 2 bytes(16 bits)
*/

public class Sender extends Thread
{
    public String blackBoxIP;
    public int blackBoxPort, receiverPort;
    public int numPackets, packetTrainSize, rate;
    public Timestamp[] sndTS;

    public Sender(Timestamp[] ts ,String ipAddr, int port, int recvPort, int N, int L, int r)
    {
        this.sndTS = ts;
        this.blackBoxIP = ipAddr;
        this.blackBoxPort = port;
        this.receiverPort = recvPort;
        this.numPackets = N;
        this.packetTrainSize = L;//bytes
        this.rate = r;//kbps
    }

    public void run()
    {
        readPackets();
    }

    public void readPackets()
    {
        int seqNo;
        try
        {
            int pktsize = this.packetTrainSize/this.numPackets;//in bytes
            long delayInMus = (pktsize * 1_000_000) / ((this.rate * 1000)/8) ;//micro-sec

            System.out.println(pktsize + " Bytes per packet\n" +
                    delayInMus + " micro-sec transmission time between packets\n" +
                               this.numPackets + " packets to send" );

            for (seqNo = 1; seqNo <= this.numPackets; seqNo++)
            {
                sendUdpPacket(seqNo,pktsize,delayInMus);
            }

            System.out.println(seqNo-1 + " packets sent to BlackBox");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }
    public void sendUdpPacket(int seqNum, int packetSize, long delay)
    {
        long sendTime, currTime;

        try
        {
            byte[] payload = new byte[packetSize+4+4];//+4 for seqNo & receiverPort

            byte[] portByteArray = toByteArray(this.receiverPort);
            System.arraycopy(portByteArray,2,payload,0,2);//set receiverPort

            byte[] seqNumByteArray = toByteArray(seqNum);
            System.arraycopy(seqNumByteArray,0,payload,2,seqNumByteArray.length);//set seqNum

            DatagramSocket sendSocket = new DatagramSocket();
            InetAddress bBoxIP = InetAddress.getByName(this.blackBoxIP);
            DatagramPacket packet = new DatagramPacket(payload,payload.length,bBoxIP, this.blackBoxPort);


            //record sendTime

            sendTime = (System.nanoTime()/1000) + delay;
            if(seqNum == 1)
            {
                this.sndTS[seqNum-1].setSendTime(seqNum,sendTime);//(seqNum-1) => seqNum start from 1
            }
            else
            {
                this.sndTS[seqNum-1].setSendTime(seqNum,sendTime);//(seqNum-1) => seqNum start from 1
            }


            do
            {
                currTime = System.nanoTime()/1000;//micro-sec
            }while (currTime < sendTime);//busy wait to create fake delay

            sendSocket.send(packet);
            sendSocket.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Converts an integer to a byte array.
     * @param value an integer
     * @return a byte array representing the integer
     */
    public byte[] toByteArray(int value)
    {
        byte[] Result = new byte[4];
        Result[3] = (byte) ((value >>> (8*0)) & 0xFF);
        Result[2] = (byte) ((value >>> (8*1)) & 0xFF);
        Result[1] = (byte) ((value >>> (8*2)) & 0xFF);
        Result[0] = (byte) ((value >>> (8*3)) & 0xFF);
        return Result;
    }
}
