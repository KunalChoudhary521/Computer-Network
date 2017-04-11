package part2;

public class Timestamp
{
    public int seqNo;
    public long sendTime, recvTime;
    public static long startTime;//1 start time for all instances

    public void setRecvTime(int sqNum, long rcvTime)
    {
        this.seqNo = sqNum;
        this.recvTime = rcvTime;
    }
    public void setSendTime(int sqNum, long sndTime)
    {
        this.seqNo = sqNum;
        this.sendTime = sndTime;
    }
    public static void setStartTime()//one global start time for sender & receiver
    {
        //System.out.println("Start Time has been set");
        startTime = (System.nanoTime() / 1000);//in microseconds
    }
    public static long getStartTime()
    {
        return startTime;
    }
}
