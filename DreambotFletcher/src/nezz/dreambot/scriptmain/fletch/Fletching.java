package nezz.dreambot.scriptmain.fletch;

public enum Fletching{
	Arrows("Arrow shaft","Logs", 305,2,1,5),
	Shortbowu("Shortbow (u)","Logs", 305,3,5,5),
	Longbowu("Longbow (u)","Logs", 305,4,10,10),
	OakShortbowu("Oak shortbow (u)","Oak logs", 304, 2, 20, 16.5),
	OakLongbowu("Oak longbow (u)","Oak logs", 304, 3, 25, 25),
	WillowShortbowu("Willow shortbow (u)","Willow logs", 304, 2, 35, 33.3),
	WillowLongbowu("Willow longbow (u)","Willow logs", 304, 3, 40, 41.5);
	
	private String name, log;
	private int parent, child, level;
	private double experience;
	Fletching(String name,String reqItem, int parent, int child, int level, double experience){
		this.name = name;
		this.parent = parent;
		this.child = child;
		this.level = level;
		this.experience = experience;
		this.log = reqItem;
	}
	
	public String getName(){
		return this.name;
	}
	public int getParent(){
		return this.parent;
	}
	public int getChild(){
		return this.child;
	}
	public int getLevel(){
		return this.level;
	}
	public double getExperience(){
		return this.experience;
	}
	public String getLog(){
		return log;
	}
	public String getTree(){
		if(getLog().equals("Logs"))
			return "Tree";
		else
			return (name.split(" ")[0]);
	}
}

