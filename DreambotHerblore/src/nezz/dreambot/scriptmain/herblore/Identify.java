package nezz.dreambot.scriptmain.herblore;

import nezz.dreambot.herblore.gui.ScriptVars;
import nezz.dreambot.tools.PricedItem;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.items.Item;

public class Identify extends States{

	public Identify(AbstractScript as, ScriptVars sv){
		this.as = as;
		this.sv = sv;
		this.lootList.add(new PricedItem(sv.yourHerb.getName(), sv.yourHerb.getUnnotedCleanId(), as.getClient().getMethodContext(), false));
	}
	Condition depositItems = new Condition(){
		public boolean verify(){
			return as.getInventory().isEmpty();
		}
	};
	Condition withdrawItems = new Condition(){
		public boolean verify(){
			return !as.getInventory().isEmpty();
		}
	};
	public String getState(){
		if(as.getInventory().contains(sv.yourHerb.getUnnotedGrimyId()))
			return "IDENTIFY";
		else
			return "BANK";
	}
	
	public int execute() throws InterruptedException{
		int returnThis = -1;
		state = getState();
		switch(state){
		case "BANK":
			if(as.getBank().isOpen()){
				if(as.getInventory().contains(sv.yourHerb.getUnnotedCleanId())){
					as.getBank().depositAllItems();
					MethodProvider.sleepUntil(depositItems,2000);
					returnThis = 200;
				}
				else{
					if(as.getBank().contains(sv.yourHerb.getUnnotedGrimyId())){
						as.getBank().withdrawAll(sv.yourHerb.getUnnotedGrimyId());
						MethodProvider.sleepUntil(withdrawItems,2000);
						returnThis = 200;
					}
					else{
						as.getBank().close();
						returnThis = -1;
					}
				}
			}
			else{
				updateLoot();
				as.getBank().open();
				if(as.getClient().getMenu().containsAction("Use"))
					as.getMouse().click();
				returnThis = 200;
			}
			break;
		case "IDENTIFY":
			if(as.getBank().isOpen()){
				as.getBank().close();
			}
			else{
				for(int i = 0; i < 28; i++){
					Item item = as.getInventory().getItemInSlot(i);
					if(item != null && item.getName().contains("Grimy")){
						as.getInventory().slotInteract(i, "Clean");
						MethodProvider.sleep(100,300);
						this.updateLoot();
					}
				}
			}
			returnThis = Calculations.random(600,800);
			this.updateLoot();
			break;
		}
		return returnThis;
	}

	public String getMode() {
		return "Identify Herbs";
	}

}
