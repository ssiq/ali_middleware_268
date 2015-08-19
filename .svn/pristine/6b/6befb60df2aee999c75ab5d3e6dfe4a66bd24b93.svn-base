package race.momtest.messageStoreTest;

import com.alibaba.middleware.race.mom.model.NotSendMessagePair;
import com.alibaba.middleware.race.mom.store.queue.NotSendQueue;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by wlw on 15-8-13.
 */
public class TestNotSendQueue {
    public static void main(String[]args) throws IOException, InterruptedException {
        String name=TestFilePath.path+"notsend";
        int fileLength=4096*4;
        ConcurrentLinkedQueue<NotSendMessagePair> concurrentLinkedQueue=new ConcurrentLinkedQueue<>();
        NotSendQueue notSendQueue=new NotSendQueue(name,fileLength,concurrentLinkedQueue);
        notSendQueue.start();
        NotSendMessagePair notSendMessagePair1=notSendQueue.addNotSend("1","a");
        NotSendMessagePair notSendMessagePair2=notSendQueue.addNotSend("2","b");
        System.out.println("the original result:");
        System.out.println(notSendMessagePair1.toString());
        System.out.println(notSendMessagePair2.toString());
        notSendQueue.force();
        notSendQueue.close();
        System.out.println("the linked queue");
        while (true)
        {
            NotSendMessagePair notSendMessagePair=concurrentLinkedQueue.poll();
            if(notSendMessagePair==null)break;
            System.out.println(notSendMessagePair.toString());
        }
        Thread.sleep(1000);
        concurrentLinkedQueue=new ConcurrentLinkedQueue<>();
        notSendQueue=new NotSendQueue(name,fileLength,concurrentLinkedQueue);
        notSendQueue.start();
        System.out.println("restart the linked queue");
        while (true)
        {
            NotSendMessagePair notSendMessagePair=concurrentLinkedQueue.poll();
            if(notSendMessagePair==null)break;
            System.out.println(notSendMessagePair.toString());
        }
        notSendQueue.remove();
    }
}
