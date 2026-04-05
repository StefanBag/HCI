import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class WordSearch extends JFrame {

    private static final long serialVersionUID = 1L;

    private final String[] words;
    private final int gridSize;  // number of rows/cols in the grid
    private final int cell;      // pixel size of each grid cell

    private char[][] grid;
    private JLabel[][] cellLabels;
    private boolean[][] foundCells; // cells that belong to a found word

    // Selection state: -1 means nothing selected
    private int startRow = -1;
    private int startCol = -1;

    private boolean[] wordFound;
    private JLabel[] wordLabels;
    private int foundCount = 0;
    private JLabel scoreLabel;

    // Colors consistent with the rest of the app (white tiles, green board bg)
    private static final Color COLOR_DEFAULT  = Color.WHITE;
    private static final Color COLOR_SELECTED = new Color(255, 235, 59);  // yellow – selected start
    private static final Color COLOR_HOVER    = new Color(200, 230, 255); // light blue – hover
    private static final Color COLOR_FOUND    = new Color(102, 187, 106); // green – found word
    private static final Color COLOR_WRONG    = new Color(255, 138, 128); // red – wrong selection flash

    // -----------------------------------------------------------------------
    public WordSearch(String[] words) {
        this.words = words;
        this.wordFound  = new boolean[words.length];

        // Adapt grid size to the words: at least as wide as the longest word,
        // at least 10 cells, capped at 14 so it fits on screen.
        int maxLen = 0;
        for (String w : words) maxLen = Math.max(maxLen, w.length());
        this.gridSize = Math.min(14, Math.max(10, maxLen));
        // Each cell must fit within 280 px of width (the grid column of the layout)
        this.cell = 280 / gridSize;

        this.cellLabels = new JLabel[gridSize][gridSize];
        this.foundCells = new boolean[gridSize][gridSize];
        this.wordLabels = new JLabel[words.length];

        setTitle("Word Search");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 860, 510);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        buildUI(contentPane);
        generateGrid();
        renderGrid();
    }

    // -----------------------------------------------------------------------
    // UI construction
    // -----------------------------------------------------------------------
    private void buildUI(JPanel contentPane) {

        // --- Instruction panel (matches style in Alphabetical/MissingLetter) ---
        JPanel instructionPanel = new JPanel(new java.awt.BorderLayout());
        instructionPanel.setBackground(new Color(255, 240, 0));
        instructionPanel.setBorder(new LineBorder(new Color(0, 140, 70), 4, true));
        instructionPanel.setBounds(42, 34, 776, 90);

        JLabel instructionLabel = new JLabel(
            "<html><div style='text-align:center;'>"
            + "Click a starting letter, then click the ending letter to select a word.<br>"
            + "Words can be hidden horizontally, vertically, or diagonally!"
            + "</div></html>",
            JLabel.CENTER
        );
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        instructionLabel.setForeground(Color.BLACK);
        instructionLabel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        instructionPanel.add(instructionLabel, java.awt.BorderLayout.CENTER);
        contentPane.add(instructionPanel);

        // --- Back button ---
        JButton backBtn = new JButton();
        backBtn.setBorderPainted(false);
        backBtn.setOpaque(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setIcon(new ImageIcon(WordSearch.class.getResource("/Resources/Images/back.png")));
        backBtn.setPressedIcon(new ImageIcon(WordSearch.class.getResource("/Resources/Images/back-Pressed.png")));
        backBtn.setToolTipText("Return to Main Menu");
        backBtn.setBounds(42, 124, 89, 34);
        backBtn.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                WordSearch.this,
                "Your current progress will be lost. Return to the main menu?",
                "Exit to Main Menu",
                JOptionPane.YES_NO_OPTION
            );
            if (choice == JOptionPane.YES_OPTION) {
                new MainMenu(words).setVisible(true);
                dispose();
            }
        });
        contentPane.add(backBtn);

        // --- Score label (centred in the button row, matching MissingLetter style) ---
        scoreLabel = new JLabel("Found: 0 out of " + words.length, SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 22));
        scoreLabel.setForeground(Color.YELLOW);
        scoreLabel.setBounds(0, 124, 844, 34);
        contentPane.add(scoreLabel);

        // --- Reset button (top-right corner, matching Alphabetical style) ---
        JButton resetBtn = new JButton();
        resetBtn.setBorderPainted(false);
        resetBtn.setOpaque(false);
        resetBtn.setContentAreaFilled(false);
        resetBtn.setIcon(new ImageIcon(WordSearch.class.getResource("/Resources/Images/restart.png")));
        resetBtn.setPressedIcon(new ImageIcon(WordSearch.class.getResource("/Resources/Images/restart-pressed.png")));
        resetBtn.setToolTipText("Reset the puzzle");
        resetBtn.setBounds(713, 124, 89, 34);
        resetBtn.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                WordSearch.this,
                "Are you sure you want to reset the puzzle?",
                "Reset Puzzle",
                JOptionPane.YES_NO_OPTION
            );
            if (choice == JOptionPane.YES_OPTION) resetGame();
        });
        contentPane.add(resetBtn);

        // --- Grid panel ---
        int gridPx = gridSize * cell; // pixel size of the grid square
        int gridX  = 35;
        int gridY  = 162;

        JPanel gridPanel = new JPanel(null);
        gridPanel.setOpaque(false);
        gridPanel.setBounds(gridX, gridY, gridPx, gridPx);
        contentPane.add(gridPanel);

        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                JLabel lbl = new JLabel("", SwingConstants.CENTER);
                lbl.setOpaque(true);
                lbl.setBackground(COLOR_DEFAULT);
                lbl.setFont(new Font("Arial", Font.BOLD, Math.max(10, cell - 8)));
                lbl.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
                lbl.setBounds(c * cell, r * cell, cell, cell);

                final int row = r, col = c;
                lbl.addMouseListener(new MouseAdapter() {
                    @Override public void mouseClicked(MouseEvent e)  { handleCellClick(row, col); }
                    @Override public void mouseEntered(MouseEvent e)  { onCellEnter(row, col); }
                    @Override public void mouseExited(MouseEvent e)   { onCellExit(row, col); }
                });

                cellLabels[r][c] = lbl;
                gridPanel.add(lbl);
            }
        }

        // --- Word list panel (to the right of the grid) ---
        int listX = gridX + gridPx + 18;
        int listW = 844 - listX - 12;
        JPanel wordListPanel = new JPanel(null);
        wordListPanel.setOpaque(false);
        wordListPanel.setBounds(listX, gridY, listW, gridPx);
        contentPane.add(wordListPanel);

        buildWordList(wordListPanel, listW, gridPx);

        // --- Background image – added LAST so it stays behind every other component ---
        JLabel background = new JLabel();
        background.setIcon(new ImageIcon(WordSearch.class.getResource("/Resources/Images/GreenBoard.jpg")));
        background.setBounds(0, 0, 844, 471);
        contentPane.add(background);
    }

    /** Lays out the word labels in 1 or 2 columns inside the word-list panel. */
    private void buildWordList(JPanel panel, int panelW, int panelH) {
        boolean twoCol  = words.length > 8;
        int     colSize = twoCol ? (words.length + 1) / 2 : words.length;
        int     labelH  = Math.min(30, panelH / Math.max(colSize, 1));
        int     fontSize = words.length > 14 ? 13 : 15;
        int     col2X   = panelW / 2;

        for (int i = 0; i < words.length; i++) {
            boolean rightCol = twoCol && i >= colSize;
            int x    = rightCol ? col2X : 5;
            int yIdx = rightCol ? i - colSize : i;
            int y    = yIdx * labelH + (labelH - 18) / 2; // vertically centre within row

            JLabel lbl = new JLabel(words[i].toUpperCase());
            lbl.setFont(new Font("Arial", Font.BOLD, fontSize));
            lbl.setForeground(Color.WHITE);
            lbl.setBounds(x, y, col2X - 8, labelH);
            wordLabels[i] = lbl;
            panel.add(lbl);
        }
    }

    // -----------------------------------------------------------------------
    // Grid generation
    // -----------------------------------------------------------------------
    private void generateGrid() {
        grid = new char[gridSize][gridSize];
        Random rand = new Random();

        // Directions: {deltaRow, deltaCol}
        int[][] dirs = {
            { 0,  1}, { 0, -1},   // horizontal right / left
            { 1,  0}, {-1,  0},   // vertical down / up
            { 1,  1}, { 1, -1},   // diagonal ↘ ↙
            {-1,  1}, {-1, -1}    // diagonal ↗ ↖
        };

        // Shuffle words so shorter ones don't always get placed first
        List<String> shuffled = new ArrayList<>();
        for (String w : words) shuffled.add(w.toUpperCase());
        Collections.shuffle(shuffled, rand);

        for (String word : shuffled) {
            if (word.length() > gridSize) continue; // word longer than grid – skip
            boolean placed = false;
            for (int attempt = 0; attempt < 300 && !placed; attempt++) {
                int[] dir = dirs[rand.nextInt(dirs.length)];
                int dr = dir[0], dc = dir[1];

                // Compute valid starting-cell ranges so the word stays within bounds
                int minR = dr < 0 ? word.length() - 1 : 0;
                int maxR = dr > 0 ? gridSize - word.length() : gridSize - 1;
                int minC = dc < 0 ? word.length() - 1 : 0;
                int maxC = dc > 0 ? gridSize - word.length() : gridSize - 1;
                if (minR > maxR || minC > maxC) continue;

                int r = minR + rand.nextInt(maxR - minR + 1);
                int c = minC + rand.nextInt(maxC - minC + 1);

                // Check for conflicts with already-placed letters
                boolean fits = true;
                for (int i = 0; i < word.length(); i++) {
                    char existing = grid[r + i * dr][c + i * dc];
                    if (existing != 0 && existing != word.charAt(i)) {
                        fits = false;
                        break;
                    }
                }
                if (fits) {
                    for (int i = 0; i < word.length(); i++) {
                        grid[r + i * dr][c + i * dc] = word.charAt(i);
                    }
                    placed = true;
                }
            }
        }

        // Fill any remaining empty cells with random uppercase letters
        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                if (grid[r][c] == 0) {
                    grid[r][c] = (char) ('A' + rand.nextInt(26));
                }
            }
        }
    }

    /** Pushes the current grid characters into the cell JLabels. */
    private void renderGrid() {
        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                cellLabels[r][c].setText(String.valueOf(grid[r][c]));
                if (!foundCells[r][c]) {
                    cellLabels[r][c].setBackground(COLOR_DEFAULT);
                }
            }
        }
    }

    // -----------------------------------------------------------------------
    // Interaction handlers
    // -----------------------------------------------------------------------
    private void onCellEnter(int row, int col) {
        if (!foundCells[row][col] && !(row == startRow && col == startCol)) {
            cellLabels[row][col].setBackground(COLOR_HOVER);
        }
    }

    private void onCellExit(int row, int col) {
        if (!foundCells[row][col]) {
            if (row == startRow && col == startCol) {
                cellLabels[row][col].setBackground(COLOR_SELECTED);
            } else {
                cellLabels[row][col].setBackground(COLOR_DEFAULT);
            }
        }
    }

    private void handleCellClick(int row, int col) {
        if (startRow == -1) {
            // First click – mark as selection start
            startRow = row;
            startCol = col;
            cellLabels[row][col].setBackground(COLOR_SELECTED);
        } else {
            if (row == startRow && col == startCol) {
                // Clicking the same cell cancels the selection
                if (!foundCells[row][col]) cellLabels[row][col].setBackground(COLOR_DEFAULT);
                startRow = -1;
                startCol = -1;
                return;
            }
            // Second click – attempt to match
            int prevStartRow = startRow, prevStartCol = startCol;
            startRow = -1;
            startCol = -1;
            if (!foundCells[prevStartRow][prevStartCol]) {
                cellLabels[prevStartRow][prevStartCol].setBackground(COLOR_DEFAULT);
            }
            tryMatch(prevStartRow, prevStartCol, row, col);
        }
    }

    /**
     * Checks whether the line from (r1,c1) to (r2,c2) spells one of the
     * unfound words (forward or backward).  If so, highlights those cells
     * and marks the word found.  Otherwise briefly flashes red feedback.
     */
    private void tryMatch(int r1, int c1, int r2, int c2) {
        int dr = r2 - r1;
        int dc = c2 - c1;

        // Selection must be a straight line: same row, same col, or true diagonal
        boolean horizontal = (dr == 0 && dc != 0);
        boolean vertical   = (dc == 0 && dr != 0);
        boolean diagonal   = (Math.abs(dr) == Math.abs(dc) && dr != 0);

        if (!horizontal && !vertical && !diagonal) {
            JOptionPane.showMessageDialog(this,
                "Please select letters in a straight line\n(horizontal, vertical, or diagonal).",
                "Invalid Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int len  = Math.max(Math.abs(dr), Math.abs(dc)) + 1;
        int stepR = dr == 0 ? 0 : (dr > 0 ? 1 : -1);
        int stepC = dc == 0 ? 0 : (dc > 0 ? 1 : -1);

        // Build the selected string
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(grid[r1 + i * stepR][c1 + i * stepC]);
        }
        String forward  = sb.toString();
        String backward = sb.reverse().toString();

        for (int i = 0; i < words.length; i++) {
            if (wordFound[i]) continue;
            String target = words[i].toUpperCase();
            if (target.equals(forward) || target.equals(backward)) {
                markWordFound(i, r1, c1, len, stepR, stepC);
                return;
            }
        }

        // No match – flash the selection red for brief feedback
        flashCells(r1, c1, len, stepR, stepC, COLOR_WRONG);
    }

    private void markWordFound(int wordIdx, int r1, int c1, int len, int stepR, int stepC) {
        wordFound[wordIdx] = true;
        foundCount++;

        for (int i = 0; i < len; i++) {
            int r = r1 + i * stepR;
            int c = c1 + i * stepC;
            foundCells[r][c] = true;
            cellLabels[r][c].setBackground(COLOR_FOUND);
            cellLabels[r][c].setForeground(Color.WHITE);
        }

        // Strike-through and green tint on the word list label
        wordLabels[wordIdx].setText(
            "<html><strike>" + words[wordIdx].toUpperCase() + "</strike></html>"
        );
        wordLabels[wordIdx].setForeground(new Color(144, 238, 144));

        scoreLabel.setText("Found: " + foundCount + " out of " + words.length);

        if (foundCount == words.length) {
            JOptionPane.showMessageDialog(this,
                "Congratulations! You found all " + words.length + " words!",
                "Puzzle Complete!", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /** Briefly colours a line of cells, then restores any that aren't already found. */
    private void flashCells(int r1, int c1, int len, int stepR, int stepC, Color flashColor) {
        for (int i = 0; i < len; i++) {
            int r = r1 + i * stepR, c = c1 + i * stepC;
            if (!foundCells[r][c]) cellLabels[r][c].setBackground(flashColor);
        }
        Timer t = new Timer(400, e -> {
            for (int i = 0; i < len; i++) {
                int r = r1 + i * stepR, c = c1 + i * stepC;
                if (!foundCells[r][c]) cellLabels[r][c].setBackground(COLOR_DEFAULT);
            }
        });
        t.setRepeats(false);
        t.start();
    }

    // -----------------------------------------------------------------------
    // Reset
    // -----------------------------------------------------------------------
    private void resetGame() {
        foundCount = 0;
        startRow   = -1;
        startCol   = -1;

        for (int i = 0; i < words.length; i++) {
            wordFound[i] = false;
            wordLabels[i].setText(words[i].toUpperCase());
            wordLabels[i].setForeground(Color.WHITE);
        }
        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                foundCells[r][c] = false;
                cellLabels[r][c].setForeground(Color.BLACK);
            }
        }
        scoreLabel.setText("Found: 0 out of " + words.length);
        generateGrid();
        renderGrid();
    }
}
