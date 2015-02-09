package nezz.dreambot.fisher.enums;

public enum Fish {
	SHRIMP("Small fishing net","", new String[]{"Raw shrimps", "Raw anchovies"}, "Net"),
	TROUT("Fly fishing rod","Feather",new String[]{"Raw trout","Raw salmon"}, "Lure"),
	HERRING("Fishing rod", "Fishing bait", new String[]{"Raw herring", "Raw sardine"}, "Bait"),
	LOBSTER("Lobster pot","", new String[]{"Raw lobster"}, "Cage");
	
	private String itemName;
	private String[] reqActions;
	private String reqItem;
	private String[] fish;
	Fish(String itemName, String reqItem, String[] fish, String...reqActions){
		this.itemName = itemName;
		this.reqItem = reqItem;
		this.reqActions = reqActions;
		this.fish = fish;
	}
	public String getItemName(){
		return this.itemName;
	}
	public String[] getRequiredActions(){
		return this.reqActions;
	}
	public String getRequiredItem(){
		return this.reqItem;
	}
	public String[] getFish(){
		return this.fish;
	}
}
