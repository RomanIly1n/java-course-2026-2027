public final class ComplexMatrix {
    private static final double EPS = 1e-10;

    private final int rows;
    private final int cols;
    private final Complex[][] data;

    public ComplexMatrix(Complex[][] source) {
        if (source == null || source.length == 0 || source[0] == null || source[0].length == 0) {
            throw new IllegalArgumentException("Матрица не может быть пустой");
        }
        this.rows = source.length;
        this.cols = source[0].length;
        this.data = new Complex[rows][cols];
        for (int i = 0; i < rows; i++) {
            if (source[i] == null || source[i].length != cols) {
                throw new IllegalArgumentException("Все строки матрицы должны иметь одинаковую длину");
            }
            for (int j = 0; j < cols; j++) {
                if (source[i][j] == null) {
                    throw new IllegalArgumentException("Элемент матрицы не может быть null");
                }
                data[i][j] = source[i][j];
            }
        }
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public Complex get(int i, int j) {
        return data[i][j];
    }

    public boolean isSquare() {
        return rows == cols;
    }

    public ComplexMatrix add(ComplexMatrix other) {
        if (rows != other.rows || cols != other.cols) {
            throw new IllegalArgumentException(
                    "Сложение невозможно: размеры матриц " + rows + "x" + cols
                            + " и " + other.rows + "x" + other.cols + " не совпадают");
        }
        Complex[][] result = new Complex[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = data[i][j].add(other.data[i][j]);
            }
        }
        return new ComplexMatrix(result);
    }

    public ComplexMatrix multiply(ComplexMatrix other) {
        if (cols != other.rows) {
            throw new IllegalArgumentException(
                    "Умножение невозможно: число столбцов первой матрицы (" + cols
                            + ") не равно числу строк второй (" + other.rows + ")");
        }
        Complex[][] result = new Complex[rows][other.cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < other.cols; j++) {
                Complex sum = Complex.ZERO;
                for (int k = 0; k < cols; k++) {
                    sum = sum.add(data[i][k].multiply(other.data[k][j]));
                }
                result[i][j] = sum;
            }
        }
        return new ComplexMatrix(result);
    }

    public ComplexMatrix divide(ComplexMatrix other) {
        return multiply(other.inverse());
    }

    public ComplexMatrix transpose() {
        Complex[][] result = new Complex[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[j][i] = data[i][j];
            }
        }
        return new ComplexMatrix(result);
    }

    public Complex determinant() {
        if (!isSquare()) {
            throw new IllegalArgumentException("Определитель существует только у квадратной матрицы");
        }
        int n = rows;
        Complex[][] a = copyData();
        Complex det = Complex.ONE;

        for (int col = 0; col < n; col++) {
            int pivot = findPivotRow(a, col, n);
            if (a[pivot][col].abs() < EPS) {
                return Complex.ZERO;
            }
            if (pivot != col) {
                swapRows(a, pivot, col);
                det = det.negate();
            }
            det = det.multiply(a[col][col]);
            for (int r = col + 1; r < n; r++) {
                Complex factor = a[r][col].divide(a[col][col]);
                for (int k = col; k < n; k++) {
                    a[r][k] = a[r][k].subtract(factor.multiply(a[col][k]));
                }
            }
        }
        return det;
    }

    public ComplexMatrix inverse() {
        if (!isSquare()) {
            throw new IllegalArgumentException("Обратная матрица существует только для квадратной матрицы");
        }
        int n = rows;
        Complex[][] a = new Complex[n][2 * n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                a[i][j] = data[i][j];
                a[i][n + j] = (i == j) ? Complex.ONE : Complex.ZERO;
            }
        }

        for (int col = 0; col < n; col++) {
            int pivot = findPivotRow(a, col, n);
            if (a[pivot][col].abs() < EPS) {
                throw new ArithmeticException("Матрица вырождена, обратной не существует");
            }
            if (pivot != col) {
                swapRows(a, pivot, col);
            }
            Complex pivotValue = a[col][col];
            for (int k = 0; k < 2 * n; k++) {
                a[col][k] = a[col][k].divide(pivotValue);
            }
            for (int r = 0; r < n; r++) {
                if (r == col) {
                    continue;
                }
                Complex factor = a[r][col];
                if (factor.isZero()) {
                    continue;
                }
                for (int k = 0; k < 2 * n; k++) {
                    a[r][k] = a[r][k].subtract(factor.multiply(a[col][k]));
                }
            }
        }

        Complex[][] result = new Complex[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = a[i][n + j];
            }
        }
        return new ComplexMatrix(result);
    }

    private Complex[][] copyData() {
        Complex[][] copy = new Complex[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                copy[i][j] = data[i][j];
            }
        }
        return copy;
    }

    private static int findPivotRow(Complex[][] a, int col, int n) {
        int pivot = col;
        double best = a[col][col].abs();
        for (int r = col + 1; r < n; r++) {
            double current = a[r][col].abs();
            if (current > best) {
                best = current;
                pivot = r;
            }
        }
        return pivot;
    }

    private static void swapRows(Complex[][] a, int i, int j) {
        Complex[] tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
    }

    @Override
    public String toString() {
        String[][] cells = new String[rows][cols];
        int width = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j] = data[i][j].toString();
                width = Math.max(width, cells[i][j].length());
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            sb.append("[ ");
            for (int j = 0; j < cols; j++) {
                if (j > 0) {
                    sb.append("  ");
                }
                sb.append(String.format("%-" + width + "s", cells[i][j]));
            }
            sb.append(" ]");
            if (i < rows - 1) {
                sb.append(System.lineSeparator());
            }
        }
        return sb.toString();
    }
}
