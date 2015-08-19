package race.momtest.messageStoreTest;

import com.alibaba.middleware.race.mom.Message;
import com.alibaba.middleware.race.mom.serializer.Serializer;
import com.alibaba.middleware.race.mom.store.queue.MessageBlockQueue;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.nio.charset.Charset;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by wlw on 15-8-16.
 *
 * the result is:
 * encode time:0.00665
 * cache encode use time:1.4397164886819758E12
 * map encode use time:9.0E-4
 *
 * map much faster the the other two
 */
public class TestCacheAndSerilizer {

    private static String BODY="hello mom ";
    private static Charset charset=Charset.forName("utf-8");
    private static Random random=new Random();
    private static Message newMessage()
    {
        Message msg=new Message();
        int code=random.nextInt(100000);
        msg.setBody(BODY.getBytes(charset));
        msg.setProperty("area", "hz" + code);
        return msg;
    }

    public static void main(String[]args)
    {
        final Serializer serializer=Serializer.getMessageSerialier();
        int times=1000;
        long total=0;
        Message[] list=new Message[times];

        for(int i=0;i<times;++i)
        {
            list[i]=newMessage();
        }

        int cycle=20;

        for(int j=0;j<cycle;++j)
        {
            for(int i=0;i<times;++i)
            {
                long ti=System.currentTimeMillis();
                byte[]bytes=serializer.encode(list[i]);
                total+=System.currentTimeMillis()-ti;
            }
        }
        System.out.println("encode time:" + ((double) total / (times*cycle)));

        LoadingCache<Message,byte[]> cache=CacheBuilder.newBuilder()
                .maximumSize(1000)
                .build(new CacheLoader<Message, byte[]>() {

                    @Override
                    public byte[] load(Message key) throws Exception {
                        return serializer.encode(key);
                    }
                });

        long cacheTotal=0;
        for(int j=0;j<cycle;++j)
        {
            for(int i=0;i<times;++i)
            {
                long ti=0;
                byte[] bytes=cache.getUnchecked(list[i]);
                cacheTotal+=(System.currentTimeMillis()-ti);
            }
        }

        System.out.println("cache encode use time:"+(((double)cacheTotal)/(cycle*times)));

        ConcurrentMap<Message,byte[]> map=new ConcurrentHashMap<>();
        long mapTotal=0;
        for(int j=0;j<cycle;++j)
        {
            for(int i=0;i<times;++i)
            {
                long ti=System.currentTimeMillis();
                Message message=list[i];
                byte[]bytes=map.get(message);
                if(bytes==null)
                {
                    map.putIfAbsent(message,serializer.encode(message));
                }
                mapTotal+=System.currentTimeMillis()-ti;
            }
        }

        for(int i=0;i<times;++i)
        {
            long ti=System.currentTimeMillis();
            map.remove(list[i]);
            mapTotal+=System.currentTimeMillis()-ti;
        }
        System.out.println("map encode use time:"+(((double)mapTotal)/(cycle*times)));
    }
}
