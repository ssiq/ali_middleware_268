package race.momtest.messageStoreTest;

import com.alibaba.middleware.race.mom.store.queue.MessageBlockQueue;

import java.io.IOException;

/**
 * Created by wlw on 15-8-14.
 */
public class TestMessageBlockQueue {
    public static void main(String[]args) throws IOException {
        String name=TestFilePath.path+"message";
        int fileLength=4096*(1<<10);
        MessageBlockQueue messageBlockQueue=new MessageBlockQueue(name,fileLength,null);
        messageBlockQueue.start();
        int l1=20;
        int l2=30;
        byte[]b1=new byte[l1];
        byte[]b2=new byte[l2];
        for(int i=0;i<l1;++i)b1[i]=(byte)i;
        for(int i=0;i<l2;++i)b2[i]=(byte)(i+2);
        int bb1=messageBlockQueue.add(b1);
        int bb2=messageBlockQueue.add(b2);
        messageBlockQueue.force();
        messageBlockQueue.close();
        messageBlockQueue=new MessageBlockQueue(name,fileLength,null);
        messageBlockQueue.start();
        byte[]res=messageBlockQueue.get(bb1);
        for(int i=0;i<l1;++i)
        {
            if(res[i]!=b1[i]){
                System.out.println("error:1 "+i);
                return;
            }
        }
        res=messageBlockQueue.get(bb2);
        for(int i=0;i<l2;++i)
        {
            if(res[i]!=b2[i]){
                System.out.println("error:1 "+i);
                return;
            }
        }
        messageBlockQueue.remove();
    }
}
