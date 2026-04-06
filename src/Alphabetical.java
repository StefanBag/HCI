import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Alphabetical extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	// Master model holding all words in current order
	DefaultListModel<String> listModel = new DefaultListModel<>();

	// Sorted list for reference (case-insensitive alphabetical order)
	List<String> SortedList = new ArrayList<>();

	private String[] words;
	// Two JLists that display portions of the master model
	private JList<String> leftList;
	private JList<String> rightList;

	// Tracks the currently selected word in the master model
	private int selectedMasterIndex = -1;

	private boolean helpMode = false; // flag for help mode

	// Maximum number of rows per column
	private static final int MAX_ROWS_PER_COLUMN = 13;

	// Flag to ignore selection events during manual updates
	private boolean ignoreSelectionEvents = false;

	private final JLabel Background = new JLabel("");

	private final Border padding = new EmptyBorder(10, 15, 10, 15);

	private JTextArea[] listedNumbers;

	// --- Make buttons class fields so layoutLists() can see them ---
	private JButton WordUpBtn;
	private JButton WordDownBtn;

	public Alphabetical(String[] words) {
		this.words = words;
		// Populate master model and sorted list
		for (String word : words) {
			listModel.addElement(word);
			SortedList.add(word);
		}
		Collections.sort(SortedList, String.CASE_INSENSITIVE_ORDER);

		// Frame setup
		setTitle("Alphabetical");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 860, 510);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// populate each listedNumbers JTextArea array
		listedNumbers = new JTextArea[words.length];
		for (int i = 0; i < words.length; i++) {
			listedNumbers[i] = new JTextArea();
			listedNumbers[i].setText(String.valueOf(i + 1));
			listedNumbers[i].setEditable(false);
			listedNumbers[i].setOpaque(false);
			listedNumbers[i].setFont(new Font("Ariel", Font.PLAIN, 16));
			listedNumbers[i].setForeground(new Color(255, 255, 255));
			contentPane.add(listedNumbers[i]);
		}

		// Instructions panel
		JPanel instructionPanel = new JPanel(new BorderLayout());
		instructionPanel.setBackground(new Color(255, 240, 0));
		instructionPanel.setBorder(new LineBorder(new Color(0, 140, 70), 4, true));
		instructionPanel.setBounds(42, 34, 770, 90);

		JLabel instructionLabel = new JLabel(
				"<html><div style='text-align: center;'>Click on a word, then use the up/down arrows to move it. Arrange the words in <br>alphabetical order (A to Z)</div></html>",
				JLabel.CENTER);
		instructionLabel.setFont(new Font("Arial", Font.PLAIN, 20));
		instructionLabel.setForeground(Color.BLACK);
		instructionLabel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

		instructionPanel.add(instructionLabel, BorderLayout.EAST);
		Background.add(instructionPanel);

		// Back button
		JButton BackBtn = new JButton();
		BackBtn.setBorderPainted(false);
		BackBtn.setOpaque(false);
		BackBtn.setContentAreaFilled(false);
		BackBtn.setIcon(new ImageIcon(Alphabetical.class.getResource("/Resources/Images/back.png")));
		BackBtn.setPressedIcon(new ImageIcon(Alphabetical.class.getResource("/Resources/Images/back-Pressed.png")));
		BackBtn.setBounds(47, 137, 89, 34);
		BackBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.showConfirmDialog(Alphabetical.this,
						"Your current sorting progress will be lost. Are you sure you want to return to the main menu?",
						"Exit to Main Menu", JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_OPTION) {
					MainMenu x = new MainMenu(words);
					x.setVisible(true);
					dispose();
				}
			}
		});
		contentPane.add(BackBtn);

		// ---------- Create the two JLists ----------
		leftList = new JList<>();
		rightList = new JList<>();

		// Common settings for both lists
		leftList.setFont(new Font("Mongolian Baiti", Font.PLAIN, 20));
		rightList.setFont(new Font("Mongolian Baiti", Font.PLAIN, 20));
		leftList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		rightList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		leftList.setFixedCellHeight(20);
		rightList.setFixedCellHeight(20);
		leftList.setBorder(BorderFactory.createEmptyBorder());
		rightList.setBorder(BorderFactory.createEmptyBorder());

		// Custom renderers that know whether they belong to left or right list
		leftList.setCellRenderer(new HelpCellRenderer(true));
		rightList.setCellRenderer(new HelpCellRenderer(false));

		// Selection listeners – update global selected index (ignored when flag is
		// true)
		leftList.addListSelectionListener(e -> {
			if (ignoreSelectionEvents)
				return;
			if (!e.getValueIsAdjusting()) {
				int local = leftList.getSelectedIndex();
				if (local != -1) {
					selectedMasterIndex = local;
				} else {
					selectedMasterIndex = -1;
				}
			}
		});
		rightList.addListSelectionListener(e -> {
			if (ignoreSelectionEvents)
				return;
			if (!e.getValueIsAdjusting()) {
				int local = rightList.getSelectedIndex();
				if (local != -1) {
					int leftCount = Math.min(listModel.getSize(), MAX_ROWS_PER_COLUMN);
					selectedMasterIndex = leftCount + local;
				} else {
					selectedMasterIndex = -1;
				}
			}
		});

		// --- Create UP and DOWN buttons (now as fields) ---
		WordUpBtn = new JButton("UP");
		WordUpBtn.setSelectedIcon(null);
		WordUpBtn.setFocusPainted(false);
		WordUpBtn.setMargin(new Insets(2, 14, 2, 1));
		WordUpBtn.setIconTextGap(0);
		WordUpBtn.setOpaque(false);
		WordUpBtn.setContentAreaFilled(false);
		WordUpBtn.setBorderPainted(false);
		WordUpBtn.setIcon(new ImageIcon(Alphabetical.class.getResource("/Resources/Images/Up (2).png")));
		WordUpBtn.setPressedIcon(new ImageIcon(Alphabetical.class.getResource("/Resources/Images/Up (4).png")));
		WordUpBtn.setBounds(406, 158, 75, 70);
		WordUpBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (selectedMasterIndex > 0) {
					String word = listModel.remove(selectedMasterIndex);
					listModel.add(selectedMasterIndex - 1, word);
					selectedMasterIndex--;
					ignoreSelectionEvents = true;
					updateLists();
					layoutLists();
					setSelectionToMasterIndex(selectedMasterIndex);
					ignoreSelectionEvents = false;
				}
			}
		});

		WordDownBtn = new JButton("Down");
		WordDownBtn.setMargin(new Insets(2, 14, 2, 1));
		WordDownBtn.setIconTextGap(0);
		WordDownBtn.setFocusPainted(false);
		WordDownBtn.setOpaque(false);
		WordDownBtn.setContentAreaFilled(false);
		WordDownBtn.setBorderPainted(false);
		WordDownBtn.setIcon(new ImageIcon(Alphabetical.class.getResource("/Resources/Images/Down (2).png")));
		WordDownBtn.setPressedIcon(new ImageIcon(Alphabetical.class.getResource("/Resources/Images/Down (4).png")));
		WordDownBtn.setBounds(406, 251, 75, 70);
		WordDownBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (selectedMasterIndex != -1 && selectedMasterIndex < listModel.getSize() - 1) {
					String word = listModel.remove(selectedMasterIndex);
					listModel.add(selectedMasterIndex + 1, word);
					selectedMasterIndex++;
					ignoreSelectionEvents = true;
					updateLists();
					layoutLists();
					setSelectionToMasterIndex(selectedMasterIndex);
					ignoreSelectionEvents = false;
				}
			}
		});

		// Add lists and buttons to content pane
		contentPane.add(leftList);
		contentPane.add(rightList);
		contentPane.add(WordUpBtn);
		contentPane.add(WordDownBtn);

		// Initially fill the two lists and lay them out (buttons now exist)
		updateLists();
		layoutLists(); // uses button positions to place right list
		PositionNumbers(); // numbers placed based on final list bounds

		// Submit button – checks if list is sorted
		JButton SubmitBtn = new JButton();
		SubmitBtn.setIcon(new ImageIcon(Alphabetical.class.getResource("/Resources/Images/submitbutton.png")));
		SubmitBtn.setPressedIcon(
				new ImageIcon(Alphabetical.class.getResource("/Resources/Images/submitbutton-pressed.png")));
		SubmitBtn.setOpaque(false);
		SubmitBtn.setContentAreaFilled(false);
		SubmitBtn.setBorderPainted(false);
		SubmitBtn.setToolTipText("Submit your sorted list for verification");
		SubmitBtn.setBounds(47, 360, 89, 34);
		SubmitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (AlphabeticalCheck()) {
					JOptionPane.showMessageDialog(Alphabetical.this, "List Sorted alphabetically! List will now reset",
							"success!", JOptionPane.INFORMATION_MESSAGE);
					ResetList();
				} else {
					JOptionPane.showMessageDialog(Alphabetical.this,
							"List not sorted alphabetically. " + numberOfIncorrectWords() + " words in wrong position!",
							"Try Again", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		contentPane.add(SubmitBtn);

		// Reset button – restores original order
		JButton ResetBtn = new JButton();
		ResetBtn.setBorderPainted(false);
		ResetBtn.setOpaque(false);
		ResetBtn.setContentAreaFilled(false);
		ResetBtn.setIcon(new ImageIcon(Alphabetical.class.getResource("/Resources/Images/restart.png")));
		ResetBtn.setPressedIcon(new ImageIcon(Alphabetical.class.getResource("/Resources/Images/restart-pressed.png")));
		ResetBtn.setBounds(721, 137, 89, 34);
		ResetBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.showConfirmDialog(Alphabetical.this,
						"Are you sure you want to reset the word order?", "Confirm Reset", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (result == JOptionPane.YES_OPTION) {
					ResetList();
				}
			}
		});
		contentPane.add(ResetBtn);

		// Help button – toggles help mode
		JButton HelpBtn = new JButton();
		HelpBtn.setOpaque(false);
		HelpBtn.setContentAreaFilled(false);
		HelpBtn.setBorderPainted(false);
		HelpBtn.setIcon(new ImageIcon(Alphabetical.class.getResource("/Resources/Images/information.png")));
		HelpBtn.setPressedIcon(
				new ImageIcon(Alphabetical.class.getResource("/Resources/Images/information-pressed.png")));
		HelpBtn.setBounds(403, 360, 89, 34);
		HelpBtn.setToolTipText("Green = correct position | Yellow = close | Red = far from correct spot");
		HelpBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				helpMode = !helpMode;
				leftList.repaint();
				rightList.repaint();
			}
		});
		contentPane.add(HelpBtn);

		// Background – add last so it stays behind all other components
		Background.setIcon(new ImageIcon(Alphabetical.class.getResource("/Resources/Images/GreenBoard.jpg")));
		Background.setBounds(0, 0, 844, 471);
		contentPane.add(Background);
	}

	/**
	 * Updates the two JLists to reflect the current state of the master model. The
	 * left list gets up to MAX_ROWS_PER_COLUMN items. The right list gets any
	 * remaining items.
	 */
	private void updateLists() {
		int total = listModel.getSize();
		int leftCount = Math.min(total, MAX_ROWS_PER_COLUMN);
		int rightCount = total - leftCount;

		// Left list model
		DefaultListModel<String> leftModel = new DefaultListModel<>();
		for (int i = 0; i < leftCount; i++) {
			leftModel.addElement(listModel.getElementAt(i));
		}
		leftList.setModel(leftModel);
		leftList.setVisibleRowCount(leftCount > 0 ? leftCount : 1);

		// Right list model
		if (rightCount > 0) {
			DefaultListModel<String> rightModel = new DefaultListModel<>();
			for (int i = leftCount; i < total; i++) {
				rightModel.addElement(listModel.getElementAt(i));
			}
			rightList.setModel(rightModel);
			rightList.setVisibleRowCount(rightCount);
		} else {
			rightList.setModel(new DefaultListModel<>());
			rightList.setVisibleRowCount(1);
		}
	}

	/**
	 * reset the list
	 */
	private void ResetList() {
		listModel.clear();
		for (String word : words) {
			listModel.addElement(word);
		}
		selectedMasterIndex = -1;
		ignoreSelectionEvents = true;
		updateLists();
		layoutLists();
		ignoreSelectionEvents = false;
		leftList.clearSelection();
		rightList.clearSelection();
	}

	/**
	 * Positions the two lists. Left list stays at fixed coordinates. Right list is
	 * placed exactly 25 pixels to the right of the UP/DOWN buttons.
	 */
	private void layoutLists() {
		int total = listModel.getSize();
		int leftCount = Math.min(total, MAX_ROWS_PER_COLUMN);
		int rightCount = total - leftCount;

		// Left list bounds (unchanged)
		leftList.setBounds(190, 149, leftList.getPreferredSize().width + 10, leftList.getPreferredSize().height);

		if (rightCount > 0) {
			// Right list X = button right edge + 25 pixels
			int buttonRightEdge = WordUpBtn.getX() + WordUpBtn.getWidth();
			int rightX = buttonRightEdge + 25;
			int rightY = leftList.getY(); // same Y as left list
			rightList.setBounds(rightX, rightY, rightList.getPreferredSize().width + 10,
					rightList.getPreferredSize().height);
			rightList.setVisible(true);
		} else {
			rightList.setVisible(false);
		}
	}

	/**
	 * Position the numbers of leftList and RightList.
	 */
	private void PositionNumbers() {
		int total = listModel.getSize();
		int numberWidth = 50;
		if (total > 0) {
			String largest = String.valueOf(total);
			numberWidth = Math.max(40, largest.length() * 12 + 10);
		}
		int numberHeight = leftList.getFixedCellHeight();

		int x = 0;
		int leftListY = leftList.getY();
		for (int i = 0; i < listedNumbers.length; i++) {
			ListModel<String> model = leftList.getModel();
			if (i < model.getSize()) {
				int y = leftListY + (i * numberHeight);
				listedNumbers[i].setBounds(leftList.getX() - numberWidth, y, numberWidth, numberHeight);
			} else {
				int y = leftListY + (x++ * numberHeight);
				listedNumbers[i].setBounds(rightList.getX() + rightList.getPreferredSize().width + 10, y, numberWidth,
						numberHeight);
			}
		}
	}

	/**
	 * Manually selects the word at the given master index in the appropriate list.
	 * Assumes ignoreSelectionEvents is already true.
	 */
	private void setSelectionToMasterIndex(int masterIndex) {
		if (masterIndex < 0 || masterIndex >= listModel.getSize()) {
			return;
		}
		int leftCount = Math.min(listModel.getSize(), MAX_ROWS_PER_COLUMN);
		if (masterIndex < leftCount) {
			leftList.setSelectedIndex(masterIndex);
		} else {
			int rightIndex = masterIndex - leftCount;
			if (rightList.isVisible() && rightIndex < rightList.getModel().getSize()) {
				rightList.setSelectedIndex(rightIndex);
			}
		}
	}

	// ---------- Helper methods for correctness checking ----------
	private boolean AlphabeticalCheck() {
		for (int i = 0; i < words.length; i++) {
			if (!listModel.get(i).equals(SortedList.get(i)))
				return false;
		}
		return true;
	}

	private int numberOfIncorrectWords() {
		int num = 0;
		for (int i = 0; i < words.length; i++) {
			if (!listModel.get(i).equals(SortedList.get(i)))
				num++;
		}
		return num;
	}

	private int DistanceToCorrectPosition(int index) {
		String TargetWord = listModel.get(index);
		for (int i = 0; i < words.length; i++) {
			if (SortedList.get(i).equals(TargetWord)) {
				return Math.abs(index - i);
			}
		}
		return Integer.MAX_VALUE;
	}

	// ---------- Custom cell renderer that knows its column ----------
	private class HelpCellRenderer extends DefaultListCellRenderer {
		private final boolean isLeft;

		public HelpCellRenderer(boolean isLeft) {
			this.isLeft = isLeft;
		}

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			setBorder(padding);

			if (helpMode) {
				int leftCount = Math.min(listModel.getSize(), MAX_ROWS_PER_COLUMN);
				int globalIndex = isLeft ? index : leftCount + index;

				int dist = DistanceToCorrectPosition(globalIndex);
				Color bg;
				if (dist == 0) {
					bg = Color.green;
				} else if (dist <= 3) {
					bg = Color.yellow;
				} else {
					bg = Color.red;
				}
				setBackground(bg);

				if (isSelected) {
					setBorder(BorderFactory.createLineBorder(Color.black, 2));
				} else {
					setBorder(null);
				}
			} else {
				setBorder(null);
			}
			return this;
		}
	}
}