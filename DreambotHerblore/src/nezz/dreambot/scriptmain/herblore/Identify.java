package nezz.dreambot.scriptmain.herblore;

import nezz.dreambot.herblore.gui.ScriptVars;
import nezz.dreambot.tools.PricedItem;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.items.Item;

public class Identify extends States {

	public Identify(AbstractScript as, ScriptVars sv) {
		this.as = as;
		this.sv = sv;
		this.lootList.add(new PricedItem(sv.yourHerb.getName(), sv.yourHerb.getUnnotedCleanId(), false));
	}

	Condition depositItems = Inventory::isEmpty;
	Condition withdrawItems = () -> !Inventory.isEmpty();

	public String getState() {
		if (Inventory.contains(sv.yourHerb.getUnnotedGrimyId()))
			return "IDENTIFY";
		else
			return "BANK";
	}

	public int execute() {
		int returnThis = -1;
		state = getState();
		switch (state) {
			case "BANK":
				if (Bank.isOpen()) {
					if (Inventory.contains(sv.yourHerb.getUnnotedCleanId())) {
						Bank.depositAllItems();
						Sleep.sleepUntil(depositItems, 2000);
						returnThis = 200;
					} else {
						if (Bank.contains(sv.yourHerb.getUnnotedGrimyId())) {
							Bank.withdrawAll(sv.yourHerb.getUnnotedGrimyId());
							Sleep.sleepUntil(withdrawItems, 2000);
							returnThis = 200;
						} else {
							Bank.close();
						}
					}
				} else {
					updateLoot();
					Bank.open();
					if (Inventory.isItemSelected()) {
						Inventory.deselect();
					}
					returnThis = 200;
				}
				break;
			case "IDENTIFY":
				if (Bank.isOpen()) {
					Bank.close();
				} else {
					for (int i = 0; i < 28; i++) {
						Item item = Inventory.getItemInSlot(i);
						if (item != null && item.getName().contains("Grimy")) {
							Inventory.slotInteract(i, "Clean");
							Sleep.sleep(100, 300);
							this.updateLoot();
						}
					}
				}
				returnThis = Calculations.random(600, 800);
				this.updateLoot();
				break;
		}
		return returnThis;
	}

	public String getMode() {
		return "Identify Herbs";
	}

}
