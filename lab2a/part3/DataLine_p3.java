package part3;


import java.net.DatagramPacket;

public class DataLine_p3
{
    public int SeqNo;
    public long delayInNs;
    public int packetSize;
    public DatagramPacket udpPacket;

    public DataLine_p3(int sq, int delay, int pksize, DatagramPacket sk)
    {
        this.SeqNo = sq;//for debugging
        this.delayInNs = delay;
        this.packetSize = pksize;//for debugging
        this.udpPacket = sk;
    }

}
