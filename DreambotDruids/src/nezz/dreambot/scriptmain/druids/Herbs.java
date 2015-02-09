package nezz.dreambot.scriptmain.druids;

public enum Herbs    {
    GUAM(3, 249, 250, 199, 200,2.5),
    MARRENTILL(5, 251, 252, 201, 202,3.8),
    TARROMIN(11, 253, 254, 203, 204,5),
    HARRALANDER(20, 255, 256, 205, 206,6.3),
    RANARR(25, 257, 258, 207, 208, 7.5),
    TOADFLAX(30, 2998, 2999, 3049, 3050, 8),
    IRIT(40, 259, 260, 209, 210,8.8),
    AVANTOE(48, 261, 262, 211, 212,10),
    KWUARM(54, 263, 264, 213, 214,11.3),
    SNAPDRAGON(59, 3000, 3001, 3051, 3052,11.8),
    CADANTINE(65, 265, 266, 215, 216,12.5),
    LANTADYME(67, 2481, 2482, 2485, 2486,13.1),
    DWARFWEED(70, 267, 268, 217, 218,13.8),
    TORSTOL(75, 269, 270, 219, 220,15);

    private int idLevel, unnotedCleanId, notedCleanId, unnotedGrimyId, notedGrimyId;
    double expGained;

    Herbs(int idLevel, int unnotedCleanId, int notedCleanId,  int unnotedGrimyId, int notedGrimyId, double expGained)    {
        this.idLevel = idLevel;
        this.unnotedCleanId = unnotedCleanId;
        this.notedCleanId = notedCleanId;
        this.unnotedGrimyId = unnotedGrimyId;
        this.notedGrimyId = notedGrimyId;
        this.expGained = expGained;
    }

    public static Herbs get(int index)    {
        for (Herbs h: values())
            if (index == h.ordinal())
                return h;
        return null;
    }
    
    public static Herbs getForName(String herbName){
    	for(Herbs h: values()){
    		if(h.getName().equalsIgnoreCase(herbName.toLowerCase()))
    			return h;
    	}
    	return null;
    }
    
    public static Herbs getForUNGrimyID(int id){
    	for(Herbs h: values()){
    		if(h.getUnnotedGrimyId() == id){
    			return h;
    		}
    	}
    	return null;
    }

    public int getUnnotedGrimyId() {
        return unnotedGrimyId;
    }

    public int getIdLevel() {
        return idLevel;
    }

    public int getUnnotedCleanId() {
        return unnotedCleanId;
    }

    public int getNotedCleanId() {
        return notedCleanId;
    }

    public int getNotedGrimyId() {
        return notedGrimyId;
    }

    public String getName() {
        return name().charAt(0) +  name().substring(1).toLowerCase();
    }
    
    public double getExperience(){
    	return expGained;
    }

    public boolean canIdHerb(int currentLevel)    {
        return currentLevel >= idLevel;
    }
}