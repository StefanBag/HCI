import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.*;

public class Input extends JFrame {

	private String[] words;

	public Input() {
		setTitle("Word Input");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 860, 510);
		setLocationRelativeTo(null);
		setResizable(false);

		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// ── Step 1 components ──────────────────────────────────────────

		JLabel questionLabel = new JLabel("How many words would you like to enter? (1-20)", SwingConstants.CENTER);
		questionLabel.setFont(new Font("Comic Sans MS", Font.BOLD | Font.ITALIC, 24));
		questionLabel.setForeground(Color.WHITE);
		questionLabel.setBounds(0, 130, 860, 40);

		SpinnerNumberModel spinnerModel = new SpinnerNumberModel(5, 1, 20, 1);
		JSpinner spinner = new JSpinner(spinnerModel);
		spinner.setFont(new Font("Courier New", Font.BOLD, 22));
		spinner.setBounds(355, 200, 150, 40);

		JButton confirmBtn = new JButton();
		confirmBtn.setIcon(new ImageIcon(Input.class.getResource("/Resources/Images/play.png")));
		confirmBtn.setPressedIcon(new ImageIcon(Input.class.getResource("/Resources/Images/play-pressed.png")));
		confirmBtn.setOpaque(false);
		confirmBtn.setContentAreaFilled(false);
		confirmBtn.setBorderPainted(false);
		confirmBtn.setBounds(345, 280, 160, 69);

		contentPane.add(questionLabel);
		contentPane.add(spinner);
		contentPane.add(confirmBtn);

		// ── Step 2 components ──────────────────────────────────────────

		JLabel enterWordsLabel = new JLabel("Enter your words below:", SwingConstants.CENTER);
		enterWordsLabel.setFont(new Font("Comic Sans MS", Font.BOLD | Font.ITALIC, 22));
		enterWordsLabel.setForeground(Color.WHITE);
		enterWordsLabel.setBounds(100, 48, 660, 35);
		enterWordsLabel.setVisible(false);
		contentPane.add(enterWordsLabel);

		// Back button
		JButton backBtn = new JButton();
		backBtn.setIcon(new ImageIcon(Input.class.getResource("/Resources/Images/back.png")));
		backBtn.setPressedIcon(new ImageIcon(Input.class.getResource("/Resources/Images/back-Pressed.png")));
		backBtn.setOpaque(false);
		backBtn.setContentAreaFilled(false);
		backBtn.setBorderPainted(false);
		backBtn.setBounds(47, 50, 89, 34);
		backBtn.setVisible(false);
		contentPane.add(backBtn);

		// Submit button
		JButton submitBtn = new JButton();
		submitBtn.setIcon(new ImageIcon(Input.class.getResource("/Resources/Images/submitbutton.png")));
		submitBtn.setPressedIcon(new ImageIcon(Input.class.getResource("/Resources/Images/submitbutton-pressed.png")));
		submitBtn.setOpaque(false);
		submitBtn.setContentAreaFilled(false);
		submitBtn.setBorderPainted(false);
		submitBtn.setBounds(345, 400, 160, 40);
		submitBtn.setVisible(false);
		contentPane.add(submitBtn);

		// Panel that holds word fields — fits inside the green chalkboard area
		JPanel fieldsPanel = new JPanel();
		fieldsPanel.setLayout(null);
		fieldsPanel.setOpaque(false);
		fieldsPanel.setBounds(40, 88, 775, 300);
		fieldsPanel.setVisible(false);
		contentPane.add(fieldsPanel);

		// ── Helpers to show/hide each step ────────────────────────────
		Runnable showStep1 = () -> {
			questionLabel.setVisible(true);
			spinner.setVisible(true);
			confirmBtn.setVisible(true);
			enterWordsLabel.setVisible(false);
			backBtn.setVisible(false);
			submitBtn.setVisible(false);
			fieldsPanel.setVisible(false);
		};

		Runnable showStep2 = () -> {
			questionLabel.setVisible(false);
			spinner.setVisible(false);
			confirmBtn.setVisible(false);
			enterWordsLabel.setVisible(true);
			backBtn.setVisible(true);
			submitBtn.setVisible(true);
			fieldsPanel.setVisible(true);
		};

		// ── Confirm button ─────────────────────────────────────────────
		confirmBtn.addActionListener(e -> {
			int count = (int) spinner.getValue();
			words = new String[count];

			fieldsPanel.removeAll();

			// Layout: up to 10 per column, two columns if needed
			// fieldsPanel is 775 wide, 320 tall — fits inside green board area
			int rows   = Math.min(count, 10);
			int fieldH = 24;
			int fieldW = 290;
			int gap    = (300 - rows * fieldH) / (rows + 1);
			int col2X  = 455;
			int startY = gap;

			JTextField[] fields = new JTextField[count];

			for (int i = 0; i < count; i++) {
				boolean isRight = i >= 10;
				int col = isRight ? i - 10 : i;
				int x   = isRight ? col2X : 55;
				int y   = startY + col * (fieldH + gap);

				JLabel numLabel = new JLabel((i + 1) + ".", SwingConstants.RIGHT);
				numLabel.setFont(new Font("Courier New", Font.BOLD, 18));
				numLabel.setForeground(Color.WHITE);
				numLabel.setBounds(x - 48, y, 40, fieldH);
				fieldsPanel.add(numLabel);

				fields[i] = new JTextField();
				fields[i].setFont(new Font("Courier New", Font.PLAIN, 14));
				fields[i].setBounds(x, y, fieldW, fieldH);
				final int fi = i;
				fields[fi].addKeyListener(new KeyAdapter() {
					@Override
					public void keyTyped(KeyEvent e) {
						if (!Character.isLetter(e.getKeyChar())) {
							e.consume();
						} else if (fields[fi].getText().length() >= 20) {
							e.consume();
						}
					}
				});
				fieldsPanel.add(fields[i]);
			}

			fieldsPanel.revalidate();
			fieldsPanel.repaint();
			showStep2.run();

			// ── Submit action (registered fresh each time) ─────────────
			// Remove old listeners first
			for (ActionListener al : submitBtn.getActionListeners()) {
				submitBtn.removeActionListener(al);
			}
			submitBtn.addActionListener(ev -> {
				boolean allFilled = true;
				for (int i = 0; i < count; i++) {
					String word = fields[i].getText().trim();
					if (word.isEmpty()) {
						allFilled = false;
						break;
					}
					words[i] = word;
				}
				if (!allFilled) {
					JOptionPane.showMessageDialog(Input.this,
							"Please fill in all word fields before submitting.",
							"Missing Words", JOptionPane.WARNING_MESSAGE);
				} else {
					MainMenu m = new MainMenu(words);
					m.setVisible(true);
					dispose();
				}
			});
		});

		// ── Back button ────────────────────────────────────────────────
		backBtn.addActionListener(e -> showStep1.run());

		// ── Background ─────────────────────────────────────────────────
		JLabel background = new JLabel();
		background.setIcon(new ImageIcon(Input.class.getResource("/Resources/Images/GreenBoard.jpg")));
		background.setBounds(0, 0, 844, 471);
		contentPane.add(background);

		setVisible(true);
	}

	public String[] getWords() {
		return words;
	}
}