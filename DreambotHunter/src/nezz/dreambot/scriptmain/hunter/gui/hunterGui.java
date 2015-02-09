package nezz.dreambot.scriptmain.hunter.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.Toolkit;

import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;

import nezz.dreambot.scriptmain.hunter.Hunt;


public class hunterGui extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	JComboBox<Hunt> comboBox = new JComboBox<Hunt>();
	private JTextField stopAtField;


	public hunterGui(final ScriptVars var) {
		setTitle("DreamBot Hunter");
		setIconImage(Toolkit.getDefaultToolkit().getImage(hunterGui.class.getResource("/javax/swing/plaf/metal/icons/ocean/computer.gif")));
		setAlwaysOnTop(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 276, 193);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 270, 131);
		contentPane.add(panel);
		panel.setLayout(null);
		
		comboBox.setModel(new DefaultComboBoxModel<Hunt>(Hunt.values()));
		comboBox.setBounds(10, 11, 128, 20);
		panel.add(comboBox);
		
		JLabel lblStopAt = new JLabel("Stop at:");
		lblStopAt.setBounds(10, 42, 54, 14);
		panel.add(lblStopAt);
		
		stopAtField = new JTextField();
		stopAtField.setText("19");
		stopAtField.setBounds(74, 42, 40, 20);
		panel.add(stopAtField);
		stopAtField.setColumns(10);
		
		JButton btnNewButton = new JButton("Start!");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				var.huntThis = Hunt.values()[comboBox.getSelectedIndex()];
				var.stopAt = Integer.parseInt(stopAtField.getText());
				var.started = true;
				dispose();
			}
		});
		btnNewButton.setBounds(0, 130, 270, 34);
		contentPane.add(btnNewButton);
	}
}
