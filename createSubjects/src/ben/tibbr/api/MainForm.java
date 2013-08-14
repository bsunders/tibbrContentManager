/**
 * MainForm.java
 * @author Ben Sunderland <bsunderl@tibco.com>
 * Created on 12/08/2013 10:49:18 am
 *
 */
package ben.tibbr.api;


// File formatfor SUBJECTS:
// System_name,Description,Security
//
//    System name  <parent>.<new_subject>
// e.g.   HLB.Divisions.Marketing_and_BD,HLB Subject,public

// File format for USERS:
// user_name,password,first_name,last_name,email
//
// e.g. testuser,password,Test,Account,jbloggs@tibco.com



import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.PrintStream;

import javax.swing.Box;

/**
 * MainForm
 * @author Ben Sunderland
 *
 */
@SuppressWarnings("serial")
public class MainForm extends JFrame {

	private JPanel contentPane;
	private JTextField txtCSVInput;
	private JTextField txtTibbrURL = new JTextField("http://172.16.101.129");
	private JTextField txtTibbrPassword = new JPasswordField("password");
	private JTextField txtTibbrUser = new JTextField("tibbradmin");
	private JButton btnFileSelector;
	private JFileChooser fc;
	private JButton btnExecuteCommand;
	private JRadioButton rdbtnCreateUsers;
	private JRadioButton rdbtnCreateSubjects;
	private File file;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainForm frame = new MainForm();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainForm() {
		setTitle("tibbr Content Manager");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 576, 685);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		AddComponentsToUI();

	}
	
	
	
	 /**
	 * AddComponentsToUI
	 */
	private void AddComponentsToUI() {
		// TODO Auto-generated method stub
		  //Create a file chooser
        fc = new JFileChooser();
		
         
		txtTibbrURL.setBounds(151, 27, 200, 28);
		contentPane.add(txtTibbrURL);
		txtTibbrURL.setColumns(10);
		
		txtTibbrPassword.setBounds(151, 104, 200, 28);
		contentPane.add(txtTibbrPassword);
		txtTibbrPassword.setColumns(10);
		
		txtTibbrUser.setBounds(151, 65, 200, 28);
		contentPane.add(txtTibbrUser);
		txtTibbrUser.setColumns(10);
		
		JLabel lblGetCSV = new JLabel("CSV Input File:");
		lblGetCSV.setBounds(32, 276, 113, 16);
		contentPane.add(lblGetCSV);
		
		txtCSVInput = new JTextField();
		txtCSVInput.setBounds(158, 270, 266, 28);
		contentPane.add(txtCSVInput);
		txtCSVInput.setColumns(10);
		
		
		ButtonListener listener = new ButtonListener();
		
		 btnFileSelector = new JButton("Choose File...");
		
		btnFileSelector.setBounds(436, 270, 117, 29);
		btnFileSelector.addActionListener(listener);
		contentPane.add(btnFileSelector);
		
		rdbtnCreateSubjects = new JRadioButton("Create Subjects");
		rdbtnCreateSubjects.setBounds(22, 193, 141, 23);
		rdbtnCreateSubjects.setSelected(true);
		contentPane.add(rdbtnCreateSubjects);
		
		rdbtnCreateUsers = new JRadioButton("Create Users");
		rdbtnCreateUsers.setBounds(22, 228, 141, 23);
		contentPane.add(rdbtnCreateUsers);
		
		
		ButtonGroup group = new ButtonGroup();
		group.add(rdbtnCreateSubjects);
		group.add(rdbtnCreateUsers);
		
		 btnExecuteCommand = new JButton("Run");
		
		
		btnExecuteCommand.setBounds(436, 326, 117, 28);
		btnExecuteCommand.addActionListener(listener);
		contentPane.add(btnExecuteCommand);
		
		Box horizontalBox = Box.createHorizontalBox();
		horizontalBox.setBounds(6, 179, 560, 198);
		contentPane.add(horizontalBox);
		
		JPanel pnlOutput = new JPanel();
		pnlOutput.setBounds(6, 399, 560, 246);
		contentPane.add(pnlOutput);
		
		JLabel lblTibbrUrl = new JLabel("tibbr URL:");
		lblTibbrUrl.setBounds(32, 33, 97, 16);
		contentPane.add(lblTibbrUrl);
		
		JLabel lblTibbrUser = new JLabel("tibbr User:");
		lblTibbrUser.setBounds(32, 71, 97, 16);
		contentPane.add(lblTibbrUser);
		
		JLabel lblTibbrPassword = new JLabel("tibbr Password:");
		lblTibbrPassword.setBounds(32, 110, 97, 16);
		contentPane.add(lblTibbrPassword);
		
		
		
		Box horizontalBox_1 = Box.createHorizontalBox();
		horizontalBox_1.setBounds(6, 6, 564, 145);
		contentPane.add(horizontalBox_1);
		
		
		
		
		  // setup text area for the output console
        JTextArea consoleTextArea = new JTextArea("");
        consoleTextArea.setPreferredSize(new Dimension(560, 246));
        //float newSize = (float) 10.0;
		Font biggerFont = consoleTextArea.getFont().deriveFont(10f );
		consoleTextArea.setFont(biggerFont);
      
        TextAreaOutputStream taos = new TextAreaOutputStream( consoleTextArea, 35 ); // max lines = 100        
        PrintStream ps = new PrintStream( taos );
        System.setOut( ps );
		
		JScrollPane sp = new JScrollPane(consoleTextArea,
						ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
						ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		// add the scroller (containing the console) to the panel 
		pnlOutput.add(sp );
		//for (int i=0; i<10; i++)
		//	System.out.println("Test");
		
	}



	class ButtonListener implements ActionListener {

	        public void actionPerformed(ActionEvent e) {
	        	// FIle selector button pressed
	            if (e.getSource() == btnFileSelector) {
	                int returnVal = fc.showOpenDialog(MainForm.this);
	     
	                if (returnVal == JFileChooser.APPROVE_OPTION) {
	                     file = fc.getSelectedFile();
	                    //This is where a real application would open the file.
	                    System.out.println("File Selected: " + file.getName() + ".\n");
	                    txtCSVInput.setText(file.getPath());
	                } else {
	                	System.out.println("Open command cancelled by user.\n" );
	                }

	            
	            }  // Run button pressed. Select action based on radio.
	            else if (e.getSource() == btnExecuteCommand) {
	               if ( tibbrCredsValid())
	            	   CreateContent();
	               else
	            	   Alert("Credentials invalid!");

	            }
	        }

	    }
	 
	
	
	private void Alert(String message){
		
		JOptionPane.showMessageDialog(null, message);
		
	}
	
	
	/**
	 * CreateContent
	 */
	private void CreateContent() {
		
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
 
            @Override
            protected Void doInBackground() throws Exception {
				
            	TibbrContentManager tibbr = new TibbrContentManager(
            								txtTibbrURL.getText(),
            								txtTibbrUser.getText(),
            								txtTibbrPassword.getText());
            	if (!tibbr.loginUser())
            		return null;
            	
            	System.out.println("Input file location: "+file.getPath());
            	
            	if (rdbtnCreateSubjects.isSelected() ){
            		System.out.println("Creating Subjects...");   
            		tibbr.createSubjectsFromFile(file.getPath());   
	            }
            	else if (rdbtnCreateUsers.isSelected()){
            		System.out.println("Creating Users...");   
	            	tibbr.createUsersFromFile(file.getPath());  
	            }
            		
            	return null;
            }
        };
        worker.execute();
	}

	

	/**
	 * tibbrCredsValid
	 * @return
	 */
	private boolean tibbrCredsValid() {
		
		if (txtTibbrURL.getText().equals("") ||
				txtTibbrUser.getText().equals("") ||
				txtTibbrPassword.getText().equals("") )
			return false;
		return true;
	}
	
	
	 
}













