package com.alibaba.middleware.race.mom.util;

import java.util.Random;

/**
 * Created by wlw on 15-8-16.
 */
public class NameCreater {
    private static Random random=new Random(12);

    public static String newName()
    {
        return ""+random.nextInt(999)+System.currentTimeMillis();
    }

    public static void main(String[]args)
    {
        System.out.println(newName());
    }
}
