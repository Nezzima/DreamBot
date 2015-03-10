package nezz.dreambot.thiever.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.Toolkit;

import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;

import org.dreambot.api.methods.container.impl.bank.BankLocation;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;

import nezz.dreambot.thiever.enums.Thieving;


public class thieverGui extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	JComboBox<BankLocation> comboBox_1 = new JComboBox<BankLocation>();
	JComboBox<Thieving> comboBox = new JComboBox<Thieving>();


	public thieverGui(final ScriptVars var) {
		setTitle("DreamBot Pack Buyer");
		setIconImage(Toolkit.getDefaultToolkit().getImage(thieverGui.class.getResource("/javax/swing/plaf/metal/icons/ocean/computer.gif")));
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
		
		JLabel lblYourThieving = new JLabel("Your Thieving:");
		lblYourThieving.setBounds(10, 11, 72, 14);
		panel.add(lblYourThieving);
		
		comboBox.setModel(new DefaultComboBoxModel<Thieving>(Thieving.values()));
		comboBox.setBounds(92, 8, 168, 20);
		panel.add(comboBox);
		
		JLabel lblYourBank = new JLabel("Your Bank:");
		lblYourBank.setBounds(10, 36, 72, 14);
		panel.add(lblYourBank);
		
		comboBox_1.setModel(new DefaultComboBoxModel<BankLocation>(BankLocation.values()));
		comboBox_1.setBounds(92, 33, 168, 20);
		panel.add(comboBox_1);
		
		JButton btnNewButton = new JButton("Start!");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				var.yourBank = BankLocation.values()[comboBox_1.getSelectedIndex()];
				var.yourThieving = Thieving.values()[comboBox.getSelectedIndex()];
				var.started = true;
				dispose();
			}
		});
		btnNewButton.setBounds(0, 101, 270, 34);
		contentPane.add(btnNewButton);
	}
}
