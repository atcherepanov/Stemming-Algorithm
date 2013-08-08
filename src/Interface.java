import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.*;

public class Interface implements ActionListener
{
	JTextField search;
	JTextArea textArea;
	Search searcher = new Search();
	private boolean stop = false;
	Interface()		
	{	
		
		try {
			searcher.load();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		JFrame jfrm = new JFrame("Text Fields");
		jfrm.setLayout(null);
		jfrm.setSize(900, 600);
		jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		search = new JTextField(15);
		textArea = new JTextArea(5, 5);
		JScrollPane scrollPane = new JScrollPane(textArea); 
		textArea.setEditable(false);

	
		// Set first panel to retrieve data from user
		JPanel inFieldPane = new JPanel();
		inFieldPane.setLayout(new FlowLayout());
		inFieldPane.add(new JLabel("Query input"));
		inFieldPane.add(search);
		search.addActionListener(this);
		jfrm.add(inFieldPane);
		inFieldPane.setBounds(0, 0, 195, 160);
		jfrm.setResizable(false); 
		//Set second panel to submit data for processing
		JPanel submitPane = new JPanel();
		submitPane.setLayout(new FlowLayout());
		inFieldPane.add(new JLabel("Press button to Search"));
		JButton submitButton = new JButton("Submit");
		submitButton.addActionListener(this);
		inFieldPane.add(submitButton);
		jfrm.add(submitPane);
		submitPane.setBounds(0, 30, 195, 60);
		// Set third panel to display processed data
		JPanel outFieldPane= new JPanel();
		outFieldPane.setLayout(new FlowLayout());
		outFieldPane.add(new JLabel("Results"));
		JPanel outFieldPane2= new JPanel();
		outFieldPane2.setLayout(new BoxLayout(outFieldPane2, 0));
		outFieldPane2.add(scrollPane);
		jfrm.add(outFieldPane);		
		outFieldPane.setBounds(220, 0, 100, 30);	
		jfrm.add(outFieldPane2);		
		outFieldPane2.setBounds(220, 50, 600, 500);
		textArea.setSize(400, 400);
		scrollPane.setSize(400, 400);
		jfrm.setVisible(true);
	}
		
	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().equals("Submit"))
		{
			
			try {
				textArea.setText(searcher.query(search.getText()));
			} catch (IOException e1) {
				
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				
				e1.printStackTrace();
			}
			
		}
	}
	
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				new Interface();
			}
		});
	}
}