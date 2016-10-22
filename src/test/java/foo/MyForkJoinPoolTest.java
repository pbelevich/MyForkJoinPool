package foo;

import org.junit.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

/**
 * @author Pavel Belevich
 */
public class MyForkJoinPoolTest {

    @Test
    public void test() throws ExecutionException, InterruptedException {
        final MyForkJoinPool fjp = new MyForkJoinPool();
        final MyRecursiveTask<FibonacciNumber> task = fjp.submit(new FibonacciTask(20));
        final FibonacciNumber fn = task.get();
        fjp.shutdown();
        assertEquals(20, fn.n);
        assertEquals(6765, fn.value);
    }

}