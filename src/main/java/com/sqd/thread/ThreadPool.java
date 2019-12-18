package com.sqd.thread;

import java.util.concurrent.*;

/***
 * 线程池
 */
public class ThreadPool {

    private static ExecutorService exec ;

    static {
        exec =  new ThreadPoolExecutor(3, 10, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());
    }

    private ThreadPool(){}

    public static ExecutorService getInstance(){
        return exec;
    }

    public static void shutdown(){
        exec.shutdown();
    }

}
