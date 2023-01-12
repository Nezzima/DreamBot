package nezz.dreambot.roof;

import lombok.Getter;
import lombok.Setter;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.utilities.Sleep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * contains areas and obstacle information
 */
@Getter
@Setter
public class Roof {
	private Obstacle obstacle;
	private Area area;

	public Roof(Obstacle obstacle){
		this.obstacle = obstacle;
	}

	public Roof buildArea(Area area){
		List<Tile> areaTiles = new ArrayList<>(Arrays.asList(area.getTiles()));
		if(this.area != null){
			areaTiles.addAll(Arrays.asList(area.getTiles()));
		}
		this.area = new Area(areaTiles.toArray(new Tile[0]));
		return this;
	}

	public int traverse(Roof next){
		if(obstacle.traverse()){
			Sleep.sleepUntil(()->!area.contains(Players.getLocal().getTile()), ()->Players.getLocal().isMoving() || Players.getLocal().isAnimating(), 3000, 300);
			if(next != null){
				Sleep.sleepUntil(()->next.getArea().contains(Players.getLocal().getTile()), ()->Players.getLocal().isMoving() || Players.getLocal().isAnimating(), 3000, 300);
				if(next.getArea().contains(Players.getLocal().getTile())){
					return Calculations.random(250,600);
				}
			}
			else{
				Sleep.sleepUntil(()->!Players.getLocal().isMoving() && !Players.getLocal().isAnimating(), 3000);
				Sleep.sleepTicks(2);
				return Calculations.random(450,800);
			}
		}
		return Calculations.random(120,350);
	}
}
