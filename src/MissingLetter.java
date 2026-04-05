import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class MissingLetter extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField inputField;
	private JLabel lblCounter;
	private JPanel wordPanel;

	// Game variables
	private ArrayList<String> gameWords = new ArrayList<>();
	private int totalWords;
	private int correctCount = 0;
	private String currentWord;
	private int hiddenIndex;
	private String[] words;

	public MissingLetter(String[] words) {
		this.words = words;

		// Initialize the list of words
		Collections.addAll(gameWords, words);
		totalWords = gameWords.size();

		setTitle("Missing Letter");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 860, 510);
		setLocationRelativeTo(null);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// Background Label
		JLabel Background = new JLabel("");
		Background.setIcon(new ImageIcon(MissingLetter.class.getResource("/Resources/Images/GreenBoard.jpg")));
		Background.setBounds(0, 0, 844, 471);
		Background.setLayout(null);
		contentPane.add(Background);

		// Instructions panel
		JPanel instructionPanel = new JPanel(new BorderLayout());
		instructionPanel.setBackground(new Color(255, 240, 0));
		instructionPanel.setBorder(new LineBorder(new Color(0, 140, 70), 4, true));
		instructionPanel.setBounds(42, 34, 776, 90);

		JLabel instructionLabel = new JLabel(
			"<html><div style='text-align: center;'>Type the missing letter in the box and click SUBMIT.<br>Get them all right to win!</div></html>",
			JLabel.CENTER
		);
		instructionLabel.setFont(new Font("Arial", Font.PLAIN, 20));
		instructionLabel.setForeground(Color.BLACK);
		instructionLabel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

		instructionPanel.add(instructionLabel, BorderLayout.CENTER);
		Background.add(instructionPanel);

		// Score label
		lblCounter = new JLabel("Score: 0 out of " + totalWords, JLabel.CENTER);
		lblCounter.setForeground(Color.YELLOW);
		lblCounter.setFont(new Font("Arial", Font.BOLD, 28));
		lblCounter.setBounds(0, 125, 844, 35);
		Background.add(lblCounter);

		// Back button
		JButton BackBtn = new JButton();
		BackBtn.setBorderPainted(false);
		BackBtn.setOpaque(false);
		BackBtn.setContentAreaFilled(false);
		BackBtn.setIcon(new ImageIcon(MissingLetter.class.getResource("/Resources/Images/back.png")));
		BackBtn.setPressedIcon(new ImageIcon(MissingLetter.class.getResource("/Resources/Images/back-Pressed.png")));
		BackBtn.setBounds(42, 126, 89, 34);
		BackBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.showConfirmDialog(
					MissingLetter.this,
					"Your current progress will be lost. Are you sure you want to return to the main menu?",
					"Exit to Main Menu",
					JOptionPane.YES_NO_OPTION
				);
				if (result == JOptionPane.YES_OPTION) {
					MainMenu x = new MainMenu(words);
					x.setVisible(true);
					dispose();
				}
			}
		});
		Background.add(BackBtn);

		// Container for the word slots
		wordPanel = new JPanel();
		wordPanel.setOpaque(false);
		wordPanel.setBounds(0, 200, 844, 100);
		wordPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 0));
		Background.add(wordPanel);

		// Input Field for the single letter
		inputField = new JTextField();
		inputField.setHorizontalAlignment(SwingConstants.CENTER);
		inputField.setFont(new Font("Arial", Font.BOLD, 25));
		inputField.setBounds(370, 310, 100, 50);
		Background.add(inputField);
		inputField.setColumns(10);

		// Submit Button
		JButton btnSubmit = new JButton();
		btnSubmit.setIcon(new ImageIcon(MissingLetter.class.getResource("/Resources/Images/submitbutton.png")));
		btnSubmit.setPressedIcon(
			new ImageIcon(MissingLetter.class.getResource("/Resources/Images/submitbutton-pressed.png")));
		btnSubmit.setOpaque(false);
		btnSubmit.setContentAreaFilled(false);
		btnSubmit.setBorderPainted(false);
		btnSubmit.setToolTipText("Submit your answer");
		btnSubmit.setBounds(376, 370, 89, 34);
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				checkAnswer();
			}
		});
		Background.add(btnSubmit);

		// Start the first round
		nextRound();
	}

	// Logic to pick the next word and display it
	public void nextRound() {
		if (gameWords.isEmpty()) {
			JOptionPane.showMessageDialog(
				this,
				"Congratulations! You beat the game.\nThe game will now be reset.\nIf you wish to leave, click the BACK button."
			);
			resetGame();
			return;
		}

		// Take the first word from the list
		currentWord = gameWords.get(0).toUpperCase();

		// Pick a random index to hide
		Random rand = new Random();
		hiddenIndex = rand.nextInt(currentWord.length());

		generateWordSlots(wordPanel, currentWord, hiddenIndex);
		inputField.setText("");
		inputField.requestFocus();
	}

	// Logic to check if the user is correct
	private void checkAnswer() {
		String userGuess = inputField.getText().trim().toUpperCase();

		if (userGuess.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Empty field. You need to enter a letter.");
			return;
		}

		if (userGuess.length() != 1 || !Character.isLetter(userGuess.charAt(0))) {
			JOptionPane.showMessageDialog(this, "Please enter one single letter.");
			return;
		}

		char correctLetter = currentWord.charAt(hiddenIndex);

		if (userGuess.charAt(0) == correctLetter) {
			JOptionPane.showMessageDialog(this, "CORRECT!");
			gameWords.remove(0);
			correctCount++;
			lblCounter.setText("Score: " + correctCount + " out of " + totalWords);
		} else {
			JOptionPane.showMessageDialog(this, "INCORRECT!");
			String failedWord = gameWords.remove(0);
			gameWords.add(failedWord);
		}

		nextRound();
	}

	// Dynamically draws the letters on screen
	public void generateWordSlots(JPanel container, String word, int hideIdx) {
		container.removeAll();

		for (int i = 0; i < word.length(); i++) {
			JLabel slot = new JLabel();

			if (i == hideIdx) {
				slot.setText("?");
				slot.setForeground(Color.RED);
			} else {
				slot.setText(String.valueOf(word.charAt(i)));
				slot.setForeground(Color.BLACK);
			}

			slot.setOpaque(true);
			slot.setBackground(Color.WHITE);
			slot.setHorizontalAlignment(JLabel.CENTER);
			slot.setFont(new Font("Arial", Font.BOLD, 30));
			slot.setPreferredSize(new java.awt.Dimension(60, 60));
			slot.setBorder(javax.swing.BorderFactory.createLineBorder(Color.BLACK, 2));

			container.add(slot);
		}

		container.revalidate();
		container.repaint();
	}

	// Reset the game back to the start
	private void resetGame() {
		gameWords.clear();
		Collections.addAll(gameWords, words);

		totalWords = gameWords.size();
		correctCount = 0;
		currentWord = null;
		hiddenIndex = -1;

		lblCounter.setText("Score: 0 out of " + totalWords);
		inputField.setText("");
		inputField.setEnabled(true);
		inputField.requestFocus();

		nextRound();
	}
}