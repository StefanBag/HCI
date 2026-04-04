import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

public class MixedWords extends JFrame {

    private static final long serialVersionUID = 1L;

    private String[] words;
    private int numWords;

    private JPanel contentPane;
    private JPanel lettersPanel;
    private JButton[] letterButtons;
    private int selectedIndex = -1;

    private JButton leftArrow;
    private JButton rightArrow;
    private JButton nextBtn;
    private JLabel scoreLabel;

    private int currentWordIndex = 0;
    private String currentWord;
    private int score = 0;

    public MixedWords(String[] words) {
        this.words = words;
        this.numWords = words.length;

        setTitle("Mixed Words");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo((Component) null);

        contentPane = new BackgroundPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.setBorder(BorderFactory.createEmptyBorder(55, 75, 65, 75));
        setContentPane(contentPane);

        buildUI();
        loadWord();
    }

    private void buildUI() {
        JPanel topContainer = new JPanel();
        topContainer.setOpaque(false);
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));

        JPanel instructionPanel = new JPanel(new BorderLayout());
        instructionPanel.setBackground(new Color(255, 240, 0));
        instructionPanel.setBorder(new LineBorder(new Color(0, 140, 70), 6, true));
        instructionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        instructionPanel.setPreferredSize(new Dimension(1000, 200));

        JLabel instructionLabel = new JLabel(
            "<html><div style='text-align: center;'>Click a letter then arrow to move a letter.<br>Rearrange the letters to spell a word.</div></html>",
            JLabel.CENTER
        );
        instructionLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 48));
        instructionLabel.setForeground(Color.BLACK);
        instructionLabel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        instructionPanel.add(instructionLabel, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton backBtn = new JButton();
        backBtn.setIcon(new ImageIcon(MixedWords.class.getResource("/Resources/Images/back.png")));
        backBtn.setPressedIcon(new ImageIcon(MixedWords.class.getResource("/Resources/Images/back-Pressed.png")));
        backBtn.setBorderPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setFocusPainted(false);
        backBtn.setOpaque(false);
        backBtn.setPreferredSize(new Dimension(140, 55));

        backBtn.addActionListener(e -> {
            MainMenu menu = new MainMenu(words);
            menu.setVisible(true);
            dispose();
        });

        scoreLabel = new JLabel("Score: 0 out of " + numWords, JLabel.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 68));
        scoreLabel.setForeground(Color.YELLOW);

        gbc.gridx = 0;
        gbc.weightx = 0.0;
        topPanel.add(backBtn, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        topPanel.add(scoreLabel, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.0;
        JPanel rightSpacer = new JPanel();
        rightSpacer.setOpaque(false);
        rightSpacer.setPreferredSize(backBtn.getPreferredSize());
        topPanel.add(rightSpacer, gbc);

        topContainer.add(instructionPanel);
        topContainer.add(Box.createVerticalStrut(10));
        topContainer.add(topPanel);

        contentPane.add(topContainer, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        JPanel middleStack = new JPanel();
        middleStack.setOpaque(false);
        middleStack.setLayout(new BoxLayout(middleStack, BoxLayout.Y_AXIS));

        lettersPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        lettersPanel.setOpaque(false);
        lettersPanel.setPreferredSize(new Dimension(950, 150));
        lettersPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        letterButtons = new JButton[10];
        for (int i = 0; i < letterButtons.length; i++) {
            final int index = i;

            JButton btn = new JButton("");
            btn.setFont(new Font("Arial", Font.BOLD, 44));
            btn.setMargin(new Insets(0, 0, 0, 0));
            btn.setOpaque(true);
            btn.setContentAreaFilled(true);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setBackground(Color.WHITE);
            btn.setPreferredSize(new Dimension(120, 120));
            btn.setHorizontalAlignment(JLabel.CENTER);
            btn.setVerticalAlignment(JLabel.CENTER);

            btn.addActionListener(e -> selectLetter(index));
            letterButtons[i] = btn;
        }

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 5));
        bottomPanel.setOpaque(false);
        bottomPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftArrow = new JButton("<");
        leftArrow.setFont(new Font("Arial", Font.BOLD, 48));
        leftArrow.setPreferredSize(new Dimension(140, 90));
        styleButton(leftArrow);

        rightArrow = new JButton(">");
        rightArrow.setFont(new Font("Arial", Font.BOLD, 48));
        rightArrow.setPreferredSize(new Dimension(140, 90));
        styleButton(rightArrow);

        nextBtn = new JButton("NEXT WORD");
        nextBtn.setFont(new Font("Arial", Font.BOLD, 36));
        nextBtn.setPreferredSize(new Dimension(320, 90));
        styleButton(nextBtn);

        bottomPanel.add(leftArrow);
        bottomPanel.add(rightArrow);
        bottomPanel.add(nextBtn);

        middleStack.add(lettersPanel);
        middleStack.add(Box.createVerticalStrut(12));
        middleStack.add(bottomPanel);

        centerPanel.add(middleStack);
        contentPane.add(centerPanel, BorderLayout.CENTER);

        leftArrow.addActionListener(e -> moveLeft());
        rightArrow.addActionListener(e -> moveRight());
        nextBtn.addActionListener(e -> nextWord());
    }

    private void styleButton(JButton button) {
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBackground(Color.WHITE);
    }

    private void loadWord() {
        currentWord = words[currentWordIndex];

        List<Character> letters = new ArrayList<>();
        for (char c : currentWord.toCharArray()) {
            letters.add(c);
        }

        Collections.shuffle(letters);
        lettersPanel.removeAll();

        for (int i = 0; i < letterButtons.length; i++) {
            if (i < letters.size()) {
                letterButtons[i].setText(String.valueOf(letters.get(i)));
                letterButtons[i].setVisible(true);
                letterButtons[i].setBackground(Color.WHITE);
                lettersPanel.add(letterButtons[i]);
            } else {
                letterButtons[i].setText("");
                letterButtons[i].setVisible(false);
            }
        }

        selectedIndex = -1;
        resetArrowColors();
        lettersPanel.revalidate();
        lettersPanel.repaint();
    }

    private void selectLetter(int index) {
        if (!letterButtons[index].isVisible()) {
            return;
        }

        if (selectedIndex != -1 && letterButtons[selectedIndex].isVisible()) {
            letterButtons[selectedIndex].setBackground(Color.WHITE);
        }

        selectedIndex = index;
        letterButtons[selectedIndex].setBackground(Color.YELLOW);
        resetArrowColors();
    }

    private void moveLeft() {
        if (selectedIndex > 0 && letterButtons[selectedIndex - 1].isVisible()) {
            swap(selectedIndex, selectedIndex - 1);
            selectedIndex--;

            for (int i = 0; i < currentWord.length(); i++) {
                letterButtons[i].setBackground(Color.WHITE);
            }

            letterButtons[selectedIndex].setBackground(Color.YELLOW);
            flashArrow(leftArrow);
        }
    }

    private void moveRight() {
        if (selectedIndex != -1 && selectedIndex < currentWord.length() - 1) {
            swap(selectedIndex, selectedIndex + 1);
            selectedIndex++;

            for (int i = 0; i < currentWord.length(); i++) {
                letterButtons[i].setBackground(Color.WHITE);
            }

            letterButtons[selectedIndex].setBackground(Color.YELLOW);
            flashArrow(rightArrow);
        }
    }

    private void flashArrow(JButton arrow) {
        arrow.setBackground(Color.YELLOW);

        Timer timer = new Timer(500, e -> arrow.setBackground(Color.WHITE));
        timer.setRepeats(false);
        timer.start();
    }

    private void resetArrowColors() {
        leftArrow.setBackground(Color.WHITE);
        rightArrow.setBackground(Color.WHITE);
    }

    private void swap(int i, int j) {
        String temp = letterButtons[i].getText();
        letterButtons[i].setText(letterButtons[j].getText());
        letterButtons[j].setText(temp);
    }

    private void nextWord() {
        StringBuilder attempt = new StringBuilder();

        for (int i = 0; i < currentWord.length(); i++) {
            attempt.append(letterButtons[i].getText());
        }

        if (attempt.toString().equals(currentWord)) {
            score++;
            nextBtn.setBackground(Color.GREEN);
        } else {
            nextBtn.setBackground(Color.RED);
        }

        Timer timer = new Timer(1000, e -> nextBtn.setBackground(Color.WHITE));
        timer.setRepeats(false);
        timer.start();

        scoreLabel.setText("Score: " + score + " out of " + numWords);

        currentWordIndex++;
        if (currentWordIndex >= words.length) {
            JOptionPane.showMessageDialog(this, "Done! Final Score: " + score + " out of " + numWords);
            currentWordIndex = 0;
            score = 0;
            scoreLabel.setText("Score: " + score + " out of " + numWords);
        }

        loadWord();
    }

    private static class BackgroundPanel extends JPanel {

        private Image background;

        public BackgroundPanel() {
            background = new ImageIcon(
                MixedWords.class.getResource("/Resources/Images/GreenBoard.jpg")
            ).getImage();
            setLayout(new BorderLayout());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
    }
}