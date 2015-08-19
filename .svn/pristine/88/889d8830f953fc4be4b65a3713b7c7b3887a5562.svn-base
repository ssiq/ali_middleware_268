package race.momtest.stake;

import com.alibaba.middleware.race.mom.error.InvokeFailException;
import com.alibaba.middleware.race.mom.error.RemoteRunOuttimeException;
import com.alibaba.middleware.race.mom.model.MessageConst;
import com.alibaba.middleware.race.mom.model.TransmittingMessage;
import com.alibaba.middleware.race.mom.service.Client;
import com.alibaba.middleware.race.mom.util.Sem;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by wlw on 15-8-18.
 */
public class NetDelayAsync {

    public static void main(String[]args) throws InterruptedException, InvokeFailException, RemoteRunOuttimeException {
        final Client client=new Client(300,10000);
        final String ip=System.getProperty("SIP");
        System.out.println("connect:"+ip);
        client.connect(ip,9999,null);
        final Schema schema= RuntimeSchema.getSchema(DelayMessaage.class);

        ExecutorService executorService= Executors.newFixedThreadPool(300);
        final int size=1000;
        int thread=200;
        final AtomicLong tt=new AtomicLong(0);
        final Sem sem=new Sem();
        for(int j=0;j<thread;++j)
        {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    sem.up();
                    LinkedBuffer linkedBuffer=LinkedBuffer.allocate(1024);
                    for(int i=0;i<size;++i)
                    {
                        linkedBuffer.clear();
                        DelayMessaage delayMessaage=new DelayMessaage();
                        byte[]bytes= ProtobufIOUtil.toByteArray(delayMessaage, schema, linkedBuffer);
                        TransmittingMessage transmittingMessage=TransmittingMessage.wrapRequestMessage(MessageConst.getTEST(), bytes);
                        TransmittingMessage response= null;
                        try {
                            response = client.doSyncInvoke(ip, transmittingMessage);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (InvokeFailException e) {
                            e.printStackTrace();
                        } catch (RemoteRunOuttimeException e) {
                            e.printStackTrace();
                        }
                        byte[] rebyte=response.getBody();
                        delayMessaage=new DelayMessaage();
                        ProtobufIOUtil.mergeFrom(rebyte, delayMessaage, schema);
                        tt.addAndGet(System.currentTimeMillis()-delayMessaage.getTime());
                    }
                    sem.down();
                }
            });
        }

        sem.end();
        sem.await();
        System.out.println("delay time:"+(tt.doubleValue())/(size*thread*size));
    }
}
