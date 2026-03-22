import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class MainMenu extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/** Scale/crop to fill (no empty bands). */
	private static ImageIcon iconCover(URL url, int w, int h) {
		try {
			BufferedImage src = ImageIO.read(url);
			if (src == null) {
				return new ImageIcon(url);
			}
			double s = Math.max((double) w / src.getWidth(), (double) h / src.getHeight());
			int nw = (int) Math.round(src.getWidth() * s), nh = (int) Math.round(src.getHeight() * s);
			BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = out.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.drawImage(src, (w - nw) / 2, (h - nh) / 2, nw, nh, null);
			g.dispose();
			return new ImageIcon(out);
		} catch (Exception e) {
			return new ImageIcon(url);
		}
	}

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
		WordSearchBtn.setBounds(42, 35, 252, 133);
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
		JButton AlphabeticalBtn = new JButton("");
		AlphabeticalBtn.setIcon(new ImageIcon(MainMenu.class.getResource("/Resources/Images/Alphabet.png")));
		AlphabeticalBtn.setBounds(568, 35, 252, 133);
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
		MixedWordsBtn.setBounds(42, 295, 252, 133);
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
		MissingLetterBtn.setBounds(568, 295, 252, 133);
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
		JButton WordleBtn = new JButton("");
		WordleBtn.setMargin(new Insets(0, 0, 0, 0));
		WordleBtn.setIcon(iconCover(MainMenu.class.getResource("/Resources/Images/wordle.png"), 252, 133));
		WordleBtn.setBounds(304, 168, 252, 133);
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

		JLabel background = new JLabel("");
		background.setIcon(new ImageIcon(MainMenu.class.getResource("/Resources/Images/GreenBoard.jpg")));
		background.setBounds(0, 0, 844, 471);
		contentPane.add(background);

	}
}
