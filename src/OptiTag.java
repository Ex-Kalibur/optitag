import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

public class OptiTag {
	
	private static JTextArea inputSequenceField;
	private static JTextField replacementSequenceField;
	private static JTextArea resultTextArea;
	
	private static final Font DEFAULT_FONT = new Font("Courier New", Font.PLAIN, 18);
;

	public static void main(String[] args) {
		JFrame frame = new JFrame("OptiTag - Tag Sequence Optimiser");
		ImageIcon icon = new ImageIcon(OptiTag.class.getResource("logo40x40.png"));
		frame.setSize(new Dimension(1280, 960));
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		frame.setIconImage(icon.getImage());
		frame.setLocationRelativeTo(null);
		
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.LINE_AXIS));
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
		JPanel sequenceInputPanel = getSequenceFileLoaderPanel();
		JPanel replacementInputPanel = getReplacementPanel();
		JButton runButton = new JButton("Run");
		runButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				run();
			}
		});
		
		leftPanel.add(sequenceInputPanel);
		leftPanel.add(Box.createVerticalStrut(8));
		leftPanel.add(replacementInputPanel);
		leftPanel.add(Box.createVerticalStrut(8));
		leftPanel.add(runButton);
		leftPanel.add(Box.createVerticalStrut(8));
		
		contentPanel.add(leftPanel);
		contentPanel.add(new JSeparator(JSeparator.HORIZONTAL));
		contentPanel.add(getResultsPanel());
		
		frame.add(contentPanel);
		frame.pack();
		frame.setVisible(true);
	}

	private static JPanel getSequenceFileLoaderPanel() {
		JPanel fileLoaderPanel = new JPanel();
		fileLoaderPanel.setLayout(new BoxLayout(fileLoaderPanel, BoxLayout.PAGE_AXIS));
		fileLoaderPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(4, 4, 4, 4),
				BorderFactory.createTitledBorder("Protein Sequence:")));
		
		JPanel fileChooserPanel = new JPanel();
		fileChooserPanel.setLayout(new BoxLayout(fileChooserPanel, BoxLayout.LINE_AXIS));
		JLabel fileChooserLabel = new JLabel("Sequence File:");
		JTextField filePathField = new JTextField(20);
		filePathField.setEditable(false);
		
		inputSequenceField = new JTextArea();
		inputSequenceField.setColumns(50);
		inputSequenceField.setRows(20);
		inputSequenceField.setLineWrap(true);
		inputSequenceField.setFont(DEFAULT_FONT);
		
		JScrollPane scrollPane = new JScrollPane(inputSequenceField);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		JButton browseButton = new JButton("Browse");
		browseButton.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  
	        JFileChooser fileChooser = new JFileChooser();
	        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
	        int returnValue = fileChooser.showOpenDialog(null);
	        if (returnValue == JFileChooser.APPROVE_OPTION) {
	          File selectedFile = fileChooser.getSelectedFile();
	          filePathField.setText(selectedFile.getAbsolutePath());
	          inputSequenceField.setText(getFileContents(selectedFile));
	        }
	      }
	    });
		
		fileChooserPanel.add(fileChooserLabel);
		fileChooserPanel.add(Box.createHorizontalStrut(8));
		fileChooserPanel.add(filePathField);
		fileChooserPanel.add(Box.createHorizontalStrut(8));
		fileChooserPanel.add(browseButton);

		
		fileLoaderPanel.add(fileChooserPanel);
		fileLoaderPanel.add(Box.createVerticalStrut(8));
		fileLoaderPanel.add(scrollPane);
		return fileLoaderPanel;
	}
	
	private static JPanel getReplacementPanel() {
		JPanel replacementPanel = new JPanel();
		replacementPanel.setLayout(new BoxLayout(replacementPanel, BoxLayout.LINE_AXIS));
		replacementPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(4, 4, 4, 4),
				BorderFactory.createTitledBorder("Replacement Sequence:")));
		
		JLabel sequenceLabel = new JLabel("Sequence:");
		replacementSequenceField = new JTextField(20);
		replacementSequenceField.setFont(DEFAULT_FONT);
		
		replacementPanel.add(sequenceLabel);
		replacementPanel.add(Box.createHorizontalStrut(8));
		replacementPanel.add(replacementSequenceField);
		return replacementPanel;
	}
	
	private static JPanel getResultsPanel(){
		JPanel resultsPanel = new JPanel();
		resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.LINE_AXIS));
		resultsPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(4, 4, 4, 4),
				BorderFactory.createTitledBorder("Results:")));
		
		resultTextArea = new JTextArea();
		resultTextArea.setEditable(false);
		resultTextArea.setColumns(50);
		resultTextArea.setFont(DEFAULT_FONT);
		
		JScrollPane scrollPane = new JScrollPane(resultTextArea);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		resultsPanel.add(scrollPane);
		
		return resultsPanel;
	}
	
	private static String getFileContents(File file){
		StringBuilder builder = new StringBuilder();
		if(file != null){
			try {
				Scanner sc = new Scanner(file);
				while(sc.hasNext()){
					builder.append(sc.nextLine());
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return builder.toString().replaceAll("\\s", "");
	}

	private static void run(){
		resultTextArea.setText("");
		
		String input = inputSequenceField.getText().toUpperCase();
		String match = replacementSequenceField.getText().toUpperCase().replaceAll("\\s", "");
		int index = -1;
		StringBuilder builder = new StringBuilder();
		ArrayList<Solution> solutions = new ArrayList<>();
		
		double rate = 0;
		for(int i = 0; i < input.length() - match.length(); i++){
			
			rate = 0;
			for(int j = 0; j < match.length(); j++){
				char inputChar = input.charAt(i + j);
				char matchChar = match.charAt(j);
				if(inputChar == 'R' || inputChar == 'H' || inputChar == 'K'){
					if(matchChar == 'R' || matchChar == 'H' || matchChar == 'K'){
						if(j == 0) index = i;
						if(inputChar == matchChar) rate += 1;
						else rate += 0.5;
						
						builder.append(inputChar);
					} else builder.append("-");
				} else if(inputChar == 'D' || inputChar == 'E'){
					if(matchChar == 'D' || matchChar == 'E'){
						if(j == 0) index = i;
						if(inputChar == matchChar) rate += 1;
						else rate += 0.5;
						
						builder.append(inputChar);
					} else builder.append("-");
				} else if(inputChar == 'S' || inputChar == 'T' || inputChar == 'N' || inputChar == 'Q'){
					if(matchChar == 'S' || matchChar == 'T' || matchChar == 'N' || matchChar == 'Q'){
						if(j == 0) index = i;
						if(inputChar == matchChar) rate += 1;
						else rate += 0.5;
						
						builder.append(inputChar);
					} else builder.append("-");
				} else if(inputChar == 'A' || inputChar == 'V' || inputChar == 'I' || inputChar == 'L'
						|| inputChar == 'M' || inputChar == 'F' || inputChar == 'Y' || inputChar == 'W'){
					if( matchChar == 'A' || matchChar == 'V' || matchChar == 'I' || matchChar == 'L'
							|| matchChar == 'M' || matchChar == 'F' || matchChar == 'Y' || matchChar == 'W'){
						if(j == 0) index = i;
						if(inputChar == matchChar) rate += 1;
						else rate += 0.5;
						
						builder.append(inputChar);
					} else builder.append("-");
				} else if((inputChar == 'C' || inputChar == 'G' || inputChar == 'P') && inputChar == matchChar){
					if(j == 0) index = i;
					rate += 1;
					
					builder.append(inputChar);
				} else {
					builder.append("-");
				}
			}
			
			if(index != -1){
				solutions.add(new Solution(builder.toString(), rate/(double)match.length(), index));
				index = -1;
			} builder.setLength(0);
		}
		
		Collections.sort(solutions);
		
		
		
		for(Solution solution: solutions){
			resultTextArea.append(solution.toString() + "\n");
		}
		resultTextArea.append("\n-- " + solutions.size() + " result(s) --");
	}
}
