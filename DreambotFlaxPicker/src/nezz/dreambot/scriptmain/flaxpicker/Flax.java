package nezz.dreambot.scriptmain.flaxpicker;

import nezz.dreambot.tools.PricedItem;
import org.dreambot.api.Client;
import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.widgets.Menu;

import java.awt.*;

@ScriptManifest(author = "Nezz", category = Category.MONEYMAKING, description = "Picks flax", name = "DreamBot Flax Picker", version = 0)
public class Flax extends AbstractScript {


	private final Tile FLAX_TILE = new Tile(2741, 3446, 0);
	private final Area FLAX_AREA = new Area(2737, 3450, 2745, 3444);

	private int runThresh = Calculations.random(30, 70);
	private int walkThresh = Calculations.random(4, 8);

	private Timer timer;
	private PricedItem flax = null;
	private GameObject flaxToPick = null;

	public void onStart() {
		flax = new PricedItem("Flax", false);
		timer = new Timer();
	}

	private State state = null;

	private enum State {
		PICK, BANK
	}

	private State getState() {
		if (Inventory.isFull()) {
			return State.BANK;
		} else
			return State.PICK;
	}

	private GameObject getFlax() {
		if (flaxToPick == null || !flaxToPick.exists()) {
			flaxToPick = GameObjects.closest("Flax");
		}
		return flaxToPick;
	}

	@Override
	public int onLoop() {
		flax.update();
		if (!Walking.isRunEnabled() && Walking.getRunEnergy() > runThresh) {
			Walking.toggleRun();
			runThresh = Calculations.random(30, 70);
		}
		if (!Walking.shouldWalk(walkThresh)) {
			return Calculations.random(500, 700);
		}
		walkThresh = Calculations.random(4, 8);
		state = getState();
		switch (state) {
			case BANK:
				flaxToPick = null;
				if (BankLocation.SEERS.getArea(5).contains(Players.getLocal())) {
					if (Bank.isOpen()) {
						Bank.depositAllItems();
						Sleep.sleepUntil(Inventory::isEmpty, 1200);
					} else {
						Bank.open();
						Sleep.sleepUntil(Bank::isOpen, 1200);
					}
				} else {
					Walking.walk(BankLocation.SEERS.getCenter());
					sleep(700, 900);
				}
				break;
			case PICK:
				if (FLAX_AREA.contains(Players.getLocal())) {
					if (Menu.getDefaultAction().equals("Pick")) {
						Mouse.click(false);
						sleep((int) (Calculations.random(300, 400) * Client.seededRandom()));
					} else {
						GameObject flax = getFlax();
						if (flax != null) {
							flax.interact("Pick");
							sleep((int) (Calculations.random(300, 500) * Client.seededRandom()));
						}
					}
				} else {
					Walking.walk(FLAX_TILE);
					sleep(700, 900);
				}
				break;
		}
		return (int) (Calculations.random(200, 300) * Client.seededRandom());
	}

	public void onPaint(Graphics g) {
		if (state != null) {
			g.drawString("State: " + state, 10, 35);
			g.drawString("Runtime: " + timer.formatTime(), 10, 50);
			g.drawString("Flax(p/h): " + flax.getAmount() + "(" + timer.getHourlyRate(flax.getAmount()) + ")", 10, 65);
		}
	}


}
