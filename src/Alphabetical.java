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

public class Alphabetical extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	// Master model holding all words in current order
	DefaultListModel<String> listModel = new DefaultListModel<>();

	// Sorted list for reference (case‑insensitive alphabetical order)
	List<String> SortedList = new ArrayList<>();

	// Original words (used for reset)
	// Original words (used for reset)
	String words[] = { "apple", "banana", "carrot", "dork", "evil", "fortnite", "grass", "harp", "immune", "jamie",
			"hello", "rello", "dog", "meow", "zebra", "orange", "pencil", "keyboard", "monitor", "bottle", "window",
			"rocket", "island", "shadow", "thunder" };

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

	public Alphabetical() {
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
			listedNumbers[i].setFont(new Font("Monospaced", Font.PLAIN, 16));
			listedNumbers[i].setForeground(new Color(255, 255, 255));
			contentPane.add(listedNumbers[i]);

		}

		// Instructions
		JTextArea InstructionsTxt = new JTextArea();
		InstructionsTxt.setBackground(Color.YELLOW);
		InstructionsTxt.setForeground(Color.BLACK);
		InstructionsTxt.setFont(new Font("Javanese Text", Font.PLAIN, 14));
		InstructionsTxt.setWrapStyleWord(true);
		InstructionsTxt.setLineWrap(true);
		InstructionsTxt.setEditable(false);
		InstructionsTxt.setText(
				"Click on a word, then use the up/down arrows to move it. Arrange the words in alphabetical order (A to Z)");
		InstructionsTxt.setBounds(208, 52, 461, 64);
		contentPane.add(InstructionsTxt);

		// Back button
		JButton BackBtn = new JButton();
		BackBtn.setBorderPainted(false);
		BackBtn.setOpaque(false);
		BackBtn.setContentAreaFilled(false);
		BackBtn.setIcon(new ImageIcon(Alphabetical.class.getResource("/Resources/Images/back.png")));
		BackBtn.setPressedIcon(new ImageIcon(Alphabetical.class.getResource("/Resources/Images/back-Pressed.png")));
		BackBtn.setBounds(47, 38, 89, 34);
		BackBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainMenu x = new MainMenu();
				x.setVisible(true);
				dispose();
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

		// Initially fill the two lists from the master model and lay them out
		updateLists();
		layoutLists();
		PositionNumbers();

		// Add lists to content pane
		contentPane.add(leftList);
		contentPane.add(rightList);

		// Move word up button
		JButton WordUpBtn = new JButton("UP");
		WordUpBtn.setSelectedIcon(null);
		WordUpBtn.setMargin(new Insets(2, 14, 2, 1));
		WordUpBtn.setIconTextGap(0);
		WordUpBtn.setOpaque(false);
		WordUpBtn.setContentAreaFilled(false);
		WordUpBtn.setBorderPainted(false);
		WordUpBtn.setIcon(new ImageIcon(Alphabetical.class.getResource("/Resources/Images/ArrowUp.png")));
		WordUpBtn.setPressedIcon(new ImageIcon(Alphabetical.class.getResource("/Resources/Images/ArrowUpPressed.png")));
		WordUpBtn.setBounds(406, 158, 75, 70);
		WordUpBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (selectedMasterIndex > 0) {
					// Swap with previous element in master model
					String word = listModel.remove(selectedMasterIndex);
					listModel.add(selectedMasterIndex - 1, word);
					selectedMasterIndex--;

					// Update both lists and restore selection manually
					ignoreSelectionEvents = true;
					updateLists();
					layoutLists();
					setSelectionToMasterIndex(selectedMasterIndex);
					ignoreSelectionEvents = false;
				}
			}
		});
		contentPane.add(WordUpBtn);

		// Move word down button
		JButton WordDownBtn = new JButton("Down");
		WordDownBtn.setMargin(new Insets(2, 14, 2, 1));
		WordDownBtn.setIconTextGap(0);
		WordDownBtn.setOpaque(false);
		WordDownBtn.setContentAreaFilled(false);
		WordDownBtn.setBorderPainted(false);
		WordDownBtn.setIcon(new ImageIcon(Alphabetical.class.getResource("/Resources/Images/ArrowDown.png")));
		WordDownBtn.setPressedIcon(
				new ImageIcon(Alphabetical.class.getResource("/Resources/Images/ArrowDown-Pressed.png")));
		WordDownBtn.setBounds(406, 251, 75, 64);
		WordDownBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (selectedMasterIndex != -1 && selectedMasterIndex < listModel.getSize() - 1) {
					// Swap with next element in master model
					String word = listModel.remove(selectedMasterIndex);
					listModel.add(selectedMasterIndex + 1, word);
					selectedMasterIndex++;

					// Update both lists and restore selection manually
					ignoreSelectionEvents = true;
					updateLists();
					layoutLists();
					setSelectionToMasterIndex(selectedMasterIndex);
					ignoreSelectionEvents = false;
				}
			}
		});
		contentPane.add(WordDownBtn);

		// Submit button – checks if list is sorted
		JButton SubmitBtn = new JButton();
		SubmitBtn.setIcon(new ImageIcon(Alphabetical.class.getResource("/Resources/Images/scorebutton.png")));
		SubmitBtn.setPressedIcon(
				new ImageIcon(Alphabetical.class.getResource("/Resources/Images/scorebutton-pressed.png")));
		SubmitBtn.setOpaque(false);
		SubmitBtn.setContentAreaFilled(false);
		SubmitBtn.setBorderPainted(false);
		SubmitBtn.setBounds(47, 360, 89, 41);
		SubmitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (AlphabeticalCheck()) {
					JOptionPane.showMessageDialog(Alphabetical.this, "List Sorted alphabetically!", "success!",
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(Alphabetical.this,
							"List not sorted alphabetically. " + numberOfIncorrectWords() + " words in wrong position!",
							"Try Again", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		contentPane.add(SubmitBtn);

		// Undo button (placeholder)
		JButton UndoBtn = new JButton("Undo");
		UndoBtn.setVisible(false);
		UndoBtn.setBounds(498, 93, 89, 23);
		UndoBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO: implement undo functionality
			}
		});
		contentPane.add(UndoBtn);

		// Reset button – restores original order
		JButton ResetBtn = new JButton();
		ResetBtn.setBorderPainted(false);
		ResetBtn.setOpaque(false);
		ResetBtn.setContentAreaFilled(false);
		ResetBtn.setIcon(new ImageIcon(Alphabetical.class.getResource("/Resources/Images/restart.png")));
		ResetBtn.setPressedIcon(new ImageIcon(Alphabetical.class.getResource("/Resources/Images/restart-pressed.png")));
		ResetBtn.setBounds(714, 99, 89, 34);
		ResetBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				listModel.clear();
				for (String word : words) {
					listModel.addElement(word);
				}
				selectedMasterIndex = -1;
				ignoreSelectionEvents = true;
				updateLists();
				layoutLists();
				ignoreSelectionEvents = false;
				// Clear any lingering selection
				leftList.clearSelection();
				rightList.clearSelection();
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
		HelpBtn.setBounds(395, 360, 89, 34);
		HelpBtn.setToolTipText("Green = correct position | Yellow = close | Red = far from correct spot");
		HelpBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				helpMode = !helpMode;
				leftList.repaint();
				rightList.repaint();
			}
		});
		contentPane.add(HelpBtn);

		// Background – add first so it stays behind all other components
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
	 * Positions the two lists and shows/hides the right list as needed.
	 */
	private void layoutLists() {
		int total = listModel.getSize();
		int leftCount = Math.min(total, MAX_ROWS_PER_COLUMN);
		int rightCount = total - leftCount;

		leftList.setBounds(208, 149, leftList.getPreferredSize().width + 100, leftList.getPreferredSize().height);

		if (rightCount > 0) {
			int rightX = leftList.getX() + leftList.getWidth() + 135;
			int rightY = leftList.getY();
			rightList.setBounds(rightX, rightY, rightList.getPreferredSize().width + 100,
					rightList.getPreferredSize().height);
			rightList.setVisible(true);
		} else {
			rightList.setVisible(false);
		}

	}

	/**
	 * Position the numbers of leftList and RightList to be called ONLY once, not
	 * after every action
	 */

	private void PositionNumbers() {
		int total = listModel.getSize();
		// Number list: width based on the largest number, height = total rows * fixed
		// cell height
		int numberWidth = 50; // fallback
		if (total > 0) {
			String largest = String.valueOf(total);
			// Estimate width: roughly 12 pixels per digit + padding
			numberWidth = Math.max(40, largest.length() * 12 + 10);
		}
		int numberHeight = leftList.getFixedCellHeight();

		// using this idea, iterate and populate the numbers beside the space beside the
		// list
		int x = 0;
		int leftListY = leftList.getY();
		for (int i = 0; i < listedNumbers.length; i++) {
			// first check if we dont need to populate the listed numbers of rightlist
			ListModel<String> model = leftList.getModel();
			if (i < model.getSize()) { // print the leftList numbers
				int y = leftListY + (i * numberHeight);
				listedNumbers[i].setBounds(leftList.getX() - numberWidth - 5, y, numberWidth, numberHeight);
			} else { // print the rightList numbers
				int y = leftListY + (x++ * numberHeight);
				listedNumbers[i].setBounds(rightList.getX() + rightList.getPreferredSize().width + 120, y, numberWidth,
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