package part3;

/**
 * Created by rahmanz5 on 3/28/2017.
 */
public class part3 {

    public static void main(String[] args){
        WRRScheduler scheduler = new WRRScheduler(8000, "localhost", "localhost", 8001,
                1000000, 3, 1518, new long[] {100000, 100000, 100000},
                new long[] { 1, 1, 1}, new long[] { 8000000, 6000000, 2000000},
                "WRR-test-receiver.txt","WRR-test-sender.txt");

        TGenPosCustom ts1 = new TGenPosCustom("poisson.data", "localhost", 8000, 8, "ts1-3-2.txt");
        TGenPosCustom ts2 = new TGenPosCustom("poisson.data", "localhost", 8000, 6, "ts2-3-2.txt");
        TGenPosCustom ts3 = new TGenPosCustom("poisson.data", "localhost", 8000, 2, "ts3-3-2.txt");
    }
}
