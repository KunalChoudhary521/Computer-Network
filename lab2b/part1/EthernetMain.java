import TokenBucket.TokenBucket;

public class EthernetMain
{
    public static void main (String[] args)
    {
        TokenBucket bucket = new TokenBucket(50000, "localhost", 50001, 1520,
                100*1520, 10000, 5000, "bucketEthernet.txt");

        new Thread (bucket).start();
    }


}
