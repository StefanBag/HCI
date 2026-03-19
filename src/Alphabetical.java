import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
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
	String words[] = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J" }; // temp to hold all words
	List<String> SortedList = new ArrayList<>();
	DefaultListModel<String> listModel = new DefaultListModel<>();

	public Alphabetical() {
		for (String word : words) {
			listModel.addElement(word);
			SortedList.add(word);

		}
		Collections.sort(SortedList, String.CASE_INSENSITIVE_ORDER); // sort in true alphabetical order
		WordList = new JList<>(listModel);

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
		InstructionsTxt.setWrapStyleWord(true);
		InstructionsTxt.setLineWrap(true);
		InstructionsTxt.setEditable(false);
		InstructionsTxt.setText(
				"Click on a word, then use the up/down arrows to move it. Arrange the words in alphabetical order (A to Z)");
		InstructionsTxt.setBounds(373, 11, 461, 45);
		contentPane.add(InstructionsTxt);

		// back button
		JButton BackBtn = new JButton("BACK");
		BackBtn.setBounds(34, 12, 89, 23);
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
		WordList.setFont(new Font("Mongolian Baiti", Font.PLAIN, 15));
		WordList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// make the list size adapt to the number of items
		WordList.setVisibleRowCount(words.length);
		Dimension prefferredSize = WordList.getPreferredSize();
		WordList.setBounds(34, 123, prefferredSize.width + 20, prefferredSize.height);
		contentPane.add(WordList);

		// move word up button
		JButton WordUpBtn = new JButton("UP");
		WordUpBtn.setBounds(497, 210, 89, 23);
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
		WordDownBtn.setBounds(631, 210, 89, 23);
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
		SubmitBtn.setBounds(576, 328, 89, 23);
		SubmitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// logic to check if list is ordered alphabetically
				if (AlphabeticalCheck()) {// if true
					JOptionPane.showMessageDialog(Alphabetical.this, "List Sorted alphabetically", "success!",
							JOptionPane.INFORMATION_MESSAGE);
					System.out.println("List sorted alphabetically");
				} else {
					JOptionPane.showMessageDialog(Alphabetical.this, "List not sorted alphabetically", "Try Again",
							JOptionPane.WARNING_MESSAGE);
					System.out.println("List not sorted alphabetically");
				}
			}
		});
		contentPane.add(SubmitBtn);

		// undo previous move
		JButton UndoBtn = new JButton("Undo");
		UndoBtn.setBounds(241, 152, 89, 23);
		UndoBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// logic to undo the last performed action

			}
		});
		contentPane.add(UndoBtn);

		// reset the word order to original order
		JButton ResetBtn = new JButton("Reset");
		ResetBtn.setBounds(241, 210, 89, 23);
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
		HelpBtn.setBounds(241, 276, 89, 23);
		HelpBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// logic to help the user sort the list alphabetically
				// make the index green if in correct position
				// make the index yellow if 1-3 indexes from correct position
				// make the index red if 4+ indexes from correct position
			}
		});
		contentPane.add(HelpBtn);
	}

	// method to check whether the list is correct or not
	private boolean AlphabeticalCheck() {
		for (int i = 0; i < words.length; i++) {
			if (!listModel.get(i).equals(SortedList.get(i)))
				return false;
		}
		return true;
	}
}
