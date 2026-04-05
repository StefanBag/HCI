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
    private JButton submitBtn;
    private JLabel scoreLabel;

    private int currentWordIndex = 0;
    private String currentWord;
    private int score = 0;

    public MixedWords(String[] words) {
        this.words = words;
        this.numWords = words.length;

        setTitle("Mixed Words");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 860, 510);
        setLocationRelativeTo(null);

        contentPane = new BackgroundPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        setContentPane(contentPane);

        buildUI();
        loadWord();
    }

    private ImageIcon resizeIcon(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(MixedWords.class.getResource(path));
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private void buildUI() {
        JPanel topContainer = new JPanel();
        topContainer.setOpaque(false);
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));

        JPanel instructionPanel = new JPanel(new BorderLayout());
        instructionPanel.setBackground(new Color(255, 240, 0));
        instructionPanel.setBorder(new LineBorder(new Color(0, 140, 70), 4, true));
        instructionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        instructionPanel.setPreferredSize(new Dimension(760, 110));

        JLabel instructionLabel = new JLabel(
            "<html><div style='text-align: center;'>Click a letter, then use the arrows to move it.<br>Rearrange the letters to spell the word.</div></html>",
            JLabel.CENTER
        );
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 30));
        instructionLabel.setForeground(Color.BLACK);
        instructionLabel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
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
        backBtn.setPreferredSize(new Dimension(89, 34));

        backBtn.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                MixedWords.this,
                "Your current sorting progress will be lost. Are you sure you want to return to the main menu?",
                "Exit to Main Menu",
                JOptionPane.YES_NO_OPTION
            );

            if (result == JOptionPane.YES_OPTION) {
                MainMenu menu = new MainMenu(words);
                menu.setVisible(true);
                dispose();
            }
        });

        scoreLabel = new JLabel("Score: 0 out of " + numWords, JLabel.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 30));
        scoreLabel.setForeground(Color.YELLOW);

        gbc.gridx = 0;
        gbc.weightx = 0.0;
        gbc.insets = new Insets(0, 10, 0, 0);
        topPanel.add(backBtn, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 0, 0);
        topPanel.add(scoreLabel, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.0;
        JPanel rightSpacer = new JPanel();
        rightSpacer.setOpaque(false);
        rightSpacer.setPreferredSize(backBtn.getPreferredSize());
        topPanel.add(rightSpacer, gbc);

        topContainer.add(instructionPanel);
        topContainer.add(Box.createVerticalStrut(8));
        topContainer.add(topPanel);

        contentPane.add(topContainer, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        JPanel middleStack = new JPanel();
        middleStack.setOpaque(false);
        middleStack.setLayout(new BoxLayout(middleStack, BoxLayout.Y_AXIS));

        lettersPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        lettersPanel.setOpaque(false);
        lettersPanel.setPreferredSize(new Dimension(760, 100));
        lettersPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        letterButtons = new JButton[10];
        for (int i = 0; i < letterButtons.length; i++) {
            final int index = i;

            JButton btn = new JButton("");
            btn.setFont(new Font("Arial", Font.BOLD, 30));
            btn.setMargin(new Insets(0, 0, 0, 0));
            btn.setOpaque(true);
            btn.setContentAreaFilled(true);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setBackground(Color.WHITE);
            btn.setPreferredSize(new Dimension(65, 65));
            btn.setHorizontalAlignment(JLabel.CENTER);
            btn.setVerticalAlignment(JLabel.CENTER);

            btn.addActionListener(e -> selectLetter(index));
            letterButtons[i] = btn;
        }

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        bottomPanel.setOpaque(false);
        bottomPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftArrow = new JButton();
        leftArrow.setIcon(resizeIcon("/Resources/Images/LeftArrow (2).png", 110, 80));
        leftArrow.setPressedIcon(resizeIcon("/Resources/Images/LeftArrow (1).png", 110, 80));
        leftArrow.setBorderPainted(false);
        leftArrow.setContentAreaFilled(false);
        leftArrow.setFocusPainted(false);
        leftArrow.setOpaque(false);
        leftArrow.setPreferredSize(new Dimension(110, 80));

        rightArrow = new JButton();
        rightArrow.setIcon(resizeIcon("/Resources/Images/RightArrow (2).png", 110, 80));
        rightArrow.setPressedIcon(resizeIcon("/Resources/Images/RightArrow (1).png", 110, 80));
        rightArrow.setBorderPainted(false);
        rightArrow.setContentAreaFilled(false);
        rightArrow.setFocusPainted(false);
        rightArrow.setOpaque(false);
        rightArrow.setPreferredSize(new Dimension(110, 80));

        submitBtn = new JButton();
        submitBtn.setIcon(resizeIcon("/Resources/Images/submitbutton.png", 190, 70));
        submitBtn.setPressedIcon(resizeIcon("/Resources/Images/submitbutton-pressed.png", 190, 70));
        submitBtn.setBorderPainted(false);
        submitBtn.setContentAreaFilled(false);
        submitBtn.setFocusPainted(false);
        submitBtn.setOpaque(false);
        submitBtn.setPreferredSize(new Dimension(190, 70));

        bottomPanel.add(leftArrow);
        bottomPanel.add(rightArrow);
        bottomPanel.add(submitBtn);

        middleStack.add(Box.createVerticalStrut(10));
        middleStack.add(lettersPanel);
        middleStack.add(Box.createVerticalStrut(10));
        middleStack.add(bottomPanel);

        centerPanel.add(middleStack);
        contentPane.add(centerPanel, BorderLayout.CENTER);

        leftArrow.addActionListener(e -> moveLeft());
        rightArrow.addActionListener(e -> moveRight());
        submitBtn.addActionListener(e -> submitWord());
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
    }

    private void moveLeft() {
        if (selectedIndex > 0 && letterButtons[selectedIndex - 1].isVisible()) {
            swap(selectedIndex, selectedIndex - 1);
            selectedIndex--;

            for (int i = 0; i < currentWord.length(); i++) {
                letterButtons[i].setBackground(Color.WHITE);
            }

            letterButtons[selectedIndex].setBackground(Color.YELLOW);
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
        }
    }

    private void swap(int i, int j) {
        String temp = letterButtons[i].getText();
        letterButtons[i].setText(letterButtons[j].getText());
        letterButtons[j].setText(temp);
    }

    private void submitWord() {
        StringBuilder attempt = new StringBuilder();

        for (int i = 0; i < currentWord.length(); i++) {
            attempt.append(letterButtons[i].getText());
        }

        if (attempt.toString().equals(currentWord)) {
            score++;
        }

        scoreLabel.setText("Score: " + score + " out of " + numWords);

        currentWordIndex++;
        if (currentWordIndex >= words.length) {
            JOptionPane.showMessageDialog(
                this,
                "Game over! Final Score: " + score + " out of " + numWords + "\nThe game will now reset.",
                "Game Complete",
                JOptionPane.INFORMATION_MESSAGE
            );

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