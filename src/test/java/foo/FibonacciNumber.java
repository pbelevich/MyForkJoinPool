package foo;

/**
 * @author Pavel Belevich
 */
public class FibonacciNumber {

    static final String INDENT = "   ";

    final Thread thread;
    final int n;
    final int value;
    final FibonacciNumber fn1;
    final FibonacciNumber fn2;

    private FibonacciNumber(int n, int value, FibonacciNumber fn1, FibonacciNumber fn2) {
        this.thread = Thread.currentThread();
        this.n = n;
        this.value = value;
        this.fn1 = fn1;
        this.fn2 = fn2;
    }

    public FibonacciNumber(int n, int value) {
        this(n, value, null, null);
    }

    public FibonacciNumber plus(FibonacciNumber that) {
        return new FibonacciNumber(Math.max(this.n, that.n) + 1, this.value + that.value, this, that);
    }

    @Override
    public String toString() {
        return "FibonacciNumber{" +
                "thread=" + thread +
                ", n=" + n +
                ", value=" + value +
                '}';
    }

    private String toStringWithIndent(String indent) {
        return indent + toString();
    }

    public String toStringAll() {
        return toStringAll("");
    }

    private String toStringAll(String indent) {
        return toStringWithIndent(indent) + (fn1 != null && fn2 != null ? "\n" + fn1.toStringAll(INDENT + indent) + "\n" + fn2.toStringAll(INDENT + indent) : "");
    }

}
