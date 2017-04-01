
public class Timestamp
{
    public int seqNo;
    public long sendTime, recvTime;

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
}
