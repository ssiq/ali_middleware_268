package race.momtest.messageStoreTest;

import com.alibaba.middleware.race.mom.util.NameCreater;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by wlw on 15-8-18.
 */
public class TestDifferentFileWrite {
    public static void main(String[]args)
    {
        String path=TestFilePath.path;
        int size=1000;
        long length=4096*3;
        RandomAccessFile[] randomAccessFiles=new RandomAccessFile[size];
        for(int i=0;i<size;++i)
        {
            try {
                randomAccessFiles[i]=new RandomAccessFile(path+NameCreater.newName(),"rw");
                randomAccessFiles[i].setLength(length);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        byte[] bytes=new byte[(int)length];
        for(int i=0;i<length;++i)
        {
            bytes[i]=(byte)i;
        }
        long t=0;
        for(int i=0;i<size;++i)
        {
            try {
                MappedByteBuffer mappedByteBuffer=randomAccessFiles[i].getChannel().map(FileChannel.MapMode.READ_WRITE,0,length);
                mappedByteBuffer.position(0);
                mappedByteBuffer.put(bytes);
                long bt=System.currentTimeMillis();
                mappedByteBuffer.force();
                t+=System.currentTimeMillis()-bt;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("ave use time:"+((double)t)/size);
    }
}
