package nezz.dreambot.scriptmain.hunter;


public enum Hunt {
	CrimsonSwift("Crimson Swift", "Bird snare", "Bird snare", 9345, 9373, 9344, "Lay", "Check", "Dismantle", "Red feather", "Bones", "Raw bird meat"),
	CopperLongtail("Copper Longtail", "Bird snare", "Bird snare", 9345, 9379, 9344, "Lay", "Check", "Dismantle", "Orange feather", "Bones", "Raw bird meat"),
	TropicalWagtail("Tropical Wagtail", "Bird snare", "Bird snare", 9345, 9348, 9344, "Lay", "Check", "Dismantle", "Stripy feather", "Bones", "Raw bird meat"),
	GreyChin("Grey Chin", "Box trap", "Shaking box", 9380, 9382, 9385, "Lay", "Check", "Dismantle", "Chinchompa"),
	RedChin("Red Chin", "Box trap", "Shaking box", 9380, 9383, 9385, "Lay", "Check", "Dismantle", "Red chinchompa");

	private final String name;
	private final String trapName;
	private final String emptyTrapName;
	private final String layAction;
	private final String emptyAction;
	private final String dismantleAction;
	private final String trackItem;
	private final int emptyTrapID;
	private final int fullTrapID;
	private final int brokenTrapID;
	private final String[] dropItems;

	Hunt(String name, String trapName, String emptyTrapName, int emptyTrapID, int fullTrapID, int brokenTrapID, String layAction, String emptyAction, String dismantleAction, String trackItem, String... dropItems) {
		this.name = name;
		this.trapName = trapName;
		this.emptyTrapName = emptyTrapName;
		this.emptyTrapID = emptyTrapID;
		this.fullTrapID = fullTrapID;
		this.brokenTrapID = brokenTrapID;
		this.layAction = layAction;
		this.emptyAction = emptyAction;
		this.dismantleAction = dismantleAction;
		this.trackItem = trackItem;
		this.dropItems = dropItems;
	}

	public String getName() {
		return this.name;
	}

	public String getTrapName() {
		return this.trapName;
	}

	public String getEmptyTrapName() {
		return this.emptyTrapName;
	}

	public String getLayAction() {
		return this.layAction;
	}

	public String getEmptyAction() {
		return this.emptyAction;
	}

	public String getTrackItem() {
		return this.trackItem;
	}

	public String getDismantleAction() {
		return this.dismantleAction;
	}

	public int getEmptyTrapID() {
		return this.emptyTrapID;
	}

	public int getFullTrapID() {
		return this.fullTrapID;
	}

	public int getBrokenTrapID() {
		return this.brokenTrapID;
	}

	public String[] getDropItems() {
		return this.dropItems;
	}
}
