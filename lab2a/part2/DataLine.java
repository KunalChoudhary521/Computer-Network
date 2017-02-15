//package part2;


import java.net.DatagramPacket;

public class DataLine
{
    public int SeqNo;
    public long delayInNs;
    public int packetSize;
    public DatagramPacket udpPacket;

    public DataLine(int sq, int delay, int pksize, DatagramPacket sk)
    {
        this.SeqNo = sq;//for debugging
        this.delayInNs = delay;
        this.packetSize = pksize;//for debugging
        this.udpPacket = sk;
    }

}
