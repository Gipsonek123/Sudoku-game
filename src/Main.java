import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class Main extends JFrame {

    private static final int SIZE = 9;
    private static final int SUBGRID_SIZE = 3;
    private JTextField[][] board;
    private Timer timer;
    private long startTime;
    private JLabel timerLabel;
    private boolean isSudokuSolved = false;
    private long solveTime;

    public Main() {
        createStartWindow();
    }

    private void createStartWindow() {
        JFrame startFrame = new JFrame("Sudoku");
        startFrame.setSize(400, 200);
        startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startFrame.setLayout(new GridBagLayout());

        JLabel welcomeLabel = new JLabel("Witaj! Wybierz poziom gry: ");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        startFrame.add(welcomeLabel, gbc);

        JButton easyButton = new JButton("Łatwy");
        easyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startFrame.dispose();
                initializeGame(1);
            }
        });
        gbc.gridy = 1;
        startFrame.add(easyButton, gbc);

        JButton mediumButton = new JButton("Średni");
        mediumButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startFrame.dispose();
                initializeGame(20);
            }
        });
        gbc.gridy = 2;
        startFrame.add(mediumButton, gbc);

        JButton hardButton = new JButton("Trudny");
        hardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startFrame.dispose();
                initializeGame(55);
            }
        });
        gbc.gridy = 3;
        startFrame.add(hardButton, gbc);

        startFrame.setLocationRelativeTo(null);
        startFrame.setVisible(true);
    }

    private void initializeGame(int numbersToRemove) {
        setTitle("Sudoku");
        setSize(620, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(SIZE, SIZE));

        board = new JTextField[SIZE][SIZE];


        int[][] sudokuBoard = generateRandomSudoku(numbersToRemove);

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = new JTextField();
                board[i][j].setHorizontalAlignment(JTextField.CENTER);

                Font font = new Font("Arial", Font.BOLD, 24);
                board[i][j].setFont(font);

                ((AbstractDocument) board[i][j].getDocument()).setDocumentFilter(new SudokuDocumentFilter());

                board[i][j].setText(sudokuBoard[i][j] == 0 ? "" : String.valueOf(sudokuBoard[i][j]));

                if (sudokuBoard[i][j] != 0) {
                    board[i][j].setEditable(false);
                }

                panel.add(board[i][j]);

                int topBorder = (i % SUBGRID_SIZE == 0) ? 4 : 1;
                int leftBorder = (j % SUBGRID_SIZE == 0) ? 4 : 1;
                int bottomBorder = (i % SUBGRID_SIZE == 2) ? 4 : 1;
                int rightBorder = (j % SUBGRID_SIZE == 2) ? 4 : 1;

                board[i][j].setBorder(BorderFactory.createMatteBorder(topBorder, leftBorder, bottomBorder, rightBorder, Color.BLACK));
            }
        }

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTimerLabel();
            }
        });

        // Inicjalizacja etykiety z czasem
        timerLabel = new JLabel("Czas: 00:00");
        timerLabel.setHorizontalAlignment(JLabel.RIGHT);
        timerLabel.setForeground(Color.BLUE);

        // Ustawienia czcionki dla etykiety czasu
        Font timerFont = new Font("Arial", Font.BOLD, 24); // Możesz dostosować czcionkę i rozmiar
        timerLabel.setFont(timerFont);

        // Dodanie etykiety z czasem do panelu
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(timerLabel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Rozpoczęcie timera
        startTime = System.currentTimeMillis();
        timer.start();

        JButton checkButton = new JButton("Sprawdź");
        checkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkSudoku();
            }
        });

        JButton newSudokuButton = new JButton("Nowe Sudoku");
        newSudokuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                Main newSudokuGame = new Main();
            }
        });

        JButton resetButton = new JButton("Resetuj");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetBoard();
            }
        });

        add(panel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(checkButton);
        buttonPanel.add(newSudokuButton);
        buttonPanel.add(resetButton);
        add(buttonPanel, BorderLayout.SOUTH);
        setVisible(true);
    }


    private void updateTimerLabel() {
        long elapsedTime = System.currentTimeMillis() - startTime;
        long seconds = elapsedTime / 1000;
        long minutes = seconds / 60;
        seconds %= 60;
        String formattedTime = String.format("%02d:%02d", minutes, seconds);
        timerLabel.setText("Czas: " + formattedTime);
    }
    private void resetBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j].isEditable()) {
                    board[i][j].setText("");
                }
            }
        }
    }


    private int[][] generateRandomSudoku(int numbersToRemove) {
        int[][] board = new int[SIZE][SIZE];
        solve(board);

        Random random = new Random();

        for (int k = 0; k < numbersToRemove; k++) {
            int i = random.nextInt(SIZE);
            int j = random.nextInt(SIZE);

            while (board[i][j] == 0) {
                i = random.nextInt(SIZE);
                j = random.nextInt(SIZE);
            }

            int originalValue = board[i][j];
            board[i][j] = 0;

            if (!hasUniqueSolution(board)) {
                board[i][j] = originalValue;
                k--;
            }
        }

        return board;
    }

    private boolean hasUniqueSolution(int[][] board) {
        int[][] copy = new int[SIZE][SIZE];
        copyBoard(board, copy);

        return countSolutions(copy) == 1;
    }

    private int countSolutions(int[][] board) {
        int[][] copy = new int[SIZE][SIZE];
        copyBoard(board, copy);

        int[] result = {0};

        solveSudokuWithCount(copy, result);

        return result[0];
    }

    private void solveSudokuWithCount(int[][] board, int[] result) {
        int[] emptyCell = findEmptyCell(board);

        if (emptyCell == null) {
            result[0]++;
            if (result[0] == 2) {
                // Znaleziono drugie rozwiązanie, zakończ działanie
                return;
            }
            // Kontynuuj szukanie kolejnych rozwiązań
            return;
        }

        int row = emptyCell[0];
        int col = emptyCell[1];

        int[] randomNumbers = generateRandomPermutation(SIZE);

        for (int num : randomNumbers) {
            if (isValid(board, row, col, num)) {
                board[row][col] = num;
                solveSudokuWithCount(board, result);
                board[row][col] = 0;

                if (result[0] == 2) {
                    // Znaleziono drugie rozwiązanie, zakończ działanie
                    return;
                }
            }
        }
    }

    private void copyBoard(int[][] source, int[][] target) {
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(source[i], 0, target[i], 0, SIZE);
        }
    }

    private void solve(int[][] board) {
        solveSudoku(board);
    }

    private boolean isValidSudoku(int[][] board) {
        // Sprawdź wiersze i kolumny
        for (int i = 0; i < SIZE; i++) {
            if (!isValidGroup(board[i]) || !isValidGroup(getColumn(board, i))) {
                return false;
            }
        }

        // Sprawdź podkwadraty 3x3
        for (int i = 0; i < SIZE; i += SUBGRID_SIZE) {
            for (int j = 0; j < SIZE; j += SUBGRID_SIZE) {
                if (!isValidGroup(getSubgrid(board, i, j))) {
                    return false;
                }
            }
        }

        return true;
    }

    private int[] getColumn(int[][] board, int col) {
        int[] column = new int[SIZE];
        for (int i = 0; i < SIZE; i++) {
            column[i] = board[i][col];
        }
        return column;
    }

    private int[] getSubgrid(int[][] board, int startRow, int startCol) {
        int[] subgrid = new int[SIZE];
        int index = 0;
        for (int i = startRow; i < startRow + SUBGRID_SIZE; i++) {
            for (int j = startCol; j < startCol + SUBGRID_SIZE; j++) {
                subgrid[index++] = board[i][j];
            }
        }
        return subgrid;
    }

    private boolean isValidGroup(int[] group) {
        boolean[] seen = new boolean[SIZE + 1];
        for (int num : group) {
            if (num != 0 && seen[num]) {
                return false; // Powtarzająca się liczba w grupie
            }
            seen[num] = true;
        }
        return true;
    }

    private boolean checkSudoku() {
        if (isSudokuSolved) {
            JOptionPane.showMessageDialog(Main.this, "Sudoku zostało już poprawnie rozwiązane!\nCzas rozwiązania: " + getFormattedTime(solveTime), "Informacja", JOptionPane.INFORMATION_MESSAGE);
            return true; // Zwróć true, aby zablokować dalsze działanie
        }

        int[][] currentBoard = new int[SIZE][SIZE];

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                try {
                    currentBoard[i][j] = Integer.parseInt(board[i][j].getText());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(Main.this, "Uzupełnij wszystkie pola przed sprawdzeniem Sudoku!", "Błąd", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        }

        if (!isBoardCompletelyFilled(currentBoard)) {
            JOptionPane.showMessageDialog(Main.this, "Uzupełnij wszystkie pola przed sprawdzeniem Sudoku!", "Błąd", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        boolean solved = isValidSudoku(currentBoard);

        if (solved) {
            timer.stop();
            solveTime = System.currentTimeMillis() - startTime;
            JOptionPane.showMessageDialog(Main.this, "Sudoku rozwiązane poprawnie!\nCzas rozwiązania: " + getFormattedTime(solveTime), "Wynik", JOptionPane.INFORMATION_MESSAGE);
            isSudokuSolved = true; // Ustaw flagę na true po poprawnym rozwiązaniu
            disableBoardEditing(); // Zablokuj edycję pól planszy
        } else {
            JOptionPane.showMessageDialog(Main.this, "Sudoku rozwiązane błędnie!", "Wynik", JOptionPane.ERROR_MESSAGE);
        }

        return solved;
    }

    private boolean isBoardCompletelyFilled(int[][] board) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }


    private String getFormattedTime(long timeInMillis) {
        long seconds = timeInMillis / 1000;
        long minutes = seconds / 60;
        seconds %= 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void disableBoardEditing() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j].setEditable(false);
            }
        }
    }
    private boolean solveSudoku(int[][] board) {
        int[] emptyCell = findEmptyCell(board);

        if (emptyCell == null) {
            return true;
        }

        int row = emptyCell[0];
        int col = emptyCell[1];

        int[] randomNumbers = generateRandomPermutation(SIZE);

        for (int num : randomNumbers) {
            if (isValid(board, row, col, num)) {
                board[row][col] = num;

                if (solveSudoku(board)) {
                    return true;
                }

                board[row][col] = 0;
            }
        }

        return false;
    }

    private int[] generateRandomPermutation(int size) {
        int[] permutation = new int[size];
        for (int i = 0; i < size; i++) {
            permutation[i] = i + 1;
        }

        Random random = new Random();
        for (int i = size - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);

            int temp = permutation[i];
            permutation[i] = permutation[j];
            permutation[j] = temp;
        }

        return permutation;
    }

    private boolean isValid(int[][] board, int row, int col, int num) {
        for (int i = 0; i < SIZE; i++) {
            if (board[row][i] == num || board[i][col] == num) {
                return false;
            }
        }

        int subgridStartRow = row - row % 3;
        int subgridStartCol = col - col % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[subgridStartRow + i][subgridStartCol + j] == num) {
                    return false;
                }
            }
        }

        return true;
    }

    private int[] findEmptyCell(int[][] board) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 0) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    static class SudokuDocumentFilter extends DocumentFilter {
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            String newText = fb.getDocument().getText(0, fb.getDocument().getLength()) + text;

            if (newText.matches("[1-9]") && newText.length() <= 1) {
                super.replace(fb, offset, length, text, attrs);
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main sudokuSolver = new Main();
        });
    }
}

