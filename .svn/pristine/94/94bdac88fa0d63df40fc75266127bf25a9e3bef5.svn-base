package com.alibaba.middleware.race.mom.store.queue;

import com.alibaba.middleware.race.mom.broker.DefaultGroupChannel;
import com.alibaba.middleware.race.mom.broker.GroupChannel;
import com.alibaba.middleware.race.mom.broker.GroupChannelManager;
import com.alibaba.middleware.race.mom.message.TopicManager;
import com.alibaba.middleware.race.mom.serializer.Serializer;
import com.alibaba.middleware.race.mom.store.util.StringSerilizer;
import io.protostuff.runtime.RuntimeSchema;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by wlw on 15-8-9.
 *
 * 记录包括福永的4个字节的flag,16字节的groupId,16字节的topic,32字节的filter,8字节的time
 */
public class SubscriptQueue extends FixedItemSizeQueue{

    private TopicManager<GroupChannel> channelTopicManager;
    private FileChannel fileChannel;
    private boolean newFile=false;
    private GroupChannelManager groupChannelManager=GroupChannelManager.getInstance();
    private static int groupIdOffSet=0;
    private static int groupIdLength=16;
    private static int topicOffset=groupIdOffSet+groupIdLength;
    private static int topicLength=16;
    private static int filterOffset=topicOffset+topicLength;
    private static int filterLength=16;
    private static int timeOffset=filterOffset+filterLength;
    private static int timeLength=8;
    private static int oneLength=timeLength+timeOffset+manageLength;

    private class SubscriptMessage{
        private String groupId;
        private String topic;
        private String filter;
        private long time;

        public SubscriptMessage(String groupId, String topic, String filter, long time) {
            this.groupId = groupId;
            this.topic = topic;
            this.filter = filter;
            this.time = time;
        }

        public SubscriptMessage() {
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public String getFilter() {
            return filter;
        }

        public void setFilter(String filter) {
            this.filter = filter;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        @Override
        public String toString() {
            return "SubscriptMessage{" +
                    "groupId='" + groupId + '\'' +
                    ", topic='" + topic + '\'' +
                    ", filter='" + filter + '\'' +
                    ", time=" + time +
                    '}';
        }
    }

    private StringSerilizer stringSerilizer=new StringSerilizer();

    public SubscriptQueue(String filename,int fileLength,TopicManager<GroupChannel> channelTopicManager) {
        super(oneLength);
        forceLevel=0;
        fullOffSet=50;
        this.channelTopicManager=channelTopicManager;
        this.fileLength=fileLength;
        this.fileName=filename;
    }

    private SubscriptMessage getMessage(int index)
    {
        byte[] bytes=get(index);
        if(bytes==null)return null;
        String groupId=stringSerilizer.decode(bytes, groupIdOffSet, groupIdLength);
        String topic=stringSerilizer.decode(bytes, topicOffset, topicLength);
        String filter=stringSerilizer.decode(bytes,filterOffset,filterLength);
        ByteBuffer byteBuffer=ByteBuffer.wrap(bytes);
        long time=byteBuffer.getLong(timeOffset);
        return new SubscriptMessage(groupId,topic,filter,time);
    }

    public void start() throws IOException
    {
        File file=new File(this.fileName);
        boolean isExit=file.exists();
        randomAccessFile=new RandomAccessFile(fileName,"rw");
        if(!isExit)
        {
            newFile=true;
            randomAccessFile.setLength(fileLength);
        }
        fileChannel=randomAccessFile.getChannel();
        mappedByteBuffer=fileChannel.map(FileChannel.MapMode.READ_WRITE,0,fileChannel.size());
        this.fileLength=(int)fileChannel.size();
        if(!newFile)
        {
            ByteBuffer byteBuffer=slice();
            byteBuffer.position(0);
            initHeader();
            int index=initialBegin;
            while (index<end.get())
            {
                SubscriptMessage subscriptMessage=getMessage(index);
                if(subscriptMessage!=null)
                {
                    String groupId=subscriptMessage.getGroupId();
                    groupChannelManager.addGroupChannel(groupId,new DefaultGroupChannel(groupId));
                    channelTopicManager.addTopic(subscriptMessage.getTopic(), subscriptMessage.getFilter(),
                            groupChannelManager.getChannelGroup(subscriptMessage.getGroupId()));
                    System.out.println("load one subscript message:"+subscriptMessage.toString());
                }
                index+=getItemSize();
            }
        }
        haveStarted();
    }

    public int addSubscipt(String groupId,String topic,String filter,long time)
    {
        byte[] bytes=new byte[getRealItemSize()];
        stringSerilizer.encode(groupId,bytes,groupIdOffSet,groupIdLength);
        stringSerilizer.encode(topic,bytes,topicOffset,topicLength);
        stringSerilizer.encode(filter, bytes, filterOffset, filterLength);
        ByteBuffer byteBuffer=ByteBuffer.wrap(bytes);
        byteBuffer.putLong(timeOffset,time);
        return add(bytes);
    }

    private static class SubscriptQueueFactory implements QueueFactory
    {

        private int fileLength;
        private TopicManager<GroupChannel> channelTopicManager;

        public SubscriptQueueFactory(int fileLength, TopicManager<GroupChannel> channelTopicManager) {
            this.fileLength = fileLength;
            this.channelTopicManager = channelTopicManager;
        }

        @Override
        public Object getNewQueue(String name) {
            return new SubscriptQueue(name,fileLength,channelTopicManager);
        }
    }

    public static QueueFactory getFactory(int fileLength,TopicManager<GroupChannel> channelTopicManager)
    {
        return new SubscriptQueueFactory(fileLength,channelTopicManager);
    }
}
