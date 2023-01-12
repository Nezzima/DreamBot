package nezz.dreambot.scriptmain.hillprayer;

import org.dreambot.api.Client;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.SkillTracker;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;

import java.awt.*;

@ScriptManifest(name = "Prayer on the Hill", author = "Nezz", description = "Kills hill giants and buries their bones", version = 1, category = Category.PRAYER)
public class HillPrayer extends AbstractScript {

	private final Timer t = new Timer();

	private enum State {
		KILL, LOOT, BURY, SLEEP
	}

	private State getState() {
		if (Players.getLocal().isInCombat()) {
			return State.SLEEP;
		} else {
			GroundItem gi = GroundItems.closest("Big bones", "Limpwurt root");
			if (gi != null) {
				return State.LOOT;
			} else if (Inventory.contains("Big bones")) {
				return State.BURY;
			} else
				return State.KILL;
		}
	}

	private State state = null;

	@Override
	public void onStart() {
		SkillTracker.start(Skill.PRAYER);
	}

	@Override
	public int onLoop() {
		if (!Client.isLoggedIn()) {
			return 600;
		}
		state = getState();
		switch (state) {
			case BURY:
				Inventory.interact("Big bones", "Bury");
				sleep(600, 900);
				break;
			case KILL:
				if (Players.getLocal().isInCombat()) {
					return Calculations.random(300, 600);
				}
				NPC giant = NPCs.closest(n -> {
					if (n == null || n.getName() == null || !n.getName().equals("Hill Giant"))
						return false;
					return !n.isInCombat() || (n.getInteractingCharacter() != null && n.getInteractingCharacter().getName().equals(Players.getLocal().getName()));
				});
				if (giant != null) {
					giant.interact("Attack");
					Sleep.sleepUntil(() -> Players.getLocal().isInCombat(), 2000);
				}
				break;
			case LOOT:
				GroundItem gi = GroundItems.closest("Big bones", "Limpwurt root");
				if (gi != null) {
					if (gi.isOnScreen()) {
						gi.interact("Take");
						sleep(900, 1200);
					} else {
						Walking.walk(gi.getTile());
					}
				}
				break;
			case SLEEP:
				sleep(300, 600);
				break;
		}
		return Calculations.random(300, 600);
	}

	public void onPaint(Graphics g) {
		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", Font.BOLD, 11));
		g.drawString("Time Running: " + t.formatTime(), 25, 50);
		g.drawString("Experience(p/h): " + SkillTracker.getGainedExperience(Skill.PRAYER) + "(" + SkillTracker.getGainedExperiencePerHour(Skill.PRAYER) + ")", 25, 65);
		g.drawString("Level(gained): " + Skills.getRealLevel(Skill.PRAYER) + "(" + SkillTracker.getGainedLevels(Skill.PRAYER) + ")", 25, 80);
		if (state != null)
			g.drawString("State: " + state, 25, 95);
	}

	@Override
	public void onExit() {

	}
}