package nezz.dreambot.scriptmain.herblore;

import java.awt.Graphics;

import nezz.dreambot.herblore.gui.ScriptVars;
import nezz.dreambot.herblore.gui.herbloreGui;
import nezz.dreambot.tools.RunTimer;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.SkillTracker;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;

@ScriptManifest(author = "Nezz", category = Category.HERBLORE, 
	description = "ID's, makes potions, or debugs trade/inventory screens", 
	name = "Dreambot's Herblore", version = 0)
public class Herblore extends AbstractScript{

	private final ScriptVars sv = new ScriptVars();
	herbloreGui gui;
	private States states;
	private boolean started = false;
	RunTimer timer;
	public void onStart(){
		gui = new herbloreGui(sv);
		gui.setVisible(true);
		while(!sv.started){
			sleep(500);
		}
		if(sv.id)
			states = new Identify(this, sv);
		else if(sv.debug)
			states = new DebugScreens(this, sv);
		else if(sv.potions)
			states = new Potions(this, sv);
		else if(sv.unfPotions)
			states = new UnfPotions(this,sv);
		else
			states = null;
		if(states == null){
			System.out.println("States is null?");
		}
		SkillTracker.start(Skill.HERBLORE);
		timer = new RunTimer();
		started = true;
		log("Starting Dreambot's Herblore Script!");
	}
	
	@Override
	public int onLoop() {
		if(states == null){
			stop();
			return 1;
		}
		else{
			int ret = 0;
			try {
				ret = states.execute();
			} catch (InterruptedException e) {
				ret = -1;
				e.printStackTrace();
			}
			if(ret < 0){
				stop();
				return 100;
			}
			else{
				return ret;
			}
		}
	}
	
	public void onPaint(Graphics g){
		if(started){
			g.drawString("Runtime: " + timer.format(), 10, 30);
			states.draw(g);
		}
		
	}

}
