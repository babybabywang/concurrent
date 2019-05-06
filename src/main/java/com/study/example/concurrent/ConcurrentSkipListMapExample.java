package com.study.example.concurrent;

import com.study.anno.ThreadSafe;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;

/**
 * ConcurrentSkipListMap线程安全
 *
 * @author huangsm
 * @version V1.0
 */
@Slf4j
@ThreadSafe
public class ConcurrentSkipListMapExample {
    /**
     * 请求总数
     */
    private static int clientTotal = 5000;
    /**
     * 同时并发执行的线程数
     */
    private static int threadTotal = 200;

    private static Map<Integer, Integer> map = new ConcurrentSkipListMap<>();

    public static void main(String[] args) throws InterruptedException {
        ExecutorService threadPool = Executors.newCachedThreadPool();

        final Semaphore semaphore = new Semaphore(threadTotal);

        final CountDownLatch lock = new CountDownLatch(clientTotal);
        for (int i = 0; i < clientTotal; i++) {
            final int count = i;
            threadPool.execute(() -> {
                try {
                    semaphore.acquire();
                    update(count);
                    semaphore.release();
                } catch (Exception e) {
                    log.error("exception", e);
                }
                lock.countDown();
            });
        }
        lock.await();
        threadPool.shutdown();
        log.info("{}", map.size());
    }

    private static void update(int i) {
        map.put(i, i);
    }
}
