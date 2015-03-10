package nezz.dreambot.scriptmain.thiever;

import java.awt.Graphics;
import java.awt.Point;

import nezz.dreambot.thiever.gui.ScriptVars;
import nezz.dreambot.thiever.gui.thieverGui;
import nezz.dreambot.tools.RunTimer;

import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.input.mouse.destination.impl.EntityDestination;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.items.Item;


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
		if(getInventory().isFull()){
			return State.BANK;
		}
		return State.STEAL;
	}
	
	private int healthPerc(){
		int currHealth = getSkills().getBoostedLevels(Skill.HITPOINTS);
		int maxHealth = getSkills().getRealLevel(Skill.HITPOINTS);
		return ((currHealth*100)/maxHealth);
	}
	
	private boolean containsOneOf(String...itemNames){
		for(String name : itemNames){
			if(name != null && getInventory().contains(name))
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
		getSkillTracker().start(Skill.THIEVING);
		startTile = getPlayers().myPlayer().getTile();
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
				Item item = getInventory().getItemInSlot(i);
				if(item != null){
					for(String name : sv.yourThieving.getDropItems()){
						if(name != null && name.equals(item.getName())){
							getInventory().slotInteract(i, "Drop");
							sleep(200,400);
							break;
						}
					}
				}
			}
			if(stealFrom != null){
				EntityDestination ed = new EntityDestination(getClient().getInstance(), stealFrom);
				if(ed != null){
					Point p = ed.getGaussPoint();
					getMouse().move(p);
				}
			}
			sleep(300,600);
			break;
		case STEAL:
			log(sv.yourThieving.getName());
			stealFrom = getNpcs().closest(sv.yourThieving.getName());
			if(stealFrom == null){
				stealFrom = getGameObjects().closest(sv.yourThieving.getName());
			}
			if(stealFrom != null && getPlayers().myPlayer().getAnimation() == -1 && stealFrom.isOnScreen()){
				log("Thieving!");
				stealFrom.interact(sv.yourThieving.getAction());
				sleep(300,500);
			} else{
				if(startTile.distance(getPlayers().myPlayer()) > 10){
					getWalking().walk(startTile);
					sleep(300,600);
				}
				else{
					sleep(300,600);
				}
			}
				
			break;
		case BANK:
			Bank bank = getBank();
			if(bank.isOpen()){
				bank.depositAllItems();
				sleep(300,600);
			}
			else{
				if(sv.yourBank.getCenter().distance(getPlayers().myPlayer()) > 10){
					getWalking().walk(sv.yourBank.getCenter());
					sleep(300,600);
				}
				else{
					bank.open(sv.yourBank);
					sleep(300,600);
				}
			}
			break;
		case HEAL:
			for(int i = 0; i < 28; i++){
				Item item = getInventory().getItemInSlot(i);
				if(item != null && item.hasAction("Eat")){
					getInventory().slotInteract(i, "Eat");
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
			g.drawString("Experience gained(p/h): " + getSkillTracker().getGainedExperience(Skill.THIEVING) +"(" + getSkillTracker().getGainedExperiencePerHour(Skill.THIEVING) + ")", 5, 90);
			g.drawString("Level(gained): " + getSkills().getRealLevel(Skill.THIEVING) + "(" + getSkillTracker().getGainedLevels(Skill.THIEVING) + ")", 5, 105);
			g.drawString("Runtime: " + timer.format(), 5, 120);
			if(state != null)
				g.drawString("State: " + state.toString(), 5, 135);
		}
		
	}

}
