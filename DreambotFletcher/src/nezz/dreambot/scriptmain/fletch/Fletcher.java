package nezz.dreambot.scriptmain.fletch;

import java.awt.Graphics;
import java.awt.Rectangle;

import nezz.dreambot.fletcher.gui.ScriptVars;
import nezz.dreambot.fletcher.gui.fletchGUI;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.widget.Widget;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;

@ScriptManifest(author = "Nezz", category = Category.FLETCHING, description = "Fletcher", name = "Dreambot Fletcher", version = 0)
public class Fletcher extends AbstractScript{

	ScriptVars sv = new ScriptVars();
	Timer t;
	boolean started = false;
	State state;
	private enum State{
		FLETCH, CHOP, DROP, BANK
	}
	
	private State getState(){
		if(getInventory().isFull()){
			if(getInventory().contains(sv.fletch.getLog()) && getInventory().contains("Knife")){
				return State.FLETCH;
			}
			else{
				if(sv.chopNDrop){
					return State.DROP;
				}
				else{
					return State.BANK;
				}
			}
		}
		else{
			if(!sv.chopNDrop){
				if(getInventory().contains(sv.fletch.getLog()) && getInventory().contains("Knife")){
					return State.FLETCH;
				}
				else{
					return State.BANK;
				}
			}
			else{
				return State.CHOP;
			}
		}
	}
	
	public void onStart(){
		fletchGUI gui = new fletchGUI(sv);
		gui.setVisible(true);
		while(!sv.started){
			sleep(300);
		}
		t = new Timer();
		getSkillTracker().start(Skill.FLETCHING);
		log("Starting Dreambot Fletcher");
		started = true;
	}
	@Override
	public int onLoop() {
		int returnThis = -1;
		if(progress())
			return 1;
		state = getState();
		switch(state){
		case BANK:
			if(getBank().isOpen()){
				if(getInventory().contains(sv.fletch.getName())){
					getBank().deposit(sv.fletch.getName());
					sleepUntil(new Condition(){
						public boolean verify(){
							return !getInventory().contains(sv.fletch.getName());
						}
					},2000);
					returnThis = Calculations.random(300,600);
				}
				else{
					if(!getBank().contains(sv.fletch.getLog())){
						log("Out of: " + sv.fletch.getLog());
						return -1;
					}
					getBank().withdraw(sv.fletch.getLog());
					sleepUntil(new Condition(){
						public boolean verify(){
							return getInventory().contains(sv.fletch.getLog());
						}
					},2000);
					returnThis = Calculations.random(300,600);
				}
			}
			else{
				getBank().openNearestBank();
				sleepUntil(new Condition(){
					public boolean verify(){
						return getBank().isOpen();
					}
				},2000);
				returnThis = Calculations.random(300,600);
			}
			break;
		case CHOP:
			if(getInventory().isItemSelected()){
				getMouse().click();
			}
			GameObject tree = getGameObjects().getClosest(sv.fletch.getTree());
			if(tree != null && tree.exists() && getLocalPlayer().getAnimation() == -1){
				tree.interact("Chop down");
				sleepUntil(new Condition(){
					public boolean verify(){
						return getLocalPlayer().getAnimation() != -1;
					}
				}, 3500);
				returnThis = Calculations.random(300,600);
			}
			else{
				sleep(300,600);
				returnThis = Calculations.random(300,500);
			}
			break;
		case DROP:
			for(int i = 0; i < 28; i++){
				Item item = getInventory().getItemInSlot(i);
				if(item != null && !item.getName().contains("axe") && !item.getName().equals("Knife")){
					getInventory().interactWithSlot(i, "Drop");
					sleep(100,300);
				}
			}
			returnThis = Calculations.random(300,600);
			break;
		case FLETCH:
			if(getBank().isOpen()){
				getBank().close();
				sleepUntil(new Condition(){
					public boolean verify(){
						return !getBank().isOpen();
					}
				},1200);
				return 1;
			}
			else if(getLocalPlayer().getAnimation() == -1){
				if(getInventory().contains("Knife")){
					final Widget par = getWidgets().getWidget(sv.fletch.getParent());
					WidgetChild chil = null;
					if(par != null){
						chil = par.getChild(sv.fletch.getChild());
					}
					if(chil != null && chil.isVisible()){
						/*getMouse().move(chil.getRectangle());
						for(String action : getClient().getMenu().getMenuActions()){
							log(action);
						}
						getMouse().click(true);
						try {
							getClient().getMenu().clickAction("Make X");
						} catch (InterruptedException e) {
							e.printStackTrace();
						}*/
						chil.interact("Make X");
						sleepUntil(new Condition(){
							public boolean verify(){
								return !par.getChild(sv.fletch.getChild()).isVisible();
							}
						},2000);
						if(!chil.isVisible()){
							int typ = Calculations.random(1,9);
							getKeyboard().type((Integer.toString(typ) + Integer.toString(typ) + Integer.toString(typ)),true);
							sleep(456,765);
						}
						returnThis = Calculations.random(300,600);
					}
					else{
						getInventory().interactWithItem("Knife", "Use");
						sleep(345,654);
						Rectangle r = getInventory().getSlotBounds(getInventory().getSlotForName(sv.fletch.getLog()));
						if(r != null){
							getMouse().move(r.getLocation());
							getMouse().click();
							sleepUntil(new Condition(){
								public boolean verify(){
									Widget par = getWidgets().getWidget(sv.fletch.getParent());
									WidgetChild chil = null;
									if(par != null){
										chil = par.getChild(sv.fletch.getChild());
									}
									return chil != null && chil.isVisible();
								}
							},2000);
						}
						returnThis = Calculations.random(300,600);
					}
				}
				else{
					log("No knife!?");
					returnThis = -1;
				}
			}
			else{
				returnThis = Calculations.random(300,600);
			}
			break;
		}
		return returnThis;
	}
	
	public boolean progress(){
		if(!sv.progress || sv.fletch.ordinal() == Fletching.values().length-1)
			return false;
		if(getSkills().getRealLevel(Skill.FLETCHING) >= Fletching.values()[sv.fletch.ordinal()+1].getLevel()){
			sv.fletch = Fletching.values()[sv.fletch.ordinal()+1];
			return true;
		}
		return false;
	}
	
	public void onPaint(Graphics g){
		if(started){
			if(state != null){
				g.drawString("State: " + state.toString(), 10, 50);
			}
			g.drawString("Runtime: " + t.formatTime(), 10, 65);
			g.drawString("Experience(p/h): " + getSkillTracker().getGainedExperience(Skill.FLETCHING) + "(" + getSkillTracker().getGainedExperiencePerHour(Skill.FLETCHING) + ")", 10, 80);
			g.drawString("Level(gained): " + getSkills().getRealLevel(Skill.FLETCHING) + "(" + getSkillTracker().getGainedLevels(Skill.FLETCHING) + ")", 10, 95);
			g.drawString("Fletching: " + sv.fletch.getName(), 10, 110);
		}
	}

}
