package demo.test;

import demo.service.RaceDO;
import org.junit.Assert;

/**
 * Created by wlw on 15-7-29.
 */
public class TestAssert {
    public static void main(String[]args)
    {
        Assert.assertEquals(new RaceDO(),new RaceDO());
    }

}
