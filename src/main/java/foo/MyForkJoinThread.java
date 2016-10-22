package foo;

/**
 * @author Pavel Belevich
 */
public class MyForkJoinThread extends Thread {

    final MyForkJoinPool forkJoinPool;

    public MyForkJoinThread(MyForkJoinPool forkJoinPool) {
        this.forkJoinPool = forkJoinPool;
    }

    @Override
    public void run() {
        while (true) {
            try {
                final Object item = this.forkJoinPool.queue.takeLast();
                if (item == this.forkJoinPool.STOP) {
                    break;
                }
                MyRecursiveTask<?> task = (MyRecursiveTask<?>) item;
                task.call();
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
    }

}
