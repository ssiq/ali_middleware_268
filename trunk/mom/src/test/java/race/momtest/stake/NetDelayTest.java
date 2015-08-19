package race.momtest.stake;

import com.alibaba.middleware.race.mom.error.InvokeFailException;
import com.alibaba.middleware.race.mom.error.RemoteRunOuttimeException;
import com.alibaba.middleware.race.mom.model.MessageConst;
import com.alibaba.middleware.race.mom.model.TransmittingMessage;
import com.alibaba.middleware.race.mom.service.Client;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**
 * Created by wlw on 15-8-18.
 */
public class NetDelayTest {

    public static void main(String[]args) throws InterruptedException, InvokeFailException, RemoteRunOuttimeException {
        Client client=new Client(300,10000);
        String ip=System.getProperty("SIP");
        System.out.println("connect:"+ip);
        client.connect(ip,9999,null);
        LinkedBuffer linkedBuffer=LinkedBuffer.allocate(1024);
        Schema schema= RuntimeSchema.getSchema(DelayMessaage.class);

        int size=1000;
        long tt=0;
        for(int i=0;i<size;++i)
        {
            linkedBuffer.clear();
            DelayMessaage delayMessaage=new DelayMessaage();
            byte[]bytes=ProtobufIOUtil.toByteArray(delayMessaage,schema,linkedBuffer);
            TransmittingMessage transmittingMessage=TransmittingMessage.wrapRequestMessage(MessageConst.getTEST(), bytes);
            TransmittingMessage response=client.doSyncInvoke(ip, transmittingMessage);
            byte[] rebyte=response.getBody();
            delayMessaage=new DelayMessaage();
            ProtobufIOUtil.mergeFrom(rebyte,delayMessaage,schema);
            tt+=System.currentTimeMillis()-delayMessaage.getTime();
        }

        System.out.println("delay time:"+((double)tt)/size);
    }

}
