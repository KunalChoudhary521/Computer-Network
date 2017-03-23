package part1;


import java.net.DatagramPacket;

public class Packet
{
    public int SeqNo;
    public long delayInNs;
    public int packetSize;
    public DatagramPacket udpPacket;

    public Packet(int sq, long delay, int pksize, DatagramPacket sk)
    {
        this.SeqNo = sq;//for debugging
        this.delayInNs = delay;//delay needed according to trace file
        this.packetSize = pksize;//for debugging
        this.udpPacket = sk;
    }

}
