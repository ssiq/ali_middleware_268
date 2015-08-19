package race.momtest.messageStoreTest;

import com.alibaba.middleware.race.mom.store.queue.SendMessageQueue;

import java.io.IOException;

/**
 * Created by wlw on 15-8-13.
 */
public class TestSendMessageQueue {
    public static void main(String[]args) throws IOException, InterruptedException {
        String name=TestFilePath.path+"message";
        int size=4096*4;
        int itemSize=(1<<6);
        SendMessageQueue sendMessageQueue=new SendMessageQueue(itemSize,name,size);
        sendMessageQueue.start();
        byte[] bytes=new byte[50];
        for(int i=0;i<bytes.length;++i)
        {
            bytes[i]=(byte)i;
        }
        System.out.println("bytes1:");
        for(int i=0;i<bytes.length;++i)
        {
            System.out.print(bytes[i]+" ");
        }
        System.out.println();
        sendMessageQueue.add(bytes);
        bytes[0]=22;
        System.out.println("bytes2:");
        for(int i=0;i<bytes.length;++i)
        {
            System.out.print(bytes[i]+" ");
        }
        System.out.println();
        sendMessageQueue.add(bytes);
        sendMessageQueue.force();
        sendMessageQueue.close();
        Thread.sleep(1000);
        sendMessageQueue=new SendMessageQueue(itemSize,name,size);
        sendMessageQueue.start();
        int index=8;
        byte[]bytes1=sendMessageQueue.get(index);
        System.out.println("restart bytes1:");
        boolean error=false;
        for(int i=0;i<bytes.length;++i)
        {
            if(bytes1[i]!=(byte)i)error=true;
            System.out.print(bytes1[i]+" ");
        }
        System.out.println();
        System.out.println("is ok:"+error);
        byte[]bytes2=sendMessageQueue.get(index+itemSize);
        error=false;
        System.out.println("restart bytes2:");
        for(int i=0;i<bytes.length;++i)
        {
            if(bytes2[i]!=bytes[i])error=true;
            System.out.print(bytes2[i]+" ");
        }
        System.out.println();
        System.out.println("is ok:"+error);
        sendMessageQueue.remove();
    }
}
