package part2;

import PacketScheduler.PacketScheduler;

public class PrioritySch
{
    public static void main(String[] args)
    {
        //Exercise 2.2: Packet classification and priority scheduling
        PacketScheduler ps = new PacketScheduler(5000,"localhost", 5001,
                20000000,      //20 Mbps
                2,              //2 FIFO buffer
                1500,        //in bytes (from 2.1)
                new long [] {100*1024, 100*1024},  //100kB bufferCapacity
                "ps.txt");


        new Thread(ps).start();// start packet scheduler
    }
}
