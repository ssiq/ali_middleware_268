package race.momtest.messageStoreTest;

import com.alibaba.middleware.race.mom.Message;
import com.alibaba.middleware.race.mom.util.NameCreater;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Random;

/**
 * Created by wlw on 15-8-16.
 *
 * use write empty page:
 * System.currentTimeMillis use time:0.0
 * pre alloc file use time:5.08
 * file channel write with no metadata time:81.58
 * file delete time:0.03
 *
 * USE SET LENGTH :
 * System.currentTimeMillis use time:0.0
 * pre alloc file use time:0.16
 * file channel write with no metadata time:35.53
 * file delete time:0.02
 */
public class TestBufferedChannel {

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

    public static void main(String[]args) throws IOException {
        String path=TestFilePath.path;

        int pageSize=4096;
        int times=100;
        String[] names=new String[times];
        byte[]emptyByte=new byte[pageSize];
        long tc=0;


        for(int i=0;i<times;++i)
        {
            long ti=System.currentTimeMillis();
            tc+=System.currentTimeMillis()-ti;
        }
        System.out.println("System.currentTimeMillis use time:"+((double)tc/times));

        tc=0;
        for(int i=0;i<times;++i)
        {
            String name=path+NameCreater.newName();
            long ti=System.currentTimeMillis();
            RandomAccessFile randomAccessFile=new RandomAccessFile(name,"rw");
//            randomAccessFile.write(emptyByte);
            randomAccessFile.setLength(pageSize);

            tc+=System.currentTimeMillis()-ti;
            randomAccessFile.write(emptyByte);
            randomAccessFile.close();
            names[i]=name;
        }
        System.out.println("pre alloc file use time:"+((double)tc/times));

        byte[] putByte=new byte[pageSize];
        for(int i=0;i<pageSize;++i)
        {
            putByte[i]=(byte)i;
        }

        tc=0;
        ByteBuffer byteBuffer=ByteBuffer.allocate(pageSize);
        for(int i=0;i<times;++i)
        {
            FileChannel fileChannel=new RandomAccessFile(names[i],"rw").getChannel();
            long ti=System.currentTimeMillis();
            byteBuffer.put(putByte);
            byteBuffer.flip();
            fileChannel.write(byteBuffer);
            fileChannel.force(false);
            tc+=System.currentTimeMillis()-ti;
            byteBuffer.clear();
            fileChannel.close();
        }
        System.out.println("file channel write with no metadata time:"+((double)tc/times));

        tc=0;
        for(int i=0;i<times;++i)
        {
            long ti=System.currentTimeMillis();
            File file=new File(names[i]);
            file.delete();
            tc+=System.currentTimeMillis()-ti;
        }
        System.out.println("file delete time:"+((double)tc/times));
    }
}
