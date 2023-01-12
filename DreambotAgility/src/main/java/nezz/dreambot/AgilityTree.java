package nezz.dreambot;

import nezz.dreambot.course.impl.AlKharid;
import nezz.dreambot.course.impl.Draynor;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.frameworks.treebranch.TreeScript;
import org.dreambot.api.script.listener.PaintListener;

import java.awt.*;

@ScriptManifest(category = Category.AGILITY, name = "Nezz Agility", description = "Free Agility script", author = "Nezz", version = 1.0)
public class AgilityTree extends TreeScript implements PaintListener {
	//TODO probably make it finish course before checking if next course is valid.
	@Override
	public void onStart(String... params) {
		onStart();
	}

	@Override
	public void onStart() {
		addBranches(
//				new AlKharid(),
				new Draynor());
	}

	@Override
	public void onExit() {
		super.onExit();
	}

	@Override
	public void onPaint(Graphics2D graphics) {
		if(getCurrentBranchName() != null){
			graphics.drawString(getCurrentBranchName()+":"+getCurrentLeafName(), 5, 135);
			graphics.drawString("Moving: " + Players.getLocal().isMoving(), 5, 150);
		}
	}
}
