package nezz.dreambot.thiever.enums;

public enum Thieving {
	MAN("Man", "Pickpocket"),
	TEA_STALL("Tea stall", "Steal-from", "Cup of tea"),
	SILK_STALL("Silk stall", "Steal-from", "Silk"),
	BAKERS_STALL("Baker's stall", "Steal-from"),
	MASTER_FARMER("Master Farmer", "Pickpocket");

	private final String npcName;
	private final String action;
	private final String[] dropItems;

	Thieving(String npcName, String action, String... dropItems) {
		this.npcName = npcName;
		this.action = action;
		this.dropItems = dropItems;
	}

	public String getName() {
		return this.npcName;
	}

	public String getAction() {
		return this.action;
	}

	public String[] getDropItems() {
		return this.dropItems;
	}

}
