package race.momtest.messageStoreTest;

import com.alibaba.middleware.race.mom.Message;
import com.alibaba.middleware.race.mom.model.IndexMessage;
import com.alibaba.middleware.race.mom.serializer.Serializer;
import com.alibaba.middleware.race.mom.store.queue.MessageBlockQueue;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.concurrent.*;

/**
 * Created by wlw on 15-8-16.
 */
public class TestMessageBlockStress {

    static int size=4096*100;

    private static MessageBlockQueue newQueue(String name)
    {
        return null;
    }

    public static void main(String[]args) throws IOException, InterruptedException {
        String BODY="hello mom ";
        Message msg=new Message();
        Charset charset=Charset.forName("utf-8");
        Random random=new Random();
        int code=random.nextInt(100000);
        msg.setBody(BODY.getBytes(charset));
        msg.setProperty("area", "hz" + code);
        Serializer serializer=Serializer.getMessageSerialier();
        final byte[]bytes=serializer.encode(msg);
        System.out.println("the message byte is:"+bytes.length);
        final String name=TestFilePath.path+"messagefile";
        final ConcurrentMap<String,IndexMessage> map=new ConcurrentHashMap<>();
        final MessageBlockQueue messageBlockQueue=new MessageBlockQueue(name,size,map);
        messageBlockQueue.start();
        //one thread is use time 33ms
//        for(int i=0;i<10000;++i)
//        {
//            long time=System.currentTimeMillis();
//            messageBlockQueue.add(bytes);
//            messageBlockQueue.testForce();
//            System.out.println("i use time:"+(System.currentTimeMillis()-time));
//        }

        final int tnum=30;
        ExecutorService executorService= Executors.newFixedThreadPool(tnum);
        long beginTime=System.currentTimeMillis();
        for(int j=0;j<tnum;++j)
        {
            final int finalJ = j;
            executorService.submit(new Runnable() {
                @Override
                public void run() {
//                    MessageBlockQueue m=new MessageBlockQueue(name+finalJ,size,map);
//                    try {
//                        m.start();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    long inb=System.currentTimeMillis();
                    for(int i=0;i<1000;++i)
                    {
                        long time=System.currentTimeMillis();
                        messageBlockQueue.add(bytes);
                        messageBlockQueue.force();
//                        messageBlockQueue.testForce();
//                        System.out.println("thread "+ finalJ +" i:"+i+" use time:"+(System.currentTimeMillis()-time));
                    }
                    System.out.println("thread "+finalJ+" use time:"+(System.currentTimeMillis()-inb));
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(10000, TimeUnit.SECONDS);
        System.out.println("use time:"+(System.currentTimeMillis()-beginTime));
        //test force:22872
        //force 8:24275
        //force 30:24179
    }
}
