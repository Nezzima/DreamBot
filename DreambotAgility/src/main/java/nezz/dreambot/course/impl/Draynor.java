package nezz.dreambot.course.impl;

import nezz.dreambot.course.Course;
import nezz.dreambot.roof.Obstacle;
import nezz.dreambot.roof.Roof;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;

import java.util.ArrayList;
import java.util.List;

public class Draynor extends Course {
	private List<Roof> areas = new ArrayList<>();

	public Draynor() {
		super(new Tile(3104, 3279, 0), new Obstacle("Rough wall", "Climb", new Tile(3104, 3279, 0)));
		areas.add(new Roof(new Obstacle("Tightrope", "Cross", null)).buildArea(new Area(3099, 3277, 3103, 3281, 3)));
		areas.add(new Roof(new Obstacle("Tightrope", "Cross", null)).buildArea(new Area(new Tile(3090, 3276, 3),
				new Tile(3091, 3276, 3),
				new Tile(3089, 3275, 3),
				new Tile(3090, 3275, 3),
				new Tile(3091, 3275, 3),
				new Tile(3088, 3274, 3),
				new Tile(3089, 3274, 3),
				new Tile(3090, 3274, 3),
				new Tile(3091, 3274, 3),
				new Tile(3089, 3273, 3),
				new Tile(3090, 3273, 3))));
		areas.add(new Roof(new Obstacle("Narrow wall", "Balance", null)).buildArea(new Area(3094, 3265, 3089, 3267, 3)));
		areas.add(new Roof(new Obstacle("Wall", "Jump-up", null)).buildArea(new Area(3088, 3261, 3088, 3258, 3)));
		areas.add(new Roof(new Obstacle("Gap", "Jump", null)).buildArea(new Area(3087, 3255, 3094, 3255, 3)));
		areas.add(new Roof(new Obstacle("Crate", "Climb-down", null)).buildArea(new Area(3096, 3261, 3101, 3256, 3)));
	}

	@Override
	public boolean isValid() {
		return Skill.AGILITY.getBoostedLevel() >= 10;
	}

	@Override
	public List<Roof> getRoofs() {
		return areas;
	}
}
