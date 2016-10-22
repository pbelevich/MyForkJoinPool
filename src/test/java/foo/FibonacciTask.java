package foo;

/**
 * @author Pavel Belevich
 */
public class FibonacciTask extends MyRecursiveTask<FibonacciNumber> {

    private final int n;

    public FibonacciTask(int n) {
        this.n = n;
    }

    @Override
    protected FibonacciNumber compute() {
        if (n < 2) {
            return new FibonacciNumber(n, n);
        } else {
            FibonacciTask ft1 = new FibonacciTask(n - 1);
            ft1.fork();
            FibonacciTask ft2 = new FibonacciTask(n - 2);
            return ft2.compute().plus(ft1.join());
        }
    }

    @Override
    public String toString() {
        return "FibonacciTask{" +
                "n=" + n +
                "} " + super.toString();
    }

}
