import TokenBucket.TokenBucket;

public class VideoMain
{
    public static void main (String[] args)
    {
        TokenBucket vidbucket = new TokenBucket(50000, "localhost", 50001, 65507,
                100*65507, 85000, 5000, "bucketVid.txt");

        new Thread (vidbucket).start();
    }
}
