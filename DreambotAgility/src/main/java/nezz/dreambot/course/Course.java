package nezz.dreambot.course;

import nezz.dreambot.roof.Obstacle;
import nezz.dreambot.roof.Roof;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Map;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.frameworks.treebranch.Branch;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;

import java.util.List;

public abstract class Course extends Branch {

	private final Tile start;
	private final Obstacle startObs;

	public Course(Tile start, Obstacle startObs){
		this.start = start;
		this.startObs = startObs;
	}

	public abstract List<Roof> getRoofs();

	public int start(){
		if(start.distance() > 5 || !Map.canReach(start)){
			if(Walking.getDestinationDistance() > 5){
				if(!Walking.isRunEnabled() && Walking.getRunEnergy() > 35){
					Walking.toggleRun();
					Sleep.sleepTick();
				}
				return Calculations.random(350, 800);
			}
			Walking.walk(start);
		}
		else{
			if(startObs.traverse()){
				Sleep.sleepUntil(()->getRoofs().get(0).getArea().contains(Players.getLocal().getTile()) && !Players.getLocal().isMoving() && !Players.getLocal().isAnimating(), ()->Players.getLocal().isMoving() || Players.getLocal().isAnimating(), 3000,150);
				return Calculations.random(400,700);
			}
		}
		return Calculations.random(100,250);
	}

	public boolean needsStart(){
		return getCurrent() == null;
	}

	private int currentIndex = -1;

	public Roof getCurrent(){
		Logger.debug("Current index; " + currentIndex);
		for(int i = Math.max(currentIndex, 0); i < getRoofs().size(); i++){
			Roof roof = getRoofs().get(i);
			if(roof.getArea().contains(Players.getLocal().getTile())){
				currentIndex = i;
				return roof;
			}
		}
		if(currentIndex >= 0 && Players.getLocal().getTile().getZ() > 0){
			//we shouldn't reset
			return getRoofs().get(currentIndex);
		}
		currentIndex = -1;
		return null;
	}
	public Roof getNext(){
		if(currentIndex >= getRoofs().size()-1){
			return null;
		}
		return getRoofs().get(currentIndex+1);
	}

	@Override
	public int onLoop(){
		if(needsStart()){
			Logger.debug("Starting course!");
			return start();
		}
		Roof curr = getCurrent();
		return curr != null ? curr.traverse(getNext()) : Calculations.random(400, 700);
	}
}
