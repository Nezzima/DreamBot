package nezz.dreambot.scriptmain.packbuyer;

import java.awt.Graphics;

import nezz.dreambot.packbuyer.gui.ScriptVars;
import nezz.dreambot.packbuyer.gui.buyerGui;
import nezz.dreambot.tools.PricedItem;
import nezz.dreambot.tools.RunTimer;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Shop;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.items.Item;

@ScriptManifest(author = "Nezz", name = "DreamBot Pack Buyer", version = 0, category = Category.MONEYMAKING, description = "Buys any pack and opens it")
public class PackBuyer extends AbstractScript{

	private boolean hopWorlds = false;
	private final int[] f2pWorlds = new int[]{381,382,384,393,394};

	ScriptVars sv = new ScriptVars();

	PricedItem feathers;
	State state;
	RunTimer timer;
	
	boolean started = false;
	private enum State{
		BUY, OPEN_PACKS, HOP
	}

	private State getState(){
		if(getInventory().contains(sv.packName)){
			return State.OPEN_PACKS;
		}
		if(hopWorlds)
			return State.HOP;
		if(getInventory().contains(sv.packName)){
			return State.OPEN_PACKS;
		}
		else
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

		String itemName = sv.packName.replace(" pack", "");
		log(itemName);
		feathers = new PricedItem(itemName, getClient().getMethodContext(), false);
		timer = new RunTimer();
		started = true;
	}

	@Override
	public int onLoop() {
		log("looping");
		final Shop s = getShop();
		if(feathers == null){
			String itemName = sv.packName.replace(" pack", "");
			log(itemName);
			feathers = new PricedItem(itemName, getClient().getMethodContext(), false);
		}
		if(getPlayers().myPlayer().isMoving() && getClient().getDestination() != null && getClient().getDestination().distance(getPlayers().myPlayer().getTile()) > 3)
			return Calculations.random(200,300);
		state = getState();
		switch(state){
		case HOP:
			if(sv.hopWorlds){
				int hopTo = f2pWorlds[Calculations.random(0,f2pWorlds.length-1)];
				while(hopTo == getClient().getCurrentWorld())
					hopTo = f2pWorlds[Calculations.random(0,f2pWorlds.length-1)];
				getWorldHopper().hopWorld(hopTo);
			}
			else{
				try {
					Thread.sleep(Calculations.random(30000,50000));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			hopWorlds = false;
			break;
		case BUY:
			if(!s.isOpen()){
				s.open();
				waitFor(new Condition(){
					@Override
					public boolean verify() {
						return s.isOpen();
					}
				},1500);
			}
			else{
				Item pack = s.get(sv.packName);
				if(pack != null && pack.getAmount() > sv.minAmt){
					s.purchase(pack, 10);
					waitFor(new Condition(){
						public boolean verify(){
							return getInventory().contains(sv.packName);
						}
					},1000);
				}
				else{
					try {
						Thread.sleep(Calculations.random(200,300));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				pack = s.get(sv.packName);
				if(pack == null || pack.getAmount() <= sv.minAmt/2)
					hopWorlds = true;
			}
			break;
		case OPEN_PACKS:
			if(s.isOpen()){
				s.close();
			}
			else{
				for(int i = 0; i < 28; i++){
					Item it = getInventory().getItemInSlot(i);
					if(it != null && it.getName().equals(sv.packName)){
						getInventory().slotInteract(i, "Open");
						try {
							Thread.sleep(Calculations.random(100,150));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					feathers.update();
				}
				feathers.update();
			}
			break;
		}
		return Calculations.random(100,200);
	}

	public void waitFor(Condition c, int timeout){
		long t = System.currentTimeMillis();
		while(System.currentTimeMillis() - t < timeout && !c.verify()){
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void onPaint(Graphics g){
		if(started){
			if(state != null){
				g.drawString("State: " + state.toString(), 5, 50);
			}
			g.drawString(feathers.getName() + " bought(p/h): " + feathers.getAmount() + "(" + timer.getPerHour(feathers.getAmount()) +")", 5, 65);
			g.drawString("GP Made(p/h): " + feathers.getAmount()*(feathers.getPrice() - sv.perItem) + "(" + timer.getPerHour(feathers.getAmount()*(feathers.getPrice()-sv.perItem)) + ")", 5, 80);
			g.drawString("Time run: " + timer.format(),5, 95);
		}
	}

}
