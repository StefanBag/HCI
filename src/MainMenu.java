import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class MainMenu extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/*
	 * MainMenu GUI class which will replace the original Main Menu GUI from the old
	 * application we will modify
	 */
	public MainMenu() {
		setTitle("Main Menu");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 860, 510);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// word search button
		JButton WordSearchBtn = new JButton("WORD SEARCH");
		WordSearchBtn.setBounds(71, 62, 197, 95);
		WordSearchBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// word search button game action
				// open word search button window/GUI and close MainMenu window/GUI
				WordSearch x = new WordSearch();
				x.setVisible(true);
				dispose();
			}
		});
		contentPane.add(WordSearchBtn);

		// alphabetical button
		JButton AlphabeticalBtn = new JButton("ALPHABETICAL");
		AlphabeticalBtn.setBounds(627, 62, 197, 95);
		AlphabeticalBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Alphabetical button game action
				// open word Alphabetical button window/GUI and close MainMenu window/GUI
				Alphabetical x = new Alphabetical();
				x.setVisible(true);
				dispose();
			}
		});
		contentPane.add(AlphabeticalBtn);

		// mixed words button
		JButton MixedWordsBtn = new JButton("MIXED WORDS");
		MixedWordsBtn.setBounds(71, 333, 197, 95);
		MixedWordsBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// word Mixed Words button game action
				// open Mixed Words button window/GUI and close MainMenu window/GUI
				MixedWords x = new MixedWords();
				x.setVisible(true);
				dispose();
			}
		});
		contentPane.add(MixedWordsBtn);

		// missing letter button
		JButton MissingLetterBtn = new JButton("MISSING LETTER");
		MissingLetterBtn.setBounds(627, 333, 197, 95);
		MissingLetterBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// word Missing Letter button game action
				// open Missing Letter button window/GUI and close MainMenu window/GUI
				MissingLetter x = new MissingLetter();
				x.setVisible(true);
				dispose();
			}
		});
		contentPane.add(MissingLetterBtn);

		// Wordle button
		JButton WordleBtn = new JButton("WORDLE");
		WordleBtn.setBounds(312, 165, 197, 95);
		WordleBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Wordle button game action
				// opens Wordle window/GUI and closes MainMenu Window/GUI
				Wordle x = new Wordle();
				x.setVisible(true);// opens new window
				dispose(); // closes this window

			}
		});
		contentPane.add(WordleBtn);

	}
}
