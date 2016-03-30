package test;

import main.java.com.shike.id.IdGenerator;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by MLS on 16/3/29.
 */
public class Test {
    public static Logger logger = Logger.getLogger(Test.class);
    private static  AtomicInteger count = new AtomicInteger(0);
    private final int limit = 20000000;
    private class GenId implements Runnable {
        IdGenerator idGenerator = IdGenerator.getIdGenerator();
        public void run() {
            StringBuilder id;
            try {
                while (count.get() < limit) {
                    id = idGenerator.getId("324234242342423424");
                    //System.out.println(Thread.currentThread().getName() + " " + id + " count: " + count);
                    count.getAndIncrement();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

//    @org.junit.Test
//    public void testTime() {
//        try {
//
//            long start = System.currentTimeMillis();
//            ExecutorService idPool = Executors.newFixedThreadPool(128);
//            //int num = ((ThreadPoolExecutor)idPool).getActiveCount();
//            do { //n个线程生成100W个Id
//                GenId genId = new GenId();
//                idPool.execute(genId);
//            } while (((ThreadPoolExecutor)idPool).getActiveCount() > 0);
//
//            idPool.shutdown();
//            long end = System.currentTimeMillis();
//            System.out.println("cost: " + (end - start));
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//    }

    @org.junit.Test
    public void testTimeSingle() {
        IdGenerator idGenerator = IdGenerator.getIdGenerator();
        StringBuilder id;
        try {
            int i = 0;
            long start = System.currentTimeMillis();
            while (i++ < limit) {
                id = idGenerator.getId("324234242342423424");
            }
            long end = System.currentTimeMillis();
            System.out.println("cost: " + (end - start));
            //System.out.println(Thread.currentThread().getName() + " " + id + " count: " + count);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
