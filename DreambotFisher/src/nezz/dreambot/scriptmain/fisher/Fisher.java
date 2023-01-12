package nezz.dreambot.scriptmain.fisher;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import nezz.dreambot.fisher.gui.ScriptVars;
import nezz.dreambot.fisher.gui.fisherGui;
import nezz.dreambot.tools.PricedItem;
import nezz.dreambot.tools.RunTimer;

import org.dreambot.api.Client;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
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
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;

@ScriptManifest(author = "Nezz", description = "AIO Fisher", name = "DreamBot AIO Fisher", version = 0, category = Category.FISHING)
public class Fisher extends AbstractScript{
	
	private RunTimer timer;
	private Tile startTile;
	ScriptVars sv = new ScriptVars();
	Hashtable<Tile,Tile> poolHash = new Hashtable<Tile,Tile>();
	Filter<NPC> poolFilter = new Filter<NPC>(){
		@Override
		public boolean match(NPC n) {
			if(n == null)// || !n.exists())
				return false;
			if(n.getName() == null || !n.getName().equals("Fishing spot"))
				return false;
			String[] actions = n.getActions();
			if(actions == null || actions.length <= 0)
				return false;
			for(String action : sv.yourFish.getRequiredActions()){
				if(!n.hasAction(action))
					return false;
			}
			if(!canReach(n))
				return false;
			return true;
		}
	};
	List<PricedItem> lootTrack = new ArrayList<PricedItem>();
	//current state
	private State state;
	NPC myPool = null;
	long activeTime = 0;
	private boolean started = false;
	private enum State{
		FISH, DROP, BANK
	}
	
	private State getState(){
		if(Inventory.isFull()){
			if(sv.powerFish){
				return State.DROP;
			}
			else{
				return State.BANK;
			}
		}
		return State.FISH;
	}
	
	public Tile getWalkableTile(NPC pool){
		Tile walkableTile = null;
		if(!poolHash.containsKey(pool.getTile())){
			Tile t = pool.getTile();
			Tile n = new Tile(t.getX(), t.getY() +1, t.getZ());
			Tile e = new Tile(t.getX()+1, t.getY(), t.getZ());
			Tile s = new Tile(t.getX(), t.getY() -1, t.getZ());
			Tile w = new Tile(t.getX()-1, t.getY(), t.getZ());
			if(Map.canReach(n))
				walkableTile = n;
			else if(Map.canReach(e))
				walkableTile = e;
			else if(Map.canReach(s))
				walkableTile = s;
			else if(Map.canReach(w))
				walkableTile = w;
			else
				walkableTile = null;
			if(walkableTile != null){
				log("Adding tile to hash: <" + pool.getTile() + "," + walkableTile+">");
				poolHash.put(pool.getTile(), walkableTile);
			}
		}
		else{
			walkableTile = poolHash.get(pool.getTile());
		}
		return walkableTile;
	}
	
	public boolean canReach(NPC pool){
		return getWalkableTile(pool) != null;
	}
	
	@Override
	public void onStart(){
		fisherGui gui = new fisherGui(sv);
		gui.setVisible(true);
		while(!sv.started){
			sleep(100);
		}
		startTile = Players.getLocal().getTile();
		SkillTracker.start(Skill.FISHING);
		for(String s : sv.yourFish.getFish()){
			lootTrack.add(new PricedItem(s,false));
		}
		timer = new RunTimer();
		log("Starting DreamBot's AIO Fishing script!");
		started = true;
	}
	
	private boolean needToStop(){
		if(!Inventory.contains(sv.yourFish.getItemName()))
			return true;
		if(!sv.yourFish.getRequiredItem().equals("") && !Inventory.contains(sv.yourFish.getRequiredItem()))
			return true;
		return false;
	}
	
	private void updateLoot(){
		for(PricedItem p : lootTrack){
			p.update();
		}
	}
	
	@Override
	public int onLoop() {
		if(needToStop()){
			stop();
			return 1;
		}
		Player myPlayer = Players.getLocal();
		if(!Walking.isRunEnabled() && Walking.getRunEnergy() > Calculations.random(30,70)){
			Walking.toggleRun();
			Sleep.sleepUntil(new Condition(){
				public boolean verify(){
					return Walking.isRunEnabled();
				}
			},1200);
		}
		if(myPlayer.isMoving() && Client.getDestination() != null && Client.getDestination().distance(myPlayer) > 5)
			return Calculations.random(300, 600);
		state = getState();
		switch(state){
		case BANK:
			if(Bank.isOpen()){
				for(int i = 0; i < 28; i++){
					Item item = Inventory.getItemInSlot(i);
					if(item != null){
						if(item.getName().equals(sv.yourFish.getItemName()) || item.getName().equals(sv.yourFish.getRequiredItem()))
							continue;
						else{
							Bank.depositAll(item.getName());
							sleep(600);
						}
					}
				}
			}
			else{
				Area a = sv.yourBank.getArea(5);
				if(a.contains(Players.getLocal().getTile())){
					Bank.open(sv.yourBank);
				}
				else{
					Walking.walk(sv.yourBank.getCenter());
					Sleep.sleepUntil(new Condition(){
						public boolean verify(){
							return Players.getLocal().isMoving();
						}
					},1200);
				}
			}
			break;
		case DROP:
			for(int i = 0; i < 28; i++){
				Item item = Inventory.getItemInSlot(i);
				if(item != null && !item.getName().equals(sv.yourFish.getItemName()) && !item.getName().equals(sv.yourFish.getRequiredItem())){
					Inventory.interact(i, "Drop");
					sleep(Calculations.random(150,350));
				}
			}
			break;
		case FISH:
			if(Bank.isOpen()){
				Bank.close();
				Sleep.sleepUntil(new Condition(){
					public boolean verify(){
						return !Bank.isOpen();
					}
				},1200);
			}
			if(myPlayer.getAnimation() == -1 || (System.currentTimeMillis() - activeTime > Calculations.random(250000,280000))){
				NPC pool = NPCs.closest(poolFilter);
				myPool = pool;
				if(pool != null){
					if(pool.isOnScreen() && pool.distance(Players.getLocal()) < 5){
						pool.interact(sv.yourFish.getRequiredActions()[0]);
						Sleep.sleepUntil(new Condition(){
							public boolean verify(){
								return Players.getLocal().getAnimation() != -1;
							}
						},1200);
						sleep(1500);
						activeTime = System.currentTimeMillis();
					}
					else{
						log("Walk to pool");
						Walking.walk(getWalkableTile(myPool));
						Sleep.sleepUntil(new Condition(){
							public boolean verify(){
								return Players.getLocal().isMoving();
							}
						},1000);
					}
				}
				else{
					if(this.startTile.distance(Players.getLocal()) < 15)
						sleep(600);
					else{
						if(myPlayer.distance(startTile) > 5){
							Walking.walk(startTile);
							Sleep.sleepUntil(new Condition(){
								public boolean verify(){
									return Players.getLocal().isMoving();
								}
							},1200);
						}
						else{
							Walking.walk(myPlayer.getTile());
							Sleep.sleepUntil(new Condition(){
								public boolean verify(){
									return Players.getLocal().isMoving();
								}
							},1200);
							sleep(300);
						}
					}
				}
			}
			break;
		}
		updateLoot();
		return (int)Calculations.nextGaussianRandom(500, 200);
	}
	
	@Override
	public void onExit(){
		log("Stopping testing!");
	}

	public void onPaint(Graphics g) {
		if(started){
			g.setColor(Color.green);
			g.drawString("Experience(p/h): " + SkillTracker.getGainedExperience(Skill.FISHING) + "(" + SkillTracker.getGainedExperiencePerHour(Skill.FISHING) + ")",5, 90);
			g.drawString("Runtime: " + timer.format(), 5, 105);
			g.drawString("Level(gained): " + Skills.getRealLevel(Skill.FISHING) + "(" + SkillTracker.getGainedLevels(Skill.FISHING) + ")", 5, 120);
			int place = 0;
			for(int i = 0; i < lootTrack.size(); i++){
				PricedItem p = lootTrack.get(i);
				if(p != null && p.getAmount() > 0){
					g.drawString(p.getName() + "(p/h):" + p.getAmount() + "(" + timer.getPerHour(p.getAmount()) + ")", 5, 135 + place*15);
					place++;
				}
			}
			if(state != null)
				g.drawString("State: " + state.toString(), 5, 135 + place*15);
			
		}
	}

}
