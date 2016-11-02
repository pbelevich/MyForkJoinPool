package foo;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author Pavel Belevich
 */
public class MyForkJoinThread extends Thread {

    final MyForkJoinPool forkJoinPool;
    final BlockingDeque<Object> queue = new LinkedBlockingDeque<>();

    public MyForkJoinThread(MyForkJoinPool forkJoinPool) {
        this.forkJoinPool = forkJoinPool;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Object item = queue.pollLast();
                if (item == this.forkJoinPool.STOP) {
                    break;
                }
                if (item == null) {
                    forkJoinPool.queue.offer(queue);
                }
                item = queue.takeLast();
                if (item == this.forkJoinPool.STOP) {
                    break;
                }
                MyRecursiveTask<?> task = (MyRecursiveTask<?>) item;
                task.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
