
/**
    Usage:
    java Estimator [blackboxIP] [bloackBoxPort] [N] [L] [r]
*/

public class Estimator
{

    public static void main(String[] args)
    {
        String blackBoxIP, fileOutput = "Estimator.txt";
        int blackBoxPort, receiverPort;
        int numPackets, packetTrainSize, rate;

        try
        {
            blackBoxIP = args[0];
            blackBoxPort = Integer.parseInt(args[1]);
            numPackets = Integer.parseInt(args[2]);//N
            packetTrainSize = Integer.parseInt(args[3]);//L
            rate = Integer.parseInt(args[4]);//r

            Timestamp[] tsKeeper = new Timestamp[numPackets];
            for (int i = 0; i < tsKeeper.length; i++)
            {
                tsKeeper[i] = new Timestamp();
            }

            receiverPort = 5001;//BlackBox sends packets to this port

            new Receiver(tsKeeper, receiverPort, 2048).start();

            new Sender(tsKeeper,blackBoxIP,blackBoxPort,receiverPort,
                    numPackets,packetTrainSize,rate).start();//sender thread

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
