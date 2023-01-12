package nezz.dreambot.scriptmain.powerminer;

import nezz.dreambot.powerminer.gui.ScriptVars;
import nezz.dreambot.powerminer.gui.minerGui;
import org.dreambot.api.Client;
import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Map;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.SkillTracker;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;

import java.awt.*;
import java.util.List;

@ScriptManifest(author = "Nezz", description = "Power Miner", name = "DreamBot Power Miner", version = 1.1, category = Category.MINING)
public class Miner extends AbstractScript {

	private Timer timer;
	ScriptVars sv = new ScriptVars();
	//current state
	private State state;
	private GameObject currRock = null;
	//private Tile startTile = null;
	private MineTask currTask = null;
	private int taskPlace = 0;
	private boolean started = false;
	private minerGui gui = null;

	private enum State {
		MINE, DROP, BANK, GUI
	}

	private State getState() {
		if (!started) {
			return State.GUI;
		}
		if (currTask.isPowerMine()) {
			if (Inventory.contains(currTask.getOreName()))
				return State.DROP;
		} else if (Inventory.isFull()) {
			return State.BANK;
		}
		return State.MINE;
	}

	@Override
	public void onStart() {
		log("Starting DreamBot's AIO Mining script!");
	}

	@Override
	public int onLoop() {
		if (started) {
			if (currTask.reachedGoal()) {
				log("Finished current task!");
				taskPlace++;
				if (taskPlace >= sv.tasks.size()) {
					log("Finished all tasks!");
					stop();
					return 1;
				}
				currTask = sv.tasks.get(taskPlace);
				currTask.resetTimer();
				return 200;
			}
			Player myPlayer = Players.getLocal();
			if (!Walking.isRunEnabled() && Walking.getRunEnergy() > Calculations.random(30, 70)) {
				Walking.toggleRun();
			}
			if (myPlayer.isMoving() && Client.getDestination() != null && Client.getDestination().distance(myPlayer) > 5)
				return Calculations.random(300, 600);
			if (Players.getLocal().isInCombat())
				return Calculations.random(300, 600);
		}
		state = getState();
		switch (state) {
			case GUI:
				if (gui == null) {
					gui = new minerGui(sv);
					sleep(300);
				} else if (!gui.isVisible() && !sv.started) {
					gui.setVisible(true);
					sleep(1000);
				} else {
					if (!sv.started) {
						sleep(300);
					} else {
						currTask = sv.tasks.get(0);
						currTask.resetTimer();
						SkillTracker.start(Skill.MINING);
						timer = new Timer();
						started = true;
					}
				}
				break;
			case BANK:
				if (Bank.isOpen()) {
					if (Inventory.get(i -> {
						if (i == null || i.getName() == null) {
							return false;
						}
						return i.getName().contains("pickaxe");
					}) != null) {
						for (int i = 0; i < 28; i++) {
							final Item item = Inventory.getItemInSlot(i);
							if (item != null && !item.getName().contains("pickaxe")) {
								Bank.depositAll(item.getName());
								Sleep.sleepUntil(() -> !Inventory.contains(item.getName()), 2000);
							}
						}
					} else {
						Bank.depositAllItems();
						Sleep.sleepUntil(Inventory::isEmpty, 2000);
					}
				} else {
					if (currTask.getBank().getArea(4).contains(Players.getLocal())) {
						Bank.open();
						Sleep.sleepUntil(Bank::isOpen, 2000);
					} else {
						Walking.walk(currTask.getBank().getCenter());
						Sleep.sleepUntil(() -> Players.getLocal().isMoving(), 2000);
					}
				}
				break;
			case DROP:
				currRock = null;
				Item ore = Inventory.get(currTask.getOreName());
				if (ore != null) {
					Inventory.interact(ore.getName(), "Drop");
					Sleep.sleepUntil(() -> {
						Item ore1 = Inventory.get(currTask.getOreName());
						return ore1 == null;
					}, 1200);
				}
				break;
			case MINE:
				if (Bank.isOpen()) {
					Bank.close();
					Sleep.sleepUntil(() -> !Bank.isOpen(), 1200);
				} else {
					if (currTask.getStartTile().distance(Players.getLocal()) > 10) {
						Walking.walk(currTask.getStartTile());
						Sleep.sleepUntil(() -> Players.getLocal().isMoving(), 2000);
					} else if ((currTask.dontMove() && !Players.getLocal().getTile().equals(currTask.getStartTile()))) {
						Walking.walk(currTask.getStartTile());
						Sleep.sleepUntil(() -> Players.getLocal().isMoving(), 2000);
						Sleep.sleepUntil(() -> !Players.getLocal().isMoving(), 2000);
					} else {
						if (Camera.getPitch() < 270) {
							Camera.rotateToPitch((int) (Calculations.random(300, 400) * Client.seededRandom()));
						}
						if (Players.getLocal().getAnimation() == -1 && (currRock == null || !currRock.isOnScreen() || !currTask.isPowerMine()))
							currRock = currTask.getRock();//GameObjects.getClosest(currTask.getIDs());
						if (Players.getLocal().getAnimation() == -1) {
							if (currRock != null && currRock.exists()) {
								if (currRock.interact("Mine")) {
									if (currTask.isPowerMine()) {
										hover(true);
									} else {
										Sleep.sleepUntil(() -> Players.getLocal().getAnimation() != -1, 2000);
										Sleep.sleepUntil(() -> Players.getLocal().getAnimation() == -1, 1800);
										sleep(300, 500);
									}
								}
							}
						} else
							sleep(300, 600);
					}
				}
				currTask.getTracker().update();
				break;
		}
		return 200;
	}

	public int getFirstEmptySlot() {
		for (int i = 0; i < 28; i++) {
			Item it = Inventory.getItemInSlot(i);
			if (it == null || it.getName().contains("ore")) {
				return i;
			}
		}
		return 0;
	}

	public void hover(boolean fromInteract) {
		int firstEmpty = getFirstEmptySlot();
		Rectangle r = Inventory.slotBounds(firstEmpty);
		if (!r.contains(Mouse.getPosition())) {
			int x1 = (int) r.getCenterX() - Calculations.random(0, 10);
			int y1 = (int) r.getCenterY() - Calculations.random(0, 10);
			int x2 = (int) r.getCenterX() + Calculations.random(0, 10);
			int y2 = (int) r.getCenterY() + Calculations.random(0, 10);
			int fX = Calculations.random(x1, x2);
			int fY = Calculations.random(y1, y2);
			Mouse.move(new Point(fX, fY));
		}
		if (fromInteract) {
			Sleep.sleepUntil(() -> Players.getLocal().getAnimation() != -1, 2000);
		}
	}

	@Override
	public void onExit() {
		log("Stopping testing!");
	}

	public void onPaint(Graphics g) {
		if (started) {
			g.setColor(Color.green);
			if (state != null)
				g.drawString("State: " + state, 5, 50);
			g.drawString("Total Runtime: " + timer.formatTime(), 5, 65);
			g.drawString("Task Runtime: " + currTask.getTimer().formatTime(), 5, 80);
			g.drawString("Experience(p/h): " + SkillTracker.getGainedExperience(Skill.MINING) + "(" + SkillTracker.getGainedExperiencePerHour(Skill.MINING) + ")", 5, 95);
			g.drawString("Level(gained): " + Skills.getRealLevel(Skill.MINING) + "(" + SkillTracker.getGainedLevels(Skill.MINING) + ")", 5, 110);
			g.drawString("Ores(p/h): " + currTask.getTracker().getAmount() + "(" + timer.getHourlyRate(currTask.getTracker().getAmount()) + ")", 10, 125);
			g.drawString("Current task: " + currTask.getOreName() + "::" + currTask.getGoal(), 10, 140);
			for (int i = 0; i < sv.tasks.size(); i++) {
				MineTask mt = sv.tasks.get(i);
				if (mt != null) {
					if (mt.getFinished()) {
						g.setColor(Color.blue);
					} else {
						g.setColor(Color.red);
					}
					String task = mt.getOreName() + "::" + mt.getGoal();
					g.drawString(task, 10, 155 + i * 15);
				}
			}
		} else {
			List<GameObject> rocks = GameObjects.all(go -> {
				if (go == null || !go.exists() || go.getName() == null || !go.getName().equals("Rocks"))
					return false;
				return go.isOnScreen();
			});
			if (!rocks.isEmpty()) {
				for (GameObject go : rocks) {
					Tile rockTile = go.getTile();
					Rectangle tileRect = Map.getBounds(rockTile);
					Point startPoint = new Point(tileRect.x, (int) tileRect.getCenterY());
					g.drawString("ID: " + go.getID(), startPoint.x, startPoint.y);
				}
			}
		}
	}
}
