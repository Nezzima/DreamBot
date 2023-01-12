package nezz.dreambot.roof;

import lombok.*;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;

@Getter
@Setter
@AllArgsConstructor
@Builder(access = AccessLevel.PUBLIC)
public class Obstacle {
	private String name;
	private String action;
	private Tile start;

	public boolean traverse(){
		if(start != null && start.distance() > 3){
			Walking.walk(start);
			Sleep.sleepUntil(()->start.distance() <= 3, ()-> Players.getLocal().isMoving(), 1200, 200);
			return false;
		}
		GameObject obj = GameObjects.closest(f->f.getName().equalsIgnoreCase(name) && f.hasAction(action), start != null ? start : Players.getLocal().getTile());
		return obj != null && obj.interact(action);
	}
}
