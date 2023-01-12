package nezz.dreambot.scriptmain.druids;

import nezz.dreambot.druids.gui.ScriptVars;
import nezz.dreambot.druids.gui.druidsGui;
import nezz.dreambot.tools.PricedItem;
import org.dreambot.api.Client;
import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.SkillTracker;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@ScriptManifest(author = "Nezz", description = "Kills druids at Ardy", name = "DreamBot Druid Killer", version = 0, category = Category.COMBAT)
public class Druids extends AbstractScript {

	private Timer timer;
	ScriptVars sv = new ScriptVars();
	Filter<NPC> druidFilter = n -> {
		if (n == null || n.getActions() == null || n.getActions().length <= 0)
			return false;
		if (n.getName() == null || !n.getName().equals("Chaos druid"))
			return false;
		if (n.isInCombat()) {
			Character c = n.getInteractingCharacter();
			if (c == null)
				return false;
			if (c.getName() == null)
				return false;
			return c.getName().equals(Players.getLocal().getName());
		}
		return true;
	};
	Area druidArea = new Area(new Tile(2560, 3358, 0), new Tile(2564, 3354, 0));

	Filter<GroundItem> itemFilter = gi -> {
		if (gi == null || !gi.exists() || gi.getName() == null) {
			return false;
		}
		if (!druidArea.contains(gi)) {
			return false;
		}
		for (int i = 0; i < sv.loot.length; i++) {
			if (gi.getName().contains("Grimy")) {
				for (int ii = 0; ii < sv.keepHerbs.length; ii++) {
					if (gi.getID() == sv.keepHerbs[ii])
						return true;
				}
				return false;
			} else if (gi.getName().equals(sv.loot[i]))
				return true;
		}
		return false;
	};
	Condition itemDeposited = Inventory::isEmpty;
	Condition attacking = () -> Players.getLocal().isInCombat();
	private final Tile druidsTile = new Tile(2565, 3356, 0);
	/*Tile[] druidsToBank = new Tile[]{new Tile(2565,3356,0),new Tile(2567,3356,0),
			new Tile(2569,3356,0),new Tile(2570,3355,0),new Tile(2571,3354,0),
			new Tile(2572,3353,0),new Tile(2573,3352,0),new Tile(2574,3351,0),
			new Tile(2575,3350,0),new Tile(2577,3350,0),new Tile(2579,3350,0),
			new Tile(2580,3351,0),new Tile(2582,3351,0),new Tile(2582,3353,0),
			new Tile(2582,3355,0),new Tile(2582,3357,0),new Tile(2582,3359,0),
			new Tile(2582,3361,0),new Tile(2582,3363,0),new Tile(2582,3365,0),
			new Tile(2582,3367,0),new Tile(2583,3368,0),new Tile(2585,3368,0),
			new Tile(2587,3368,0),new Tile(2589,3368,0),new Tile(2591,3368,0),
			new Tile(2593,3368,0),new Tile(2595,3368,0),new Tile(2597,3368,0),
			new Tile(2599,3368,0),new Tile(2601,3368,0),new Tile(2603,3368,0),
			new Tile(2605,3368,0),new Tile(2607,3368,0),new Tile(2608,3367,0),
			new Tile(2609,3366,0),new Tile(2610,3365,0),new Tile(2610,3363,0),
			new Tile(2610,3361,0),new Tile(2610,3359,0),new Tile(2610,3357,0),
			new Tile(2611,3355,0),new Tile(2612,3354,0),new Tile(2613,3353,0),
			new Tile(2613,3351,0),new Tile(2613,3349,0),new Tile(2613,3347,0),
			new Tile(2613,3345,0),new Tile(2613,3343,0),new Tile(2613,3341,0),
			new Tile(2614,3339,0),new Tile(2615,3338,0),new Tile(2599,3378,0),
			new Tile(2615,3338,0),new Tile(2616,3337,0),new Tile(2616,3335,0),
			new Tile(2617,3334,0)};*/

	List<PricedItem> lootTrack = new ArrayList<>();
	GameObject door = null;
	private boolean started = false;
	//current state
	private State state;

	private enum State {
		ATTACK, LOOT, WALK_TO_BANK, WALK_TO_DRUIDS, BANK, DROP
	}

	private State getState() {
		if (needsToDrop())
			return State.DROP;
		else if (Inventory.isFull()) {
			if (BankLocation.ARDOUGNE_NORTH.getArea(3).contains(Players.getLocal())) {
				return State.BANK;
			} else
				return State.WALK_TO_BANK;
		} else {
			if (druidArea.contains(Players.getLocal())) {
				GroundItem gi = GroundItems.closest(itemFilter);
				if (gi != null && druidArea.contains(gi)) {
					return State.LOOT;
				} else {
					return State.ATTACK;
				}
			} else {
				return State.WALK_TO_DRUIDS;
			}
		}
	}

	@Override
	public void onStart() {
		druidsGui gui = new druidsGui(sv);
		gui.setVisible(true);
		while (!sv.started) {
			sleep(100);
		}
		timer = new Timer();
		for (int i = 0; i < sv.keepHerbs.length; i++) {
			Herbs h = Herbs.getForUNGrimyID(sv.keepHerbs[i]);
			if (h != null) {
				lootTrack.add(new PricedItem("Herb", h.getUnnotedGrimyId(), false));
			}
		}
		for (int i = 0; i < sv.loot.length; i++) {
			if (sv.loot[i].equals("Herb"))
				continue;
			lootTrack.add(new PricedItem(sv.loot[i], false));
		}
		SkillTracker.start(Skill.DEFENCE);
		SkillTracker.start(Skill.ATTACK);
		SkillTracker.start(Skill.STRENGTH);
		//Client.getInstance().getScriptManager().getIDleMouseController().setIdleTime(10000);
		//Client.disableIdleMouse();
		started = true;
		log("Starting DreamBot's Druid Killing Script!");
	}

	private void updateLoot() {
		for (PricedItem p : lootTrack) {
			p.update();
		}
	}

	private boolean needHerb(int id) {
		for (int i = 0; i < sv.keepHerbs.length; i++) {
			if (id == sv.keepHerbs[i])
				return true;
		}
		return false;
	}

	private boolean needItem(String name) {
		for (int i = 0; i < sv.loot.length; i++) {
			if (name.equalsIgnoreCase(sv.loot[i].toLowerCase()) || name.contains("Grimy")) {
				return true;
			}
		}
		return false;
	}

	private boolean needsToDrop() {
		for (int i = 0; i < 28; i++) {
			Item item = Inventory.getItemInSlot(i);
			if (item != null && !item.getName().equals("") && !item.getName().equals("null")) {
				if (item.getName().contains("Grimy") && !needHerb(item.getID())) {
					return true;
				} else if (!needItem(item.getName()))
					return true;
			}
		}

		return false;
	}

	@Override
	public int onLoop() {
		Player myPlayer = Players.getLocal();
		if (!Walking.isRunEnabled() && Walking.getRunEnergy() > Calculations.random(30, 70)) {
			Walking.toggleRun();
		}
		if (myPlayer.isMoving() && Client.getDestination() != null && Client.getDestination().distance(myPlayer) > 5)
			return Calculations.random(300, 600);
		Dialogues.clickContinue();
		state = getState();
		switch (state) {
			case DROP:
				for (int i = 0; i < 28; i++) {
					Item item = Inventory.getItemInSlot(i);
					if (item != null) {
						if (item.getName().contains("Grimy") && !needHerb(item.getID())) {
							if (Herbs.getForUNGrimyID(item.getID()).canIdHerb(Skill.HERBLORE.getBoostedLevel())) {
								Inventory.interact(i, "Identify");
								sleep(600, 900);
							}
							Inventory.interact(i, "Drop");
							sleep(600, 900);
						} else if (!needItem(item.getName())) {
							Inventory.interact(i, "Drop");
							sleep(600, 900);
						}
					}
				}
				break;
			case BANK:
				if (Bank.isOpen()) {
					Bank.depositAllItems();
					Sleep.sleepUntil(itemDeposited, 1000);
				} else {
					Bank.open(BankLocation.ARDOUGNE_SOUTH);
					Sleep.sleepUntil(Bank::isOpen, 1200);
				}
				break;
			case ATTACK:
				NPC druid = NPCs.closest(druidFilter);
				if (druid != null) {
					if (!myPlayer.isInCombat()) {
						druid.interact("Attack");
						Sleep.sleepUntil(attacking, 3000);
					} else {
						sleep(400, 800);
					}
				} else {
					sleep(300, 600);
				}
				break;
			case LOOT:
				if (myPlayer.isInCombat())
					break;
				final GroundItem gi = GroundItems.closest(itemFilter);
				if (gi != null && druidArea.contains(gi.getTile())) {
					gi.interact("Take");
					if (Mouse.getLastCrosshairColorID() == 2) {
						Sleep.sleepUntil(() -> {
							GroundItem gi_ = GroundItems.closest(_gi -> {
								if (_gi == null || _gi.getName() == null)
									return false;
								if (!itemFilter.match(_gi))
									return false;
								return _gi.getID() == gi.getID() && _gi.getTile().equals(gi.getTile());
							});
							return gi_ == null;
						}, 2000);
					}
				}
				break;
			case WALK_TO_BANK:
				if (druidArea.contains(Players.getLocal())) {
					door = GameObjects.closest("Door");
					if (door != null) {
						door.interact("Open");
						Sleep.sleepUntil(() -> !druidArea.contains(Players.getLocal()), 1200);
					}
				} else {
					Walking.walk(BankLocation.ARDOUGNE_NORTH.getCenter());
					//Walking.walkTilePath(druidsToBank, Calculations.random(20,30));
				}
				break;
			case WALK_TO_DRUIDS:
				if (Bank.isOpen()) {
					Bank.close();
					Sleep.sleepUntil(() -> !Bank.isOpen(), 1200);
				}
				if (myPlayer.getTile().getY() > 9000) {
					GameObject ladder = GameObjects.closest("Ladder");
					if (ladder != null) {
						if (ladder.interact("Climb-up")) {
							Sleep.sleepUntil(() -> Players.getLocal().getTile().getY() < 9000, 2000);
						}
					}
				} else {
				/*
				Tile[] bankToDruids = new Tile[druidsToBank.length];
				for(int i = 0; i < druidsToBank.length; i++){
					bankToDruids[i] = druidsToBank[druidsToBank.length - 1 - i];
				}*/
					if (Players.getLocal().distance(druidsTile) < 8) {
						door = GameObjects.closest(11723);
						if (door != null) {
							door.interact("Pick-lock");
							Sleep.sleepUntil(() -> druidArea.contains(Players.getLocal()), 1200);
						}
					} else {
						Walking.walk(druidsTile);
						//Walking.walkTilePath(bankToDruids, Calculations.random(10,15));
					}
				}
				break;
		}
		updateLoot();
		return 200;
	}

	@Override
	public void onExit() {
		log("Stopping testing!");
	}

	public long getGainedExperience() {
		long att;
		long str;
		long def;
		att = SkillTracker.getGainedExperience(Skill.ATTACK);
		str = SkillTracker.getGainedExperience(Skill.STRENGTH);
		def = SkillTracker.getGainedExperience(Skill.DEFENCE);
		return att + str + def;
	}

	public long getGainedExperienceHour() {
		long att;
		long str;
		long def;
		att = SkillTracker.getGainedExperiencePerHour(Skill.ATTACK);
		str = SkillTracker.getGainedExperiencePerHour(Skill.STRENGTH);
		def = SkillTracker.getGainedExperiencePerHour(Skill.DEFENCE);
		return att + str + def;
	}

	public void onPaint(Graphics g) {
		if (started) {
			int baseY = 15;
			g.setColor(Color.green);
			if (state != null)
				g.drawString("State: " + state, 5, baseY);
			baseY += 15;
			g.drawString("Runtime: " + timer.formatTime(), 5, baseY);
			baseY += 15;
			g.drawString("Experience(p/h): " + getGainedExperience() + "(" + getGainedExperienceHour() + ")", 5, baseY);
			baseY += 15;
			//g.drawString("Level(gained): " + Skills.getRealLevel(Skill.DEFENCE) + "(" + SkillTracker.getGainedLevels(Skill.DEFENCE) + ")", 5, baseY);
			//baseY+=15;
			baseY = 15;
			for (int i = 0; i < lootTrack.size(); i++) {
				PricedItem p = lootTrack.get(i);
				if (p != null && p.getAmount() > 0) {
					String name = p.getName();
					if (p.getId() > 0) {
						name = Herbs.getForUNGrimyID(p.getId()).getName();
					}
					g.drawString(name + "(p/h):" + p.getAmount() + "(" + timer.getHourlyRate(p.getAmount()) + ")", 400, baseY);
					baseY += 15;
				}
			}
		}
	}

}
