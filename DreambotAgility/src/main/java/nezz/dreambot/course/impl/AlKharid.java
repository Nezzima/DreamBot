package nezz.dreambot.course.impl;

import nezz.dreambot.course.Course;
import nezz.dreambot.roof.Obstacle;
import nezz.dreambot.roof.Roof;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;

import java.util.ArrayList;
import java.util.List;

public class AlKharid extends Course {
	private List<Roof> areas = new ArrayList<>();

	public AlKharid() {
		super(new Tile(3273, 3195, 0), new Obstacle("Rough wall", "Climb", new Tile(3273, 3195, 0)));
		areas.add(new Roof(new Obstacle("Tightrope", "Cross", null)).buildArea(new Area(3279, 3193, 3270, 3180, 3)));
		areas.add(new Roof(new Obstacle("Cable", "Swing-across", null)).buildArea(new Area(3273, 3174, 3264, 3161, 3)));
		areas.add(new Roof(new Obstacle("Zip line", "Teeth-grip", null)).buildArea(new Area(3282, 3175, 3303, 3160, 3)));
		areas.add(new Roof(new Obstacle("Tropical Tree", "Swing-across", null)).buildArea(new Area(3321, 3157, 3308, 3173, 1)));
		areas.add(new Roof(new Obstacle("Roof top beams", "Climb", null)).buildArea(new Area(3317, 3174, 3312, 3180, 2)));
		areas.add(new Roof(new Obstacle("Tightrope", "Cross", null)).buildArea(new Area(3319, 3180, 3310, 3187, 3)));
		areas.add(new Roof(new Obstacle("Gap", "Jump", null)).buildArea(new Area(3306, 3184, 3296, 3195, 3)));
	}

	@Override
	public boolean isValid() {
		return Skill.AGILITY.getBoostedLevel() >= 20;
	}

	@Override
	public List<Roof> getRoofs() {
		return areas;
	}
}
