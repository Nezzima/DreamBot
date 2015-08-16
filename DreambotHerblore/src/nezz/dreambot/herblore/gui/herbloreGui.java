package nezz.dreambot.herblore.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.Toolkit;

import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;






import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;

import javax.swing.JTabbedPane;

import nezz.dreambot.scriptmain.herblore.Herbs;
import nezz.dreambot.scriptmain.herblore.Pots;


public class herbloreGui extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	JComboBox<Herbs> comboBox_1;// = new JComboBox();
	JComboBox<String> comboBox;// = new JComboBox();
	JComboBox<Pots> comboBox_2;
	JLabel label = new JLabel("1");
	JLabel level = new JLabel("1");
	JLabel ing_1 = new JLabel("Ing. 1");
	JLabel ing_2 = new JLabel("Ing. 2");



	public herbloreGui(final ScriptVars var) {
		setTitle("DreamBot");
		setIconImage(Toolkit.getDefaultToolkit().getImage(herbloreGui.class.getResource("/javax/swing/plaf/metal/icons/ocean/computer.gif")));
		setAlwaysOnTop(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 308, 174);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 302, 115);
		contentPane.add(tabbedPane);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Main", null, panel, null);
		panel.setLayout(null);
		
		comboBox = new JComboBox<String>();
		comboBox.setModel(new DefaultComboBoxModel<String>(new String[] {"Identify", "Potions", "Debug", "Unf Potions"}));
		comboBox.setBounds(66, 11, 76, 20);
		panel.add(comboBox);
		
		JLabel lblMode = new JLabel("Mode:");
		lblMode.setBounds(10, 14, 46, 14);
		panel.add(lblMode);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Identify", null, panel_1, null);
		panel_1.setLayout(null);
		
		comboBox_1 = new JComboBox<Herbs>();
		comboBox_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Herbs h = Herbs.values()[comboBox_1.getSelectedIndex()];
				var.yourHerb = h;
				label.setText(""+h.getIdLevel());
			}
		});
		comboBox_1.setModel(new DefaultComboBoxModel<Herbs>(Herbs.values()));
		comboBox_1.setBounds(123, 11, 139, 20);
		panel_1.add(comboBox_1);
		
		JLabel lblChooseYourHerb = new JLabel("Choose your Herb:");
		lblChooseYourHerb.setBounds(10, 14, 123, 14);
		panel_1.add(lblChooseYourHerb);
		
		JLabel lblLevelRequired = new JLabel("Level Required:");
		lblLevelRequired.setBounds(10, 62, 92, 14);
		panel_1.add(lblLevelRequired);
		
		label.setBounds(123, 62, 46, 14);
		panel_1.add(label);
		
		
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("Potions", null, panel_2, null);
		panel_2.setLayout(null);
		
		JLabel lblChooseYourPotion = new JLabel("Choose your Potion:");
		lblChooseYourPotion.setBounds(14, 8, 98, 14);
		panel_2.add(lblChooseYourPotion);
		
		comboBox_2 = new JComboBox<Pots>();
		comboBox_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Pots p = Pots.values()[comboBox_2.getSelectedIndex()];
				var.yourPot = p;
				ing_1.setText(p.getIngredientOne());
				ing_2.setText(p.getIngredientTwo());
				level.setText("" + p.getLevel());
			}
		});
		comboBox_2.setModel(new DefaultComboBoxModel<Pots>(Pots.values()));
		comboBox_2.setBounds(117, 5, 165, 20);
		panel_2.add(comboBox_2);
		
		JLabel lblIng = new JLabel("Ing. 1:");
		lblIng.setBounds(14, 33, 46, 14);
		panel_2.add(lblIng);
		
		JLabel lblIng_1 = new JLabel("Ing. 2:");
		lblIng_1.setBounds(14, 48, 46, 14);
		panel_2.add(lblIng_1);
		
		JLabel lblLevel = new JLabel("Level: ");
		lblLevel.setBounds(14, 62, 46, 14);
		panel_2.add(lblLevel);
		
		level.setBounds(70, 62, 46, 14);
		panel_2.add(level);
		
		ing_1.setBounds(70, 33, 133, 14);
		panel_2.add(ing_1);
		
		ing_2.setBounds(70, 48, 133, 14);
		panel_2.add(ing_2);
		
		
		JButton btnNewButton = new JButton("START");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(comboBox.getSelectedIndex() == 0)
					var.id = true;
				else if(comboBox.getSelectedIndex() == 1)
					var.potions = true;
				else if(comboBox.getSelectedIndex() == 2)
					var.debug = true;
				else if(comboBox.getSelectedIndex() == 3)
					var.unfPotions = true;
				var.yourHerb = Herbs.values()[comboBox_1.getSelectedIndex()];
				var.started = true;
				dispose();
			}
		});
		btnNewButton.setBounds(0, 119, 302, 23);
		contentPane.add(btnNewButton);
	}
}
