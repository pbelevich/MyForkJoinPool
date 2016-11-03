package foo;

import org.junit.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

/**
 * @author Pavel Belevich
 */
public class MyForkJoinPoolTest {

    @Test
    public void testFibonacciTaskComputeAndJoin() throws ExecutionException, InterruptedException {
        final MyForkJoinPool fjp = new MyForkJoinPool();
        final MyRecursiveTask<FibonacciNumber> task = fjp.submit(new FibonacciTaskComputeAndJoin(30));
        final FibonacciNumber fn = task.get();
        fjp.shutdown();
        assertEquals(30, fn.n);
        assertEquals(832040, fn.value);
    }

    @Test
    public void testFibonacciTaskJoinAndJoin() throws ExecutionException, InterruptedException {
        final MyForkJoinPool fjp = new MyForkJoinPool();
        final MyRecursiveTask<FibonacciNumber> task = fjp.submit(new FibonacciTaskJoinAndJoin(30));
        final FibonacciNumber fn = task.get();
        fjp.shutdown();
        assertEquals(30, fn.n);
        assertEquals(832040, fn.value);
    }

}