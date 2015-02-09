package nezz.dreambot.autobuyer.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.Toolkit;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;

public class buyerGui extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	private JTextField txtItemName;
	private JTextField txtShopName;
	JCheckBox chckbxHopWorlds = new JCheckBox("hop worlds");
	private JTextField txtMinAmt;


	public buyerGui(final ScriptVars var) {
		setTitle("DreamBot Pack Buyer");
		setIconImage(Toolkit.getDefaultToolkit().getImage(buyerGui.class.getResource("/javax/swing/plaf/metal/icons/ocean/computer.gif")));
		setAlwaysOnTop(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 276, 237);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 270, 163);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JLabel lblItemName = new JLabel("Item Name:");
		lblItemName.setBounds(0, 11, 93, 14);
		panel.add(lblItemName);
		
		txtItemName = new JTextField();
		txtItemName.setText("Iron arrow");
		txtItemName.setBounds(96, 9, 132, 20);
		panel.add(txtItemName);
		txtItemName.setColumns(10);
		
		txtShopName = new JTextField();
		txtShopName.setText("Betty");
		txtShopName.setColumns(10);
		txtShopName.setBounds(96, 36, 132, 20);
		panel.add(txtShopName);
		
		JLabel lblShopName = new JLabel("Shop Name:");
		lblShopName.setBounds(0, 38, 93, 14);
		panel.add(lblShopName);
		
		JLabel lblPerItem = new JLabel("f2p:");
		lblPerItem.setBounds(0, 69, 93, 14);
		panel.add(lblPerItem);
		
		JLabel lblHopWorlds = new JLabel("Hop Worlds:");
		lblHopWorlds.setBounds(0, 94, 73, 14);
		panel.add(lblHopWorlds);
		
		chckbxHopWorlds.setBounds(96, 90, 97, 23);
		panel.add(chckbxHopWorlds);
		
		JLabel lblMinimumAmt = new JLabel("Minimum Amt:");
		lblMinimumAmt.setBounds(0, 121, 93, 14);
		panel.add(lblMinimumAmt);
		
		txtMinAmt = new JTextField();
		txtMinAmt.setText("10");
		txtMinAmt.setBounds(96, 118, 39, 20);
		panel.add(txtMinAmt);
		txtMinAmt.setColumns(10);
		
		final JCheckBox chckbxFpOnly = new JCheckBox("f2p only");
		chckbxFpOnly.setBounds(96, 63, 97, 23);
		panel.add(chckbxFpOnly);
		
		JButton btnNewButton = new JButton("Start!");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				var.shopName = txtShopName.getText();
				var.itemName = txtItemName.getText();
				var.hopWorlds = chckbxHopWorlds.isSelected();
				var.f2p = chckbxFpOnly.isSelected();
				var.minAmt = Integer.parseInt(txtMinAmt.getText());
				var.started = true;
				dispose();
			}
		});
		btnNewButton.setBounds(0, 174, 270, 34);
		contentPane.add(btnNewButton);
	}
}
