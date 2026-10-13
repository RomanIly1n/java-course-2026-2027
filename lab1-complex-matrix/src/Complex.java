import java.util.Locale;
import java.util.Objects;

public final class Complex {
    public static final Complex ZERO = new Complex(0, 0);
    public static final Complex ONE = new Complex(1, 0);

    private static final double EPS = 1e-10;

    private final double re;
    private final double im;

    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    public double getRe() {
        return re;
    }

    public double getIm() {
        return im;
    }

    public Complex add(Complex other) {
        return new Complex(re + other.re, im + other.im);
    }

    public Complex subtract(Complex other) {
        return new Complex(re - other.re, im - other.im);
    }

    public Complex multiply(Complex other) {
        double newRe = re * other.re - im * other.im;
        double newIm = re * other.im + im * other.re;
        return new Complex(newRe, newIm);
    }

    public Complex divide(Complex other) {
        if (other.isZero()) {
            throw new ArithmeticException("Деление на комплексный ноль");
        }
        double denominator = other.re * other.re + other.im * other.im;
        double newRe = (re * other.re + im * other.im) / denominator;
        double newIm = (im * other.re - re * other.im) / denominator;
        return new Complex(newRe, newIm);
    }

    public Complex negate() {
        return new Complex(-re, -im);
    }

    public Complex conjugate() {
        return new Complex(re, -im);
    }

    public double abs() {
        return Math.hypot(re, im);
    }

    public boolean isZero() {
        return abs() < EPS;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Complex)) {
            return false;
        }
        Complex other = (Complex) o;
        return Double.compare(re, other.re) == 0 && Double.compare(im, other.im) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(re, im);
    }

    @Override
    public String toString() {
        String reStr = format(re);
        String imStr = format(Math.abs(im));
        String sign = (im < 0 && !imStr.equals("0")) ? "-" : "+";
        return reStr + sign + imStr + "i";
    }

    private static String format(double value) {
        if (Math.abs(value) < EPS) {
            return "0";
        }
        if (Math.abs(value - Math.rint(value)) < EPS) {
            return String.valueOf((long) Math.rint(value));
        }
        String s = String.format(Locale.US, "%.4f", value);
        s = s.replaceAll("0+$", "");
        s = s.replaceAll("\\.$", "");
        if (s.equals("-0")) {
            return "0";
        }
        return s;
    }
}
