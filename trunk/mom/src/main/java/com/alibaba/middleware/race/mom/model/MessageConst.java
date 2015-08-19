package com.alibaba.middleware.race.mom.model;

/**
 * Created by wlw on 15-8-5.
 */
public class MessageConst {
    private static final int POST=0;
    private static final int BROAD=1;
    private static final int SBSCRIPT=2;
    private static final int POSTRESULT=3;
    private static final int BROADRESULT=4;
    private static final int HEARTBEAT=5;
    private static final int CLOSE=6;
    private static final int TEST=7;

    public static int getPOST() {
        return POST;
    }

    public static int getBROAD() {
        return BROAD;
    }

    public static int getSBSCRIPT() {
        return SBSCRIPT;
    }

    public static int getPOSTRESULT() {
        return POSTRESULT;
    }

    public static int getBROADRESULT() {
        return BROADRESULT;
    }

    public static int getHEARTBEAT() {
        return HEARTBEAT;
    }

    public static int getCLOSE() {
        return CLOSE;
    }

    public static int getTEST() {
        return TEST;
    }
}
