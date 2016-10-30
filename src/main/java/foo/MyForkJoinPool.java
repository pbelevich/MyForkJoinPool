package foo;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author Pavel Belevich
 */
public class MyForkJoinPool {

    private static volatile MyForkJoinPool commonPool;

    final ThreadLocal<MyRecursiveTask> caller = new ThreadLocal<>();
    final BlockingDeque<Object> queue = new LinkedBlockingDeque<>();
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
        this.queue.offer(task);
        return task;
    }

    public void shutdown() {
        this.queue.clear();
        for (int i = 0; i < threads.length; i++) {
            queue.offer(STOP);
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

}
