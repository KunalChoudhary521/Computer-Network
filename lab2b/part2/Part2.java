package part2;

import TokenBucket.TokenBucket;
import part1.ReferenceTrafficGenerator;
import part1.ReferenceTrafficSink;

/**
 * Created by Haashir on 2/20/2017.
 */
public class Part2 {

    public static void main(String args[]){

        int bucketSize = 100;
        int tokenRate = 625000;
        int maxPacketSize = 100;
        int bufferCapacity = tokenRate*10;
        String fileName = "bucket-2-2-1.data";

        TokenBucket bucket = new TokenBucket(8001, "localhost",
                8002, maxPacketSize, bufferCapacity, bucketSize,tokenRate,
                fileName);

        System.out.println("Bucket Size: " + bucketSize + " bytes");
        System.out.println("Byte Rate: " + (double)((tokenRate)*8)/1000000 + " Mbps");

        new Thread(bucket).start();

    }
}
