package nezz.dreambot.course;

import nezz.dreambot.roof.Obstacle;
import nezz.dreambot.roof.Roof;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Map;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.frameworks.treebranch.Branch;
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
		int ind = 0;
		for(Roof roof : getRoofs()){
			if(roof.getArea().contains(Players.getLocal().getTile())){
				currentIndex = ind;
				return roof;
			}
			ind++;
		}
		currentIndex = -1;
		return null;
	}
	public Roof getNext(){
		if(currentIndex < 0){
			return getRoofs().get(0);
		}
		if(currentIndex >= getRoofs().size()-1){
			return null;
		}
		return getRoofs().get(currentIndex+1);
	}

	public int execute(){
		if(needsStart()){
			return start();
		}
		Roof curr = getCurrent();
		return curr != null ? curr.traverse(getNext()) : Calculations.random(400, 700);
	}
}
