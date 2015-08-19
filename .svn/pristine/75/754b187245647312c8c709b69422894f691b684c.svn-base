package com.alibaba.middleware.race.mom.model;

import com.alibaba.middleware.race.mom.serializer.Serializer;
import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by wlw on 15-8-2.
 */
public class TransmittingMessage {
    private transient static AtomicInteger RequestId = new AtomicInteger(0);
    private transient static int REQUEST_INDEX=0;
    private transient static int RESPONSE_INDEX=1;
    private transient static int ONE_WAY=2;
    private int action;
    private int flag=0;
    private transient byte[] body;
    private int opaque;
    private transient static Serializer serializer=Serializer.getTransmittingMessaageSerialier();
    private transient int id;
    private transient static Map<Integer,byte[]> cacheMap=new ConcurrentHashMap<>();

    public TransmittingMessage() {
        opaque=RequestId.incrementAndGet();
        id=opaque;
    }

    public long getId() {
        return id;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public int getOpaque() {
        return opaque;
    }

    public void setOpaque(int opaque) {
        this.opaque = opaque;
    }

    public boolean isRequeset()
    {
        return (flag&(1<<REQUEST_INDEX))!=0;
    }

    public boolean isResponse()
    {
        return (flag&(1<<RESPONSE_INDEX))!=0;
    }

    public boolean isOneway()
    {
        return (flag&(1<<ONE_WAY))!=0;
    }

    public void setOneWay()
    {
        flag|=(1<<ONE_WAY);
    }

    public void setRequest()
    {
        flag|=(1<<REQUEST_INDEX);
    }

    public void setResponse()
    {
        flag|=(1<<RESPONSE_INDEX);
    }

//    private static AtomicInteger atomicInteger=new AtomicInteger(0);
    public void encode(ByteBuf byteBuf)
    {
        byte[] bytes=cacheMap.get(this.id);
        if(bytes!=null)
        {
//            System.out.println("cached:"+atomicInteger.incrementAndGet());
            byteBuf.writeBytes(bytes);
        }else{
            int length=4;
            byte[] header=serializer.encode(this);
            length+=header.length;
            if(body!=null)
            {
                length+=body.length;
            }
            byteBuf.writeInt(length);
            byteBuf.writeInt(header.length);
            byteBuf.writeBytes(header);
            if(body!=null)
            {
                byteBuf.writeBytes(body);
            }
        }
    }

    public void preEncode()
    {
        byte[] header=serializer.encode(this);
        int length=4+header.length;
        if(this.body!=null)
        {
            length+=body.length;
        }
        byte[]res=new byte[length+4];
        ByteBuffer byteBuffer=ByteBuffer.wrap(res);
        byteBuffer.position(0);
        byteBuffer.putInt(length);
        byteBuffer.putInt(header.length);
        byteBuffer.put(header);
        byteBuffer.put(this.body);
        cacheMap.put(this.id,res);
    }

    public static TransmittingMessage decode(byte[]bytes)
    {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        return decode(byteBuffer);
    }

    public static TransmittingMessage decode(ByteBuffer byteBuffer)
    {
        int length=byteBuffer.limit();
        int headerLength = byteBuffer.getInt();
        byte[] headerData = new byte[headerLength];
        byteBuffer.get(headerData);
        TransmittingMessage transmittingMessage=new TransmittingMessage();
        serializer.decode(headerData, transmittingMessage);
        byte[]bodydata=null;
        int bodylength=length-headerLength-4;
        if(bodylength>0)
        {
            bodydata=new byte[bodylength];
            byteBuffer.get(bodydata);
        }
        transmittingMessage.setBody(bodydata);
        return transmittingMessage;
    }

    private static TransmittingMessage wrapMessage(int action, byte[] body)
    {
        TransmittingMessage transmittingMessage=new TransmittingMessage();
        transmittingMessage.setAction(action);
        transmittingMessage.setBody(body);
        return transmittingMessage;
    }

    public static TransmittingMessage wrapRequestMessage(int action, byte[] body)
    {
        TransmittingMessage transmittingMessage=wrapMessage(action,body);
        transmittingMessage.setRequest();
        return transmittingMessage;
    }

    public static TransmittingMessage wrapResponseMessage(int action,byte[] body)
    {
        TransmittingMessage transmittingMessage=wrapMessage(action,body);
        transmittingMessage.setResponse();
        return transmittingMessage;
    }

    @Override
    public String toString() {
        return "TransmittingMessage{" +
                "action=" + action +
                ", flag=" + flag +
                ", opaque=" + opaque +
                '}';
    }
}
