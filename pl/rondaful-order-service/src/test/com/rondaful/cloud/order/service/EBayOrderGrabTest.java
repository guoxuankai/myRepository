package com.rondaful.cloud.order.service;

import com.rondaful.cloud.OrderApplication;
import com.rondaful.cloud.order.task.ManualSyncEbayOrderTesk;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import sun.nio.ch.ThreadPool;


import java.util.concurrent.*;


@SpringBootTest(classes = OrderApplication.class)
@RunWith(SpringRunner.class)
public class EBayOrderGrabTest {



    @Test
    public void testEbayPlatformOrderGrabTest() throws InterruptedException {

        ManualSyncEbayOrderTesk  tesk=new ManualSyncEbayOrderTesk();
        BlockingQueue<Runnable> works=new LinkedBlockingDeque<>();
        ExecutorService service=new ThreadPoolExecutor(2,2,1000, TimeUnit.SECONDS,works);
        service.execute(tesk);
    }
}
