package nezz.dreambot.scriptmain.hunter;

import nezz.dreambot.scriptmain.hunter.gui.ScriptVars;
import nezz.dreambot.scriptmain.hunter.gui.hunterGui;
import nezz.dreambot.tools.PricedItem;
import org.dreambot.api.Client;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Map;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.SkillTracker;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;

import java.awt.*;
import java.util.List;

@ScriptManifest(author = "Nezz", description = "Catches stuff", name = "DreamBot Hunter", version = 0, category = Category.HUNTING)
public class Hunter extends AbstractScript {

	PricedItem track;
	ScriptVars sv = new ScriptVars();

	public void onStart() {
		hunterGui gui = new hunterGui(sv);
		gui.setVisible(true);
		while (!sv.started) {
			sleep(200);
		}
		track = new PricedItem(sv.huntThis.getTrackItem(), false);
		Tile startTile = Players.getLocal().getTile();
		sv.trapTiles = new Tile[getTrapAmount()];
		getTileArray(getTrapAmount());
		SkillTracker.start(Skill.HUNTER);
		startingLevel = Skills.getRealLevel(Skill.HUNTER);
		rt = new Timer();
		log("Starting script!");
	}

	private int getTrapAmount() {
		int amount = 0;
		int level = Skills.getRealLevel(Skill.HUNTER);
		if (level < 20)
			amount = 1;
		else if (level < 40)
			amount = 2;
		else if (level < 60)
			amount = 3;
		else if (level < 80)
			amount = 4;
		else {
			amount = 5;
		}
		if (Inventory.count(sv.huntThis.getTrapName()) < amount) {
			amount = Inventory.count(sv.huntThis.getTrapName());
		}
		return amount;
	}

	private void getTileArray(int traps) {
		Tile p = Players.getLocal().getTile();
		switch (traps) {
			case 1:
				sv.trapTiles[0] = Players.getLocal().getTile();
				break;
			case 2:
				for (int i = 0; i < sv.trapTiles.length; i++) {
					sv.trapTiles[i] = new Tile(Players.getLocal().getTile().getX() - i, Players.getLocal().getTile().getY(), Players.getLocal().getTile().getZ());
				}
				break;
			case 3:
				for (int i = 0; i < sv.trapTiles.length; i++) {
					sv.trapTiles[i] = new Tile(Players.getLocal().getTile().getX() - i, Players.getLocal().getTile().getY(), Players.getLocal().getTile().getZ());
				}
				break;
			case 4:
				sv.trapTiles[0] = p;
				sv.trapTiles[1] = new Tile(p.getX() - 1, p.getY() + 1, p.getZ());
				sv.trapTiles[2] = new Tile(p.getX() + 1, p.getY() + 1, p.getZ());
				sv.trapTiles[3] = new Tile(p.getX() - 1, p.getY() - 1, p.getZ());
				break;
			case 5:
				sv.trapTiles[0] = p;
				sv.trapTiles[1] = new Tile(p.getX() - 1, p.getY() + 1, p.getZ());
				sv.trapTiles[2] = new Tile(p.getX() + 1, p.getY() + 1, p.getZ());
				sv.trapTiles[3] = new Tile(p.getX() - 1, p.getY() - 1, p.getZ());
				sv.trapTiles[4] = new Tile(p.getX() + 1, p.getY() - 1, p.getZ());
				break;
		}
	}

	private Timer rt;

	private int startingLevel = 0;

	private State state;

	private enum State {
		LAY_TRAP, PICK_TRAP, EMPTY_TRAP, SLEEP, DROP
	}

	private boolean trapDown() {
		for (Tile t : sv.trapTiles) {
			List<GroundItem> tileItems = GroundItems.getForTile(t);
			if (tileItems == null || tileItems.size() <= 0) {
				continue;
			}
			for (GroundItem gi : tileItems) {
				if (gi != null && gi.getName() != null) {
					return gi.getName().equals(sv.huntThis.getTrapName());
				}
			}
		}
		return false;
	}

	private boolean trapOnTile(Tile t) {
		List<GroundItem> tileItems = GroundItems.getForTile(t);
		if (tileItems == null || tileItems.size() <= 0) {
			return false;
		}
		for (GroundItem gi : tileItems) {
			if (gi != null && gi.getName() != null) {
				return gi.getName().equals(sv.huntThis.getTrapName());
			}
		}
		return false;
	}

	private State getState() {
		if (trapDown()) {
			return State.PICK_TRAP;
		}
		if (sv.huntThis.getDropItems().length > 0 && Inventory.contains(sv.huntThis.getDropItems()) && !(trapFull() || trapBroken() || !allTrapsLaid()))
			return State.DROP;
		if (trapFull() || trapBroken())
			return State.EMPTY_TRAP;
		if (!allTrapsLaid())
			return State.LAY_TRAP;
		return State.SLEEP;
	}

	@Override
	public int onLoop() {
		if (Skills.getRealLevel(Skill.HUNTER) >= sv.stopAt) {
			stop();
			return -1;
		} else if (getTrapAmount() > sv.trapTiles.length) {
			sv.trapTiles = new Tile[getTrapAmount()];
			getTileArray(getTrapAmount());
		}
		state = getState();
		switch (state) {
			case DROP:
				if (standingOnTrap()) {
					final Tile t = new Tile(Players.getLocal().getX(), Players.getLocal().getY() - 1, Players.getLocal().getZ());
					Map.interact(t, "Walk here");
					Sleep.sleepUntil(() -> Players.getLocal().getTile().equals(t) && !Players.getLocal().isMoving(), 5000);
				} else {
					for (int i = 0; i < sv.huntThis.getDropItems().length; i++) {
						if (Inventory.contains(sv.huntThis.getDropItems()[i])) {
							Inventory.interact(sv.huntThis.getDropItems()[i], "Drop");
							sleep(Calculations.random(400, 700));
						}
					}
				}
				break;
			case EMPTY_TRAP:
				if (trapFull()) {
					GameObject fullTrap = getFullTrap();
					if (fullTrap != null) {
						fullTrap.interact(sv.huntThis.getEmptyAction());
						long t = System.currentTimeMillis();
						while (System.currentTimeMillis() - t < 3000 && posContainsFullTrap(fullTrap.getTile())) {
							sleep(30);
						}
					}
				}
				if (trapBroken()) {
					GameObject brokenTrap = getBrokenTrap();
					if (brokenTrap != null) {
						brokenTrap.interact(sv.huntThis.getDismantleAction());
						long t = System.currentTimeMillis();
						while (System.currentTimeMillis() - t < 3000 && posContainsBrokenTrap(brokenTrap.getTile())) {
							sleep(30);
						}
					}
				}
				break;
			case LAY_TRAP:
				final Tile nextPos = getNextTrapPos();
				if (nextPos != null) {
					if (!Players.getLocal().getTile().equals(nextPos)) {
						Tile t = Client.getDestination();
						if (t != null && t.equals(nextPos)) {
							Sleep.sleepUntil(() -> Players.getLocal().getTile().equals(nextPos), 1200);
						} else {
							Map.interact(nextPos, "Walk here");
							Sleep.sleepUntil(() -> Players.getLocal().getTile().equals(nextPos), 1200);
						}
					} else {
						Item trap = Inventory.get(sv.huntThis.getTrapName());
						if (trap != null) {
							if (Players.getLocal().getTile().equals(nextPos)) {
								Inventory.interact(trap.getName(), sv.huntThis.getLayAction());
								long t = System.currentTimeMillis();
								while (System.currentTimeMillis() - t < 6000 && (!posContainsTrap(nextPos) || Players.getLocal().getAnimation() != -1)) {
									sleep(30);
								}
							}
						}
						sleep(Calculations.random(1100, 1400));
					}
				}
				break;
			case PICK_TRAP:
				final GroundItem yourTrap = GroundItems.closest(sv.huntThis.getTrapName());
				if (yourTrap != null) {
					yourTrap.interact("Take");
					Sleep.sleepUntil(() -> !trapOnTile(yourTrap.getTile()), 1200);
				}
				break;
			case SLEEP:
				sleep(Calculations.random(100, 200));
				if (standingOnTrap()) {
					final Tile t = new Tile(Players.getLocal().getX(), Players.getLocal().getY() - 1, Players.getLocal().getZ());
					Map.interact(t, "Walk here");
					Sleep.sleepUntil(() -> Players.getLocal().getTile().equals(t), 1200);
					sleep(Calculations.random(300, 800));
				}
				break;
		}
		return Calculations.random(150, 250);
	}

	private boolean standingOnTrap() {
		for (int i = 0; i < sv.trapTiles.length; i++) {
			if (Players.getLocal().getTile().equals(sv.trapTiles[i]))
				return true;
		}
		return false;
	}

	private GameObject getBrokenTrap() {
		for (int i = 0; i < sv.trapTiles.length; i++) {
			final Tile yourPos = sv.trapTiles[i];
			if (posContainsBrokenTrap(yourPos)) {
				return GameObjects.closest(r -> {
					if (r == null || r.getID() != sv.huntThis.getBrokenTrapID())
						return false;
					return r.getTile().equals(yourPos);
				});
			}

		}
		return null;
	}

	private GameObject getFullTrap() {
		for (int i = 0; i < sv.trapTiles.length; i++) {
			final Tile yourPos = sv.trapTiles[i];
			if (posContainsFullTrap(yourPos)) {
				return GameObjects.closest(r -> {
					if (r == null || r.getID() != sv.huntThis.getFullTrapID())
						return false;
					return r.getTile().equals(yourPos);
				});
			}

		}
		return null;
	}

	private boolean trapBroken() {
		for (int i = 0; i < sv.trapTiles.length; i++) {
			final Tile yourPos = sv.trapTiles[i];
			if (posContainsBrokenTrap(yourPos))
				return true;
		}
		return false;
	}

	private boolean posContainsBrokenTrap(final Tile p) {
		GameObject currTrap = GameObjects.closest(r -> {
			if (r.getName() == null || r.getID() != sv.huntThis.getBrokenTrapID())
				return false;
			return r.getTile().equals(p);
		});
		return currTrap != null;
	}

	private boolean trapFull() {
		for (int i = 0; i < sv.trapTiles.length; i++) {
			final Tile yourPos = sv.trapTiles[i];
			if (posContainsFullTrap(yourPos))
				return true;
		}
		return false;
	}

	private boolean posContainsFullTrap(final Tile p) {
		GameObject currTrap = GameObjects.closest(r -> {
			if (r.getName() == null || r.getID() != sv.huntThis.getFullTrapID())
				return false;
			return r.getTile().equals(p);
		});
		return currTrap != null;
	}

	private boolean allTrapsLaid() {
		return getNextTrapPos() == null;
	}

	private Tile getNextTrapPos() {
		for (int i = 0; i < sv.trapTiles.length; i++) {
			final Tile yourPos = sv.trapTiles[i];
			if (!posContainsTrap(yourPos))
				return sv.trapTiles[i];
		}
		return null;
	}

	private boolean posContainsTrap(final Tile p) {
		GameObject currTrap = GameObjects.closest(r -> {
			if (r.getName() == null || !r.getName().equals(sv.huntThis.getTrapName()))
				return false;
			return r.getTile().equals(p);
		});
		return currTrap != null;
	}


	public void onExit() {
		log("Exiting script!");
	}

	@Override
	public void onPaint(Graphics2D g) {
		if (sv.started) {
			track.update();
			if (state != null)
				g.drawString("State: " + state, 5, 50);
			g.drawString("Exp gained(p/h): " + SkillTracker.getGainedExperience(Skill.HUNTER) + "(" + SkillTracker.getGainedExperiencePerHour(Skill.HUNTER) + ")", 5, 65);
			g.drawString("Level: " + Skills.getRealLevel(Skill.HUNTER) + "(" + (Skills.getRealLevel(Skill.HUNTER) - startingLevel) + ")", 5, 80);
			g.drawString("Runtime: " + rt.formatTime(), 5, 95);
			g.drawString("Currently hunting: " + sv.huntThis.getName(), 5, 110);
			g.drawString(track.getName() + "(p/h): " + track.getAmount() + "(" + rt.getHourlyRate(track.getAmount()) + ")", 5, 125);
		}
	}

}
