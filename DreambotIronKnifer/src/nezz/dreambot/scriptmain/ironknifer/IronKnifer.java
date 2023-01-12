package nezz.dreambot.scriptmain.ironknifer;

import nezz.dreambot.tools.PricedItem;
import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Map;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.awt.*;

@ScriptManifest(author = "Nezz", category = Category.SMITHING, description = "Makes iron knives in Varrock", name = "DreamBot Iron Knifer", version = 0)
public class IronKnifer extends AbstractScript {
	PricedItem ironKnife = null;
	private State state = null;
	private Timer t = null;
	private final Tile BANK_TILE = new Tile(3185, 3436, 0);
	private final Tile ANVIL_TILE = new Tile(3188, 3425, 0);
	private AnvilSmith as = null;
	private GameObject bankBooth = null;
	private long boothUpdate = 0;
	private long lastAnimated = 0;
	private int runThresh = Calculations.random(30, 70);
	private boolean movedAnvil = false;
	private boolean movedBank = false;

	private enum State {
		BANK, SMITH
	}

	private State getState() {
		if (Inventory.contains("Iron bar")) {
			return State.SMITH;
		}
		return State.BANK;
	}

	public void onStart() {
		as = new AnvilSmith();
		ironKnife = new PricedItem("Iron knife", false);
		t = new Timer();
	}

	private Point getRandomPoint(Tile t) {
		Point point = null;
		Point p = Map.tileToMiniMap(t);
		if (p.x > 0)
			point = randomizePoint(p);
		return point;
	}

	private Point randomizePoint(Point p) {
		return new Point(p.x + Calculations.random(-5, 5), p.y + Calculations.random(-5, 5));
	}

	private Rectangle getRect(Tile t) {
		Point p = Map.tileToMiniMap(t);
		return new Rectangle(p.x - 5, p.y - 5, 10, 10);
	}

	private boolean walk(Tile t) {
		Point p = getRandomPoint(t);
		if (p == null)
			return false;
		Mouse.move(p);
		sleep(60, 150);
		Mouse.click();
		Sleep.sleepUntil(() -> Players.getLocal().isMoving(), 1200);
		return Players.getLocal().isMoving();
	}

	private GameObject getBankBooth() {
		if (bankBooth == null || System.currentTimeMillis() - boothUpdate > 5000) {
			bankBooth = GameObjects.closest(go -> {
				if (go == null || !go.exists() || go.getName() == null)
					return false;
				if (!go.getName().equals("Bank booth"))
					return false;
				return go.getTile().equals(new Tile(3186, 3436, 0));// || go.getTile().equals(new Tile(3186,3438,0));
			});
			boothUpdate = System.currentTimeMillis();
		}
		log("Booth tile: " + bankBooth.getTile());
		return bankBooth;
	}

	private boolean waitForWalk() {
		if (!Players.getLocal().isMoving() || state == null) {
			return false;
		}
		Tile destination = Walking.getDestination();
		if (destination != null && destination.distance(Players.getLocal()) < Calculations.random(3, 6)) {
			return false;
		}
		if (state.equals(State.BANK) && getBankBooth() != null && getBankBooth().isOnScreen()) {
			return false;
		} else return !state.equals(State.SMITH) || as.getAnvil() == null || !as.getAnvil().isOnScreen();
	}

	private String getCameraDirection() {
		int yaw = Camera.getYaw();
		if (yaw > 1800 || yaw < 300)
			return "N";
		else if (yaw < 800) {
			return "W";
		} else if (yaw < 1300) {
			return "S";
		} else
			return "E";
	}

	private Rectangle getHoverSpot() {
		Rectangle r = null;
		if (state.equals(State.BANK)) {
			switch (getCameraDirection()) {
				case "S":
					r = new Rectangle(0, 0, 165, 340);
					break;
				case "N":
					r = new Rectangle(330, 0, 185, 340);
					break;
				case "W":
					r = new Rectangle(0, 200, 515, 115);
					break;
				case "E":
					r = new Rectangle(0, 0, 515, 125);
					break;
			}
		} else {
			Rectangle temp = Players.getLocal().getBoundingBox();
			r = new Rectangle(temp.x - 50, temp.y - 50, temp.height + 100, temp.width + 100);
		}
		return r;
	}


	@Override
	public int onLoop() {
		if (!Walking.isRunEnabled() && Walking.getRunEnergy() > runThresh) {
			Walking.toggleRun();
			runThresh = Calculations.random(30, 70);
		}
		if (waitForWalk())
			return Calculations.random(300, 600);
		state = getState();
		switch (state) {
			case BANK:
				movedAnvil = false;
				if (!Bank.isOpen()) {
					if (BANK_TILE.distance(Players.getLocal()) > 5 && !Players.getLocal().isMoving()) {
						walk(BANK_TILE);
					} else if (!Players.getLocal().isMoving()) {
						Bank.open();
						Sleep.sleepUntil(Bank::isOpen, 3600);
					} else if (!movedBank) {
						Mouse.move(getHoverSpot());
						movedBank = true;
					}
				} else {
					if (Inventory.contains("Iron knife")) {
						ironKnife.update();
						Bank.depositAllExcept("Hammer");
						Sleep.sleepUntil(() -> !Inventory.contains("Iron knife"), 1200);
					} else if (Bank.contains("Iron bar")) {
						Bank.withdrawAll("Iron bar");
						Sleep.sleepUntil(() -> Inventory.contains("Iron bar"), 1200);
					} else {
						log("Out of bars!");
						stop();
					}
				}
				break;
			case SMITH:
				movedBank = false;
				if (as.isOpen() || Inventory.isItemSelected() || !amIAnimating()) {
					if (as.isOpen()) {
						as.makeKnife();
						Sleep.sleepUntil(() -> !as.isOpen(), 1200);
					} else {
						if (ANVIL_TILE.distance(Players.getLocal()) > 5 && !Players.getLocal().isMoving()) {
							walk(ANVIL_TILE);
						/*Walking.walk(ANVIL_TILE);
						Sleep.sleepUntil(new Condition(){
							public boolean verify(){
								return Players.getLocal().isMoving();
							}
						},1200);*/
							Tile dest = Walking.getDestination();
							if (dest != null && dest.distance(ANVIL_TILE) < 4) {
								if (!Inventory.isItemSelected()) {
									sleep(400, 1400);
									Inventory.interact("Iron bar", "Use");
								}
							}
						} else {
							if (Inventory.isItemSelected()) {
								if (!Players.getLocal().isMoving()) {
									if (as.getAnvil() != null) {
										as.getAnvil().interact("Use");
										Sleep.sleepUntil(() -> as.isOpen(), 2400);
									}
								} else if (!movedAnvil) {
									Mouse.move(getHoverSpot());
									movedAnvil = true;
								}
							} else {
								Inventory.interact("Iron bar", "Use");
								Sleep.sleepUntil(Inventory::isItemSelected, 1200);
							}
						}
					}
				} else {
					ironKnife.update();
					sleep(300, 500);
				}
				break;
		}
		return Calculations.random(100, 200);
	}

	public boolean amIAnimating() {
		if (System.currentTimeMillis() - lastAnimated > 5000) {
			if (Players.getLocal().getAnimation() != -1) {
				lastAnimated = System.currentTimeMillis();
				return true;
			}
			return false;
		}
		for (int i = 0; i < 20; i++) {
			if (Players.getLocal().getAnimation() != -1) {
				lastAnimated = System.currentTimeMillis();
				return true;
			} else {
				sleep(50);
			}
		}
		return false;
	}

	@Override
	public void onPaint(Graphics g) {
		if (t != null) {
			((Graphics2D) g).draw(getRect(BANK_TILE));
			((Graphics2D) g).draw(getRect(ANVIL_TILE));
			((Graphics2D) g).draw(getHoverSpot());
			g.drawString("Runtime: " + t.formatTime(), 10, 35);
			g.drawString("State: " + state.toString(), 10, 50);
			g.drawString("Knives(p/h): " + ironKnife.getAmount() + "(" + t.getHourlyRate(ironKnife.getAmount()) + ")", 10, 65);
		}
	}


	//312,24
	//312,1,11
}
