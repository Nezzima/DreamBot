package nezz.dreambot;

import nezz.dreambot.course.impl.AlKharid;
import nezz.dreambot.course.impl.Draynor;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.frameworks.treebranch.Tree;

@ScriptManifest(category = Category.AGILITY, name = "Nezz Agility", description = "Free Agility script", author = "Nezz", version = 1.0)
public class AgilityTree extends Tree {
	//TODO probably make it finish course before checking if next course is valid.
	@Override
	public void onStart(String... params) {
		onStart();
	}

	@Override
	public void onStart() {
		addBranches(new AlKharid(), new Draynor());
	}

	@Override
	public void onExit() {
		super.onExit();
	}
}
