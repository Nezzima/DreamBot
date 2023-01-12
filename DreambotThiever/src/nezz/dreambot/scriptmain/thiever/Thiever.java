package nezz.dreambot.scriptmain.thiever;

import nezz.dreambot.thiever.gui.ScriptVars;
import nezz.dreambot.thiever.gui.thieverGui;
import nezz.dreambot.tools.RunTimer;
import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.SkillTracker;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.items.Item;

import java.awt.*;


@ScriptManifest(author = "Nezz", description = "Thieves stuff", name = "DreamBot Thiever", version = 0, category = Category.THIEVING)
public class Thiever extends AbstractScript{

	ScriptVars sv = new ScriptVars();
	Tile startTile;
	State state;
	RunTimer timer;
	boolean started = false;
	private Entity stealFrom = null;
	private enum State{
		STEAL, DROP, HEAL, BANK
	}
	
	private State getState(){
		if(healthPerc() < 50){
			return State.HEAL;
		}
		if(containsOneOf(sv.yourThieving.getDropItems())){
			return State.DROP;
		}
		if(Inventory.isFull()){
			return State.BANK;
		}
		return State.STEAL;
	}
	
	private int healthPerc(){
		int currHealth = Skills.getBoostedLevel(Skill.HITPOINTS);
		int maxHealth = Skills.getRealLevel(Skill.HITPOINTS);
		return ((currHealth*100)/maxHealth);
	}
	
	private boolean containsOneOf(String...itemNames){
		for(String name : itemNames){
			if(name != null && Inventory.contains(name))
				return true;
		}
		return false;
	}
	
	@Override
	public void onStart(){
		thieverGui gui = new thieverGui(sv);
		gui.setVisible(true);
		while(!sv.started){
			sleep(300);
		}
		SkillTracker.start(Skill.THIEVING);
		startTile = Players.getLocal().getTile();
		timer = new RunTimer();
		started = true;
		log("Starting silk thiever!");
	}
	
	@Override
	public int onLoop() {
		state = getState();
		log(""+state);
		switch(state){
		case DROP:
			for(int i = 0; i < 28; i++){
				Item item = Inventory.getItemInSlot(i);
				if(item != null){
					for(String name : sv.yourThieving.getDropItems()){
						if(name != null && name.equals(item.getName())){
							Inventory.slotInteract(i, "Drop");
							sleep(200,400);
							break;
						}
					}
				}
			}
			if(stealFrom != null){
				Mouse.move(stealFrom);
			}
			sleep(300,600);
			break;
		case STEAL:
			log(sv.yourThieving.getName());
			stealFrom = NPCs.closest(sv.yourThieving.getName());
			if(stealFrom == null){
				stealFrom = GameObjects.closest(sv.yourThieving.getName());
			}
			if(stealFrom != null && Players.getLocal().getAnimation() == -1 && stealFrom.isOnScreen()){
				log("Thieving!");
				stealFrom.interact(sv.yourThieving.getAction());
				sleep(300,500);
			} else{
				if(startTile.distance(Players.getLocal()) > 10){
					Walking.walk(startTile);
					sleep(300,600);
				}
				else{
					sleep(300,600);
				}
			}
				
			break;
		case BANK:
			if(Bank.isOpen()){
				Bank.depositAllItems();
				sleep(300,600);
			}
			else{
				if(sv.yourBank.getCenter().distance(Players.getLocal()) > 10){
					Walking.walk(sv.yourBank.getCenter());
					sleep(300,600);
				}
				else{
					Bank.open(sv.yourBank);
					sleep(300,600);
				}
			}
			break;
		case HEAL:
			for(int i = 0; i < 28; i++){
				Item item = Inventory.getItemInSlot(i);
				if(item != null && item.hasAction("Eat")){
					Inventory.slotInteract(i, "Eat");
					sleep(600,900);
					break;
				}
			}
			break;
		}
		return 1;
	}
	
	@Override
	public void onExit(){
		log("Stopping silk thiever!");
	}

	public void onPaint(Graphics g) {
		if(started){
			g.drawString("Experience gained(p/h): " + SkillTracker.getGainedExperience(Skill.THIEVING) +"(" + SkillTracker.getGainedExperiencePerHour(Skill.THIEVING) + ")", 5, 90);
			g.drawString("Level(gained): " + Skills.getRealLevel(Skill.THIEVING) + "(" + SkillTracker.getGainedLevels(Skill.THIEVING) + ")", 5, 105);
			g.drawString("Runtime: " + timer.format(), 5, 120);
			if(state != null)
				g.drawString("State: " + state.toString(), 5, 135);
		}
		
	}

}
