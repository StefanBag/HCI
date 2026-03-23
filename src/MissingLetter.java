import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
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

	public MissingLetter() {
		// Initialize the list of words
		String[] initialWords = { "APPLE", "BANANA", "CARROT", "DORK", "EVIL", "FORTNITE", "GRASS", "HARP", "IMMUNE", "JAMIE" };
		Collections.addAll(gameWords, initialWords);
		totalWords = gameWords.size();

		setTitle("Missing Letter");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 860, 510);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// Background Label
		JLabel Background = new JLabel("");
		Background.setIcon(new ImageIcon(MissingLetter.class.getResource("/Resources/Images/GreenBoard.jpg")));
		Background.setBounds(0, 0, 844, 471);
		contentPane.add(Background);

		// Instructions
		JTextArea InstructionsTxt = new JTextArea();
		InstructionsTxt.setText("Type the missing letter in the box and click SUBMIT. Get them all right to win!");
		InstructionsTxt.setWrapStyleWord(true);
		InstructionsTxt.setLineWrap(true);
		InstructionsTxt.setFont(new Font("Javanese Text", Font.PLAIN, 16));
		InstructionsTxt.setEditable(false);
		InstructionsTxt.setBackground(Color.YELLOW);
		InstructionsTxt.setBounds(319, 24, 515, 64);
		Background.add(InstructionsTxt);

		// Back Button
		JButton BackBtn = new JButton("BACK");
		BackBtn.setBounds(37, 29, 89, 23);
		BackBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Return to MainMenu
				MainMenu x = new MainMenu(); 
				x.setVisible(true);
				dispose();
			}
		});
		Background.add(BackBtn);
		
		// Progress Counter Label
		lblCounter = new JLabel("0 / " + totalWords);
		lblCounter.setForeground(Color.WHITE);
		lblCounter.setFont(new Font("Arial", Font.BOLD, 24));
		lblCounter.setBounds(37, 80, 150, 30);
		Background.add(lblCounter);
		
		// Container for the word slots
		wordPanel = new JPanel();
		wordPanel.setOpaque(false); 
		wordPanel.setBounds(0, 180, 844, 100);
		wordPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 0));
		Background.add(wordPanel);
		
		// Input Field for the single letter
		inputField = new JTextField();
		inputField.setHorizontalAlignment(SwingConstants.CENTER);
		inputField.setFont(new Font("Arial", Font.BOLD, 25));
		inputField.setBounds(370, 300, 100, 50);
		Background.add(inputField);
		inputField.setColumns(10);
		
		// Submit Button
		JButton btnSubmit = new JButton("SUBMIT");
		btnSubmit.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnSubmit.setBounds(370, 360, 100, 40);
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
			JOptionPane.showMessageDialog(this, "YOU WIN! Click BACK to return.");
			inputField.setEnabled(false);
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
		char correctLetter = currentWord.charAt(hiddenIndex);
		
		if (userGuess.length() == 1 && userGuess.charAt(0) == correctLetter) {
			JOptionPane.showMessageDialog(this, "CORRECT!");
			gameWords.remove(0); // Remove from list since they got it right
			correctCount++;
			lblCounter.setText(correctCount + " / " + totalWords);
		} else {
			JOptionPane.showMessageDialog(this, "INCORRECT! The word was: " + currentWord);
			// Move the word to the back of the list
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
	        
	        // If this is the index we picked, show a "?"
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
}
