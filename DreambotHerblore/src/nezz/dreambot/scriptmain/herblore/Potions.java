package nezz.dreambot.scriptmain.herblore;

import nezz.dreambot.herblore.gui.ScriptVars;
import nezz.dreambot.tools.PricedItem;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.input.Keyboard;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.widget.Widget;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.widgets.WidgetChild;


public class Potions extends States {

	public Potions(AbstractScript as, ScriptVars sv) {
		this.as = as;
		this.sv = sv;
		lootList.add(new PricedItem(sv.yourPot.getName() + "(3)", false));
	}

	@Override
	public String getMode() {
		return "Making potions: " + sv.yourPot.getName();
	}

	Condition makePot = () -> Widgets.getChildWidget(309, 2) != null;

	private boolean wasAnimating = false;

	@Override
	public int execute() {
		int returnThis = -1;
		this.state = getState();
		switch (state) {
			case "Finish Potion":
				if (Bank.isOpen()) {
					Bank.close();
					returnThis = 600;
					wasAnimating = false;
				} else if (wasAnimating && amIAnimating()) {
					this.updateLoot();
					returnThis = 600;
				} else if (Widgets.getWidget(309) != null && Widgets.getWidget(309).getChild(2) != null) {
					Widget par = Widgets.getWidget(309);
					WidgetChild child = null;
					if (par != null) {
						child = par.getChild(2);
					}
					if (child != null) {
						Keyboard.type(" ", false);
						Sleep.sleepUntil(() -> Players.getLocal().isAnimating(), 1200);
						wasAnimating = Players.getLocal().isAnimating();
						returnThis = 400;
					} else {
						wasAnimating = false;
						Logger.log("Issues?");
						returnThis = 1000;
					}
				} else {
					if (!Inventory.isItemSelected()) {
						Inventory.interact(sv.yourPot.getIngredientTwo(), "Use");
						returnThis = 400;
						wasAnimating = false;
					} else {
						Inventory.interact(sv.yourPot.getUnfName(), "Use");
						Sleep.sleepUntil(makePot, 2000);
						Widget par = Widgets.getWidget(309);
						WidgetChild child = null;
						if (par != null) {
							child = par.getChild(2);
						}
						if (child != null) {
							if (child.interact("Make All")) {
								wasAnimating = true;
								Sleep.sleepUntil(() -> Players.getLocal().getAnimation() != -1, 1200);
							}
							returnThis = 400;
						} else {
							Logger.log("Issues?");
							returnThis = 1000;
						}
					}
				}
				break;
			case "Start Potion":
				if (Bank.isOpen()) {
					Bank.close();
					returnThis = 600;
					wasAnimating = false;
				} else if (wasAnimating && amIAnimating()) {
					returnThis = 600;
				} else if (Widgets.getWidget(309) != null && Widgets.getWidget(309).getChild(2) != null) {
					Widget par = Widgets.getWidget(309);
					WidgetChild child = null;
					if (par != null) {
						child = par.getChild(2);
					}
					if (child != null) {
						if (child.interact("Make All")) {
							wasAnimating = true;
							Sleep.sleepUntil(() -> Players.getLocal().getAnimation() != -1, 1200);
						} else {
							wasAnimating = false;
						}
						returnThis = 400;
					} else {
						wasAnimating = false;
						Logger.log("Issues?");
						returnThis = 1000;
					}
				} else {
					if (!Inventory.isItemSelected()) {
						Inventory.interact(sv.yourPot.getIngredientOne(), "Use");
						returnThis = 400;
						wasAnimating = false;
					} else {
						Inventory.interact("Vial of water", "Use");
						Sleep.sleepUntil(makePot, 2000);
						Widget par = Widgets.getWidget(309);
						WidgetChild child = null;
						if (par != null) {
							child = par.getChild(2);
						}
						if (child != null) {
							returnThis = 200;
							if (child.interact("Make All")) {
								wasAnimating = true;
								Sleep.sleepUntil(() -> Players.getLocal().getAnimation() != -1, 1200);
							} else {
								wasAnimating = false;
							}
						} else {
							wasAnimating = false;
							Logger.log("Issues?");
							returnThis = 1000;
						}
					}
				}
				break;
			case "Bank":
				this.updateLoot();
				wasAnimating = false;
				if (Bank.isOpen()) {
					if (Inventory.contains(sv.yourPot.getName() + "(3)")) {
						Bank.depositAllItems();
						Sleep.sleepUntil(Inventory::isEmpty, 1200);
						returnThis = 100;
					} else {
						if (Inventory.isEmpty()) {
							if (Bank.contains(sv.yourPot.getUnfName())) {
								Logger.log("withdrawing unfinished potion");
								Bank.withdraw(sv.yourPot.getUnfName(), 14);
							} else if (Bank.contains(sv.yourPot.getIngredientOne())) {
								Logger.log("withdrawing ingredient one");
								Bank.withdraw(sv.yourPot.getIngredientOne(), 14);
							} else {
								Logger.log("You don't have the items!");
								return -1;
							}
							returnThis = 600;
						} else if (Inventory.contains(sv.yourPot.getIngredientOne())) {
							if (Bank.contains("Vial of water")) {
								Logger.log("Vial of water");
								Bank.withdrawAll("Vial of water");
								returnThis = 600;
							} else {
								Logger.log("You don't have any vials of water!");
							}
						} else if (Inventory.contains(sv.yourPot.getUnfName())) {
							if (!Inventory.contains(sv.yourPot.getIngredientTwo())) {
								Logger.log("Get ingredient two");
								Bank.withdrawAll(sv.yourPot.getIngredientTwo());
								returnThis = 600;
							} else {
								Logger.log("You don't have any of your second ingredient!");
							}
						} else {
							Logger.log("Something went wrong");
						}
					}
				} else {
					Logger.log("Opening bank!");
					Bank.open();
					if (Inventory.isItemSelected()) {
						Inventory.deselect();
					}
					Logger.log("Waiting for bank to open");
					Sleep.sleepUntil(Bank::isOpen, Calculations.random(1200, 1500));

					returnThis = 200;
				}
				break;
		}
		return returnThis;
	}

	private boolean amIAnimating() {
		for (int i = 0; i < 50; i++) {
			if (Players.getLocal().getAnimation() != -1)
				return true;
			else
				Sleep.sleep(30);
		}
		return false;
	}

	@Override
	public String getState() {
		if (Inventory.contains("Vial of water") || Inventory.contains(sv.yourPot.getIngredientOne())) {
			if (Inventory.contains("Vial of water") && Inventory.contains(sv.yourPot.getIngredientOne()))
				return "Start Potion";
			else {
				if (Inventory.contains("Vial of water") && Inventory.contains(sv.yourPot.getUnfName()) && Inventory.contains(sv.yourPot.getIngredientTwo()))
					return "Finish Potion";
				return "Bank";
			}
		} else if (Inventory.contains(sv.yourPot.getUnfName())) {
			if (Inventory.contains(sv.yourPot.getIngredientTwo())) {
				return "Finish Potion";
			} else {
				return "Bank";
			}
		} else
			return "Bank";
	}


}
