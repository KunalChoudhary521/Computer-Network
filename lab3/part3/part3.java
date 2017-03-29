package part3;

/**
 * Created by rahmanz5 on 3/28/2017.
 */
public class part3 {

    public static void main(String[] args) {

        //Exercise 3-2
        WRRScheduler scheduler = new WRRScheduler(8000, "localhost",
                "localhost", 8001,
                10000000, 3, new int[] {100, 100, 100}, new long[] {100000, 100000, 100000},
                new long[] { 1, 1, 1}, new long[] { 8000000, 6000000, 2000000},
                "WRR-receiver-3-2.txt",new String[] {
                "WRR-sender1-3-2.txt","WRR-sender2-3-2.txt", "WRR-sender3-3-2.txt"});

        TGenPosCustom ts1 = new TGenPosCustom("poisson3.data", "localhost",
                8000, 8, "ts1-3-2.txt", 1);
        TGenPosCustom ts2 = new TGenPosCustom("poisson3.data", "localhost",
                8000, 6, "ts2-3-2.txt", 2);
        TGenPosCustom ts3 = new TGenPosCustom("poisson3.data", "localhost",
                8000, 2, "ts3-3-2.txt", 3);

        new Thread(ts1).start();
        new Thread(ts2).start();
        new Thread(ts3).start();

        //Exercise 3-3
        /*WRRScheduler scheduler = new WRRScheduler(8000, "localhost",
                "localhost", 8001,
                10000000, 3, new int[] {100, 100, 100}, new long[] {100000, 100000, 100000},
                new long[] { 3, 1, 1}, new long[] { 8000000, 6000000, 2000000},
                "WRR-receiver-3-3.txt",new String[] {
                "WRR-sender1-3-3.txt","WRR-sender2-3-3.txt", "WRR-sender3-3-3.txt"});

        TGenPosCustom ts1 = new TGenPosCustom("poisson3.data", "localhost",
                8000, 8, "ts1-3-3.txt", 1);
        TGenPosCustom ts2 = new TGenPosCustom("poisson3.data", "localhost",
                8000, 6, "ts2-3-3.txt", 2);
        TGenPosCustom ts3 = new TGenPosCustom("poisson3.data", "localhost",
                8000, 2, "ts3-3-3.txt", 3);

        new Thread(ts1).start();
        new Thread(ts2).start();
        new Thread(ts3).start();*/
    }
}
