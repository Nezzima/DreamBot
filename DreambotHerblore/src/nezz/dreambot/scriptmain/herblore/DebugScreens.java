package nezz.dreambot.scriptmain.herblore;

import java.awt.Color;
import java.awt.Graphics;

import nezz.dreambot.herblore.gui.ScriptVars;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.wrappers.items.Item;

public class DebugScreens extends States{

	public DebugScreens(AbstractScript as, ScriptVars sv){
		this.as = as;
		this.sv = sv;
	}
	Item[] bank;
	Item[] trade;
	@Override
	public String getMode() {
		return "Debugging screens";
	}
	
	public boolean drawBank = false;
	public boolean drawTrade = false;

	@Override
	public int execute() {
		if(as.getTrade().isOpen()){
			trade = as.getTrade().getItems(false);
			drawTrade = true;
		}
		else
			drawTrade = false;
		if(as.getBank().isOpen()){
			drawBank = true;
			bank = as.getBank().getItems();
		}
		else
			drawBank = false;
		return 500;
	}

	@Override
	public String getState() {
		return "Drawing";
	}
	
	private int drawInventory(Graphics g){
		Color color = new Color(0,0,0,120);
		g.setColor(color);
		g.fillRect(400, 2, 150, 15);
		g.setColor(Color.blue);
		g.drawString("INVENTORY:", 400, 15);
		int baseY = 30;
		for(int i = 0; i < 28; i++){
			boolean clean = false;
			g.setColor(Color.green);
			Item item = as.getInventory().getItemInSlot(i);
			if(item != null){
				Herbs h = null;
				for(Herbs herb : Herbs.values()){
					int itemId = item.getID();
					if(itemId == herb.getNotedCleanId() || itemId == herb.getUnnotedCleanId()){
						clean = true;
						h = herb;
						break;
					}
					if(itemId == herb.getNotedGrimyId() || itemId == herb.getUnnotedGrimyId()){
						clean = false;
						h = herb;
						break;
					}
				}
				if(h != null){
					g.setColor(color);
					g.fillRect(400, baseY-13, 150, 15);
					if(!h.canIdHerb(as.getSkills().getBoostedLevels(Skill.HERBLORE)) && !clean){
						g.setColor(Color.red);
					}
					else if(clean){
						g.setColor(Color.yellow);
					}
					else{
						g.setColor(Color.green);
					}
					g.drawString(h.getName() + ": " + item.getID() +"(" + item.getAmount() + ")", 400, baseY);
					baseY+=15;
				}
			}
		}
		return baseY;
	}
	private void drawBank(Graphics g, int baseY){
		Color color = new Color(0,0,0,120);
		g.setColor(color);
		g.fillRect(400, baseY-13, 150, 15);
		g.setColor(Color.blue);
		g.drawString("BANK:", 400, baseY);
		baseY+=15;
		for(Item item : bank){
			boolean clean = false;
			g.setColor(Color.green);
			if(item != null){
				Herbs h = null;
				for(Herbs herb : Herbs.values()){
					int itemId = item.getID();
					if(itemId == herb.getNotedCleanId() || itemId == herb.getUnnotedCleanId()){
						clean = true;
						h = herb;
						break;
					}
					if(itemId == herb.getNotedGrimyId() || itemId == herb.getUnnotedGrimyId()){
						clean = false;
						h = herb;
						break;
					}
				}
				if(h != null){
					g.setColor(color);
					g.fillRect(400, baseY-13, 150, 15);
					if(!h.canIdHerb(as.getSkills().getBoostedLevels(Skill.HERBLORE)) && !clean){
						g.setColor(Color.red);
					}
					else if(clean){
						g.setColor(Color.yellow);
					}
					else{
						g.setColor(Color.green);
					}
					g.drawString(h.getName() + ": " + item.getID() +"(" + item.getAmount() + ")", 400, baseY);
					baseY+=15;
				}
			}
		}
	}
	private void drawTrade(Graphics g, int baseY){
		Color color = new Color(0,0,0,120);
		g.setColor(color);
		g.fillRect(400, baseY-13, 150, 15);
		g.setColor(Color.blue);
		g.drawString("TRADE:", 400, baseY);
		baseY+=15;
		for(Item item : trade){
			boolean clean = false;
			g.setColor(Color.green);
			if(item != null){
				Herbs h = null;
				for(Herbs herb : Herbs.values()){
					int itemId = item.getID();
					if(itemId == herb.getNotedCleanId() || itemId == herb.getUnnotedCleanId()){
						clean = true;
						h = herb;
						break;
					}
					if(itemId == herb.getNotedGrimyId() || itemId == herb.getUnnotedGrimyId()){
						clean = false;
						h = herb;
						break;
					}
				}
				if(h != null){
					g.setColor(color);
					g.fillRect(400, baseY-13, 150, 15);
					if(!h.canIdHerb(as.getSkills().getBoostedLevels(Skill.HERBLORE)) && !clean){
						g.setColor(Color.red);
					}
					else if(clean){
						g.setColor(Color.yellow);
					}
					else{
						g.setColor(Color.green);
					}
					g.drawString(h.getName() + ": " + item.getID() +"(" + item.getAmount() + ")", 400, baseY);
					baseY+=15;
				}
			}
		}
	}

	@Override
	public void draw(Graphics g) {
		int y = drawInventory(g);
		if(drawBank){
			drawBank(g,y);
		}
		if(drawTrade)
			drawTrade(g,y);
	}

}
