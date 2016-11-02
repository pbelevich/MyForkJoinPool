package foo;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author Pavel Belevich
 */
public class MyForkJoinPool {

    private static volatile MyForkJoinPool commonPool;

    final ThreadLocal<MyRecursiveTask> caller = new ThreadLocal<>();
    final BlockingDeque<BlockingDeque<Object>> queue = new LinkedBlockingDeque<>();
    final Object STOP = new Object();
    final MyForkJoinThread[] threads;

    public MyForkJoinPool(int parallelism) {
        this.threads = new MyForkJoinThread[parallelism];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new MyForkJoinThread(this);
            threads[i].start();
        }
    }

    public MyForkJoinPool() {
        this(Runtime.getRuntime().availableProcessors());
    }

    public <T> MyRecursiveTask<T> submit(MyRecursiveTask<T> task) {
        final BlockingDeque<Object> freeQueue = this.queue.poll();
        if (freeQueue == null) {
            if (Thread.currentThread() instanceof MyForkJoinThread) {
                ((MyForkJoinThread) Thread.currentThread()).queue.offer(task);
            } else {
                threads[0].queue.offer(task); //TODO choose thread with smallest queue
            }
        } else {
            freeQueue.offer(task);
        }
        return task;
    }

    public void shutdown() {
        for (int i = 0; i < threads.length; i++) {
            threads[i].queue.offer(STOP);
        }
    }

    public static MyForkJoinPool commonPool() {
        if (commonPool == null) {
            synchronized (MyForkJoinPool.class) {
                if (commonPool == null) {
                    commonPool = new MyForkJoinPool();
                }
            }
        }
        return commonPool;
    }

    static MyForkJoinThread currentMyForkJoinThread() {
        if (Thread.currentThread() instanceof MyForkJoinThread) {
            return (MyForkJoinThread) Thread.currentThread();
        } else {
            return null;
        }
    }

    static MyForkJoinPool getForkJoinPool() {
        final MyForkJoinThread forkJoinThread = currentMyForkJoinThread();
        if (forkJoinThread != null) {
            return forkJoinThread.forkJoinPool;
        } else {
            return null;
        }
    }

}
