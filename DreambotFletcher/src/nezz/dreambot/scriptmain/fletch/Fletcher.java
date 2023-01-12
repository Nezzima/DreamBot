package nezz.dreambot.scriptmain.fletch;

import java.awt.Graphics;
import java.awt.Rectangle;

import nezz.dreambot.fletcher.gui.ScriptVars;
import nezz.dreambot.fletcher.gui.fletchGUI;

import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.input.Keyboard;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.SkillTracker;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.widget.Widget;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;

@ScriptManifest(author = "Nezz", category = Category.FLETCHING, description = "Fletcher", name = "Dreambot Fletcher", version = 0)
public class Fletcher extends AbstractScript {

	ScriptVars sv = new ScriptVars();
	Timer t;
	boolean started = false;
	State state;

	private enum State {
		FLETCH, CHOP, DROP, BANK
	}

	private State getState() {
		if (Inventory.isFull()) {
			if (Inventory.contains(sv.fletch.getLog()) && Inventory.contains("Knife")) {
				return State.FLETCH;
			} else {
				if (sv.chopNDrop) {
					return State.DROP;
				} else {
					return State.BANK;
				}
			}
		} else {
			if (!sv.chopNDrop) {
				if (Inventory.contains(sv.fletch.getLog()) && Inventory.contains("Knife")) {
					return State.FLETCH;
				} else {
					return State.BANK;
				}
			} else {
				return State.CHOP;
			}
		}
	}

	public void onStart() {
		fletchGUI gui = new fletchGUI(sv);
		gui.setVisible(true);
		while (!sv.started) {
			sleep(300);
		}
		t = new Timer();
		SkillTracker.start(Skill.FLETCHING);
		log("Starting Dreambot Fletcher");
		started = true;
	}

	@Override
	public int onLoop() {
		int returnThis = -1;
		if (progress())
			return 1;
		state = getState();
		switch (state) {
			case BANK:
				if (Bank.isOpen()) {
					if (Inventory.contains(sv.fletch.getName())) {
						Bank.depositAll(sv.fletch.getName());
						Sleep.sleepUntil(new Condition() {
							public boolean verify() {
								return !Inventory.contains(sv.fletch.getName());
							}
						}, 2000);
						returnThis = Calculations.random(300, 600);
					} else {
						if (!Bank.contains(sv.fletch.getLog())) {
							log("Out of: " + sv.fletch.getLog());
							return -1;
						}
						Bank.withdrawAll(sv.fletch.getLog());
						Sleep.sleepUntil(new Condition() {
							public boolean verify() {
								return Inventory.contains(sv.fletch.getLog());
							}
						}, 2000);
						returnThis = Calculations.random(300, 600);
					}
				} else {
					Bank.open();
					Sleep.sleepUntil(new Condition() {
						public boolean verify() {
							return Bank.isOpen();
						}
					}, 2000);
					returnThis = Calculations.random(300, 600);
				}
				break;
			case CHOP:
				if (Inventory.isItemSelected()) {
					Mouse.click();
				}
				GameObject tree = GameObjects.closest(sv.fletch.getTree());
				if (tree != null && tree.exists() && Players.getLocal().getAnimation() == -1) {
					tree.interact("Chop down");
					Sleep.sleepUntil(new Condition() {
						public boolean verify() {
							return Players.getLocal().getAnimation() != -1;
						}
					}, 3500);
					returnThis = Calculations.random(300, 600);
				} else {
					sleep(300, 600);
					returnThis = Calculations.random(300, 500);
				}
				break;
			case DROP:
				for (int i = 0; i < 28; i++) {
					Item item = Inventory.getItemInSlot(i);
					if (item != null && !item.getName().contains("axe") && !item.getName().equals("Knife")) {
						Inventory.interact(i, "Drop");
						sleep(100, 300);
					}
				}
				returnThis = Calculations.random(300, 600);
				break;
			case FLETCH:
				if (Bank.isOpen()) {
					Bank.close();
					Sleep.sleepUntil(new Condition() {
						public boolean verify() {
							return !Bank.isOpen();
						}
					}, 1200);
					return 1;
				} else if (Players.getLocal().getAnimation() == -1) {
					if (Inventory.contains("Knife")) {
						final Widget par = Widgets.getWidget(sv.fletch.getParent());
						WidgetChild chil = null;
						if (par != null) {
							chil = par.getChild(sv.fletch.getChild());
						}
						if (chil != null && chil.isVisible()) {
						/*Mouse.move(chil.getRectangle());
						for(String action : Menu.getMenuActions()){
							log(action);
						}
						Mouse.click(true);
						try {
							Menu.clickAction("Make X");
						} catch (InterruptedException e) {
							e.printStackTrace();
						}*/
							chil.interact("Make X");
							Sleep.sleepUntil(new Condition() {
								public boolean verify() {
									return !par.getChild(sv.fletch.getChild()).isVisible();
								}
							}, 2000);
							if (!chil.isVisible()) {
								int typ = Calculations.random(1, 9);
								Keyboard.type((Integer.toString(typ) + typ + typ), true);
								sleep(456, 765);
							}
							returnThis = Calculations.random(300, 600);
						} else {
							Inventory.interact("Knife", "Use");
							sleep(345, 654);
							Rectangle r = Inventory.slotBounds(Inventory.slot(sv.fletch.getLog()));
							if (r != null) {
								Mouse.move(r.getLocation());
								Mouse.click();
								Sleep.sleepUntil(new Condition() {
									public boolean verify() {
										Widget par = Widgets.getWidget(sv.fletch.getParent());
										WidgetChild chil = null;
										if (par != null) {
											chil = par.getChild(sv.fletch.getChild());
										}
										return chil != null && chil.isVisible();
									}
								}, 2000);
							}
							returnThis = Calculations.random(300, 600);
						}
					} else {
						log("No knife!?");
						returnThis = -1;
					}
				} else {
					returnThis = Calculations.random(300, 600);
				}
				break;
		}
		return returnThis;
	}

	public boolean progress() {
		if (!sv.progress || sv.fletch.ordinal() == Fletching.values().length - 1)
			return false;
		if (Skills.getRealLevel(Skill.FLETCHING) >= Fletching.values()[sv.fletch.ordinal() + 1].getLevel()) {
			sv.fletch = Fletching.values()[sv.fletch.ordinal() + 1];
			return true;
		}
		return false;
	}

	public void onPaint(Graphics g) {
		if (started) {
			if (state != null) {
				g.drawString("State: " + state, 10, 50);
			}
			g.drawString("Runtime: " + t.formatTime(), 10, 65);
			g.drawString("Experience(p/h): " + SkillTracker.getGainedExperience(Skill.FLETCHING) + "(" + SkillTracker.getGainedExperiencePerHour(Skill.FLETCHING) + ")", 10, 80);
			g.drawString("Level(gained): " + Skills.getRealLevel(Skill.FLETCHING) + "(" + SkillTracker.getGainedLevels(Skill.FLETCHING) + ")", 10, 95);
			g.drawString("Fletching: " + sv.fletch.getName(), 10, 110);
		}
	}

}
