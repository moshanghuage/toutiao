package com.nowcoder;

import javax.xml.ws.Service;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThread {

    private static int counter=0;
    private static AtomicInteger atomicInteger= new AtomicInteger(0);

    public static void sleep(int mills){
        try{
            Thread.sleep(new Random().nextInt(mills));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void testWithAtomic(){
        for(int i=0;i<10;++i){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sleep(1000);
                    for (int j=0;j<10;++j){
                        counter++;
                        System.out.println(counter);
                    }
                }
            }).start();
        }
    }

    public static void testAtomic(){
        for(int i=0;i<10;++i){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sleep(1000);
                    for (int j=0;j<10;++j){
                        System.out.println(atomicInteger.incrementAndGet());
                    }
                }
            }).start();
        }
    }

    private static ThreadLocal<Integer> threadLocalUserIds = new ThreadLocal<Integer>();
    private static int userId;

    public static void testThreadLocal(){
        //10个线程，针对每个线程设置它的name
        for(int i=0;i<10;++i){
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    threadLocalUserIds.set(finalI);
                    sleep(1000);
                    System.out.println("ThreadLocal:" + threadLocalUserIds.get());
                }
            }).start();
        }

        for(int i=0;i<10;++i){
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    userId = finalI;
                    sleep(1000);
                    System.out.println("NonThreadLocal:" + userId);
                }
            }).start();
        }
    }

    public static void testExecutor(){
        //先生成一个ExecutorService,使用线程池的方式
        ExecutorService service = Executors.newFixedThreadPool(2);
        //该service提交一个任务
        service.submit(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<10;i++){
                    sleep(1000);
                    System.out.println("Executor1"+i);
                }
            }
        });

        //该Service提交另一个任务
        service.submit(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<10;i++){
                    sleep(1000);
                    System.out.println("Executor2"+i);
                }
            }
        });

        //执行完之后就把它关闭
        service.shutdown();
        //检查任务有没有结束，没有结束的话就打印个东西出来
        while(!service.isTerminated()){
            sleep(1000);
            System.out.println("Wait for termination");
        }
    }

    public static void testFuture(){
        //使用Executor框架来测试future
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Integer> future = service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                sleep(1000);
                return 1;
                //会有一个抛出异常的结果,等到get这个结果时，程序会直接抛出异常
                //throw new IllegalArgumentException("异常");
            }
        });
        service.shutdown();

        try{
            //等到返回这个future这个结果时，程序会因超时而抛出异常，适合处理时间要紧的事件
            System.out.println(future.get(100,TimeUnit.MILLISECONDS));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String rags[]){
        //testAtomic();
        //testWithAtomic();
       // testThreadLocal();
        //testExecutor();
        testFuture();
    }

}

