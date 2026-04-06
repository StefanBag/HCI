import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.*;

public class Welcome extends JFrame {

    public Welcome() {
        setTitle("Welcome");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 860, 510);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

        // Title label
        JLabel title = new JLabel("Welcome To SPELLING CENTRAL", SwingConstants.CENTER);
        title.setFont(new Font("Comic Sans MS", Font.BOLD | Font.ITALIC, 32));
        title.setForeground(Color.WHITE);
        title.setBounds(0, 80, 860, 50);

        // Sub-text line 1
        JLabel line1 = new JLabel("Press PLAY To Input Words", SwingConstants.CENTER);
        line1.setFont(new Font("Courier New", Font.BOLD, 22));
        line1.setForeground(Color.WHITE);
        line1.setBounds(0, 200, 860, 25);

        // Sub-text line 2
        JLabel line2 = new JLabel("For The Activities", SwingConstants.CENTER);
        line2.setFont(new Font("Courier New", Font.BOLD, 22));
        line2.setForeground(Color.WHITE);
        line2.setBounds(0, 230, 860, 25);

        // ENTER button
        JButton enterBtn = new JButton();
        enterBtn.setIcon(new ImageIcon(getClass().getResource("/Resources/Images/play.png")));
        enterBtn.setPressedIcon(new ImageIcon(getClass().getResource("/Resources/Images/play-pressed.png")));
        enterBtn.setOpaque(false);
        enterBtn.setContentAreaFilled(false);
        enterBtn.setBorderPainted(false);
        enterBtn.setBounds(345, 300, 160, 69);
        enterBtn.addActionListener(e -> {
        	Input x = new Input();
			x.setVisible(true);
			dispose();
        });

        // Background
        JLabel background = new JLabel();
        background.setIcon(new ImageIcon(getClass().getResource("/Resources/Images/GreenBoard.jpg")));
        background.setBounds(0, 0, 844, 471);

        contentPane.add(title);
        contentPane.add(line1);
        contentPane.add(line2);
        contentPane.add(enterBtn);
        contentPane.add(background);

        setVisible(true);
    }
}