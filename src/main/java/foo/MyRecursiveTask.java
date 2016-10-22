package foo;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Pavel Belevich
 */
public abstract class MyRecursiveTask<V> {

    final BlockingQueue<MyRecursiveTask<V>> children = new LinkedBlockingQueue<>();
    volatile boolean done = false;
    volatile V result = null;

    public final MyRecursiveTask<V> fork() {
        getCaller().registerChild(this);
        return getForkJoinPool().submit(this);
    }

    public final V join() {
        waitForChildren();
        waitForResult();
        return result;
    }

    public final V get()  {
        waitForResult();
        return result;
    }

    private void waitForResult() {
        while (!done) ;
    }

    public V call() {
        setCaller(this);
        result = compute();
        done = true;
        return result;
    }

    public boolean isDone() {
        return done;
    }

    private void registerChild(MyRecursiveTask<V> child) {
        children.offer(child);
    }

    private void waitForChildren() {
        while (true) {
            boolean allChildrenDone = true;
            for (MyRecursiveTask<V> child : children) {
                if (!child.isDone()) {
                    allChildrenDone = false;
                }
            }
            if (allChildrenDone && done) {
                break;
            }
            try {
                final Object item = getForkJoinPool().queue.pollLast();
                if (item == null || item == getForkJoinPool().STOP) {
                    break;
                }
                MyRecursiveTask<?> task = (MyRecursiveTask<?>) item;
                task.call();
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
    }

    private MyRecursiveTask getCaller() {
        return getForkJoinPool().caller.get();
    }

    private void setCaller(MyRecursiveTask task) {
        getForkJoinPool().caller.set(task);
    }

    private MyForkJoinPool getForkJoinPool() {
        return ((MyForkJoinThread) Thread.currentThread()).forkJoinPool;
    }

    protected abstract V compute();

}
