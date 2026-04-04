import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Random;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * Wordle-style spelling game: Easy = 5-letter word, Hard = 6-letter word, 6 guesses each.
 */
public class Wordle extends JFrame {

	private static final long serialVersionUID = 1L;

	/** 5-letter words only. */
	private static final String[] WORD_BANK_5 = { "CRANE", "PLANT", "HOUSE", "GRAPE", "LIGHT", "BRAIN", "CHAIR",
			"STORM", "APPLE", "BEACH", "DANCE", "EARTH", "FLAME", "GHOST", "HEART", "IMAGE", "JOKER", "KNIFE", "LEMON",
			"MUSIC" };

	/** 6-letter words only (Hard difficulty). */
	private static final String[] WORD_BANK_6 = { "CRANES", "PLANTS", "BRIGHT", "DOCTOR", "SIMPLE", "MOTHER", "FATHER",
			"SCHOOL", "GARDEN", "LETTER", "NATURE", "POCKET", "RUBBER", "SIGNAL", "TICKET", "WINDOW", "YELLOW", "ABSORB",
			"BRIDGE", "CAMERA" };

	private static final int ROWS = 6;
	/** Max columns in the grid (Hard uses all 6; Easy hides the last column). */
	private static final int MAX_COLS = 6;
	private static final int TILE = 40;
	private static final int GAP = 5;
	private static final int GRID_TOP = 178;

	private static final Color TILE_EMPTY_BG = Color.WHITE;
	private static final Color TILE_TEXT = Color.BLACK;
	private static final Color FEEDBACK_GREEN = new Color(108, 169, 101);
	private static final Color FEEDBACK_YELLOW = new Color(200, 180, 86);
	private static final Color FEEDBACK_GRAY = new Color(120, 124, 126);

	private JPanel contentPane;
	private final JLabel[][] tileLabels = new JLabel[ROWS][MAX_COLS];
	private JLabel statusLabel;
	private JTextArea instructionsTxt;
	private JLabel typingHintLabel;
	private JButton submitBtn;
	private JButton newGameBtn;
	private JRadioButton easyRadio;
	private JRadioButton hardRadio;

	private final Random rng = new Random();

	/** Number of letters: 5 (Easy) or 6 (Hard). */
	private int cols = 5;
	private String secretWord;
	private StringBuilder currentGuess = new StringBuilder();
	private int currentRow;
	private boolean gameOver;
	private final JLabel Background = new JLabel("");
	private String[] words;

	public Wordle(String[] words) {
		this.words = words;
		currentRow = 0;
		gameOver = false;

		setTitle("Wordle");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 860, 620);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		instructionsTxt = new JTextArea();
		instructionsTxt.setMargin(new Insets(4, 4, 4, 4));
		instructionsTxt.setBackground(Color.YELLOW);
		instructionsTxt.setForeground(Color.BLACK);
		instructionsTxt.setFont(new Font("Javanese Text", Font.PLAIN, 14));
		instructionsTxt.setWrapStyleWord(true);
		instructionsTxt.setLineWrap(true);
		instructionsTxt.setEditable(false);
		instructionsTxt.setBounds(165, 84, 600, 60);
		contentPane.add(instructionsTxt);

		JButton BackBtn = new JButton();
		BackBtn.setBorderPainted(false);
		BackBtn.setOpaque(false);
		BackBtn.setContentAreaFilled(false);
		BackBtn.setIcon(new ImageIcon(Wordle.class.getResource("/Resources/Images/back.png")));
		BackBtn.setPressedIcon(new ImageIcon(Wordle.class.getResource("/Resources/Images/back-Pressed.png")));
		BackBtn.setBounds(60, 84, 89, 34);
		BackBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.showConfirmDialog(Wordle.this,
						"Your current game progress will be lost. Are you sure you want to return to the main menu?",
						"Exit to Main Menu", JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_OPTION) {
					MainMenu x = new MainMenu(words);
					x.setVisible(true);
					dispose();
				}
			}
		});
		contentPane.add(BackBtn);

		JLabel diffLabel = new JLabel("Difficulty:");
		diffLabel.setFont(new Font("Javanese Text", Font.PLAIN, 14));
		diffLabel.setOpaque(true);
		diffLabel.setBackground(Color.YELLOW);
		diffLabel.setForeground(Color.BLACK);
		diffLabel.setBounds(165, 148, 80, 26);
		contentPane.add(diffLabel);

		easyRadio = new JRadioButton("Easy (5 letters)", true);
		easyRadio.setBounds(245, 148, 130, 26);
		easyRadio.setOpaque(true);
		easyRadio.setBackground(Color.YELLOW);
		hardRadio = new JRadioButton("Hard (6 letters)");
		hardRadio.setBounds(375, 148, 150, 26);
		hardRadio.setOpaque(true);
		hardRadio.setBackground(Color.YELLOW);
		ButtonGroup diffGroup = new ButtonGroup();
		diffGroup.add(easyRadio);
		diffGroup.add(hardRadio);
		contentPane.add(easyRadio);
		contentPane.add(hardRadio);

		ActionListener difficultyListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int newCols = easyRadio.isSelected() ? 5 : 6;
				if (newCols != cols) {
					if (currentRow > 0 && !gameOver) {
						int result = JOptionPane.showConfirmDialog(Wordle.this,
								"Your current game progress will be lost. Are you sure you want to change difficulty?",
								"Change Difficulty", JOptionPane.YES_NO_OPTION);
						if (result != JOptionPane.YES_OPTION) {
							// Revert the radio button selection
							if (cols == 5) easyRadio.setSelected(true);
							else hardRadio.setSelected(true);
							return;
						}
					}
					cols = newCols;
					onDifficultyChanged();
				}
			}
		};
		easyRadio.addActionListener(difficultyListener);
		hardRadio.addActionListener(difficultyListener);

		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < MAX_COLS; c++) {
				JLabel tile = new JLabel("", SwingConstants.CENTER);
				tile.setOpaque(true);
				tile.setBackground(TILE_EMPTY_BG);
				tile.setForeground(TILE_TEXT);
				tile.setFont(new Font("Mongolian Baiti", Font.BOLD, 22));
				tile.setBorder(new LineBorder(Color.DARK_GRAY, 2));
				tileLabels[r][c] = tile;
				contentPane.add(tile);
			}
		}

		typingHintLabel = new JLabel();
		typingHintLabel.setFont(new Font("Javanese Text", Font.PLAIN, 14));
		typingHintLabel.setHorizontalAlignment(SwingConstants.CENTER);
		typingHintLabel.setOpaque(true);
		typingHintLabel.setBackground(Color.YELLOW);
		typingHintLabel.setForeground(Color.BLACK);
		contentPane.add(typingHintLabel);

		statusLabel = new JLabel(" ");
		statusLabel.setFont(new Font("Javanese Text", Font.PLAIN, 13));
		statusLabel.setForeground(new Color(139, 0, 0));
		contentPane.add(statusLabel);

		submitBtn = new JButton();
		submitBtn.setIcon(new ImageIcon(Wordle.class.getResource("/Resources/Images/submitbutton.png")));
		submitBtn.setPressedIcon(new ImageIcon(Wordle.class.getResource("/Resources/Images/submitbutton-pressed.png")));
		submitBtn.setOpaque(false);
		submitBtn.setContentAreaFilled(false);
		submitBtn.setBorderPainted(false);
		submitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				submitGuess();
			}
		});
		contentPane.add(submitBtn);

		newGameBtn = new JButton();
		newGameBtn.setIcon(new ImageIcon(Wordle.class.getResource("/Resources/Images/restart.png")));
		newGameBtn.setPressedIcon(new ImageIcon(Wordle.class.getResource("/Resources/Images/restart-pressed.png")));
		newGameBtn.setOpaque(false);
		newGameBtn.setContentAreaFilled(false);
		newGameBtn.setBorderPainted(false);
		newGameBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (currentRow > 0 && !gameOver) {
					int result = JOptionPane.showConfirmDialog(Wordle.this,
							"Your current game progress will be lost. Are you sure you want to restart?",
							"Restart Game", JOptionPane.YES_NO_OPTION);
					if (result != JOptionPane.YES_OPTION) return;
				}
				resetGame();
			}
		});
		contentPane.add(newGameBtn);
		installKeyboardInput();

		pickNewSecretWord();
		updateInstructionText();
		applyDifficultyLayout();
		syncGuessToGrid();

		Image boardImg = new ImageIcon(Wordle.class.getResource("/Resources/Images/GreenBoard.jpg")).getImage();
		Background.setIcon(new ImageIcon(boardImg.getScaledInstance(844, 581, Image.SCALE_SMOOTH)));
		Background.setBounds(0, 0, 844, 581);
		contentPane.add(Background);
	}

	private void updateInstructionText() {
		instructionsTxt.setText("Guess the " + cols + "-letter word in 6 tries. Type on keyboard\n"
				+ "and press SUBMIT or Enter.\n"
				+ "Green = correct position. Yellow = wrong position. Gray = not in word.");
	}

	private void installKeyboardInput() {
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
			if (!isActive() || !isFocused() || gameOver) {
				return false;
			}
			if (e.getID() != KeyEvent.KEY_PRESSED) {
				return false;
			}
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				submitGuess();
				return false;
			}
			if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE) {
				if (currentGuess.length() > 0) {
					currentGuess.deleteCharAt(currentGuess.length() - 1);
					syncGuessToGrid();
				}
				return false;
			}
			char ch = Character.toUpperCase(e.getKeyChar());
			if (Character.isLetter(ch) && currentGuess.length() < cols) {
				currentGuess.append(ch);
				syncGuessToGrid();
			}
			return false;
		});
	}

	
	private void onDifficultyChanged() {
		pickNewSecretWord();
		currentRow = 0;
		gameOver = false;
		currentGuess.setLength(0);
		clearAllTiles();
		statusLabel.setText(" ");
		updateInstructionText();
		applyDifficultyLayout();
		syncGuessToGrid();
	}

	private void clearAllTiles() {
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < MAX_COLS; c++) {
				tileLabels[r][c].setText("");
				tileLabels[r][c].setBackground(TILE_EMPTY_BG);
				tileLabels[r][c].setForeground(TILE_TEXT);
			}
		}
	}

	private void applyDifficultyLayout() {
		int gridWidth = cols * TILE + (cols - 1) * GAP;
		int gridX = (844 - gridWidth) / 2;
		int gridHeight = ROWS * TILE + (ROWS - 1) * GAP;

		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < MAX_COLS; c++) {
				JLabel tile = tileLabels[r][c];
				if (c >= cols) {
					tile.setVisible(false);
				} else {
					tile.setVisible(true);
					tile.setBounds(gridX + c * (TILE + GAP), GRID_TOP + r * (TILE + GAP), TILE, TILE);
				}
			}
		}

		typingHintLabel.setText("Type your guess on keyboard (" + cols + " letters), Backspace to erase:");
		int hintY = GRID_TOP + gridHeight + 8;
		int buttonY = hintY + 26;
		int hintWidth = gridWidth + 240;
		int hintX = (844 - hintWidth) / 2;
		typingHintLabel.setBounds(hintX, hintY, hintWidth, 22);
		statusLabel.setBounds(hintX, hintY + 22, hintWidth, 0);
		submitBtn.setBounds(gridX, buttonY, 89, 28);
		newGameBtn.setBounds(gridX + 100, buttonY, 89, 28);
	}

	private void pickNewSecretWord() {
		String[] defaultBank = cols == 5 ? WORD_BANK_5 : WORD_BANK_6;

		// Collect user-inputted words that match the current difficulty length
		java.util.List<String> userMatches = new java.util.ArrayList<>();
		if (words != null) {
			for (String w : words) {
				if (w != null && w.trim().length() == cols) {
					userMatches.add(w.trim().toUpperCase());
				}
			}
		}

		// Build pool: user matches added 3x for higher priority, then default bank
		java.util.List<String> pool = new java.util.ArrayList<>();
		for (int i = 0; i < 3; i++) pool.addAll(userMatches);
		for (String w : defaultBank) pool.add(w);

		secretWord = pool.get(rng.nextInt(pool.size()));
	}

	private void resetGame() {
		pickNewSecretWord();
		currentRow = 0;
		gameOver = false;
		currentGuess.setLength(0);
		submitBtn.setEnabled(true);
		newGameBtn.setEnabled(true);
		newGameBtn.setVisible(true);
		clearAllTiles();
		typingHintLabel.setText("Type your guess on keyboard (" + cols + " letters), Backspace to erase:");
		syncGuessToGrid();
	}

	private void syncGuessToGrid() {
		if (gameOver || currentRow >= ROWS) {
			return;
		}
		for (int c = 0; c < cols; c++) {
			JLabel tile = tileLabels[currentRow][c];
			if (c < currentGuess.length()) {
				tile.setText(String.valueOf(currentGuess.charAt(c)));
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
		String guess = currentGuess.toString();

		if (guess.length() != cols) {
			JOptionPane.showMessageDialog(Wordle.this,
					"Please enter exactly " + cols + " letters (A–Z). Current length: " + guess.length() + ".",
					"Invalid guess", JOptionPane.WARNING_MESSAGE);
			return;
		}

		Color[] feedback = computeFeedback(secretWord, guess);
		for (int c = 0; c < cols; c++) {
			JLabel tile = tileLabels[currentRow][c];
			tile.setText(String.valueOf(guess.charAt(c)));
			tile.setBackground(feedback[c]);
			tile.setForeground(Color.WHITE);
		}

		if (guess.equals(secretWord)) {
			gameOver = true;
			submitBtn.setEnabled(false);
			JOptionPane.showMessageDialog(Wordle.this, "You guessed the word: " + secretWord + "!", "You win",
					JOptionPane.INFORMATION_MESSAGE);
			promptTryAnotherWordle();
			return;
		}

		currentRow++;
		currentGuess.setLength(0);
		syncGuessToGrid();

		if (currentRow >= ROWS) {
			gameOver = true;
			submitBtn.setEnabled(false);
			JOptionPane.showMessageDialog(Wordle.this,
					"No guesses left. The word was: " + secretWord + ".", "Game over", JOptionPane.INFORMATION_MESSAGE);
			promptTryAnotherWordle();
		}
	}

	private void promptTryAnotherWordle() {
		int option = JOptionPane.showConfirmDialog(Wordle.this, "Try another Wordle?", "Play again",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (option == JOptionPane.YES_OPTION) {
			resetGame();
		} else {
			newGameBtn.setVisible(true);
			newGameBtn.setEnabled(true);
			newGameBtn.requestFocusInWindow();
			typingHintLabel.setText("Press New Game to play again, or Back to return to menu.");
		}
	}

	private static Color[] computeFeedback(String secret, String guess) {
		int n = secret.length();
		Color[] result = new Color[n];
		int[] remaining = new int[26];
		for (int i = 0; i < n; i++) {
			remaining[secret.charAt(i) - 'A']++;
		}
		boolean[] green = new boolean[n];

		for (int i = 0; i < n; i++) {
			if (secret.charAt(i) == guess.charAt(i)) {
				green[i] = true;
				result[i] = FEEDBACK_GREEN;
				remaining[guess.charAt(i) - 'A']--;
			}
		}
		for (int i = 0; i < n; i++) {
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
