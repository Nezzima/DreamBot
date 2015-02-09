package nezz.dreambot.fletcher.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.Toolkit;

import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JButton;

import nezz.dreambot.scriptmain.fletch.Fletching;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;

public class fletchGUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	JCheckBox chckbxPowerFish = new JCheckBox("Chop n Drop");
	JComboBox<Fletching> comboBox = new JComboBox<Fletching>();


	public fletchGUI(final ScriptVars var) {
		setTitle("DreamBot Fletcher");
		setIconImage(Toolkit.getDefaultToolkit().getImage(fletchGUI.class.getResource("/javax/swing/plaf/metal/icons/ocean/computer.gif")));
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
		
		JLabel lblYourFish = new JLabel("Fletch:");
		lblYourFish.setBounds(10, 11, 72, 14);
		panel.add(lblYourFish);
		
		comboBox.setModel(new DefaultComboBoxModel<Fletching>(Fletching.values()));
		comboBox.setBounds(92, 8, 168, 20);
		panel.add(comboBox);
		
		chckbxPowerFish.setBounds(92, 35, 97, 23);
		panel.add(chckbxPowerFish);
		
		final JCheckBox chckbxProgress = new JCheckBox("Progress");
		chckbxProgress.setBounds(92, 61, 97, 23);
		panel.add(chckbxProgress);
		
		JButton btnNewButton = new JButton("Start!");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				var.fletch = Fletching.values()[comboBox.getSelectedIndex()];
				var.chopNDrop = chckbxPowerFish.isSelected();
				var.progress = chckbxProgress.isSelected();
				var.started = true;
				dispose();
			}
		});
		btnNewButton.setBounds(0, 101, 270, 34);
		contentPane.add(btnNewButton);
	}
}
