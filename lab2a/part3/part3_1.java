package part3;

import TokenBucket.TokenBucket;

/**
 * Created by Family on 2/12/2017.
 */
public class part3_1 {

    public static void main(String args[]){
        TokenBucket bucket = new TokenBucket(50000, "localhost", 50001, 1024,
                100*1024, 10000, 5000, "bucket.txt");

        new Thread (bucket).start();
    }
}
