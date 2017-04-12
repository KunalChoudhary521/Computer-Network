package part1;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

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
        sendUdpPacket();
    }

    public void sendUdpPacket()
    {
        int pktsize = this.packetTrainSize/this.numPackets;//in bytes
        long delayInMus = ((pktsize * 1_000_000) / ((this.rate * 1000)/8)) ;//micro-sec

        System.out.println(pktsize + " Bytes per packet\n" +
                delayInMus + " micro-sec transmission time between packets\n" +
                this.numPackets + " packets to send" );

        long currTime = 0, sendTime;

        byte[] payload, portByteArray, seqNumByteArray;
        InetAddress bBoxIP;

        DatagramSocket sendSocket;

        int seqNum;

        try
        {
            bBoxIP = InetAddress.getByName(this.blackBoxIP);

            Timestamp.setStartTime();

            for (seqNum = 1; seqNum <= this.numPackets; seqNum++)
            {
                sendSocket = new DatagramSocket();
                payload = new byte[pktsize + 4 + 4];//+4 for seqNo & receiverPort

                portByteArray = toByteArray(this.receiverPort);
                System.arraycopy(portByteArray, 2, payload, 0, 2);//set receiverPort

                seqNumByteArray = toByteArray(seqNum);
                System.arraycopy(seqNumByteArray, 0, payload, 2, seqNumByteArray.length);//set seqNum


                DatagramPacket packet = new DatagramPacket(payload, payload.length, bBoxIP, this.blackBoxPort);


                //record sendTime
                sendTime = (System.nanoTime() / 1000) + delayInMus;
                do {
                    currTime = System.nanoTime() / 1000;//micro-sec
                } while (currTime < sendTime);//busy wait to create delay

                this.sndTS[seqNum - 1].setSendTime(seqNum,
                        currTime - Timestamp.getStartTime());//(seqNum-1) => seqNum start from 1


                sendSocket.send(packet);
                sendSocket.close();
            }

            System.out.println(seqNum-1 + " packets sent to BlackBox");
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
