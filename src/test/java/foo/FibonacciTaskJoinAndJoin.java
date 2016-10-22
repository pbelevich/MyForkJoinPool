package foo;

/**
 * @author Pavel Belevich
 */
public class FibonacciTaskJoinAndJoin extends MyRecursiveTask<FibonacciNumber> {

    private final int n;

    public FibonacciTaskJoinAndJoin(int n) {
        this.n = n;
    }

    @Override
    protected FibonacciNumber compute() {
        if (n < 2) {
            return new FibonacciNumber(n, n);
        } else {
            FibonacciTaskJoinAndJoin ft1 = new FibonacciTaskJoinAndJoin(n - 1);
            ft1.fork();
            FibonacciTaskJoinAndJoin ft2 = new FibonacciTaskJoinAndJoin(n - 2);
            ft2.fork();
            return ft2.join().plus(ft1.join());
        }
    }

    @Override
    public String toString() {
        return "FibonacciTaskComputeAndJoin{" +
                "n=" + n +
                "} " + super.toString();
    }

}
