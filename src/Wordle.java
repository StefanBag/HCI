import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * Wordle-style spelling game: guess a 5-letter word in up to 6 tries.
 * Layout and style follow {@link Alphabetical} and {@link MainMenu}.
 */
public class Wordle extends JFrame {

	private static final long serialVersionUID = 1L;

	/** EDIT: Add or change 5-letter words (uppercase recommended). All must be exactly 5 letters. */
	private static final String[] WORD_BANK = { "CRANE", "PLANT", "HOUSE", "GRAPE", "LIGHT", "BRAIN", "CHAIR", "STORM",
			"APPLE", "BEACH", "DANCE", "EARTH", "FLAME", "GHOST", "HEART", "IMAGE", "JOKER", "KNIFE", "LEMON", "MUSIC" };

	private static final int ROWS = 6;
	private static final int COLS = 5;
	private static final int TILE = 48;
	private static final int GAP = 6;

	private static final Color TILE_EMPTY_BG = Color.WHITE;
	private static final Color TILE_TEXT = Color.BLACK;
	private static final Color FEEDBACK_GREEN = new Color(108, 169, 101);
	private static final Color FEEDBACK_YELLOW = new Color(200, 180, 86);
	private static final Color FEEDBACK_GRAY = new Color(120, 124, 126);

	private JPanel contentPane;
	private final JLabel[][] tileLabels = new JLabel[ROWS][COLS];
	private JTextField guessField;
	private JLabel statusLabel;
	private final Random rng = new Random();

	private String secretWord;
	private int currentRow;
	private boolean gameOver;
	private final JLabel Background = new JLabel("");

	public Wordle() {
		pickNewSecretWord();
		currentRow = 0;
		gameOver = false;

		setTitle("Wordle");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 860, 510);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// Instructions (same pattern as Alphabetical: yellow JTextArea)
		JTextArea InstructionsTxt = new JTextArea();
		InstructionsTxt.setBackground(Color.YELLOW);
		InstructionsTxt.setForeground(Color.BLACK);
		InstructionsTxt.setFont(new Font("Javanese Text", Font.PLAIN, 14));
		InstructionsTxt.setWrapStyleWord(true);
		InstructionsTxt.setLineWrap(true);
		InstructionsTxt.setEditable(false);
		InstructionsTxt.setText(
				"Guess the 5-letter word in 6 tries. Type a word and press SUBMIT or Enter.\n"
						+ "Green = correct letter and position. Yellow = letter is in the word but wrong position. "
						+ "Gray = letter is not in the word.");
		InstructionsTxt.setBounds(200, 24, 620, 64);
		contentPane.add(InstructionsTxt);

		// Back button
		JButton BackBtn = new JButton("BACK");
		BackBtn.setBounds(37, 29, 89, 23);
		BackBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainMenu x = new MainMenu();
				x.setVisible(true);
				dispose();
			}
		});
		contentPane.add(BackBtn);

		// Tile grid
		int gridWidth = COLS * TILE + (COLS - 1) * GAP;
		int gridHeight = ROWS * TILE + (ROWS - 1) * GAP;
		int gridX = (844 - gridWidth) / 2;
		int gridY = 100;

		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				JLabel tile = new JLabel("", SwingConstants.CENTER);
				tile.setOpaque(true);
				tile.setBackground(TILE_EMPTY_BG);
				tile.setForeground(TILE_TEXT);
				tile.setFont(new Font("Mongolian Baiti", Font.BOLD, 22));
				tile.setBorder(new LineBorder(Color.DARK_GRAY, 2));
				tile.setBounds(gridX + c * (TILE + GAP), gridY + r * (TILE + GAP), TILE, TILE);
				tileLabels[r][c] = tile;
				contentPane.add(tile);
			}
		}

		JLabel guessCaption = new JLabel("Your guess (5 letters):");
		guessCaption.setFont(new Font("Javanese Text", Font.PLAIN, 14));
		guessCaption.setBounds(gridX, gridY + gridHeight + 16, 200, 20);
		contentPane.add(guessCaption);

		guessField = new JTextField();
		guessField.setFont(new Font("Mongolian Baiti", Font.PLAIN, 20));
		guessField.setBounds(gridX + 200, gridY + gridHeight + 12, 160, 28);
		guessField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char ch = e.getKeyChar();
				if (Character.isLetter(ch)) {
					e.setKeyChar(Character.toUpperCase(ch));
				} else if (ch != KeyEvent.VK_BACK_SPACE && ch != KeyEvent.VK_DELETE) {
					e.consume();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				String t = guessField.getText().toUpperCase().replaceAll("[^A-Z]", "");
				if (t.length() > 5) {
					guessField.setText(t.substring(0, 5));
				} else if (!t.equals(guessField.getText())) {
					guessField.setText(t);
				}
				syncGuessToGrid();
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					submitGuess();
				}
			}
		});
		contentPane.add(guessField);

		statusLabel = new JLabel(" ");
		statusLabel.setFont(new Font("Javanese Text", Font.PLAIN, 13));
		statusLabel.setForeground(new Color(139, 0, 0));
		statusLabel.setBounds(gridX, gridY + gridHeight + 44, gridWidth + 200, 22);
		contentPane.add(statusLabel);

		// Submit
		JButton SubmitBtn = new JButton("SUBMIT");
		SubmitBtn.setBounds(gridX, gridY + gridHeight + 72, 100, 28);
		SubmitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				submitGuess();
			}
		});
		contentPane.add(SubmitBtn);

		// New game / reset
		JButton NewGameBtn = new JButton("New Game");
		NewGameBtn.setBounds(gridX + 120, gridY + gridHeight + 72, 110, 28);
		NewGameBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetGame();
			}
		});
		contentPane.add(NewGameBtn);

		syncGuessToGrid();

		Background.setIcon(new ImageIcon(Wordle.class.getResource("/Resources/Images/GreenBoard.jpg")));
		Background.setBounds(0, 0, 844, 471);
		contentPane.add(Background);
	}

	/** EDIT: Change how the secret word is chosen if you load words from a file. */
	private void pickNewSecretWord() {
		secretWord = WORD_BANK[rng.nextInt(WORD_BANK.length)];
	}

	private void resetGame() {
		pickNewSecretWord();
		currentRow = 0;
		gameOver = false;
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				tileLabels[r][c].setText("");
				tileLabels[r][c].setBackground(TILE_EMPTY_BG);
				tileLabels[r][c].setForeground(TILE_TEXT);
			}
		}
		guessField.setText("");
		guessField.setEnabled(true);
		statusLabel.setText(" ");
		syncGuessToGrid();
	}

	/** Show the in-progress guess on the active row (before submit). */
	private void syncGuessToGrid() {
		if (guessField == null || gameOver || !guessField.isEnabled()) {
			return;
		}
		String t = guessField.getText().toUpperCase().replaceAll("[^A-Z]", "");
		if (t.length() > 5) {
			t = t.substring(0, 5);
		}
		for (int c = 0; c < COLS; c++) {
			JLabel tile = tileLabels[currentRow][c];
			if (c < t.length()) {
				tile.setText(String.valueOf(t.charAt(c)));
			} else {
				tile.setText("");
			}
			tile.setBackground(TILE_EMPTY_BG);
			tile.setForeground(TILE_TEXT);
		}
	}

	private void submitGuess() {
		statusLabel.setText(" ");
		if (gameOver) {
			return;
		}

		String raw = guessField.getText();
		if (raw == null) {
			raw = "";
		}
		String guess = raw.trim().toUpperCase().replaceAll("[^A-Z]", "");

		if (guess.length() != 5) {
			JOptionPane.showMessageDialog(Wordle.this,
					"Please enter exactly 5 letters (A–Z). Current length: " + guess.length() + ".", "Invalid guess",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		// Wordle-style feedback: greens first, then yellows with correct duplicate handling
		Color[] feedback = computeFeedback(secretWord, guess);
		for (int c = 0; c < COLS; c++) {
			JLabel tile = tileLabels[currentRow][c];
			tile.setText(String.valueOf(guess.charAt(c)));
			tile.setBackground(feedback[c]);
			tile.setForeground(Color.WHITE);
		}

		if (guess.equals(secretWord)) {
			gameOver = true;
			guessField.setEnabled(false);
			JOptionPane.showMessageDialog(Wordle.this, "You guessed the word: " + secretWord + "!", "You win",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		currentRow++;
		guessField.setText("");
		syncGuessToGrid();

		if (currentRow >= ROWS) {
			gameOver = true;
			guessField.setEnabled(false);
			JOptionPane.showMessageDialog(Wordle.this,
					"No guesses left. The word was: " + secretWord + ".", "Game over", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Standard Wordle coloring: green for exact matches; yellow uses remaining
	 * letter counts after greens (correct for duplicate letters).
	 */
	private static Color[] computeFeedback(String secret, String guess) {
		Color[] result = new Color[COLS];
		int[] remaining = new int[26];
		for (int i = 0; i < COLS; i++) {
			remaining[secret.charAt(i) - 'A']++;
		}
		boolean[] green = new boolean[COLS];

		for (int i = 0; i < COLS; i++) {
			if (secret.charAt(i) == guess.charAt(i)) {
				green[i] = true;
				result[i] = FEEDBACK_GREEN;
				remaining[guess.charAt(i) - 'A']--;
			}
		}
		for (int i = 0; i < COLS; i++) {
			if (green[i]) {
				continue;
			}
			int idx = guess.charAt(i) - 'A';
			if (remaining[idx] > 0) {
				result[i] = FEEDBACK_YELLOW;
				remaining[idx]--;
			} else {
				result[i] = FEEDBACK_GRAY;
			}
		}
		return result;
	}
}
