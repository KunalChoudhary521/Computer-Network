package part3;

import javax.xml.crypto.Data;
import java.net.DatagramPacket;
import java.util.ArrayList;

/**
 * Created by rahmanz5 on 3/28/2017.
 */
public class Queue {
    private ArrayList<DatagramPacket> queue;
    private long capacity;
    private long currSize;
    private long weight;

    public Queue(long capacity, long weight){
        queue = new ArrayList<DatagramPacket>();
        this.capacity = capacity;
        this.weight = weight;
        currSize = 0;
    }

    //FIFO queue means that packets are always added to the end of an ArrayList
    public boolean addToQueue(DatagramPacket p){
        if(currSize + p.getLength() < capacity){
            queue.add(queue.size(), p);
            currSize += p.getLength();
            return true;
        }
        return false;
    }

    //FIFO queue means that packets should be removed from the front of the ArrayList
    public DatagramPacket removeFromQueue(){
        DatagramPacket p = queue.get(0);
        currSize -= p.getLength();
        queue.remove(0);
        return p;
    }

    public long getSize(){
        return currSize;
    }
}
