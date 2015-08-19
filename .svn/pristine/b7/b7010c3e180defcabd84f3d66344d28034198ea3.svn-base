package race.momtest.utilTest;

import com.alibaba.middleware.race.mom.util.Sem;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wlw on 15-8-17.
 */
public class SemTest {
    public static void main(String[]args)
    {
        int threadNUm=200;
        ExecutorService executorService= Executors.newFixedThreadPool(threadNUm);
        final Sem sem=new Sem();
        for(int i=0;i<threadNUm;++i)
        {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    sem.up();
//                    try {
//                        Thread.sleep(20);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    sem.down();
                }
            });
        }
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        sem.end();
        sem.await();
        executorService.shutdown();
    }
}
