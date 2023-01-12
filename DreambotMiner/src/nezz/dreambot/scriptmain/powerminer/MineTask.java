package nezz.dreambot.scriptmain.powerminer;

import nezz.dreambot.tools.PricedItem;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;

import java.util.List;

public class MineTask {
	private String goal;
	private Tile startTile;
	private int[] ids;
	private boolean powermine;
	private Timer t;
	private String oreName;
	private PricedItem oreTracker;
	private BankLocation bank;
	private boolean finished = false;
	private boolean dontMove;

	private final Filter<GameObject> rockFilter = go -> {
		if (go == null || !go.exists() || go.getName() == null || !go.getName().equals("Rocks"))
			return false;
		boolean hasID = false;
		for (int i = 0; i < getIDs().length; i++) {
			if (go.getID() == getIDs()[i]) {
				hasID = true;
				break;
			}
		}
		if (!hasID)
			return false;
		return !dontMove() || !(go.distance(Players.getLocal()) > 1);
	};

	public MineTask(String oreName, int[] ids, Tile startTile, String goal, boolean powermine, BankLocation bank, boolean dontMove) {
		this.oreName = oreName;
		this.ids = ids;
		this.startTile = startTile;
		this.goal = goal;
		this.powermine = powermine;
		this.oreTracker = new PricedItem(oreName, false);
		this.bank = bank;
		this.dontMove = dontMove;
		t = new Timer();
	}

	public boolean dontMove() {
		return this.dontMove;
	}

	public void resetTimer() {
		t = new Timer();
	}

	public boolean reachedGoal() {
		if (goal.toLowerCase().contains("bank")) {
			Item ore = Bank.get(oreName);
			if (ore == null)
				return false;
			else {
				if (ore.getAmount() >= Integer.parseInt(goal.split("=")[1])) {
					this.finished = true;
					return true;
				}
				return false;
			}
		} else if (goal.toLowerCase().contains("level")) {
			this.finished = Skills.getRealLevel(Skill.MINING) >= Integer.parseInt(goal.split("=")[1]);
			return finished;
		} else if (goal.toLowerCase().contains("mine")) {
			this.finished = oreTracker.getAmount() >= Integer.parseInt(goal.split("=")[1]);
			return finished;
		}
		return false;
	}

	private GameObject getClosest(List<GameObject> rocks) {
		GameObject currRock = null;
		double dist = Double.MAX_VALUE;
		for (GameObject go : rocks) {
			if (currRock == null) {
				currRock = go;
				dist = go.distance(Players.getLocal());
				continue;
			}
			double tempDist = go.distance(Players.getLocal());
			if (tempDist < dist) {
				currRock = go;
				dist = tempDist;
			}
		}
		return currRock;
	}

	public Filter<GameObject> getRockFilter() {
		return this.rockFilter;
	}

	public GameObject getRock() {
		List<GameObject> acceptableRocks = GameObjects.all(rockFilter);
		return getClosest(acceptableRocks);
	}

	public boolean isPowerMine() {
		return powermine;
	}

	public Tile getStartTile() {
		return startTile;
	}

	public String getGoal() {
		return goal;
	}

	public PricedItem getTracker() {
		return oreTracker;
	}

	public String getOreName() {
		return oreName;
	}

	public Timer getTimer() {
		return t;
	}

	public int[] getIDs() {
		return ids;
	}

	public BankLocation getBank() {
		return bank;
	}

	public boolean getFinished() {
		return this.finished;
	}

	public void setDontMove(boolean dontMove) {
		this.dontMove = dontMove;
	}

	public void setPowerMine(boolean powermine) {
		this.powermine = powermine;
	}

	public void setStarTile(Tile startTile) {
		this.startTile = startTile;
	}

	public void setGoal(String goal) {
		this.goal = goal;
	}

	public void setTracker(PricedItem oreTracker) {
		this.oreTracker = oreTracker;
	}

	public void setOreName(String oreName) {
		this.oreName = oreName;
	}

	public void setIDs(int[] ids) {
		this.ids = ids;
	}

	public void setBank(BankLocation bank) {
		this.bank = bank;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Ore Name: ").append(oreName).append("\n");
		sb.append("IDs: ");
		for (int i = 0; i < ids.length; i++) {
			if (ids[i] > 0)
				sb.append(ids[i]);
			if (i != ids.length - 1 && ids[i + 1] != 0) {
				sb.append(",");
			}
		}
		sb.append("\n");
		sb.append("Tile: ").append(startTile.toString()).append("\n");
		sb.append("Bank: ").append(bank.toString()).append("\n");
		sb.append("Goal: ").append(goal).append("\n");
		sb.append("Powermine: ").append(powermine).append("\n");
		sb.append("Don't Move: ").append(dontMove);
		return sb.toString();
	}
}
