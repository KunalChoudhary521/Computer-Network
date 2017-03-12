package part2;

import TokenBucket.TokenBucket;
import part1.ReferenceTrafficGenerator;
import part1.ReferenceTrafficSink;

/**
 * Created by rahmanz5 on 2/20/2017.
 */
public class Part2 {

    public static void main(String args[]){


        // 2-2-1
        //TokenBucket bucket = new TokenBucket(8001, "localhost",8002, 100, 100*100, 100, 500000,"2-2-1-bucket.txt");
        // 2-2-2
        //TokenBucket bucket = new TokenBucket(8001, "localhost",8002, 100, 10*100, 500, 500000,"2-2-2-bucket.txt");
        // 2-2-3 (2-2-4)
        //TokenBucket bucket = new TokenBucket(8001, "localhost", 8002, 100, 100*100, 1000, 625000, "2-2-4-bucket.txt");

        int bs = 400;
        int tr = 1800000;

        // 3-1-1 poisson - burst size of 1500, max frame size of 100, rate of 1.2 Mbps (150000 tokens/s)
        //TokenBucket bucket = new TokenBucket(8001, "localhost", 8002, 100, 100*100, bs, tr, "3-poisson-bucket.txt");

        // 3-1-2 ethernet - burst size
        //TokenBucket bucket = new TokenBucket(8001, "localhost", 8002, 100, 100*100, bs, tr, "3-ethernet-bucket.txt");

        // 3-1-3 video  traffic, bs 400, token rate of 14.4Mbps (1 800 000 tokens/sec)
        TokenBucket bucket = new TokenBucket(8001, "localhost", 8002, 100, 100*100, bs, tr, "3-movie-bucket.txt");

        System.out.println("Bucket Size: " + bs + " bytes");
        System.out.println("Byte Rate: " + (double)((tr)*8)/1000000 + " Mbps");

        new Thread(bucket).start();

    }
}
