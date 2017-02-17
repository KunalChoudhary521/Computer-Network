import TokenBucket.TokenBucket;

public class PoissonMain
{
    public static void main (String[] args)
    {
        TokenBucket posbucket = new TokenBucket(50000, "localhost", 50001, 1500,
                100*1500, 10000, 5000, "bucketPos.txt");

        new Thread (posbucket).start();
    }
}
