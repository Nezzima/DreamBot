package nezz.dreambot.scriptmain.herblore;

import nezz.dreambot.herblore.gui.ScriptVars;
import nezz.dreambot.tools.PricedItem;

import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.bank.Bank;
import org.dreambot.api.methods.widget.Widget;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.widgets.WidgetChild;


public class Potions extends States{

	public Potions(AbstractScript as, ScriptVars sv){
		this.as = as;
		this.sv = sv;
		lootList.add(new PricedItem(sv.yourPot.getName() + "(3)", as.getClient().getMethodContext(), false));
	}
	@Override
	public String getMode() {
		return "Making potions: "+ sv.yourPot.getName();
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
		case "Finish Potion":
			if(as.getBank().isOpen()){
				as.getBank().close();
				returnThis = 600;
				wasAnimating = false;
			}
			else if(wasAnimating && amIAnimating()){
				this.updateLoot();
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
					as.getInventory().interactWithItem(sv.yourPot.getIngredientTwo(), "Use");
					returnThis = 400;
					wasAnimating = false;
				}
				else{
					as.getInventory().interactWithItem(sv.yourPot.getUnfName(), "Use");
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
						returnThis = 400;
					}
					else{
						MethodProvider.log("Issues?");
						returnThis = 1000;
					}
				}
			}
			break;
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
					as.getInventory().interactWithItem(sv.yourPot.getIngredientOne(), "Use");
					returnThis = 400;
					wasAnimating = false;
				}
				else{
					as.getInventory().interactWithItem("Vial of water", "Use");
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
				if(as.getInventory().contains(sv.yourPot.getName() + "(3)")){
					bank.depositAll();
					MethodProvider.sleepUntil(new Condition(){
						public boolean verify(){
							return as.getInventory().isEmpty();
						}
					},1200);
					returnThis = 100;
				}
				else{
					if(as.getInventory().isEmpty()){
						if(bank.contains(sv.yourPot.getUnfName())){
							bank.withdraw(sv.yourPot.getUnfName(),14);
						}
						else if(bank.contains(sv.yourPot.getIngredientOne())){
							bank.withdraw(sv.yourPot.getIngredientOne(), 14);
						}
						else{
							return -1;
						}
						returnThis = 600;
					}
					else if(as.getInventory().contains(sv.yourPot.getIngredientOne())){
						if(bank.contains("Vial of water")){
							bank.withdraw("Vial of water");
							returnThis = 600;
						}
						else
							returnThis = -1;
					}
					else if(as.getInventory().contains(sv.yourPot.getUnfName())){
						if(!as.getInventory().contains(sv.yourPot.getIngredientTwo())){
							bank.withdraw(sv.yourPot.getIngredientTwo());
							returnThis = 600;
						}
						else{
							returnThis = -1;
						}
					}
					else
						returnThis = -1;
				}
			}
			else{
				bank.openNearestBank();
				if(as.getClient().getMenu().containsAction("Use"))
					as.getMouse().click();
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
			else {
				if(as.getInventory().contains("Vial of water") && as.getInventory().contains(sv.yourPot.getUnfName()) && as.getInventory().contains(sv.yourPot.getIngredientTwo()))
					return "Finish Potion";
				return "Bank";
			}
		}
		else if(as.getInventory().contains(sv.yourPot.getUnfName())){
			if(as.getInventory().contains(sv.yourPot.getIngredientTwo())){
				return "Finish Potion";
			}
			else{
				return "Bank";
			}
		}
		else
			return "Bank";
	}


}
