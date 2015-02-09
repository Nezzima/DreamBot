package nezz.dreambot.scriptmain.druids;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import nezz.dreambot.druids.gui.ScriptVars;
import nezz.dreambot.druids.gui.druidsGui;
import nezz.dreambot.tools.PricedItem;
import nezz.dreambot.tools.RunTimer;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.bank.BankLocation;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.walking.pathfinding.SearchAlgorithm;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.utilities.impl.Filter;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.interactive.Character;

@ScriptManifest(author = "Nezz", description = "Kills druids at Ardy", name = "DreamBot Druid Killer", version = 0, category = Category.COMBAT)
public class Druids extends AbstractScript{
	
	private RunTimer timer;
	ScriptVars sv = new ScriptVars();
	Filter<NPC> druidFilter = new Filter<NPC>(){
		@Override
		public boolean match(NPC n) {
			if(n == null || n.getActions() == null || n.getActions().length <= 0)
				return false;
			if(n.getName() == null || !n.getName().equals("Chaos druid"))
				return false;
			if(n.isInCombat()){
				Character c = n.getInteractingCharacter();
				if(c == null)
					return false;
				if(c.getName() == null)
					return false;
				if(c.getName().equals(getLocalPlayer().getName()))
					return true;
				return false;
			}
			return true;
		}
	};
	Filter<GroundItem> itemFilter = new Filter<GroundItem>(){
		public boolean match(GroundItem gi){
			if(gi == null || !gi.exists() || gi.getName() == null){
				return false;
			}
			if(!druidArea.contains(gi)){
				return false;
			}
			for(int i = 0; i < sv.loot.length; i++){
				if(gi.getName().equals("Herb")){
					for(int ii = 0; ii < sv.keepHerbs.length; ii++){
						if(gi.getID() == sv.keepHerbs[ii])
							return true;
					}
					return false;
				}
				else if(gi.getName().equals(sv.loot[i]))
					return true;
			}
			return false;
		}
	};
	Condition itemDeposited = new Condition(){
		public boolean verify(){
			return getInventory().isEmpty();
		}
	};
	Condition attacking = new Condition(){
		public boolean verify(){
			return getLocalPlayer().isInCombat();
		}
	};
	Tile[] druidsToBank = new Tile[]{new Tile(2565,3356,0),new Tile(2567,3356,0),
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
			new Tile(2617,3334,0)};

	Area druidArea = new Area(new Tile(2560,3358,0), new Tile(2564,3354,0));
	List<PricedItem> lootTrack = new ArrayList<PricedItem>();
	GameObject door = null;
	private boolean started = false;
	//current state
	private State state;
	private enum State{
		ATTACK, LOOT, WALK_TO_BANK, WALK_TO_DRUIDS, BANK, DROP
	}
	
	private State getState(){
		if(needsToDrop())
			return State.DROP;
		else if(getInventory().isFull()){
			if(BankLocation.ARDOUGNE_WEST.getArea(3).contains(getLocalPlayer())){
				return State.BANK;
			}
			else
				return State.WALK_TO_BANK;
		}
		else{
			if(druidArea.contains(getLocalPlayer())){
				GroundItem gi = getGroundItems().getClosest(itemFilter);
				if(gi != null && druidArea.contains(gi)){
					return State.LOOT;
				}
				else{
					return State.ATTACK;
				}
			}
			else{
				return State.WALK_TO_DRUIDS;
			}
		}
	}
	
	@Override
	public void onStart(){
		druidsGui gui = new druidsGui(sv);
		gui.setVisible(true);
		while(!sv.started){
			sleep(100);
		}
		timer = new RunTimer();
		for(int i = 0; i < sv.keepHerbs.length; i++){
			Herbs h = Herbs.getForUNGrimyID(sv.keepHerbs[i]);
			if(h != null){
				lootTrack.add(new PricedItem("Herb", h.getUnnotedGrimyId(), getClient().getMethodContext(), false));
			}
		}
		for(int i = 0; i < sv.loot.length; i++){
			if(sv.loot[i].equals("Herb"))
				continue;
			lootTrack.add(new PricedItem(sv.loot[i], getClient().getMethodContext(), false));
		}
		getSkillTracker().start(Skill.DEFENCE);
		getSkillTracker().start(Skill.ATTACK);
		getSkillTracker().start(Skill.STRENGTH);
		getWalking().setSearchAlgorithm(SearchAlgorithm.BI_DIJKSTRA);
		//getClient().getInstance().getScriptManager().getIDleMouseController().setIdleTime(10000);
		//getClient().disableIdleMouse();
		started = true;
		log("Starting DreamBot's Druid Killing Script!");
	}
	
	private void updateLoot(){
		for(PricedItem p : lootTrack){
			p.update();
		}
	}
	
	private boolean needHerb(int id){
		for(int i =0; i < sv.keepHerbs.length; i++){
			if(id == sv.keepHerbs[i])
				return true;
		}
		return false;
	}
	
	private boolean needItem(String name){
		for(int i = 0; i < sv.loot.length; i++){
			if(name.equalsIgnoreCase(sv.loot[i].toLowerCase())){
				return true;
			}
		}
		return false;
	}
	
	private boolean needsToDrop(){
		for(int i = 0; i < 28; i++){
			Item item = getInventory().getItemInSlot(i);
			if(item != null && !item.getName().equals("") && !item.getName().equals("null")){
				if(item.getName().equals("Herb") && !needHerb(item.getID())){
					return true;
				}
				else if(!needItem(item.getName()))
					return true;
			}
		}
		
		return false;
	}
	
	@Override
	public int onLoop(){
		Player myPlayer = getPlayers().myPlayer();
		if(!getWalking().isRunEnabled() && getWalking().getRunEnergy() > Calculations.random(30,70)){
			getWalking().toggleRun();
		}
		if(myPlayer.isMoving() && getClient().getDestination() != null && getClient().getDestination().distance(myPlayer) > 5)
			return Calculations.random(300, 600);
		getDialogues().clickContinue();
		state = getState();
		switch(state){
		case DROP:
			for(int i = 0; i < 28; i++){
				Item item = getInventory().getItemInSlot(i);
				if(item != null){
					if(item.getName().equals("Herb") && !needHerb(item.getID())){
						if(Herbs.getForUNGrimyID(item.getID()).canIdHerb(getSkills().getBoostedLevels(Skill.HERBLORE))){
							getInventory().interactWithSlot(i, "Identify");
							sleep(600,900);
						}
						getInventory().interactWithSlot(i, "Drop");
						sleep(600,900);
					}
					else if(!needItem(item.getName())){
						getInventory().interactWithSlot(i, "Drop");
						sleep(600,900);
					}
				}
			}
			break;
		case BANK:
			if(getBank().isOpen()){
				getBank().depositAll();
				sleepUntil(itemDeposited,1000);
			}
			else{
				getBank().openBank(BankLocation.ARDOUGNE_EAST.getBankType());
				sleepUntil(new Condition(){
					public boolean verify(){
						return getBank().isOpen();
					}
				},1200);
			}
			break;
		case ATTACK:
			NPC druid = getNpcs().getClosest(druidFilter);
			if(druid != null){
				if(!myPlayer.isInCombat()){
					druid.interact("Attack");
					sleepUntil(attacking,3000);
				}
				else{
					sleep(400,800);
				}
			}
			else{
				sleep(300,600);
			}
			break;
		case LOOT:
			if(myPlayer.isInCombat())
				break;
			final GroundItem gi = getGroundItems().getClosest(itemFilter);
			if(gi != null && druidArea.contains(gi.getTile())){
				gi.interact("Take");
				if(getMouse().getLastCrosshairColor() == 2){
					sleepUntil(new Condition(){
						public boolean verify(){
							GroundItem gi_ = getGroundItems().getClosest(new Filter<GroundItem>(){
								public boolean match(GroundItem _gi){
									if(_gi == null || _gi.getName() == null)
										return false;
									if(!itemFilter.match(_gi))
										return false;
									if(_gi.getID() == gi.getID() && _gi.getTile().equals(gi.getTile()))
										return true;
									return false;
								}
							});
							return gi_ == null;
						}
					},2000);
				}
			}
			break;
		case WALK_TO_BANK:
			if(druidArea.contains(getLocalPlayer())){
				door = getGameObjects().getClosest("Door");
				if(door != null){
					door.interact("Open");
					sleepUntil(new Condition(){
						public boolean verify(){
							return !druidArea.contains(getLocalPlayer());
						}
					},1200);
				}
			}
			else{
				getWalking().walk(BankLocation.ARDOUGNE_WEST.getCenter());
				//getWalking().walkTilePath(druidsToBank, Calculations.random(20,30));
			}
			break;
		case WALK_TO_DRUIDS:
			if(getBank().isOpen()){
				getBank().close();
				sleepUntil(new Condition(){
					public boolean verify(){
						return !getBank().isOpen();
					}
				},1200);
			}
			if(myPlayer.getTile().getY() > 9000){
				GameObject ladder = getGameObjects().getClosest("Ladder");
				if(ladder != null){
					if(ladder.interact("Climb-up")){
						sleepUntil(new Condition(){
							public boolean verify(){
								return getLocalPlayer().getTile().getY() < 9000;
							}
						},2000);
					}
				}
			}
			else{
				/*
				Tile[] bankToDruids = new Tile[druidsToBank.length];
				for(int i = 0; i < druidsToBank.length; i++){
					bankToDruids[i] = druidsToBank[druidsToBank.length - 1 - i];
				}*/
				if(getLocalPlayer().distance(druidsToBank[0]) < 8){
					door = getGameObjects().getClosest(11723);
					if(door != null){
						door.interact("Pick-lock");
						sleepUntil(new Condition(){
							public boolean verify(){
								return druidArea.contains(getLocalPlayer());
							}
						},1200);
					}
				}
				else{
					getWalking().walk(druidsToBank[0]);
					//getWalking().walkTilePath(bankToDruids, Calculations.random(10,15));
				}
			}
			break;
		}
		updateLoot();
		return 200;
	}
	
	@Override
	public void onExit(){
		log("Stopping testing!");
	}
	
	public long getGainedExperience(){
		long att;
		long str;
		long def;
		att = getSkillTracker().getGainedExperience(Skill.ATTACK);
		str = getSkillTracker().getGainedExperience(Skill.STRENGTH);
		def = getSkillTracker().getGainedExperience(Skill.DEFENCE);
		return att + str + def;
	}
	public long getGainedExperienceHour(){
		long att;
		long str;
		long def;
		att = getSkillTracker().getGainedExperiencePerHour(Skill.ATTACK);
		str = getSkillTracker().getGainedExperiencePerHour(Skill.STRENGTH);
		def = getSkillTracker().getGainedExperiencePerHour(Skill.DEFENCE);
		return att + str + def;
	}
	
	public void onPaint(Graphics g) {
		if(started){
			int baseY = 15;
			g.setColor(Color.green);
			if(state != null)
				g.drawString("State: " + state.toString(), 5, baseY);
			baseY+= 15;
			g.drawString("Runtime: " + timer.format(), 5, baseY);
			baseY+=15;
			g.drawString("Experience(p/h): " + getGainedExperience() + "(" + getGainedExperienceHour() + ")",5, baseY);
			baseY+=15;
			//g.drawString("Level(gained): " + getSkills().getRealLevel(Skill.DEFENCE) + "(" + getSkillTracker().getGainedLevels(Skill.DEFENCE) + ")", 5, baseY);
			//baseY+=15;
			baseY = 15;
			for(int i = 0; i < lootTrack.size(); i++){
				PricedItem p = lootTrack.get(i);
				if(p != null && p.getAmount() > 0){
					String name = p.getName();
					if(p.getId() > 0){
						name = Herbs.getForUNGrimyID(p.getId()).getName();
					}
					g.drawString(name + "(p/h):" + p.getAmount() + "(" + timer.getPerHour(p.getAmount()) + ")", 400, baseY);
					baseY+=15;
				}
			}
		}
	}

}
