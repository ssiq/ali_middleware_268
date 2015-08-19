package com.alibaba.middleware.race.mom.store.util;

/**
 * Created by wlw on 15-8-10.
 */
public class StringSerilizer {

    private char endChar=0x0;

    public void encode(String s,byte[] bytes)
    {
        encode(s,bytes,0,bytes.length);
    }

    public void encode(String s,byte[]bytes,int begin,int length)
    {
        if(s.length()>length)return;
        for(int i=0;i<s.length();++i)
        {
            bytes[begin+i]=(byte)s.charAt(i);
        }
        if(s.length()<length)
        {
            bytes[begin+s.length()]=(byte)endChar;
        }
    }

    public String decode(byte[]bytes)
    {
        return decode(bytes,0,bytes.length);
    }

    public String decode(byte[]bytes,int begin,int length)
    {
        StringBuilder stringBuilder=new StringBuilder();
        for(int i=begin;i<begin+length;++i)
        {
            if(bytes[i]==endChar)break;
            stringBuilder.append((char)bytes[i]);
        }
        return stringBuilder.toString();
    }


    public static void main(String[]args)
    {
        StringSerilizer stringSerilizer=new StringSerilizer();
        byte[] bytes=new byte[10];
        String s="1234";
        stringSerilizer.encode(s,bytes);
        System.out.println(stringSerilizer.decode(bytes));
        s="1234567891";
        stringSerilizer.encode(s, bytes);
        System.out.println(stringSerilizer.decode(bytes));
    }

}
