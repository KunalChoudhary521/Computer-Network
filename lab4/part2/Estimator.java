package part2;

import java.io.FileOutputStream;
import java.io.PrintStream;

/**
    Usage:
    java Estimator [blackboxIP] [blackBoxPort] [N] [L] [r]
*/

public class Estimator
{

    public static void main(String[] args)
    {
        String blackBoxIP, fileOutput = "TimeStamps.txt";
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

            Thread recvThread = new Receiver(tsKeeper, receiverPort, 2048,fileOutput);
            recvThread.start();

            Thread sendThread = new Sender(tsKeeper,blackBoxIP,blackBoxPort,receiverPort,
                    numPackets,packetTrainSize,rate);//sender thread
            sendThread.start();

            sendThread.join();
            recvThread.join();



            //Normalized Timestamps to the Send Timestamp of 1st packet, whose timestamp should be zero.
            System.out.println("Normalizing Timestamps");

            PrintStream normPacket = new PrintStream(new FileOutputStream(fileOutput));
            for(int i = 0; i < tsKeeper.length; i++)
            {
                normPacket.printf("%-7s %-10s %s\n",
                        tsKeeper[i].seqNo, tsKeeper[i].sendTime - tsKeeper[0].sendTime,
                        tsKeeper[i].recvTime - tsKeeper[0].sendTime);
            }

            System.out.println("Sender & Receiver threads finished");

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
