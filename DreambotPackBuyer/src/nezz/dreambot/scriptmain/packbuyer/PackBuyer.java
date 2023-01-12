package nezz.dreambot.scriptmain.packbuyer;

import nezz.dreambot.packbuyer.gui.ScriptVars;
import nezz.dreambot.packbuyer.gui.buyerGui;
import nezz.dreambot.tools.PricedItem;
import org.dreambot.api.Client;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.Shop;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.items.Item;

import java.awt.*;

@ScriptManifest(author = "Nezz", name = "DreamBot Pack Buyer", version = 0, category = Category.MONEYMAKING, description = "Buys any pack and opens it")
public class PackBuyer extends AbstractScript {

	private boolean hopWorlds = false;
	private final int[] f2pWorlds = new int[]{381, 382, 384, 393, 394};

	ScriptVars sv = new ScriptVars();

	PricedItem feathers;
	State state;
	Timer timer;

	boolean started = false;

	private enum State {
		BUY, OPEN_PACKS, HOP
	}

	private State getState() {
		if (Inventory.contains(sv.packName)) {
			return State.OPEN_PACKS;
		}
		if (hopWorlds)
			return State.HOP;
		if (Inventory.contains(sv.packName)) {
			return State.OPEN_PACKS;
		} else
			return State.BUY;
	}

	public void onStart() {
		buyerGui gui = new buyerGui(sv);
		gui.setVisible(true);
		while (!sv.started) {
			Sleep.sleep(200);
		}

		String itemName = sv.packName.replace(" pack", "");
		log(itemName);
		feathers = new PricedItem(itemName, false);
		timer = new Timer();
		started = true;
	}

	@Override
	public int onLoop() {
		log("looping");
		if (feathers == null) {
			String itemName = sv.packName.replace(" pack", "");
			log(itemName);
			feathers = new PricedItem(itemName, false);
		}
		if (Players.getLocal().isMoving() && Client.getDestination() != null && Client.getDestination().distance(Players.getLocal().getTile()) > 3)
			return Calculations.random(200, 300);
		state = getState();
		switch (state) {
			case HOP:
				if (sv.hopWorlds) {
					int hopTo = f2pWorlds[Calculations.random(0, f2pWorlds.length - 1)];
					for (int i = 0; i < 15; i++) {
						if (hopTo != Worlds.getCurrentWorld()) {
							break;
						}
						hopTo = f2pWorlds[Calculations.random(0, f2pWorlds.length - 1)];
					}
					WorldHopper.hopWorld(hopTo);
				} else {
					Sleep.sleep(30000, 50000);
				}
				hopWorlds = false;
				break;
			case BUY:
				if (!Shop.isOpen()) {
					Shop.open();
					waitFor(Shop::isOpen, 1500);
				} else {
					Item pack = Shop.get(sv.packName);
					if (pack != null && pack.getAmount() > sv.minAmt) {
						Shop.purchase(pack, 10);
						waitFor(() -> Inventory.contains(sv.packName), 1000);
					} else {
						try {
							Thread.sleep(Calculations.random(200, 300));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					pack = Shop.get(sv.packName);
					if (pack == null || pack.getAmount() <= sv.minAmt / 2)
						hopWorlds = true;
				}
				break;
			case OPEN_PACKS:
				if (Shop.isOpen()) {
					Shop.close();
				} else {
					for (int i = 0; i < 28; i++) {
						Item it = Inventory.getItemInSlot(i);
						if (it != null && it.getName().equals(sv.packName)) {
							Inventory.slotInteract(i, "Open");
							try {
								Thread.sleep(Calculations.random(100, 150));
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						feathers.update();
					}
					feathers.update();
				}
				break;
		}
		return Calculations.random(100, 200);
	}

	public void waitFor(Condition c, int timeout) {
		Sleep.sleepUntil(c, timeout);
	}

	public void onPaint(Graphics g) {
		if (started) {
			if (state != null) {
				g.drawString("State: " + state, 5, 50);
			}
			g.drawString(feathers.getName() + " bought(p/h): " + feathers.getAmount() + "(" + timer.getHourlyRate(feathers.getAmount()) + ")", 5, 65);
			g.drawString("GP Made(p/h): " + feathers.getAmount() * (feathers.getPrice() - sv.perItem) + "(" + timer.getHourlyRate(feathers.getAmount() * (feathers.getPrice() - sv.perItem)) + ")", 5, 80);
			g.drawString("Time run: " + timer.formatTime(), 5, 95);
		}
	}

}
