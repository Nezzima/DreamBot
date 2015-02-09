package nezz.dreambot.scriptmain.herblore;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import nezz.dreambot.herblore.gui.ScriptVars;
import nezz.dreambot.tools.PricedItem;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.script.AbstractScript;

public abstract class States {

	public AbstractScript as;
	public ScriptVars sv;
	public String[] states;
	public String state;
	public List<PricedItem> lootList = new ArrayList<PricedItem>();
	public abstract String getMode();
	public abstract int execute() throws InterruptedException;
	public void draw(Graphics g){
		int baseY = 45;
		g.drawString("Mode: " + getMode(), 10, baseY);
		baseY+=15;
		g.drawString("Current state: " + getCurrentState(), 10, baseY);
		baseY+=15;
		g.drawString("Experience(p/h): " + as.getSkillTracker().getGainedExperience(Skill.HERBLORE) + "(" + as.getSkillTracker().getGainedExperiencePerHour(Skill.HERBLORE) + ")", 10, baseY);
		baseY+=15;
		g.drawString("Level(gained): " + as.getSkills().getRealLevel(Skill.HERBLORE) + "(" + as.getSkillTracker().getGainedLevels(Skill.HERBLORE) + ")", 10, baseY);
		baseY+=15;
		for(PricedItem pi : lootList){
			if(pi != null && pi.getAmount() > 0){
				g.drawString(pi.getName() + ": " + pi.getAmount(), 10, baseY);
				baseY+=15;
			}
		}
	}
	
	public String getCurrentState(){
		return this.state;
	}
	public abstract String getState();
	public void updateLoot(){
		for(PricedItem pi : lootList){
			if(pi != null)
				pi.update();
		}
	}
	
	public List<PricedItem> getLootList(){
		return this.lootList;
	}
}
