package nezz.dreambot.scriptmain.flaxpicker;

import java.awt.Graphics;

import nezz.dreambot.tools.PricedItem;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.interactive.GameObject;

@ScriptManifest(author = "Nezz", category = Category.MONEYMAKING, description = "Picks flax", name = "DreamBot Flax Picker", version = 0)
public class Flax extends AbstractScript{

	
	private final Tile FLAX_TILE = new Tile(2741,3446,0);
	private final Area FLAX_AREA = new Area(2737,3450,2745,3444);
	
	private int runThresh = Calculations.random(30,70);
	private int walkThresh = Calculations.random(4,8);
	
	private Timer timer;
	private PricedItem flax = null;
	private GameObject flaxToPick = null;
	
	public void onStart(){
		getClient().disableIdleCamera();
		flax = new PricedItem("Flax", getClient().getMethodContext(), false);
		timer = new Timer();
	}
	private State state = null;
	private enum State{
		PICK, BANK
	}
	private State getState(){
		if(getInventory().isFull()){
			return State.BANK;
		}
		else
			return State.PICK;
	}
	private GameObject getFlax(){
		if(flaxToPick == null || !flaxToPick.exists()){
			flaxToPick = getGameObjects().closest("Flax");
		}
		return flaxToPick;
	}
	
	@Override
	public int onLoop() {
		flax.update();
		if(!getWalking().isRunEnabled() && getWalking().getRunEnergy() > runThresh){
			getWalking().toggleRun();
			runThresh = Calculations.random(30,70);
		}
		if(!getWalking().shouldWalk(walkThresh)){
			return Calculations.random(500,700);
		}
		walkThresh = Calculations.random(4,8);
		state = getState();
		switch(state){
		case BANK:
			flaxToPick = null;
			if(BankLocation.SEERS.getArea(5).contains(getLocalPlayer())){
				if(getBank().isOpen()){
					getBank().depositAllItems();
					sleepUntil(new Condition(){
						public boolean verify(){
							return getInventory().isEmpty();
						}
					},1200);
				}
				else{
					getBank().open();
					sleepUntil(new Condition(){
						public boolean verify(){
							return getBank().isOpen();
						}
					},1200);
				}
			}
			else{
				getWalking().walk(BankLocation.SEERS.getCenter());
				sleep(700,900);
			}
			break;
		case PICK:
			if(FLAX_AREA.contains(getLocalPlayer())){
				if(getClient().getMenu().getDefaultAction().equals("Pick")){
					getMouse().click(false);
					sleep((int)(Calculations.random(300,400)*getClient().seededRandom()));
				}
				else{
					GameObject flax = getFlax();
					if(flax != null){
						if(!flax.getModel().getModelArea(flax).contains(getMouse().getPosition()))
							getMouse().move(flax);
						getMouse().click();
						sleep((int)(Calculations.random(300,500)*getClient().seededRandom()));
					}
				}
			}
			else{
				getWalking().walk(FLAX_TILE);
				sleep(700,900);
			}
			break;
		}
		return (int)(Calculations.random(200,300)*getClient().seededRandom());
	}
	
	public void onPaint(Graphics g){
		if(state != null){
			g.drawString("State: " + state.toString(), 10, 35);
			g.drawString("Runtime: " + timer.formatTime(), 10, 50);
			g.drawString("Flax(p/h): " + flax.getAmount() + "(" + timer.getHourlyRate(flax.getAmount()) + ")", 10, 65);
		}
	}
	

}
