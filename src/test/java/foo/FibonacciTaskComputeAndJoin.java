package foo;

/**
 * @author Pavel Belevich
 */
public class FibonacciTaskComputeAndJoin extends MyRecursiveTask<FibonacciNumber> {

    private final int n;

    public FibonacciTaskComputeAndJoin(int n) {
        this.n = n;
    }

    @Override
    protected FibonacciNumber compute() {
        if (n < 2) {
            return new FibonacciNumber(n, n);
        } else {
            FibonacciTaskComputeAndJoin ft1 = new FibonacciTaskComputeAndJoin(n - 1);
            ft1.fork();
            FibonacciTaskComputeAndJoin ft2 = new FibonacciTaskComputeAndJoin(n - 2);
            return ft2.compute().plus(ft1.join());
        }
    }

    @Override
    public String toString() {
        return "FibonacciTaskComputeAndJoin{" +
                "n=" + n +
                "} " + super.toString();
    }

}
