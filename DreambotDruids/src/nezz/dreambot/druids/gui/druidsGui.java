package nezz.dreambot.druids.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.Toolkit;

import javax.swing.JCheckBox;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTabbedPane;

import nezz.dreambot.scriptmain.druids.Herbs;

public class druidsGui extends JFrame {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	public druidsGui(final ScriptVars var) {
		setTitle("DreamBot");
		setIconImage(Toolkit
				.getDefaultToolkit()
				.getImage(
						druidsGui.class
								.getResource("/javax/swing/plaf/metal/icons/ocean/computer.gif")));
		setAlwaysOnTop(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 308, 257);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 302, 206);
		contentPane.add(tabbedPane);

		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Loot(Herb)", null, panel_1, null);
		panel_1.setLayout(null);

		final JCheckBox chckbxGuam = new JCheckBox("Guam");
		chckbxGuam.setBounds(16, 33, 69, 23);
		panel_1.add(chckbxGuam);

		final JCheckBox chckbxMarrentill = new JCheckBox("Marrentill");
		chckbxMarrentill.setBounds(87, 33, 81, 23);
		panel_1.add(chckbxMarrentill);

		final JCheckBox chckbxDwarf = new JCheckBox("Dwarfweed");
		chckbxDwarf.setBounds(16, 111, 88, 23);
		panel_1.add(chckbxDwarf);

		final JCheckBox chckbxTarromin = new JCheckBox("Tarromin");
		chckbxTarromin.setBounds(170, 33, 88, 23);
		panel_1.add(chckbxTarromin);

		final JCheckBox chckbxHarralander = new JCheckBox("Harralander");
		chckbxHarralander.setBounds(16, 59, 106, 23);
		panel_1.add(chckbxHarralander);

		final JCheckBox chckbxRanarr = new JCheckBox("Ranarr");
		chckbxRanarr.setBounds(99, 85, 69, 23);
		panel_1.add(chckbxRanarr);

		final JCheckBox chckbxIrit = new JCheckBox("Irit");
		chckbxIrit.setBounds(207, 59, 51, 23);
		panel_1.add(chckbxIrit);

		final JCheckBox chckbxCadantine = new JCheckBox("Cadantine");
		chckbxCadantine.setBounds(106, 111, 88, 23);
		panel_1.add(chckbxCadantine);

		final JCheckBox chckbxAvantoe = new JCheckBox("Avantoe");
		chckbxAvantoe.setBounds(124, 59, 81, 23);
		panel_1.add(chckbxAvantoe);

		final JCheckBox chckbxKwuarm = new JCheckBox("Kwuarm");
		chckbxKwuarm.setBounds(16, 85, 81, 23);
		panel_1.add(chckbxKwuarm);

		final JCheckBox chckbxTorstol = new JCheckBox("Torstol");
		chckbxTorstol.setBounds(196, 111, 69, 23);
		panel_1.add(chckbxTorstol);

		final JCheckBox chckbxLantadyme = new JCheckBox("Lantadyme");
		chckbxLantadyme.setBounds(170, 85, 88, 23);
		panel_1.add(chckbxLantadyme);

		final JCheckBox[] chckbxArray = new JCheckBox[]{chckbxGuam, chckbxMarrentill, chckbxTarromin, chckbxHarralander,
				chckbxAvantoe, chckbxIrit, chckbxKwuarm, chckbxRanarr, chckbxDwarf, chckbxCadantine, chckbxTorstol, chckbxLantadyme};

		JCheckBox chckbxAllHerbs = new JCheckBox("All Herbs");
		chckbxAllHerbs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for (JCheckBox chck : chckbxArray) {
					if (chck != null)
						chck.setSelected(true);
				}
			}
		});
		chckbxAllHerbs.setBounds(6, 7, 81, 23);
		panel_1.add(chckbxAllHerbs);

		JPanel panel_2 = new JPanel();
		panel_2.setLayout(null);
		tabbedPane.addTab("Loot(Rune)", null, panel_2, null);

		final JCheckBox chckbxAir = new JCheckBox("Air");
		chckbxAir.setBounds(16, 33, 53, 23);
		panel_2.add(chckbxAir);

		final JCheckBox chckbxEarth = new JCheckBox("Earth");
		chckbxEarth.setBounds(71, 33, 67, 23);
		panel_2.add(chckbxEarth);

		final JCheckBox chckbxBody = new JCheckBox("Body");
		chckbxBody.setBounds(140, 33, 58, 23);
		panel_2.add(chckbxBody);

		final JCheckBox chckbxMind = new JCheckBox("Mind");
		chckbxMind.setBounds(200, 33, 58, 23);
		panel_2.add(chckbxMind);

		final JCheckBox chckbxNature = new JCheckBox("Nature");
		chckbxNature.setBounds(71, 59, 67, 23);
		panel_2.add(chckbxNature);

		final JCheckBox chckbxLaw = new JCheckBox("Law");
		chckbxLaw.setBounds(16, 59, 58, 23);
		panel_2.add(chckbxLaw);

		final JCheckBox[] runeArray = new JCheckBox[]{chckbxAir, chckbxEarth, chckbxBody, chckbxMind, chckbxLaw, chckbxNature};

		JCheckBox chckbxAllRunes = new JCheckBox("All Runes");
		chckbxAllRunes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for (JCheckBox chck : runeArray) {
					if (chck != null)
						chck.setSelected(true);
				}
			}
		});
		chckbxAllRunes.setBounds(6, 7, 81, 23);
		panel_2.add(chckbxAllRunes);

		final JCheckBox chckbxMithrilBolts = new JCheckBox("Mithril Bolts");
		chckbxMithrilBolts.setBounds(6, 105, 97, 23);
		panel_2.add(chckbxMithrilBolts);

		JButton btnNewButton = new JButton("START");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				List<String> namedLoot = new ArrayList<String>();
				List<Integer> idLoot = new ArrayList<Integer>();
				boolean herb = false;
				for (JCheckBox chck : chckbxArray) {
					if (chck != null && chck.isSelected()) {
						herb = true;
						Herbs h = Herbs.getForName(chck.getText());
						if (h != null) {
							idLoot.add(h.getUnnotedGrimyId());
						}
					}
				}
				var.keepHerbs = new int[idLoot.size()];
				for (int i = 0; i < idLoot.size(); i++) {
					var.keepHerbs[i] = idLoot.get(i);
				}
				if (herb)
					namedLoot.add("Herb");
				for (JCheckBox chck : runeArray) {
					if (chck != null && chck.isSelected()) {
						namedLoot.add(chck.getText() + " rune");
					}
				}
				if (chckbxMithrilBolts.isSelected()) {
					namedLoot.add("Mithril bolts");
				}
				var.loot = namedLoot.toArray(new String[namedLoot.size()]);
				var.started = true;
				dispose();
			}
		});
		btnNewButton.setBounds(0, 205, 302, 23);
		contentPane.add(btnNewButton);
	}
}
