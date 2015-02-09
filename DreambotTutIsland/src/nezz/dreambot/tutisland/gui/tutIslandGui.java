package nezz.dreambot.tutisland.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.Toolkit;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.JTextField;

public class tutIslandGui extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField userNameField;
	private JTextField passwordField;
	private JTextField ageField;


	public tutIslandGui(final ScriptVars var) {
		setTitle("DreamBot Tutorial Island");
		setIconImage(Toolkit.getDefaultToolkit().getImage(tutIslandGui.class.getResource("/javax/swing/plaf/metal/icons/ocean/computer.gif")));
		setAlwaysOnTop(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 276, 167);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 270, 101);
		contentPane.add(panel);
		panel.setLayout(null);
		
		userNameField = new JTextField();
		userNameField.setText("userName");
		userNameField.setBounds(10, 11, 86, 20);
		panel.add(userNameField);
		userNameField.setColumns(10);
		
		passwordField = new JTextField();
		passwordField.setText("smd1234");
		passwordField.setBounds(106, 11, 86, 20);
		panel.add(passwordField);
		passwordField.setColumns(10);
		
		ageField = new JTextField();
		ageField.setText("20");
		ageField.setBounds(202, 11, 58, 20);
		panel.add(ageField);
		ageField.setColumns(10);
		
		JButton btnNewButton = new JButton("Start!");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				var.baseName = userNameField.getText();
				var.pass = passwordField.getText();
				var.age = ageField.getText();
				var.started = true;
				dispose();
			}
		});
		btnNewButton.setBounds(0, 101, 270, 34);
		contentPane.add(btnNewButton);
	}
}
