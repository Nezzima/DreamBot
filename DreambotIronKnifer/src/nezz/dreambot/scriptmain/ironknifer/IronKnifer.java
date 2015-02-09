package nezz.dreambot.scriptmain.ironknifer;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import nezz.dreambot.tools.PricedItem;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.bank.BankType;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.interactive.GameObject;

@ScriptManifest(author = "Nezz", category = Category.SMITHING, description = "Makes iron knives in Varrock", name = "DreamBot Iron Knifer", version = 0)
public class IronKnifer extends AbstractScript{
	PricedItem ironKnife = null;
	private State state = null;
	private Timer t = null;
	private final Tile BANK_TILE = new Tile(3185,3436,0);
	private final Tile ANVIL_TILE = new Tile(3188,3425,0);
	private AnvilSmith as = null;
	private GameObject bankBooth = null;
	private long boothUpdate = 0;
	private long lastAnimated = 0;
	private int runThresh = Calculations.random(30,70);
	private boolean movedAnvil = false;
	private boolean movedBank = false;
	private enum State{
		BANK, SMITH
	}
	
	private State getState(){
		if(getInventory().contains("Iron bar")){
			return State.SMITH;
		}
		return State.BANK;
	}
	
	public void onStart(){
		as = new AnvilSmith(getClient().getMethodContext());
		ironKnife = new PricedItem("Iron knife",getClient().getMethodContext(), false);
		t = new Timer();
	}
	
	private Point getRandomPoint(Tile t){
		Point point = null;
		Point p = getMap().tileToMiniMap(t);
		if(p.x > 0)
			point = randomizePoint(p);
		return point;
	}
	private Point randomizePoint(Point p){
		return new Point(p.x + Calculations.random(-5,5), p.y + Calculations.random(-5,5));
	}
	private Rectangle getRect(Tile t){
		Point p = getMap().tileToMiniMap(t);
		return new Rectangle(p.x-5, p.y-5, 10,10);
	}
	
	private boolean walk(Tile t){
		getClient().disableIdleCamera();
		Point p = getRandomPoint(t);
		if(p == null)
			return false;
		getMouse().move(p);
		sleep(60,150);
		getMouse().click();
		sleepUntil(new Condition(){
			public boolean verify(){
				return getLocalPlayer().isMoving();
			}
		}, 1200);
		getClient().enableIdleCamera();
		return getLocalPlayer().isMoving();
	}
	private GameObject getBankBooth(){
		if(bankBooth == null || System.currentTimeMillis() - boothUpdate > 5000){
			bankBooth = getGameObjects().closest(new Filter<GameObject>(){
				public boolean match(GameObject go){
					if(go == null || !go.exists() || go.getName() == null)
						return false;
					if(!go.getName().equals("Bank booth"))
						return false;
					return go.getTile().equals(new Tile(3186,3436,0));// || go.getTile().equals(new Tile(3186,3438,0));
				}
			});
			boothUpdate = System.currentTimeMillis();
		}
		log("Booth tile: " + bankBooth.getTile());
		return bankBooth;
	}
	private boolean waitForWalk(){
		if(!getLocalPlayer().isMoving() || state == null){
			return false;
		}
		Tile destination = getWalking().getDestination();
		if(destination != null && destination.distance(getLocalPlayer()) < Calculations.random(3,6)){
			return false;
		}
		if(state.equals(State.BANK) && getBankBooth() != null && getBankBooth().isOnScreen()){
			return false;
		}
		else if(state.equals(State.SMITH) && as.getAnvil() != null && as.getAnvil().isOnScreen()){
			return false;
		}
		return true;
	}
	
	private String getCameraDirection(){
		int yaw = getCamera().getYaw();
		if(yaw > 1800 || yaw < 300)
			return "N";
		else if(yaw >= 300 && yaw < 800){
			return "W";
		}
		else if(yaw >= 800 && yaw < 1300){
			return "S";
		}
		else
			return "E";
	}
	
	private Rectangle getHoverSpot(){
		Rectangle r = null;
		if(state.equals(State.BANK)){
			switch(getCameraDirection()){
				case "S":
					r = new Rectangle(0,0, 165,340);
					break;
				case "N":
					r = new Rectangle(330,0, 185,340);
					break;
				case "W":
					r = new Rectangle(0,200, 515,115);
					break;
				case "E":
					r = new Rectangle(0,0, 515,125);
					break;
			};
		}
		else{
			Rectangle temp = getLocalPlayer().getBoundingBox();
			r = new Rectangle(temp.x - 50, temp.y - 50, temp.height + 100, temp.width + 100);
		}
		return r;
	}
	
	
	@Override
	public int onLoop() {
		if(!getWalking().isRunEnabled() && getWalking().getRunEnergy() > runThresh){
			getWalking().toggleRun();
			runThresh = Calculations.random(30,70);
		}
		if(waitForWalk())
			return Calculations.random(300,600);
		state = getState();
		switch(state){
		case BANK:
			movedAnvil = false;
			if(!getBank().isOpen()){
				if(BANK_TILE.distance(getLocalPlayer()) > 5 && !getLocalPlayer().isMoving()){
					walk(BANK_TILE);
					/*getWalking().walk(BANK_TILE);
					sleepUntil(new Condition(){
						public boolean verify(){
							return getLocalPlayer().isMoving();
						}
					},1200);*/
				}
				else if(!getLocalPlayer().isMoving()){
					getBank().openBank(BankType.BOOTH);
					sleepUntil(new Condition(){
						public boolean verify(){
							return getBank().isOpen();
						}
					},3600);
				}
				else if(!movedBank){
					getMouse().move(getHoverSpot());
					//getMouse().move(getLocalPlayer());
					movedBank = true;
				}
			}
			else{
				if(getInventory().contains("Iron knife")){
					ironKnife.update();
					getBank().depositAllExcept("Hammer");
					sleepUntil(new Condition(){
						public boolean verify(){
							return !getInventory().contains("Iron knife");
						}
					},1200);
				}
				else if(getBank().contains("Iron bar")){
					getBank().withdraw("Iron bar");
					sleepUntil(new Condition(){
						public boolean verify(){
							return getInventory().contains("Iron bar");
						}
					},1200);
				}
				else{
					log("Out of bars!");
					stop();
				}
			}
			break;
		case SMITH:
			movedBank = false;
			if(as.isOpen() || getInventory().isItemSelected() || !amIAnimating()){
				if(as.isOpen()){
					as.makeKnife();
					sleepUntil(new Condition(){
						public boolean verify(){
							return !as.isOpen();
						}
					},1200);
				}
				else{
					if(ANVIL_TILE.distance(getLocalPlayer()) > 5 && !getLocalPlayer().isMoving()){
						walk(ANVIL_TILE);
						/*getWalking().walk(ANVIL_TILE);
						sleepUntil(new Condition(){
							public boolean verify(){
								return getLocalPlayer().isMoving();
							}
						},1200);*/
						Tile dest = getWalking().getDestination();
						if(dest != null && dest.distance(ANVIL_TILE) < 4){
							if(!getInventory().isItemSelected()){
								sleep(400,1400);
								getInventory().interactWithItem("Iron bar","Use");
							}
						}
					}
					else{
						if(getInventory().isItemSelected()){
							if(!getLocalPlayer().isMoving()){
								if(as.getAnvil() != null){
									as.getAnvil().interact("Use");
									sleepUntil(new Condition(){
										public boolean verify(){
											return as.isOpen();
										}
									},2400);
								}
							}
							else if(!movedAnvil){
								getMouse().move(getHoverSpot());
								movedAnvil = true;
							}
						}
						else{
							getInventory().interactWithItem("Iron bar", "Use");
							sleepUntil(new Condition(){
								public boolean verify(){
									return getInventory().isItemSelected();
								}
							},1200);
						}
					}
				}
			}
			else{
				ironKnife.update();
				sleep(300,500);
			}
			break;
		}
		return Calculations.random(100,200);
	}
	
	public boolean amIAnimating(){
		if(System.currentTimeMillis() - lastAnimated > 5000){
			if(getLocalPlayer().getAnimation() != -1){
				lastAnimated = System.currentTimeMillis();
				return true;
			}
			return false;
		}
		for(int i = 0; i < 20; i++){
			if(getLocalPlayer().getAnimation() != -1){
				lastAnimated = System.currentTimeMillis();
				return true;
			}
			else{
				sleep(50);
			}
		}
		return false;
	}
	
	@Override
	public void onPaint(Graphics g){
		if(t != null){
			((Graphics2D)g).draw(getRect(BANK_TILE));
			((Graphics2D)g).draw(getRect(ANVIL_TILE));
			((Graphics2D)g).draw(getHoverSpot());
			g.drawString("Runtime: " + t.formatTime(), 10, 35);
			g.drawString("State: " + state.toString(), 10, 50);
			g.drawString("Knives(p/h): " + ironKnife.getAmount() + "(" + t.getHourlyRate(ironKnife.getAmount()) + ")", 10, 65);
		}
	}
	
	
	//312,24
	//312,1,11
}
