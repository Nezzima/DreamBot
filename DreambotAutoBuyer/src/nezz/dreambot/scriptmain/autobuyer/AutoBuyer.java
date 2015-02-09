package nezz.dreambot.scriptmain.autobuyer;

import java.awt.Graphics;

import nezz.dreambot.autobuyer.gui.ScriptVars;
import nezz.dreambot.autobuyer.gui.buyerGui;
import nezz.dreambot.tools.PricedItem;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.shop.Shop;
import org.dreambot.api.methods.world.World;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.utilities.impl.Filter;
import org.dreambot.api.wrappers.items.Item;

@ScriptManifest(author = "Nezz", name = "DreamBot Auto Buyer", version = 0, category = Category.MONEYMAKING, description = "Buys any pack and opens it")
public class AutoBuyer extends AbstractScript{

	Timer timer;
	ScriptVars sv = new ScriptVars();
	State state;
	boolean hoppingWorlds = false;
	private long startGP = 0;
	
	boolean started = false;
	private enum State{
		BUY, HOP
	}

	private State getState(){
		if(hoppingWorlds)
			return State.HOP;
		return State.BUY;
	}

	public void onStart(){
		buyerGui gui = new buyerGui(sv);
		gui.setVisible(true);
		while(!sv.started){
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		startGP = getInventory().getCount("Coins");
		sv.item = new PricedItem(sv.itemName, getClient().getMethodContext(), true);
		timer = new Timer();
		started = true;
	}
	
	/**
	 * Gets a World ID that makes it through the filters.
	 * @return Integer value of the world.
	 */
	private int getHopWorld(){
		int hopTo = 0;
		if(sv.f2p){
			World[] worlds = getWorlds().getAll(new Filter<World>(){
				public boolean match(World w){
					if(!w.isF2P() || w.isPVP() || w.isHighRisk())
						return false;
					return true;
				}
			});
			hopTo = getWorlds().getRandomWorld(worlds).getID();
		}
		else{
			World[] worlds = getWorlds().getAll(new Filter<World>(){
				public boolean match(World w){
					if(w.isF2P() || w.isPVP() || w.isHighRisk())
						return false;
					return true;
				}
			});
			hopTo = getWorlds().getRandomWorld(worlds).getID();
		}
		return hopTo;
	}

	@Override
	public int onLoop() {
		//Check if you're logged in, if you aren't cut out and sleep for a bit.
		if(!getClient().isLoggedIn()){
			return Calculations.random(300,500);
		}
		//calculate gp used, if you don't have enough gold to go another minute kill script.
		int gp = getInventory().getCount("Coins");
		int gpPerMin = (timer.getHourlyRate((int)startGP - gp)/60)/6;
		if(gp < gpPerMin && gpPerMin > 0){
			log("You can't last another minute: " + gpPerMin);
			stop();
			return -1;
		}
		//Set Shop to a variablef or less typing
		final Shop s = getShop();
		//if you're moving and your destination is still far away, return and sleep
		if(getPlayers().myPlayer().isMoving() && getClient().getDestination() != null && getClient().getDestination().distance(getPlayers().myPlayer().getTile()) > 3)
			return Calculations.random(200,300);
		//start actual script stuff
		//get state
		state = getState();
		switch(state){
		case HOP:
			/**
			 * HOP state. This will hop worlds and disclude PVP and High Risk.
			 * If you've selected F2P hopping, it will only hop to f2p worlds, otherwise
			 * Any other members world.
			 */
			//if you're actually hopping worlds, do stuff
			if(sv.hopWorlds){
				//if shop is open close it.
				if(s.isOpen()){
					s.close();
					sleepUntil(new Condition(){
						public boolean verify(){
							return !s.isOpen();
						}
					},1200);
				}
				//find a world to hop to
				int hopTo = getHopWorld();
				//while the hopping to world is the same as your world, keep finding a new one
				while(hopTo == getClient().getCurrentWorld()){
					hopTo = getHopWorld();
				}
				//Quickhop to the world.
				log("Hopping to: " + hopTo);
				getWorldHopper().quickHop(hopTo);
				//sleep until the login handler is solving.
				sleepUntil(new Condition(){
					public boolean verify(){
						return getClient().getInstance().getScriptManager().getCurrentScript().getRandomManager().isSolving();
					}
				},30000);
			}
			else{
				//if you don't have hopping selected, just sleep for 30-50 seconds
				//this lets the items regenerate a bit in the shop.
				try {
					Thread.sleep(Calculations.random(30000,50000));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			//set hoppingWorlds to false
			hoppingWorlds = false;
			break;
		case BUY:
			/**
			 * BUY State. This state is where the shopping is handled.
			 * This will interact with the nearest entity with "Trade" in its name
			 * Then it'll search for the item.
			 */
			//if shop isn't open, try to open it.
			if(!s.isOpen()){
				s.tryOpen();
				//sleep until it's open.
				sleepUntil(new Condition(){
					@Override
					public boolean verify() {
						return s.isOpen();
					}
				},Calculations.random(1500,2500));
			}
			else if(s.validate()){
				//validate will guarantee the items in the shop are updated.
				//get the item from the shop
				Item pack = s.getItemByName(sv.itemName);
				//calculate buy amount based on min amount and current item amount
				int buyAmt = pack.getAmount() - sv.minAmt;
				//if it's not null and the amount is greater than min amount, buy it.
				if(pack != null && pack.getAmount() > sv.minAmt){
					//make buy amount divisible by 10
					//this makes it so you only use the buy 10 action
					buyAmt = buyAmt/10;
					if(buyAmt == 0)
						buyAmt++;
					buyAmt = buyAmt*10;
					//buy the item.
					s.buy(pack, buyAmt);
				}
				//revalidate to update items again
				s.validate();
				//check if the item is null or the amount is less than your min amount.
				pack = s.getItemByName(sv.itemName);
				if((pack == null || pack.getAmount() <= sv.minAmt) && sv.hopWorlds)
					hoppingWorlds = true;
				//sv.item.update();
			}
			break;
		}
		return Calculations.random(10,20);
	}
	//keeps track of the last time the profit updated
	private long profUpdate = 0;
	//keeps track of the last profit.
	private int lastProfit = 0;
	public int getProfit(){
		//if you're not logged in or the item amount is 0, return lastProfit
		if(!getClient().isLoggedIn() || getInventory().getCount(sv.item.getName()) <= 0){
			return lastProfit;
		}
		//if lastProfit is 0 or 600ms has passed since last update, update lastProfit
		if(lastProfit == 0 || System.currentTimeMillis() - profUpdate > 600){
			//update item tracker
			sv.item.update();
			//get current value of the items
			int currValue = sv.item.getValue();
			//get spent gold
			int spent = (int)startGP - getInventory().getCount("Coins");
			//get profit
			lastProfit = currValue - spent;
			//update the timer
			profUpdate = System.currentTimeMillis();
		}
		return lastProfit;
	}

	public void onPaint(Graphics g){
		if(started){
			if(state != null){
				g.drawString("State: " + state.toString(), 5, 50);
			}
			g.drawString(sv.item.getName() + " bought(p/h): " + sv.item.getAmount() + "(" + timer.getHourlyRate(sv.item.getAmount()) +")", 5, 65);
			g.drawString("GP Made(p/h): " + getProfit() + "(" + timer.getHourlyRate(getProfit()) + ")", 5, 80);
			g.drawString("Runtime: " + timer.formatTime(),5, 95);
		}
	}

}
