package nezz.dreambot.scriptmain.hunter;

import java.awt.Graphics2D;

import nezz.dreambot.scriptmain.hunter.gui.ScriptVars;
import nezz.dreambot.scriptmain.hunter.gui.hunterGui;
import nezz.dreambot.tools.PricedItem;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;

@ScriptManifest(author = "Nezz", description = "Catches stuff", name = "DreamBot Hunter", version = 0, category = Category.HUNTING)
public class Hunter extends AbstractScript{

	PricedItem track;
	private Tile startTile = null;
	ScriptVars sv = new ScriptVars();
	public void onStart(){
		hunterGui gui = new hunterGui(sv);
		gui.setVisible(true);
		while(!sv.started){
			sleep(200);
		}
		track = new PricedItem(sv.huntThis.getTrackItem(),getClient().getMethodContext(), false);
		startTile = getLocalPlayer().getTile();
		sv.trapTiles = new Tile[getTrapAmount()];
		getTileArray(getTrapAmount(), startTile);
		getSkillTracker().start(Skill.HUNTER);
		startingLevel = getSkills().getRealLevel(Skill.HUNTER);
		rt = new Timer();
		log("Starting script!");
	}
	
	private int getTrapAmount(){
		int amount = 0;
		int level = getSkills().getRealLevel(Skill.HUNTER);
		if(level < 20)
			amount = 1;
		else if(level < 40)
			amount = 2;
		else if(level < 60)
			amount = 3;
		else if(level < 80)
			amount= 4;
		else{
			amount = 5;
		}
		if(getInventory().getCount(sv.huntThis.getTrapName()) < amount){
			amount = getInventory().getCount(sv.huntThis.getTrapName());
		}
		return amount;
	}
	
	private void getTileArray(int traps, Tile startTile){
		Tile p = getLocalPlayer().getTile();
		switch(traps){
		case 1:
			sv.trapTiles[0] = getLocalPlayer().getTile();
			break;
		case 2:
			for(int i = 0; i < sv.trapTiles.length; i++){
				sv.trapTiles[i] = new Tile(getLocalPlayer().getTile().getX() - i, getLocalPlayer().getTile().getY(), getLocalPlayer().getTile().getZ());
			}
			break;
		case 3:
			for(int i = 0; i < sv.trapTiles.length; i++){
				sv.trapTiles[i] = new Tile(getLocalPlayer().getTile().getX() - i, getLocalPlayer().getTile().getY(), getLocalPlayer().getTile().getZ());
			}
			break;
		case 4:
			sv.trapTiles[0] = p;
			sv.trapTiles[1] = new Tile(p.getX() - 1, p.getY() + 1, p.getZ());
			sv.trapTiles[2] = new Tile(p.getX() + 1, p.getY() + 1, p.getZ());
			sv.trapTiles[3] = new Tile(p.getX() - 1, p.getY() - 1, p.getZ());
			break;
		case 5:
			sv.trapTiles[0] = p;
			sv.trapTiles[1] = new Tile(p.getX() - 1, p.getY() + 1, p.getZ());
			sv.trapTiles[2] = new Tile(p.getX() + 1, p.getY() + 1, p.getZ());
			sv.trapTiles[3] = new Tile(p.getX() - 1, p.getY() - 1, p.getZ());
			sv.trapTiles[4] = new Tile(p.getX() + 1, p.getY() - 1, p.getZ());
			break;
		}
	}
	
	private Timer rt;
	
	private int startingLevel = 0;
	
	private State state;
	
	Filter<GameObject> brokenSnareFilter = new Filter<GameObject>(){
		public boolean match(GameObject r){
			if(r == null || r.getID() != sv.huntThis.getBrokenTrapID())
				return false;
			return true;
		}
	};
	
	Filter<GameObject> fullSnareFilter = new Filter<GameObject>(){
		public boolean match(GameObject r){
			if(r == null || r.getID() != sv.huntThis.getFullTrapID())
				return false;
			return true;
		}
	};
	
	private enum State{
		LAY_TRAP, PICK_TRAP, EMPTY_TRAP, SLEEP, DROP
	}
	
	private boolean trapDown(){
		for(Tile t : sv.trapTiles){
			GroundItem[] tileItems = getGroundItems().getGroundItems(t);
			if(tileItems == null || tileItems.length <= 0){
				continue;
			}
			for(GroundItem gi : tileItems){
				if(gi != null && gi.getName() != null){
					return gi.getName().equals(sv.huntThis.getTrapName());
				}
			}
		}
		return false;
	}
	private boolean trapOnTile(Tile t){
		GroundItem[] tileItems = getGroundItems().getGroundItems(t);
		if(tileItems == null || tileItems.length <= 0){
			return false;
		}
		for(GroundItem gi : tileItems){
			if(gi != null && gi.getName() != null){
				return gi.getName().equals(sv.huntThis.getTrapName());
			}
		}
		return false;
	}
	
	private State getState(){
		if(trapDown()){
			return State.PICK_TRAP;
		}
		if(sv.huntThis.getDropItems().length > 0 && getInventory().contains(sv.huntThis.getDropItems()) && !(trapFull() || trapBroken() || !allTrapsLaid()))
			return State.DROP;
		if(trapFull() || trapBroken())
			return State.EMPTY_TRAP;
		if(!allTrapsLaid())
			return State.LAY_TRAP;
		return State.SLEEP;
	}
	
	@Override
	public int onLoop(){
		if(getSkills().getRealLevel(Skill.HUNTER) >= sv.stopAt){
			stop();
			return -1;
		}
		else if(getTrapAmount() > sv.trapTiles.length){
			sv.trapTiles = new Tile[getTrapAmount()];
			getTileArray(getTrapAmount(), startTile);
		}
		state = getState();
		switch(state){
		case DROP:
			if(standingOnTrap()){
				final Tile t = new Tile(getLocalPlayer().getX(), getLocalPlayer().getY() -1, getLocalPlayer().getZ());
				getMap().interact(t, "Walk here");
				sleepUntil(new Condition(){
					public boolean verify(){
						return getLocalPlayer().getTile().equals(t) && !getLocalPlayer().isMoving();
					}
				},5000);
			}
			else{
				for(int i = 0; i < sv.huntThis.getDropItems().length; i++){
					if(getInventory().contains(sv.huntThis.getDropItems()[i])){
						getInventory().interactWithItem(sv.huntThis.getDropItems()[i], "Drop");
						sleep(Calculations.random(400,700));
					}
				}
			}
			break;
		case EMPTY_TRAP:
			if(trapFull()){
				GameObject fullTrap = getFullTrap();
				if(fullTrap != null){
					fullTrap.interact(sv.huntThis.getEmptyAction());
					long t = System.currentTimeMillis();
					while(System.currentTimeMillis() - t < 3000 && posContainsFullTrap(fullTrap.getTile())){
						sleep(30);
					}
				}
			}
			if(trapBroken()){
				GameObject brokenTrap = getBrokenTrap();
				if(brokenTrap != null){
					brokenTrap.interact(sv.huntThis.getDismantleAction());
					long t = System.currentTimeMillis();
					while(System.currentTimeMillis() - t < 3000 && posContainsBrokenTrap(brokenTrap.getTile())){
						sleep(30);
					}
				}
			}
			break;
		case LAY_TRAP:
			final Tile nextPos = getNextTrapPos();
			if(nextPos != null){
				if(!getLocalPlayer().getTile().equals(nextPos)){
					Tile t = getClient().getDestination();
					if(t != null && t.equals(nextPos)){
						sleepUntil(new Condition(){
							public boolean verify(){
								return getLocalPlayer().getTile().equals(nextPos);
							}
						},1200);
					}
					else{
						getMap().interact(nextPos, "Walk here");
						sleepUntil(new Condition(){
							public boolean verify(){
								return getLocalPlayer().getTile().equals(nextPos);
							}
						},1200);
					}
				}
				else{
					Item trap = getInventory().getItem(sv.huntThis.getTrapName());/*new Filter<Item>(){
						public boolean match(Item i){
							if(i == null || i.getName() == null || !i.getName().equals(sv.huntThis.getTrapName()))
								return false;
							return i.hasAction(sv.huntThis.getLayAction());
						}
					});*/
					if(trap != null){
						if(getLocalPlayer().getTile().equals(nextPos)){
							getInventory().interactWithItem(trap.getName(), sv.huntThis.getLayAction());
							long t = System.currentTimeMillis();
							while(System.currentTimeMillis() - t < 6000 && (!posContainsTrap(nextPos) || getLocalPlayer().getAnimation() != -1)){
								sleep(30);
							}
						}
					}
					sleep(Calculations.random(1100,1400));
				}
			}
			break;
		case PICK_TRAP:
			final GroundItem yourTrap = getGroundItems().getClosest(sv.huntThis.getTrapName());
			if(yourTrap != null){
				yourTrap.interact("Take");
				sleepUntil(new Condition(){
					public boolean verify(){
						return !trapOnTile(yourTrap.getTile());
					}
				},1200);
			}
			break;
		case SLEEP:
			sleep(Calculations.random(100,200));
			if(standingOnTrap()){
				final Tile t = new Tile(getLocalPlayer().getX(), getLocalPlayer().getY() -1, getLocalPlayer().getZ());
				getMap().interact(t,"Walk here");
				sleepUntil(new Condition(){
					public boolean verify(){
						return getLocalPlayer().getTile().equals(t);
					}
				},1200);
				sleep(Calculations.random(300,800));
			}
			break;
		}
		return Calculations.random(150,250);
	}
	
	private boolean standingOnTrap(){
		for(int i = 0; i < sv.trapTiles.length; i++){
			if(getLocalPlayer().getTile().equals(sv.trapTiles[i]))
				return true;
		}
		return false;
	}
	
	private GameObject getBrokenTrap(){
		for(int i = 0; i < sv.trapTiles.length; i++){
			final Tile yourPos = sv.trapTiles[i];
			if(posContainsBrokenTrap(yourPos)){
				return getGameObjects().closest(new Filter<GameObject>(){
					public boolean match(GameObject r){
						if(r == null || r.getID() != sv.huntThis.getBrokenTrapID())
							return false;
						return r.getTile().equals(yourPos);
					}
				});
			}
				
		}
		return null;
	}
	
	private GameObject getFullTrap(){
		for(int i = 0; i < sv.trapTiles.length; i++){
			final Tile yourPos = sv.trapTiles[i];
			if(posContainsFullTrap(yourPos)){
				return getGameObjects().closest(new Filter<GameObject>(){
					public boolean match(GameObject r){
						if(r == null || r.getID() != sv.huntThis.getFullTrapID())
							return false;
						return r.getTile().equals(yourPos);
					}
				});
			}
				
		}
		return null;
	}
	
	private boolean trapBroken(){
		for(int i = 0; i < sv.trapTiles.length; i++){
			final Tile yourPos = sv.trapTiles[i];
			if(posContainsBrokenTrap(yourPos))
				return true;
		}
		return false;
	}
	
	private boolean posContainsBrokenTrap(final Tile p){
		GameObject currTrap = getGameObjects().closest(new Filter<GameObject>(){
			@Override
			public boolean match(GameObject r) {
				if(r.getName() == null || r.getID() != sv.huntThis.getBrokenTrapID())
					return false;
				return r.getTile().equals(p);
			}
		});
		return currTrap != null;
	}
	
	private boolean trapFull(){
		for(int i = 0; i < sv.trapTiles.length; i++){
			final Tile yourPos = sv.trapTiles[i];
			if(posContainsFullTrap(yourPos))
				return true;
		}
		return false;
	}
	
	private boolean posContainsFullTrap(final Tile p){
		GameObject currTrap = getGameObjects().closest(new Filter<GameObject>(){
			@Override
			public boolean match(GameObject r) {
				if(r.getName() == null || r.getID() != sv.huntThis.getFullTrapID())
					return false;
				return r.getTile().equals(p);
			}
		});
		return currTrap != null;
	}
	
	private boolean allTrapsLaid(){
		return getNextTrapPos() == null;
	}
	
	private Tile getNextTrapPos(){
		for(int i = 0; i < sv.trapTiles.length; i++){
			final Tile yourPos = sv.trapTiles[i];
			if(!posContainsTrap(yourPos))
				return sv.trapTiles[i];
		}
		return null;
	}
	
	private boolean posContainsTrap(final Tile p){
		GameObject currTrap = getGameObjects().closest(new Filter<GameObject>(){
			@Override
			public boolean match(GameObject r) {
				if(r.getName() == null || !r.getName().equals(sv.huntThis.getTrapName()))
					return false;
				return r.getTile().equals(p);
			}
		});
		return currTrap != null;
	}
	
	
	
	public void onExit(){
		log("Exiting script!");
	}
	
	@Override
	public void onPaint(Graphics2D g){
		if(sv.started){
			track.update();
			if(state != null)
				g.drawString("State: " + state.toString(), 5, 50);
			g.drawString("Exp gained(p/h): " + getSkillTracker().getGainedExperience(Skill.HUNTER) + "(" + getSkillTracker().getGainedExperiencePerHour(Skill.HUNTER) + ")", 5, 65);
			g.drawString("Level: " + getSkills().getRealLevel(Skill.HUNTER) + "(" + (getSkills().getRealLevel(Skill.HUNTER) - startingLevel) + ")", 5, 80);
			g.drawString("Runtime: " + rt.formatTime(), 5, 95);
			g.drawString("Currently hunting: " + sv.huntThis.getName(), 5, 110);
			g.drawString(track.getName() + "(p/h): " + track.getAmount() + "(" + rt.getHourlyRate(track.getAmount()) + ")", 5, 125);
		}
	}

}
