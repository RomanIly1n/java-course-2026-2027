import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    private static ComplexMatrix matrixA;
    private static ComplexMatrix matrixB;

    public static void main(String[] args) {
        System.out.println("Калькулятор матриц комплексных чисел");
        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Ваш выбор: ", 0, 8);
            switch (choice) {
                case 1 -> matrixA = inputMatrix("A");
                case 2 -> matrixB = inputMatrix("B");
                case 3 -> showMatrices();
                case 4, 5, 6 -> binaryOperation(choice);
                case 7 -> transposeOperation();
                case 8 -> determinantOperation();
                case 0 -> running = false;
            }
        }
        System.out.println("Работа завершена");
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("1. Ввести матрицу A");
        System.out.println("2. Ввести матрицу B");
        System.out.println("3. Показать матрицы");
        System.out.println("4. Сложить A + B");
        System.out.println("5. Умножить A * B");
        System.out.println("6. Разделить A / B (A * B^-1)");
        System.out.println("7. Транспонировать матрицу");
        System.out.println("8. Вычислить определитель");
        System.out.println("0. Выход");
    }

    private static void showMatrices() {
        printMatrix("A", matrixA);
        printMatrix("B", matrixB);
    }

    private static void printMatrix(String name, ComplexMatrix matrix) {
        if (matrix == null) {
            System.out.println("Матрица " + name + " ещё не введена");
            return;
        }
        System.out.println("Матрица " + name + " (" + matrix.getRows() + "x" + matrix.getCols() + "):");
        System.out.println(matrix);
    }

    private static void binaryOperation(int operation) {
        if (matrixA == null || matrixB == null) {
            System.out.println("Сначала введите обе матрицы (пункты 1 и 2)");
            return;
        }
        try {
            ComplexMatrix result = switch (operation) {
                case 4 -> matrixA.add(matrixB);
                case 5 -> matrixA.multiply(matrixB);
                default -> matrixA.divide(matrixB);
            };
            System.out.println("Результат:");
            System.out.println(result);
        } catch (IllegalArgumentException | ArithmeticException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void transposeOperation() {
        ComplexMatrix matrix = chooseMatrix();
        if (matrix == null) {
            return;
        }
        System.out.println("Результат:");
        System.out.println(matrix.transpose());
    }

    private static void determinantOperation() {
        ComplexMatrix matrix = chooseMatrix();
        if (matrix == null) {
            return;
        }
        try {
            System.out.println("Определитель: " + matrix.determinant());
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static ComplexMatrix chooseMatrix() {
        while (true) {
            String answer = readLine("Выберите матрицу (A или B): ").toUpperCase();
            if (answer.equals("A") || answer.equals("B")) {
                ComplexMatrix matrix = answer.equals("A") ? matrixA : matrixB;
                if (matrix == null) {
                    System.out.println("Матрица " + answer + " ещё не введена");
                }
                return matrix;
            }
            System.out.println("Введите A или B");
        }
    }

    private static ComplexMatrix inputMatrix(String name) {
        System.out.println("Ввод матрицы " + name);
        int rows = readPositiveInt("Число строк: ");
        int cols = readPositiveInt("Число столбцов: ");
        System.out.println("Каждую строку вводите в одну линию: для каждого элемента два числа");
        System.out.println("(вещественная и мнимая часть), например: 1 2 3 -1 для элементов 1+2i и 3-1i");
        Complex[][] data = new Complex[rows][];
        for (int i = 0; i < rows; i++) {
            data[i] = readRow(i + 1, cols);
        }
        return new ComplexMatrix(data);
    }

    private static Complex[] readRow(int number, int cols) {
        while (true) {
            String line = readLine("Строка " + number + ": ").replace(',', '.');
            String[] parts = line.split("\\s+");
            if (line.isEmpty() || parts.length != 2 * cols) {
                System.out.println("Нужно ввести ровно " + (2 * cols) + " чисел через пробел");
                continue;
            }
            try {
                Complex[] row = new Complex[cols];
                for (int j = 0; j < cols; j++) {
                    double re = Double.parseDouble(parts[2 * j]);
                    double im = Double.parseDouble(parts[2 * j + 1]);
                    if (!Double.isFinite(re) || !Double.isFinite(im)) {
                        throw new NumberFormatException();
                    }
                    row[j] = new Complex(re, im);
                }
                return row;
            } catch (NumberFormatException e) {
                System.out.println("Некорректное число, повторите ввод строки");
            }
        }
    }

    private static int readInt(String prompt, int min, int max) {
        while (true) {
            String line = readLine(prompt);
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Введите целое число от " + min + " до " + max);
        }
    }

    private static int readPositiveInt(String prompt) {
        while (true) {
            String line = readLine(prompt);
            try {
                int value = Integer.parseInt(line);
                if (value >= 1) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Введите целое положительное число");
        }
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            System.out.println();
            System.exit(0);
        }
        return scanner.nextLine().trim();
    }
}
