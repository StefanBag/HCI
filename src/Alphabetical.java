import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

public class Alphabetical extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	JList<String> WordList;
	String words[] = { "apple", "banana", "carrot", "dork", "evil", "fortnite", "grass", "harp", "Immune", "jamie" };
	List<String> SortedList = new ArrayList<>();
	DefaultListModel<String> listModel = new DefaultListModel<>();
	private boolean helpMode = false; // flag for help mode
	private final JLabel Background = new JLabel("");

	public Alphabetical() {
		for (String word : words) {
			listModel.addElement(word);
			SortedList.add(word);

		}
		Collections.sort(SortedList, String.CASE_INSENSITIVE_ORDER); // sort in true alphabetical order
		WordList = new JList<>(listModel);
		WordList.setCellRenderer(new HelpCellRenderer());

		setTitle("Alphabetical");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 860, 510);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// instructions
		JTextArea InstructionsTxt = new JTextArea();
		InstructionsTxt.setBackground(Color.YELLOW);
		InstructionsTxt.setForeground(Color.BLACK);
		InstructionsTxt.setFont(new Font("Javanese Text", Font.PLAIN, 14));
		InstructionsTxt.setWrapStyleWord(true);
		InstructionsTxt.setLineWrap(true);
		InstructionsTxt.setEditable(false);
		InstructionsTxt.setText(
				"Click on a word, then use the up/down arrows to move it. Arrange the words in alphabetical order (A to Z)");
		InstructionsTxt.setBounds(373, 24, 461, 64);
		contentPane.add(InstructionsTxt);

		// back button
		JButton BackBtn = new JButton("BACK");
		BackBtn.setBounds(37, 29, 89, 23);
		BackBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// return to MainMenu and close this window
				MainMenu x = new MainMenu();
				x.setVisible(true);
				dispose();
			}
		});
		contentPane.add(BackBtn);

		// word list
		WordList.setFont(new Font("Mongolian Baiti", Font.PLAIN, 20));
		WordList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// make the list size adapt to the number of items
		WordList.setVisibleRowCount(words.length);
		Dimension preferredSize = WordList.getPreferredSize();
		WordList.setBounds(348, 128, preferredSize.width + 100, preferredSize.height);
		contentPane.add(WordList);

		// move word up button
		JButton WordUpBtn = new JButton("UP");
		WordUpBtn.setMargin(new Insets(2, 14, 2, 1));
		WordUpBtn.setIconTextGap(0);
		WordUpBtn.setIcon(new ImageIcon(Alphabetical.class.getResource("/Resources/Images/Up-Arrow.png")));
		WordUpBtn.setBounds(237, 128, 75, 70);
		WordUpBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// logic to move the word up in the list
				int SelectedWordIndex = WordList.getSelectedIndex();
				if (SelectedWordIndex != -1 && SelectedWordIndex != 0) {
					String temp = listModel.get(SelectedWordIndex);
					listModel.set(SelectedWordIndex, listModel.get(SelectedWordIndex - 1));
					listModel.set(SelectedWordIndex - 1, temp);

					// keep selection on the moved item
					WordList.setSelectedIndex(SelectedWordIndex - 1);
				}
			}
		});
		contentPane.add(WordUpBtn);

		// move word down button
		JButton WordDownBtn = new JButton("Down");
		WordDownBtn.setMargin(new Insets(2, 14, 2, 1));
		WordDownBtn.setIconTextGap(0);
		WordDownBtn.setIcon(new ImageIcon(Alphabetical.class.getResource("/Resources/Images/Down-Arrow.png")));
		WordDownBtn.setBounds(237, 216, 75, 70);
		WordDownBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// logic to move the word down in the list
				int SelectedWordIndex = WordList.getSelectedIndex();
				if (SelectedWordIndex != -1 && SelectedWordIndex < listModel.getSize() - 1) {
					// swap elements in the model
					String temp = listModel.get(SelectedWordIndex);
					listModel.set(SelectedWordIndex, listModel.get(SelectedWordIndex + 1));
					listModel.set(SelectedWordIndex + 1, temp);

					// keep selection on the moved item
					WordList.setSelectedIndex(SelectedWordIndex + 1);
				}
			}
		});
		contentPane.add(WordDownBtn);

		// check to see if words sorted alphabetically
		JButton SubmitBtn = new JButton("SUBMIT");
		SubmitBtn.setBounds(237, 297, 89, 23);
		SubmitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// logic to check if list is ordered alphabetically
				if (AlphabeticalCheck()) {// if true
					JOptionPane.showMessageDialog(Alphabetical.this, "List Sorted alphabetically!", "success!",
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					// give the number of incorrectly placed words
					JOptionPane.showMessageDialog(Alphabetical.this,
							"List not sorted alphabetically. " + numberOfIncorrectWords() + " words in wrong position!",
							"Try Again", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		contentPane.add(SubmitBtn);

		// undo previous move
		JButton UndoBtn = new JButton("Undo");
		UndoBtn.setBounds(498, 93, 89, 23);
		UndoBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// logic to undo the last performed action

			}
		});
		contentPane.add(UndoBtn);

		// reset the word order to original order
		JButton ResetBtn = new JButton("Reset");
		ResetBtn.setBounds(392, 93, 89, 23);
		ResetBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// logic to reset the list to its original state
				// clear the list
				listModel.clear();
				// add all elements from words list again
				for (String word : words) {
					listModel.addElement(word);
				}
			}
		});
		contentPane.add(ResetBtn);

		JButton HelpBtn = new JButton("Help");
		HelpBtn.setBounds(348, 376, 89, 23);
		HelpBtn.setToolTipText("Green = correct position | Yellow = close | Red = far from correct spot");
		HelpBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// logic to help the user sort the list alphabetically
				helpMode = !helpMode;
				WordList.repaint();
			}
		});
		contentPane.add(HelpBtn);
		Background.setIcon(new ImageIcon(Alphabetical.class.getResource("/Resources/Images/GreenBoard.jpg")));
		Background.setBounds(0, 0, 844, 471);
		contentPane.add(Background);

		// JLabel LevelSelectBackground = new JLabel("");
		// LevelSelectBackground.setIcon(new ImageIcon(background));
		// LevelSelectBackground.setBounds(0, 0, 861, 482);
		// contentPane.add(LevelSelectBackground);
		// JLabel Background = new JLabel("");
		// Background.setIcon(new ImageIcon("Resources/Images/GreenBoard.jpg"));
		// Background.setBounds(0, 0, 860, 510);
		// contentPane.add(Background);
	}

	// method to check whether the list is correct or not
	private boolean AlphabeticalCheck() {
		for (int i = 0; i < words.length; i++) {
			if (!listModel.get(i).equals(SortedList.get(i)))
				return false;
		}
		return true;
	}

	// method to return the number of incorrectly placed words
	private int numberOfIncorrectWords() {
		int num = 0;
		for (int i = 0; i < words.length; i++) {
			if (!listModel.get(i).equals(SortedList.get(i)))
				num++;
		}
		return num;
	}

	// helper method to return the distance from the given index to the correct
	// sorted position
	private int DistanceToCorrectPosition(int index) {
		// first get the string at that index
		String TargetWord = listModel.get(index);
		// now find the index at which the TargetWord is in the SortedList
		for (int i = 0; i < words.length; i++) {
			if (SortedList.get(i).equals(TargetWord)) {
				return Math.abs(index - i);
			}
		}
		return Integer.MAX_VALUE;// should never return this
	}

	// custom class to paint individual cells
	private class HelpCellRenderer extends DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

			if (helpMode) {
				int dist = DistanceToCorrectPosition(index);
				Color bg;
				if (dist == 0) {
					bg = Color.green;
				} else if (dist <= 3) {
					bg = Color.yellow;
				} else {
					bg = Color.red;
				}
				setBackground(bg);

				// indicate selection with a border so the user knows which item is selected
				if (isSelected) {
					setBorder(BorderFactory.createLineBorder(Color.black, 2));
				} else {
					setBorder(null);
				}
			} else {
				// revert to default look
				setBorder(null);
			}
			return this;
		}
	}
}
