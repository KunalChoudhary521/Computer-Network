package part1;

import part1.PacketScheduler.PacketScheduler;

public class FIFOSch1p4
{
    public static void main(String[] args)
    {
        //Exercise 1.2: Implement a FIFO Scheduler
        PacketScheduler ps = new PacketScheduler(5000,"localhost", 5001,
                1000000,      //1 Mbps
                2,              //1 FIFO buffer
                2024,        //2 kBytes
                new long [] {100*1024, 100*1024},  //100kB bufferCapacity
                "ps_ex1-4.txt");


        new Thread(ps).start();// start packet scheduler
    }
}