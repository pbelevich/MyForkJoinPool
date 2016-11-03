package foo;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static foo.MyForkJoinPool.currentMyForkJoinThread;
import static foo.MyForkJoinPool.getForkJoinPool;

/**
 * @author Pavel Belevich
 */
public abstract class MyRecursiveTask<V> {

    final BlockingQueue<MyRecursiveTask<V>> children = new LinkedBlockingQueue<>();
    volatile boolean done = false;
    volatile V result = null;

    public final MyRecursiveTask<V> fork() {
        if (getForkJoinPool().queue.isEmpty()) {
            this.call();
            return this;
        } else {
            getCaller().registerChild(this);
            return getForkJoinPool().submit(this);
        }
    }

    public final V join() {
        waitForChildren();
        waitForResultActive();
        return result;
    }

    public final V get()  {
        waitForResultPassive();
        return result;
    }

    private void waitForResultActive() {
        while (!done) {
            try {
                final Object item = currentMyForkJoinThread().queue.pollLast();
                if (item == null) {
                    continue;
                }
                if (item == getForkJoinPool().STOP) {
                    break;
                }
                MyRecursiveTask<?> task = (MyRecursiveTask<?>) item;
                task.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    private void waitForResultPassive() {
        while (!done) ;
    }

    public V call() {
        setCaller(this);
        result = compute();
        done = true;
        onComplete();
        return result;
    }

    public boolean isDone() {
        return done;
    }

    private void registerChild(MyRecursiveTask<V> child) {
        children.offer(child);
    }

    private void waitForChildren() {
        while (!done) {
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
                final Object item = currentMyForkJoinThread().queue.pollLast();
                if (item == null || item == getForkJoinPool().STOP) {
                    break;
                }
                MyRecursiveTask<?> task = (MyRecursiveTask<?>) item;
                task.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private MyRecursiveTask getCaller() {
        return getForkJoinPool().caller.get();
    }

    private void setCaller(MyRecursiveTask task) {
        getForkJoinPool().caller.set(task);
    }

    protected abstract V compute();

    public void onComplete() {
    }

}
