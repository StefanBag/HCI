import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.*;

public class MainMenu extends JFrame {

	private String[] words;

	public MainMenu(String[] words) {
		this.words = words;

		setTitle("Main Menu");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 860, 510);
		setLocationRelativeTo(null);
		setResizable(false);

		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// Title
		JLabel title = new JLabel("Select An Activity", SwingConstants.CENTER);
		title.setFont(new Font("Comic Sans MS", Font.BOLD | Font.ITALIC, 40));
		title.setForeground(Color.WHITE);
		title.setBounds(0, 55, 860, 45);
		contentPane.add(title);

		// Instruction
		JLabel instruction = new JLabel("Press the back button to change your words", SwingConstants.CENTER);
		instruction.setFont(new Font("Courier New", Font.BOLD, 20));
		instruction.setForeground(Color.WHITE);
		instruction.setBounds(0, 115, 860, 25);
		contentPane.add(instruction);

		// Back button
		JButton backBtn = new JButton();
		backBtn.setIcon(new ImageIcon(MainMenu.class.getResource("/Resources/Images/back.png")));
		backBtn.setPressedIcon(new ImageIcon(MainMenu.class.getResource("/Resources/Images/back-Pressed.png")));
		backBtn.setOpaque(false);
		backBtn.setContentAreaFilled(false);
		backBtn.setBorderPainted(false);
		backBtn.setBounds(47, 60, 89, 34);
		backBtn.addActionListener(e -> {
			Input input = new Input();
			input.setVisible(true);
			dispose();
		});
		contentPane.add(backBtn);

		// ── Activity buttons ───────────────────────────────────────────
		// 5 buttons arranged in a grid: 3 on top row, 2 on bottom row, centered
		int btnW = 200;
		int btnH = 70;
		int gapX = 30;
		int row1Y = 195;
		int row2Y = 310;

		// Row 1 — 3 buttons, centered: total width = 3*200 + 2*30 = 660, start x = (860-660)/2 = 100
		int row1StartX = (860 - (3 * btnW + 2 * gapX)) / 2;

		// Row 2 — 2 buttons, centered: total width = 2*200 + 1*30 = 430, start x = (860-430)/2 = 215
		int row2StartX = (860 - (2 * btnW + gapX)) / 2;

		String[] activityNames  = { "Alphabetical", "Word Search", "Wordle", "Missing Letter", "Mixed Words" };
		String[] imageNames     = { "Alphabetical", "WordSearch", "Wordle", "MissingLetter", "MixedWords" };

		for (int i = 0; i < 5; i++) {
			final int index = i;
			JButton btn = new JButton();
			btn.setIcon(new ImageIcon(MainMenu.class.getResource("/Resources/Images/" + imageNames[i] + ".png")));
			btn.setPressedIcon(new ImageIcon(MainMenu.class.getResource("/Resources/Images/" + imageNames[i] + "-Pressed.png")));
			btn.setOpaque(false);
			btn.setContentAreaFilled(false);
			btn.setBorderPainted(false);
			btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			int x, y;
			if (i < 3) {
				x = row1StartX + i * (btnW + gapX);
				y = row1Y;
			} else {
				x = row2StartX + (i - 3) * (btnW + gapX);
				y = row2Y;
			}
			btn.setBounds(x, y, btnW, btnH);

			btn.addActionListener(e -> {
				switch (index) {
					case 0: // Alphabetical
						Alphabetical a = new Alphabetical();
						a.setVisible(true);
						dispose();
						break;
					case 1: // Word Search
						WordSearch ws = new WordSearch();
						ws.setVisible(true);
						dispose();						
						break;
					case 2: // Wordle
						Wordle w = new Wordle();
						w.setVisible(true);
						dispose();					
						break;
					case 3: // Missing Letter
						MissingLetter ml = new MissingLetter();
						ml.setVisible(true);
						dispose();					
						break;
					case 4: // Mixed Words
						MixedWords mw = new MixedWords();
						mw.setVisible(true);
						dispose();
						break;
				}
			});

			contentPane.add(btn);
		}

		// Background — added last so it stays behind everything
		JLabel background = new JLabel();
		background.setIcon(new ImageIcon(MainMenu.class.getResource("/Resources/Images/GreenBoard.jpg")));
		background.setBounds(0, 0, 844, 471);
		contentPane.add(background);

		setVisible(true);
	}
}