package nezz.dreambot.scriptmain.herblore;

import nezz.dreambot.herblore.gui.ScriptVars;
import nezz.dreambot.tools.PricedItem;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.widget.Widget;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.widgets.WidgetChild;


public class UnfPotions extends States{

	public UnfPotions(AbstractScript as, ScriptVars sv){
		this.as = as;
		this.sv = sv;
		lootList.add(new PricedItem(sv.yourPot.getName() + "(3)", as.getClient().getMethodContext(), false));
	}
	@Override
	public String getMode() {
		return "Making Unfinished potions: "+ sv.yourPot.getName();
	}

	Condition makePot = new Condition(){
		public boolean verify(){
			return as.getWidgets().getChildWidget(309, 2) != null;
		}
	};
	
	private boolean wasAnimating = false;
	@Override
	public int execute() throws InterruptedException {
		int returnThis = -1;
		this.state = getState();
		switch(state){
		case "Start Potion":
			if(as.getBank().isOpen()){
				as.getBank().close();
				returnThis = 600;
				wasAnimating = false;
			}
			else if(wasAnimating && amIAnimating()){
				returnThis = 600;
			}
			else if(as.getWidgets().getWidget(309) != null && as.getWidgets().getWidget(309).getChild(2) != null){
				Widget par = as.getWidgets().getWidget(309);
				WidgetChild child = null;
				if(par != null){
					child = par.getChild(2);
				}
				if(child != null){
					if(child.interact("Make All")){
						wasAnimating = true;
						MethodProvider.sleepUntil(new Condition(){
							public boolean verify(){
								return as.getLocalPlayer().getAnimation() != -1;
							}
						},1200);
					}
					else{
						wasAnimating = false;
					}
					returnThis = 400;
				}
				else{
					wasAnimating = false;
					MethodProvider.log("Issues?");
					returnThis = 1000;
				}
			}
			else{
				if(!as.getInventory().isItemSelected()){
					as.getInventory().interact(sv.yourPot.getIngredientOne(), "Use");
					returnThis = 400;
					wasAnimating = false;
				}
				else{
					as.getInventory().interact("Vial of water", "Use");
					MethodProvider.sleepUntil(makePot,2000);
					returnThis = 400;
					Widget par = as.getWidgets().getWidget(309);
					WidgetChild child = null;
					if(par != null){
						child = par.getChild(2);
					}
					if(child != null){
						if(child.interact("Make All")){
							wasAnimating = true;
							MethodProvider.sleepUntil(new Condition(){
								public boolean verify(){
									return as.getLocalPlayer().getAnimation() != -1;
								}
							},1200);
						}
						else{
							wasAnimating = false;
						}
						returnThis = 400;
					}
					else{
						wasAnimating = false;
						MethodProvider.log("Issues?");
						returnThis = 1000;
					}
				}
			}
			break;
		case "Bank":
			this.updateLoot();
			wasAnimating = false;
			Bank bank = as.getBank();
			if(bank.isOpen()){
				if(as.getInventory().contains(sv.yourPot.getUnfName())){
					bank.depositAllItems();
					MethodProvider.sleepUntil(new Condition(){
						public boolean verify(){
							return as.getInventory().isEmpty();
						}
					},1200);
					returnThis = 100;
				}
				else{
					if(as.getInventory().isEmpty()){
						if(bank.contains(sv.yourPot.getIngredientOne())){
							MethodProvider.log("withdrawing ingredient one");
							bank.withdraw(sv.yourPot.getIngredientOne(), 14);
						}
						else{
							MethodProvider.log("You don't have the items!");
							return -1;
						}
						returnThis = 600;
					}
					else if(as.getInventory().contains(sv.yourPot.getIngredientOne())){
						if(bank.contains("Vial of water")){
							MethodProvider.log("Vial of water");
							bank.withdrawAll("Vial of water");
							returnThis = 600;
						}
						else{
							MethodProvider.log("You don't have any vials of water!");
							returnThis = -1;
						}
					}
					else{
						MethodProvider.log("Something went wrong");
						returnThis = -1;
					}
				}
			}
			else{
				MethodProvider.log("Opening bank!");
				bank.open();
				if(as.getClient().getMenu().containsAction("Use")){
					as.getMouse().click();
				}
				else{
					MethodProvider.log("Waiting for bank to open");
					MethodProvider.sleepUntil(new Condition(){
						public boolean verify(){
							return as.getBank().isOpen();
						}
					},Calculations.random(1200,1500));
				}
				returnThis = 200;
			}
			break;
		}
		return returnThis;
	}
	
	private boolean amIAnimating() throws InterruptedException{
		for(int i = 0; i < 50; i++){
			if(as.getLocalPlayer().getAnimation() != -1)
				return true;
			else
				MethodProvider.sleep(30);
		}
		return false;
	}

	@Override
	public String getState() {
		if(as.getInventory().contains("Vial of water") || as.getInventory().contains(sv.yourPot.getIngredientOne())){
			if(as.getInventory().contains("Vial of water") && as.getInventory().contains(sv.yourPot.getIngredientOne()))
				return "Start Potion";
			else
				return "Bank";
		}
		else
			return "Bank";
	}


}
