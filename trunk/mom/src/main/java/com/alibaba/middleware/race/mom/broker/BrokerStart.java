package com.alibaba.middleware.race.mom.broker;

import com.alibaba.middleware.race.mom.store.util.StringSerilizer;

import java.net.BindException;

/**
 * Created by wlw on 15-8-2.
 */
public class BrokerStart {
    public static void main(String[]args)
    {
        if(args.length<1){
            System.out.println("not input store path");
            return;
        }
        String storePath=args[0];
        if(!storePath.endsWith("/"))
        {
            storePath+="/";
        }
        int fileSize=0;
        String name="";

        if(args.length>=2)
        {
            name=args[1];
        }

        if(args.length>=3)
        {
            fileSize=Integer.valueOf(args[2])*4096;
        }else{
            fileSize=4096;
        }
        Broker broker=new Broker(storePath,name,fileSize);
        try {
            broker.start(9999);
        } catch (BindException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("broker start");
    }
}
