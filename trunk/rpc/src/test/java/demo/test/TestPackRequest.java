package demo.test;

import com.alibaba.middleware.race.rpc.model.RpcRequest;
import demo.service.RaceDO;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**
 * Created by wlw on 15-7-29.
 */
public class TestPackRequest {
    public static void main(String[]args)
    {
        Object[] objects=new Object[]{"111", new RaceDO(),23};
        RpcRequest rpcRequest=new RpcRequest(null,objects);
        Schema schema= RuntimeSchema.getSchema(RpcRequest.class);
        LinkedBuffer linkedBuffer=LinkedBuffer.allocate();
        byte[] bytes= ProtobufIOUtil.toByteArray(rpcRequest, schema, linkedBuffer);
        RpcRequest new_rpc=new RpcRequest();
        ProtobufIOUtil.mergeFrom(bytes, new_rpc, schema);
        System.out.println(new_rpc);
    }
}
